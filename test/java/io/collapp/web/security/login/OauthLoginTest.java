
package io.collapp.web.security.login;

import io.collapp.web.security.SecurityConfiguration.SessionHandler;
import io.collapp.web.security.SecurityConfiguration.Users;
import io.collapp.web.security.login.OAuthLogin.OAuthConfiguration;
import io.collapp.web.security.login.OAuthLogin.OauthConfigurationFetcher;
import io.collapp.web.security.login.oauth.OAuthProvider;
import io.collapp.web.security.login.oauth.OAuthServiceBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.scribe.builder.api.Api;
import org.scribe.oauth.OAuthService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OauthLoginTest {

	@Mock
	private Users users;
	@Mock
    private SessionHandler sessionHandler;
	@Mock
	private OauthConfigurationFetcher configurationFetcher;
	@Mock
	private OAuthServiceBuilder serviceBuilder;
	@Mock
	private HttpServletResponse resp;
	@Mock
	private HttpServletRequest req;
	@Mock
	private HttpSession session;

	private OAuthConfiguration configuration;

	private String errorPage = "errorPage";

	private OAuthLogin oAuthLogin;

	@Before
	public void prepare() {

	    configuration = new OAuthConfiguration("http://baseUrl", Arrays.asList(
	            new OAuthProvider("google", "", ""),
	            new OAuthProvider("bitbucket", "", "")));

		oAuthLogin = new OAuthLogin(users, sessionHandler, configurationFetcher, serviceBuilder, errorPage);
		when(configurationFetcher.fetch()).thenReturn(configuration);
        when(serviceBuilder.build(any(Api.class), any(String.class), any(String.class), any(String.class), any(String.class))).thenReturn(mock(OAuthService.class));
		when(req.getSession()).thenReturn(session);
	}

	@Test
	public void initiateWithoutPost() throws IOException {
		when(req.getRequestURI()).thenReturn("/login/oauth/google");
		Assert.assertFalse(oAuthLogin.doAction(req, resp));
	}

	@Test
	public void initiateWithPostWrongUrl() throws IOException {
		when(req.getRequestURI()).thenReturn("/login/oauth/derp");
		when(req.getMethod()).thenReturn("POST");
		Assert.assertFalse(oAuthLogin.doAction(req, resp));
	}

	@Test
	public void initiateWithPost() throws IOException {
		when(req.getRequestURI()).thenReturn("/login/oauth/google");
		when(req.getMethod()).thenReturn("POST");
		Assert.assertTrue(oAuthLogin.doAction(req, resp));
		//TODO: fixme
		//verify(authResultHandler).handleAuthorizationUrl(req, resp);
	}

	@Test
	public void callbackHandle() throws IOException {
		when(req.getRequestURI()).thenReturn("/login/oauth/google/callback");
		Assert.assertTrue(oAuthLogin.doAction(req, resp));
		//TODO: fixme
		//verify(authResultHandler).handleCallback(req, resp);
	}

	@Test
	public void callbackHandleForWrongProvider() throws IOException {
		when(req.getRequestURI()).thenReturn("/login/oauth/derp/callback");
		Assert.assertFalse(oAuthLogin.doAction(req, resp));
	}

	@Test
	public void checkModelForLoginPage() {
		when(req.getSession()).thenReturn(mock(HttpSession.class));
		Map<String, Object> r = oAuthLogin.modelForLoginPage(req);
		@SuppressWarnings("unchecked")
		List<String> providers = (List<String>) r.get("loginOauthProviders");
		Assert.assertTrue(providers.contains("google"));
		Assert.assertTrue(providers.contains("bitbucket"));
		Assert.assertFalse(providers.contains("github"));
		Assert.assertFalse(providers.contains("twitter"));
		Assert.assertTrue(r.containsKey("csrfToken"));
	}


}
