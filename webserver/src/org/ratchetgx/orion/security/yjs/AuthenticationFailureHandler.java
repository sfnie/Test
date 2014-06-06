package org.ratchetgx.orion.security.yjs;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.axis.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import org.ratchetgx.orion.security.DefaultFailureUrlGetter;

public class AuthenticationFailureHandler extends
		SimpleUrlAuthenticationFailureHandler {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	private DefaultFailureUrlGetter defaultFailureUrlGetter;

	public void setDefaultFailureUrlGetter(
			DefaultFailureUrlGetter defaultFailureUrlGetter) {
		this.defaultFailureUrlGetter = defaultFailureUrlGetter;
	}

	public AuthenticationFailureHandler() {
	}

	public AuthenticationFailureHandler(String defaultFailureUrl) {
		setDefaultFailureUrl(defaultFailureUrl);
	}

	/**
	 * Performs the redirect or forward to the {@code defaultFailureUrl} if set,
	 * otherwise returns a 401 error code.
	 * <p>
	 * If redirecting or forwarding, {@code saveException} will be called to
	 * cache the exception for use in the target view.
	 */
	public void onAuthenticationFailure(HttpServletRequest request,
			HttpServletResponse response, AuthenticationException exception)
			throws IOException, ServletException {
		log.debug("onAuthenticationFailure");

		String defaultFailureUrl = null;
		try {
			if (defaultFailureUrlGetter != null) {
				defaultFailureUrl = defaultFailureUrlGetter
						.getDefaultFailureUrl(request, response, exception);
			}
		} catch (Exception e) {
			log.error("", e);
		}

		if (!StringUtils.isEmpty(defaultFailureUrl)) {
			saveException(request, exception);

			if (this.isUseForward()) {
				logger.debug("Forwarding to " + defaultFailureUrl);

				request.getRequestDispatcher(defaultFailureUrl).forward(
						request, response);
			} else {
				logger.debug("Redirecting to " + defaultFailureUrl);
				this.getRedirectStrategy().sendRedirect(request, response,
						defaultFailureUrl);
			}
		} else {
			super.onAuthenticationFailure(request, response, exception);
		}
	}
}
