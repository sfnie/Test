package org.ratchetgx.orion.common.shortcutmenu;

import java.util.List;

import org.ratchetgx.orion.common.entity.SsMenuitem;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Test {
	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"applicationContext.xml");
		ShortcutMenuService sms = (ShortcutMenuService) context
				.getBean(ShortcutMenuService.class);
		try {
			sms.addShortcutMenuItem("qsyan", "47753DA60E3B452ABDCB768EEC50B75C");
		} catch (Exception e) {
			e.printStackTrace();
		}
		// List<SsMenuitem> list = sms.getShortcutMenu("qsyan");
		// System.out.println(list.size());
		// sms.removeShortcutMenu("qsyan", "47753DA60E3B452ABDCB768EEC50B75C");
	}
}
