package org.ratchetgx.orion.module.common.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.ui.Model;

public interface IndexModel {
	public Map getData(HttpServletRequest request, HttpSession session,
			Model model);
}
