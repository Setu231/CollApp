
package io.collapp.web.security;

import io.collapp.model.Key;
import io.collapp.service.ConfigurationRepository;
import io.collapp.service.UserRepository;
import io.collapp.web.helper.UserSession;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Map;

import static org.springframework.web.context.support.WebApplicationContextUtils.getRequiredWebApplicationContext;

public class RememberMeFilter extends AbstractBaseFilter {

    private ConfigurationRepository configurationRepository;
    private UserRepository userRepository;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        WebApplicationContext ctx = getRequiredWebApplicationContext(filterConfig.getServletContext());
        this.configurationRepository = ctx.getBean(ConfigurationRepository.class);
        this.userRepository = ctx.getBean(UserRepository.class);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        Map<Key, String> configuration = configurationRepository.findConfigurationFor(EnumSet.of(Key.SETUP_COMPLETE));
        if("true".equals(configuration.get(Key.SETUP_COMPLETE))) {
            handleRememberMe(request, response);
        }
        chain.doFilter(request, response);
    }

    private void handleRememberMe(HttpServletRequest req, HttpServletResponse resp) {
        UserSession.authenticateUserIfRemembered(req, resp, userRepository);
    }

}
