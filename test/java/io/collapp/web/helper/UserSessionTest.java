
package io.collapp.web.helper;

import io.collapp.common.CookieNames;
import io.collapp.model.User;
import io.collapp.service.UserRepository;
import io.collapp.web.helper.UserSession;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UserSessionTest {

	private static final String AUTH_USER_ID = UserSession.class.getName() + ".AUTH_USER_ID";

	@Test
	public void isUserAuthenticated() {
		HttpServletRequest req = new MockHttpServletRequest();
		HttpServletResponse resp = new MockHttpServletResponse();
		User user = Mockito.mock(User.class);
		Assert.assertFalse(UserSession.isUserAuthenticated(req));

		UserSession.setUser(user.getId(), user.getAnonymous(), req, resp, Mockito.mock(UserRepository.class));

		Assert.assertTrue(UserSession.isUserAuthenticated(req));

		Assert.assertEquals(user.getId(), UserSession.getUserId(req));
	}

	@Test(expected = NullPointerException.class)
	public void getUserIdFailure() {
		UserSession.getUserId(new MockHttpServletRequest());
	}

	@Test
	public void isUserAnonymous() {
		HttpServletRequest req = new MockHttpServletRequest();
		HttpServletResponse resp = new MockHttpServletResponse();
		UserSession.setUser(1, true, req, resp, Mockito.mock(UserRepository.class));

		Assert.assertTrue(UserSession.isUserAnonymous(req));
		Assert.assertEquals(1, (int) req.getSession().getAttribute(AUTH_USER_ID));
	}

	@Test
	public void testSessionInvalidate() {

		MockHttpServletRequest req = new MockHttpServletRequest();
		HttpServletResponse resp = new MockHttpServletResponse();
		Cookie cookie = new Cookie(CookieNames.getRememberMeCookieName(), "2,056a8421-7448-4753-a932-13dc7e4cd510");
		req.setCookies(cookie);
		UserRepository userRepository = Mockito.mock(UserRepository.class);
		UserSession.setUser(1, true, req, resp, userRepository);
		UserSession.invalidate(req, resp, userRepository);

		Assert.assertNull(req.getSession().getAttribute(AUTH_USER_ID));

	}
}
