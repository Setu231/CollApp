
package io.collapp.web.config;

import io.collapp.service.UserRepository;
import io.collapp.web.helper.UserSession;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpSession;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TestWebConf {

	public static final MockHttpSession UNAUTH_SESSION;
	public static final MockHttpSession SESSION;

	static {
		HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
		HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
		UNAUTH_SESSION = new MockHttpSession();
		SESSION = new MockHttpSession();
		Mockito.when(req.getSession()).thenReturn(UNAUTH_SESSION);
		Mockito.when(req.getSession(true)).thenReturn(SESSION);
		UserSession.setUser(0, false, req, resp, Mockito.mock(UserRepository.class));
	}

}
