package org.ratchetgx.orion.security;

import java.util.Collection;
import java.util.Iterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public class SsfwAccessDecisionManager implements AccessDecisionManager {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public void decide(Authentication authentication, Object object,
            Collection<ConfigAttribute> configAttributes)
            throws AccessDeniedException, InsufficientAuthenticationException {
  
        Iterator<ConfigAttribute> itr = configAttributes.iterator();
        while (itr.hasNext()) {
            ConfigAttribute ca = itr.next();
            String needRole = ((SecurityConfig) ca).getAttribute();
            for (GrantedAuthority ga : authentication.getAuthorities()) {
                if (needRole.equals(ga.getAuthority())) {
                    return;
                }
            }
        }

        throw new AccessDeniedException("");
    }

    public boolean supports(ConfigAttribute ca) {
        return true;
    }

    public boolean supports(Class<?> arg0) {
        return true;
    }
}
