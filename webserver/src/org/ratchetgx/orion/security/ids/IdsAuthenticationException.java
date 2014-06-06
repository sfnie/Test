/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ratchetgx.orion.security.ids;

import org.springframework.security.core.AuthenticationException;

/**
 *
 * @author hrfan
 */
public class IdsAuthenticationException extends AuthenticationException {

    public IdsAuthenticationException(String msg) {
        super(msg);
    }
}
