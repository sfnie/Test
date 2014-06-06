package org.ratchetgx.orion.module.common.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface IndexNavigator {
	public String navigate(UserDetails userDetails);
}
