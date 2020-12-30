
package io.collapp.web.security;

import io.collapp.web.security.SecurityConfiguration.BasicUrlMatcher;
import io.collapp.web.security.SecurityConfiguration.LoginPageGenerator;
import io.collapp.web.security.SecurityConfiguration.SessionHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

//TODO COMPLETE
@RunWith(MockitoJUnitRunner.class)
public class SecurityConfigurationTest {

    @Mock
    private SessionHandler sessionHandler;

    @Test
    public void testBuildDefault() {
        SecurityConfiguration pc = new SecurityConfiguration()
                .request("/setup/**").denyAll()
                .request("/**").requireAuthenticated()
                .sessionHandler(sessionHandler)
                .login("/login/**", "/login/", new LoginPageGenerator() {
                    @Override
                    public void generate(HttpServletRequest req,
                            HttpServletResponse resp,
                            Map<String, LoginHandler> handlers)
                            throws IOException {
                    }
                }).logout("/logout/**", "/logout");
        pc.buildMatcherList();
    }

    @Test
    public void testBasicUrlMatcher() {
        SecurityConfiguration pc = new SecurityConfiguration();

        BasicUrlMatcher b = new BasicUrlMatcher(pc, "/setup/**");
        b.denyAll();
        b.permitAll();
        b.requireAuthenticated();
    }
}
