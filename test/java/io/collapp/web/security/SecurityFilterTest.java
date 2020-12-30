
package io.collapp.web.security;

import io.collapp.config.WebSecurityConfig;
import io.collapp.model.Key;
import io.collapp.service.ConfigurationRepository;
import io.collapp.service.UserRepository;
import io.collapp.web.security.SecurityConfiguration.SessionHandler;
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
import java.util.LinkedHashMap;
import java.util.Map;

import static org.mockito.Mockito.when;

//TODO COMPLETE the various path....: auth/not auth user. CSRF check
@RunWith(MockitoJUnitRunner.class)
public class SecurityFilterTest {

	@Mock
	private WebApplicationContext webApplicationContext;

	@Mock
	private ConfigurationRepository configurationRepository;

	@Mock
	private SessionHandler sessionHandler;

	@Mock
	private UserRepository userRepository;

	@Mock
	private FilterConfig filterConfig;

	@Mock
	private ServletContext servletContext;

	@Before
	public void prepare() {
		WebSecurityConfig webSecurityConfig = new WebSecurityConfig();

		//
		Map<String, SecurityConfiguration> paths = new LinkedHashMap<>();
		paths.put("configuredAppPathConf", webSecurityConfig.configuredApp(configurationRepository, sessionHandler, webApplicationContext));
		paths.put("unconfiguredAppPathConf", webSecurityConfig.unconfiguredApp(configurationRepository));
		//

		when(webApplicationContext.getBeansOfType(SecurityConfiguration.class)).thenReturn(paths);
		when(filterConfig.getServletContext()).thenReturn(servletContext);
		when(servletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE)).thenReturn(webApplicationContext);
	}

	@Test
	public void test() throws IOException, ServletException {

		SecurityFilter sf = new SecurityFilter();

		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("GET");

		Map<Key, String> conf = new EnumMap<>(Key.class);
		conf.put(Key.SETUP_COMPLETE, "true");
		when(configurationRepository.getValueOrNull(Mockito.eq(Key.SETUP_COMPLETE))).thenReturn("true");

		MockHttpServletResponse response = new MockHttpServletResponse();
		MockFilterChain chain = new MockFilterChain();

		sf.init(filterConfig);

		sf.doFilterInternal(request, response, chain);
	}

	@Test
	public void testSetupNotComplete() throws IOException, ServletException {

		SecurityFilter sf = new SecurityFilter();

		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("GET");

		Map<Key, String> conf = new EnumMap<>(Key.class);
		conf.put(Key.SETUP_COMPLETE, "false");
		when(configurationRepository.getValueOrNull(Mockito.eq(Key.SETUP_COMPLETE))).thenReturn(null);

		MockHttpServletResponse response = new MockHttpServletResponse();
		MockFilterChain chain = new MockFilterChain();

		sf.init(filterConfig);
		sf.doFilterInternal(request, response, chain);
	}
}
