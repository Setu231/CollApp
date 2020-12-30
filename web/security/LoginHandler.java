
package io.collapp.web.security;

import io.collapp.web.security.SecurityConfiguration.SessionHandler;
import io.collapp.web.security.SecurityConfiguration.Users;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface LoginHandler {

	boolean doAction(HttpServletRequest req, HttpServletResponse resp) throws IOException;

	boolean handleLogout(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException;

	List<String> getAllHandlerNames();
	String getBaseProviderName();

	Map<String, Object> modelForLoginPage(HttpServletRequest request);

	abstract class AbstractLoginHandler implements LoginHandler {

		protected final Users users;
		protected final SessionHandler sessionHandler;

		public AbstractLoginHandler(Users users, SessionHandler sessionHandler) {
			this.users = users;
			this.sessionHandler = sessionHandler;
		}

		@Override
		public boolean handleLogout(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		    sessionHandler.invalidate(req, resp);
		    resp.setStatus(HttpServletResponse.SC_OK);
			return true;
		}

		public Map<String, Object> modelForLoginPage(HttpServletRequest request) {
			String tokenValue =  CSRFToken.getToken(request);
			Map<String, Object> r = new HashMap<>();
			r.put("csrfToken", tokenValue);
			r.put("reqUrl", UriComponentsBuilder.fromPath(request.getParameter("reqUrl")).build().encode().toUriString());
			return r;
		}
	}
}
