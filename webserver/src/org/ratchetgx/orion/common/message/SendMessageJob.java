package org.ratchetgx.orion.common.message;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.eclipse.birt.report.model.api.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

public class SendMessageJob {

	private Logger log = LoggerFactory.getLogger(SendMessageJob.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private ApplicationContext applicationContext;

	private final String getMessageSql = "select m.wid,m.sender, m.title, m.content, r.user_id, r.type,r.send_num,r.address,r.wid rwid "
			+ "from ss_message m "
			+ "join SS_MESSAGE_RECEIVER r "
			+ "on (m.wid = r.message_id) "
			+ "where (r.send_flag = 0 and r.send_num <= m.send_max_num and "
			+ "m.expect_send_time <= ?) " + "order by r.send_num desc ";

	public void work() {
		List<Map<String, Object>> messages = jdbcTemplate.queryForList(
				getMessageSql, new Date());
		if (messages.size() == 0) {
			return;
		}
		List<String> success = new ArrayList<String>();
		List<String> failure = new ArrayList<String>();
		// 发送消息
		for (int i = 0; i < messages.size(); i++) {
			Map<String, Object> message = (Map<String, Object>) messages.get(i);
			MessageService messageService = null;
			try {
				int type = ((java.math.BigDecimal) message.get("type"))
						.intValue();
				switch (type) {
				case 1:
					messageService = (MessageService) applicationContext
							.getBean("mailMessageService");
					break;
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				continue;
			}
			String id = (String) message.get("wid");
			if (messageService == null) {
				if (log.isWarnEnabled()) {
					log.warn("找不到消息ID:" + id + " 服务。");
				}
				continue;
			}
			String sender = (String) message.get("sender");
			String title = (String) message.get("title");
			String content = (String) message.get("content");
			String address = (String) message.get("address");
			if (StringUtils.isBlank(sender) || StringUtil.isBlank(address)) {
				if (log.isWarnEnabled()) {
					log.warn("消息ID:" + id + "发送人或地址为空。");
				}
				continue;
			}
			try {
				messageService.send(sender, new String[] { address }, title,
						content);
				success.add((String) message.get("rwid"));
			} catch (Exception e) {
				if (log.isErrorEnabled()) {
					log.error(e.getMessage(), e);
				}
				failure.add((String) message.get("rwid"));
			}
		}
		// 修改是否成功标识
		if (success.size() > 0) {
			StringBuilder sql = new StringBuilder(
					"update ss_message_receiver set send_num = "
							+ " (send_num + 1),send_flag = 1 where wid in (");
			for (int i = 0; i < success.size(); i++) {
				sql.append("?").append(",");
			}
			String updateSendFlagSql = sql.substring(0, sql.length() - 1) + ")";
			if (log.isDebugEnabled()) {
				log.debug(updateSendFlagSql);
			}
			jdbcTemplate.update(updateSendFlagSql, success.toArray());
		}
		// 修改发送次数
		if (failure.size() > 0) {
			StringBuilder sql = new StringBuilder(
					"update ss_message_receiver set send_num = "
							+ " (send_num + 1) where wid in (");
			for (int i = 0; i < success.size(); i++) {
				sql.append("?").append(",");
			}
			String updateSendNumSql = sql.substring(0, sql.length() - 1) + ")";
			if (log.isDebugEnabled()) {
				log.debug(updateSendNumSql);
			}
			jdbcTemplate.update(updateSendNumSql, failure.toArray());
		}
	}
}
