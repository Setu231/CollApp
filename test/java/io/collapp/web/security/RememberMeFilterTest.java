
package io.collapp.web.security;

import io.collapp.common.CookieNames;
import io.collapp.model.Key;
import io.collapp.model.User;
import io.collapp.service.ConfigurationRepository;
import io.collapp.service.UserRepository;
import io.collapp.web.helper.UserSession;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RememberMeFilterTest {

	@Mock
	private WebApplicationContext webApplicationContext;

	@Mock
	private ConfigurationRepository configurationRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private FilterConfig filterConfig;

	@Mock
	private ServletContext servletContext;

	@Mock
	private User user;

	private static String AUTH_KEY = UserSession.class.getName() + ".AUTH_KEY";

	@Before
	public void prepare() {

		when(webApplicationContext.getBean(ConfigurationRepository.class)).thenReturn(configurationRepository);
		when(webApplicationContext.getBean(UserRepository.class)).thenReturn(userRepository);
		when(filterConfig.getServletContext()).thenReturn(servletContext);
		when(servletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE))
				.thenReturn(webApplicationContext);
	}

	@Test
	public void testRememberMeTokenExists() throws IOException, ServletException {

		RememberMeFilter rmf = new RememberMeFilter();
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.getSession().setAttribute(AUTH_KEY, false);
		Cookie cookie = new Cookie(CookieNames.getRememberMeCookieName(), "2/056a8421-7448-4753-a932-13dc7e4cd510");
		request.setCookies(cookie);
		Map<Key, String> conf = new EnumMap<>(Key.class);
		conf.put(Key.SETUP_COMPLETE, "true");
		when(userRepository.rememberMeTokenExists(Mockito.eq(2), Mockito.eq("056a8421-7448-4753-a932-13dc7e4cd510")))
				.thenReturn(Boolean.TRUE);
		when(userRepository.findById(Mockito.eq(2))).thenReturn(user);
		when(configurationRepository.findConfigurationFor(Mockito.<Set<Key>> any())).thenReturn(conf);
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockFilterChain chain = new MockFilterChain();

		rmf.init(filterConfig);

		rmf.doFilterInternal(request, response, chain);

		Mockito.verify(userRepository).findById(Mockito.eq(2));
		Mockito.verify(userRepository).rememberMeTokenExists(Mockito.eq(2),
				Mockito.eq("056a8421-7448-4753-a932-13dc7e4cd510"));

	}

	@Test
	public void testRememberMeTokenNotExists() throws IOException, ServletException {

		RememberMeFilter rmf = new RememberMeFilter();
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.getSession().setAttribute(AUTH_KEY, false);
		Cookie cookie = new Cookie(CookieNames.getRememberMeCookieName(), "2/056a8421-7448-4753-a932-13dc7e4cd510");
		request.setCookies(cookie);
		Map<Key, String> conf = new EnumMap<>(Key.class);
		conf.put(Key.SETUP_COMPLETE, "true");
		when(userRepository.rememberMeTokenExists(Mockito.eq(2), Mockito.eq("056a8421-7448-4753-a932-13dc7e4cd510")))
				.thenReturn(Boolean.FALSE);
		when(configurationRepository.findConfigurationFor(Mockito.<Set<Key>> any())).thenReturn(conf);
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockFilterChain chain = new MockFilterChain();

		rmf.init(filterConfig);

		rmf.doFilterInternal(request, response, chain);

		Mockito.verify(userRepository).rememberMeTokenExists(Mockito.eq(2),
				Mockito.eq("056a8421-7448-4753-a932-13dc7e4cd510"));
	}

	@Test
	public void testUnconfiguredSetup() throws IOException, ServletException {

		RememberMeFilter rmf = new RememberMeFilter();
		MockHttpServletRequest request = new MockHttpServletRequest();

		Map<Key, String> conf = new EnumMap<>(Key.class);
		conf.put(Key.SETUP_COMPLETE, "false");

		when(configurationRepository.findConfigurationFor(Mockito.<Set<Key>> any())).thenReturn(conf);
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockFilterChain chain = new MockFilterChain();

		rmf.init(filterConfig);

		rmf.doFilterInternal(request, response, chain);

	}
}
