/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ratchetgx.orion.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;

import org.ratchetgx.orion.common.util.*;
import org.ratchetgx.orion.security.SsfwUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.WebApplicationContext;

/**
 *
 * @author hrfan
 */
public class SsfwUtil {

    public static String COMPONENT_DBUTIL = "componet.dbUtil";
    public static String COMPONENT_BIZOBJ_UTIL = "current.request";
    public static String CURRENT_SESSION = "current.session";
    public static String CURRENT_CTX = "current.ctx";
    public static String CURRENT_MESSAGES = "current.messages";
    public static String WEBAPP_ABSOLUTE_PATH = "webapp.absolute.path";
    public static String CURRENT_REQUEST = "current.request";
    
    public static String contextPath = null;
    
    private static ThreadLocal<Map> ctx = new ThreadLocal<Map>();

    public static void setValue(Object key, Object value) {
        Map ctxMap = ctx.get();
        if (ctxMap == null) {
            ctxMap = new HashMap();
            ctx.set(ctxMap);
        }
        ctxMap.put(key, value);
    }

    public static Object getValue(Object key) {
        Map ctxMap = ctx.get();
        if (ctxMap == null) {
            ctxMap = new HashMap();
            ctx.set(ctxMap);
        }
        return ctxMap.get(key);
    }

    /**
     * 添加消息
     *
     * @param message 消息内容
     */
    public static void addMessage(String message) {
        HttpSession session = (HttpSession) SsfwUtil.getValue(SsfwUtil.CURRENT_SESSION);
        List<String> messages = (List<String>) session.getAttribute(SsfwUtil.CURRENT_MESSAGES);
        if (messages == null) {
            messages = new ArrayList<String>();
            session.setAttribute(SsfwUtil.CURRENT_MESSAGES, messages);
        }
        messages.add(message);
    }

    /**
     * 获取已添加的消息
     *
     * @return List
     */
    public static List<String> getMessages() {
        HttpSession session = (HttpSession) SsfwUtil.getValue(SsfwUtil.CURRENT_SESSION);
        List<String> messages = (List<String>) session.getAttribute(SsfwUtil.CURRENT_MESSAGES);
        if (messages == null) {
            return new ArrayList<String>();
        }
        session.removeAttribute(SsfwUtil.CURRENT_MESSAGES);
        return messages;
    }

    /**
     * 获取当前登录人的编号
     *
     * @return
     */
    public static String getCurrentBh() {
    	Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	if(principal instanceof String){
    		return (String)principal;
    	}
        SsfwUserDetails ssfwUserDetails = (SsfwUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ssfwUserDetails.getBh();
    }

    /**
     * 获取当前用户的附加信息
     *
     * @return
     */
    public static Map getCurrentAttach() {
        SsfwUserDetails ssfwUserDetails = (SsfwUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ssfwUserDetails.getAttached();
    }

    /**
     * 获取数据操作组件
     *
     * @return
     */
    public static DbUtil getDbUtil() {
        return (DbUtil) ctx.get().get(SsfwUtil.COMPONENT_DBUTIL);
    }

    /**
     * 获取业务对象操作组件
     *
     * @return
     */
    public static BizobjUtil getBizobjUtil() {
        return (BizobjUtil) ctx.get().get(SsfwUtil.COMPONENT_BIZOBJ_UTIL);
    }
    
    /**
     * 获取业务CTX上下文对象操作组件
     *
     * @return
     */
    public static WebApplicationContext getContext() {
        return (WebApplicationContext) ctx.get().get(SsfwUtil.CURRENT_CTX);
    }
}
