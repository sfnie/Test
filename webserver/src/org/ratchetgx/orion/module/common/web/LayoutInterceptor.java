package org.ratchetgx.orion.module.common.web;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.ratchetgx.orion.module.common.service.IndexService;
import org.ratchetgx.orion.security.SsfwUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class LayoutInterceptor implements HandlerInterceptor {
	
	private static Logger log = LoggerFactory.getLogger(LayoutInterceptor.class);
	
	@Autowired
	private IndexService indexService;

	@Value("#{settings['menu.mode']}")
	private String menuMode;

	public boolean preHandle(HttpServletRequest request, 
							 HttpServletResponse response, 
							 Object obj) throws Exception {
		
		HttpSession session = request.getSession();
		if (session.getAttribute("menuItems") == null) {
			log.debug("LayoutInterceptor:preHandle");
			
			initLayout(request, indexService);
		}
		
		return true;
	}
	
	public void postHandle(HttpServletRequest request, 
			   HttpServletResponse response, 
			   Object obj, 
			   ModelAndView exp) throws Exception {
	// TODO Auto-generated method stub
	
	}
	
	public void afterCompletion(HttpServletRequest request, 
			HttpServletResponse response, 
			Object obj, Exception exp) throws Exception {
	// TODO Auto-generated method stub
	
	}
	
	public void initLayout(HttpServletRequest request, 
								  IndexService indexService) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		HttpSession session = request.getSession();
		
		//匿名用户
		if (auth instanceof AnonymousAuthenticationToken) {
			session.setAttribute("menuItems", indexService.createAnonymousMenu());
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
	}
}
