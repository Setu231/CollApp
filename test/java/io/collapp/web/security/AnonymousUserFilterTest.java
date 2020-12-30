
package io.collapp.web.security;

import io.collapp.model.Key;
import io.collapp.service.ConfigurationRepository;
import io.collapp.web.security.SecurityConfiguration.SessionHandler;
import io.collapp.web.security.SecurityConfiguration.User;
import io.collapp.web.security.SecurityConfiguration.Users;
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
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AnonymousUserFilterTest {

	@Mock
	private WebApplicationContext webApplicationContext;

	@Mock
	private ConfigurationRepository configurationRepository;

	@Mock
	private SessionHandler sessionHandler;

	@Mock
	private FilterConfig filterConfig;

	@Mock
	private ServletContext servletContext;

	@Mock
	private Users users;

	@Mock
	private User user;

	@Before
	public void prepare() {

		when(webApplicationContext.getBean(Users.class)).thenReturn(users);
		when(webApplicationContext.getBean(ConfigurationRepository.class)).thenReturn(configurationRepository);
		when(webApplicationContext.getBean(SessionHandler.class)).thenReturn(sessionHandler);
		when(filterConfig.getServletContext()).thenReturn(servletContext);
		when(servletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE))
				.thenReturn(webApplicationContext);
	}

	@Test
	public void testAnonymousUserEnabled() throws IOException, ServletException {

		AnonymousUserFilter auf = new AnonymousUserFilter();
		MockHttpServletRequest request = new MockHttpServletRequest();

		Map<Key, String> conf = new EnumMap<>(Key.class);
		conf.put(Key.SETUP_COMPLETE, "true");
		conf.put(Key.ENABLE_ANON_USER, "true");

		when(configurationRepository.findConfigurationFor(Mockito.<Set<Key>> any())).thenReturn(conf);
		when(sessionHandler.isUserAuthenticated(request)).thenReturn(Boolean.FALSE);
		when(users.findUserByName("system", "anonymous")).thenReturn(user);
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockFilterChain chain = new MockFilterChain();

		auf.init(filterConfig);

		auf.doFilterInternal(request, response, chain);

		Mockito.verify(users).findUserByName("system", "anonymous");
		Mockito.verify(sessionHandler).isUserAuthenticated(request);
	}

	@Test
	public void testAnonymousUserNotEnabled() throws IOException, ServletException {

		AnonymousUserFilter auf = new AnonymousUserFilter();
		MockHttpServletRequest request = new MockHttpServletRequest();

		Map<Key, String> conf = new EnumMap<>(Key.class);
		conf.put(Key.SETUP_COMPLETE, "true");
		conf.put(Key.ENABLE_ANON_USER, "false");

		when(configurationRepository.findConfigurationFor(Mockito.<Set<Key>> any())).thenReturn(conf);
		when(sessionHandler.isUserAuthenticated(request)).thenReturn(Boolean.TRUE);
		when(sessionHandler.isUserAnonymous(request)).thenReturn(Boolean.TRUE);
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockFilterChain chain = new MockFilterChain();

		auf.init(filterConfig);

		auf.doFilterInternal(request, response, chain);
		Mockito.verify(sessionHandler).isUserAuthenticated(request);
		Mockito.verify(sessionHandler).isUserAnonymous(request);
	}
}
