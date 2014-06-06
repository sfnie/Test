package org.ratchetgx.orion.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.ratchetgx.orion.security.SsfwGrantedAuthority;
import org.ratchetgx.orion.security.SsfwUserDetails;
import org.ratchetgx.orion.security.mixjaccount.MixJAccountUsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
@Component
public class SsfwUtilExt extends SsfwUtil {
	public static Collection<SsfwGrantedAuthority> getCurrentAuthority(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Collection<SsfwGrantedAuthority> list = new ArrayList<SsfwGrantedAuthority>();
		
		if(auth instanceof AnonymousAuthenticationToken){ //匿名登录
			list.add(new SsfwGrantedAuthority("ROLE_ANONYMOUS")); 
		}else if(auth instanceof UsernamePasswordAuthenticationToken){   //账号登录
			SsfwUserDetails userDetails = (SsfwUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			list = (Collection) userDetails.getAuthorities();
		}
		return list;
	}
	 
	public static boolean isAnonymousAuthentication(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(auth instanceof AnonymousAuthenticationToken){ //匿名登录
			 return true;
		}
		return false;
	}
	
	public static String getCurrentUserName(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(auth instanceof AnonymousAuthenticationToken){ //匿名登录
			 return "游客";
		}else if(auth instanceof UsernamePasswordAuthenticationToken){   //账号登录
			SsfwUserDetails userDetails = (SsfwUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			return userDetails.getUsername();
		}
		return null;
	}
	
	//--IdsAuthenticationFilter.DB_IDENTIFIER : DB认证
	//--IdsAuthenticationFilter.IDS_IDENTIFIER ：IDS认证
	public static String getLoginUserType(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if(auth instanceof UsernamePasswordAuthenticationToken){   //账号登录
			
			if (auth instanceof MixJAccountUsernamePasswordAuthenticationToken) {
				MixJAccountUsernamePasswordAuthenticationToken mjAuth = (MixJAccountUsernamePasswordAuthenticationToken)auth;
				return mjAuth.getMode();
			} else {
				SsfwUserDetails userDetails = (SsfwUserDetails) auth.getPrincipal();
				return userDetails.getUserLoginType(); 
			}
		}
		return null;
	}
	
	/**
	 * 获取角色名称
	 * @return
	 */
	public static List<String> getUserRoles(){
		Collection<SsfwGrantedAuthority> list = getCurrentAuthority();
	    Iterator<SsfwGrantedAuthority> iter = list.iterator();
	    List<String> userRoles = new ArrayList<String>();
	    while (iter.hasNext()) {
	    	userRoles.add(iter.next().getAuthority());
	    }
	    return userRoles;
	}
	
	
	public static boolean isAdminRole(){
		Collection<SsfwGrantedAuthority> list = getCurrentAuthority();
	    Iterator<SsfwGrantedAuthority> iter = list.iterator();
	    boolean res = false;
	    while (iter.hasNext()) {
	    	if("ADMIN".equalsIgnoreCase(iter.next().getAuthority())){
	    		res = true;
	    		break;
	    	}
	    }
	    return res;
	}
	
}