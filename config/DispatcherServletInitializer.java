
package io.collapp.config;

import io.collapp.common.CookieNames;
import io.collapp.web.security.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;
import org.tuckey.web.filters.urlrewrite.gzip.GzipFilter;

import javax.servlet.*;
import javax.servlet.ServletRegistration.Dynamic;
import java.util.Collections;

public class DispatcherServletInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class<?>[] { EnableWebSocketMessageBrocker.class,
                DataSourceConfig.class,//
				PersistenceAndServiceConfig.class,//
				SchedulingServiceConfig.class,//
				WebSecurityConfig.class};
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class<?>[] { WebConfig.class };
	}

	@Override
	protected String[] getServletMappings() {
		return new String[] { "/" };
	}

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		super.onStartup(servletContext);

		// initialize cookie
		if(StringUtils.isNotEmpty(System.getProperty(CookieNames.PROPERTY_NAME))) {
			CookieNames.updatePrefix(System.getProperty(CookieNames.PROPERTY_NAME));
		}
		//

		//definition order = execution order, the first executed filter is CSFRFilter

		addFilter(servletContext, "CSFRFilter", CSFRFilter.class, "/*");

		addFilter(servletContext, "RememberMeFilter", RememberMeFilter.class, "/*");

		addFilter(servletContext, "AnonymousUserFilter", AnonymousUserFilter.class, "/*");

		addFilter(servletContext, "SecurityFilter", SecurityFilter.class, "/*");

		addFilter(servletContext, "ETagFilter", ShallowEtagHeaderFilter.class, "*.js", "*.css",//
                "/", "/project/*", "/admin/*", "/me/",//
                "*.html", "*.woff", "*.eot", "*.svg", "*.ttf");

		addFilter(servletContext, "GzipFilter", GzipFilter.class, "*.js", "*.css",//
                "/", "/project/*", "/admin/*", "/me/",//
                "/api/self", "/api/board/*", "/api/project/*");


		servletContext.setSessionTrackingModes(Collections.singleton(SessionTrackingMode.COOKIE));
		servletContext.getSessionCookieConfig().setHttpOnly(true);
		servletContext.getSessionCookieConfig().setName(CookieNames.getSessionCookieName());
	}

    private static void addFilter(ServletContext context, String filterName, Class<? extends Filter> filterClass, String... urlPatterns) {
	    javax.servlet.FilterRegistration.Dynamic hstsFilter = context.addFilter(filterName, filterClass);
        hstsFilter.setAsyncSupported(true);
        hstsFilter.addMappingForUrlPatterns(null, false, urlPatterns);
	}

	@Override
	protected void customizeRegistration(Dynamic registration) {

		MultipartConfigElement multipartConfigElement = new MultipartConfigElement("");

		registration.setMultipartConfig(multipartConfigElement);
		registration.setInitParameter("dispatchOptionsRequest", "true");
		registration.setAsyncSupported(true);
	}
}
