package org.ratchetgx.orion.module.common.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.ratchetgx.orion.module.common.service.IndexModel;
import org.ratchetgx.orion.module.common.service.IndexNavigator;
import org.ratchetgx.orion.module.common.service.IndexService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 
 * @author hrfan
 */
@Controller
public class IndexController implements ApplicationContextAware {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private IndexService indexService;
	
	@Autowired
	private LayoutInterceptor layoutInterceptor;

	@Value("#{settings['menu.mode']}")
	private String menuMode;

	@RequestMapping(value = "/index")
	public String index(HttpServletRequest request, HttpSession session,
			Model model) {
		
		layoutInterceptor.initLayout(request, indexService);
		
		/**
		Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();
		// 匿名用户
		if (auth instanceof AnonymousAuthenticationToken) {
			session.setAttribute("menuItems",
					indexService.createAnonymousMenu());
		} else {
			SsfwUserDetails userDetails = (SsfwUserDetails) auth.getPrincipal();
			// 菜单切换
			if ("switch".equalsIgnoreCase(menuMode.trim())) {
				// 取得用户角色
				Collection<GrantedAuthority> list = (Collection<GrantedAuthority>) userDetails
						.getAuthorities();
				if (!CollectionUtils.isEmpty(list)) {
					GrantedAuthority aurhority = null;
					String role = request.getParameter("role");
					if (!StringUtils.isBlank(role)) {
						// 检查当前用户角色列表中，是否包含要切换的角色。
						for (GrantedAuthority grantedAuthority : list) {
							if (grantedAuthority.getAuthority().equals(role)) {
								aurhority = grantedAuthority;
								break;
							}
						}
					}
					if (aurhority == null) {
						aurhority = list.iterator().next();
					}
					try {
						session.removeAttribute("menuItems");
						session.setAttribute("menuItems", indexService
								.createMenu(aurhority.getAuthority()));
					} catch (Exception e) {
						log.error(e.getMessage(), e);
					}
					// 角色列表保存到session中。
					session.setAttribute("roleList", list);
					session.setAttribute("currentRole",
							aurhority.getAuthority());
				}
			} else {
				session.setAttribute("menuItems", indexService.createMenu());
			}
		}
		*/
		
		// -下面添加indexModel和导航
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (this.appCtx.containsBean("indexModel")) {
			IndexModel indexModel = (IndexModel) this.appCtx
					.getBean("indexModel");
			log.debug("获取首页数据。");
			model.addAllAttributes(indexModel.getData(request, session, model));
		}
		// 登陆后导航首页
		if (this.appCtx.containsBean("indexNavigator")
				&& auth.getPrincipal() instanceof UserDetails) {
			UserDetails userDetails = (UserDetails) auth.getPrincipal();
			IndexNavigator indexNavigator = (IndexNavigator) this.appCtx
					.getBean("indexNavigator");
			String redirectTo = indexNavigator.navigate(userDetails);
			if (!StringUtils.isBlank(redirectTo)) {
				return "redirect:" + redirectTo;
			}
		}

		return "index";
	}

	@RequestMapping(value = "/navmenu")
	public String navMenu(@RequestParam final String menuItemWid,
			HttpSession session) throws Exception {

		if (menuItemWid != null) {
			session.setAttribute("menuIndex",
					indexService.getMenuIndexed(menuItemWid));
			session.setAttribute("menuitemPaths",
					indexService.getMenuNames(menuItemWid));
			session.setAttribute("currentMenuItemId", menuItemWid);
		}

		return "redirect:" + indexService.getMenupath(menuItemWid);
	}

	private ApplicationContext appCtx;

	public void setApplicationContext(ApplicationContext appCtx)
			throws BeansException {
		this.appCtx = appCtx;
	}
}
