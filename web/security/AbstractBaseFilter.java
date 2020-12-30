
package io.collapp.web.security;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class AbstractBaseFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String reqURI = req.getRequestURI();

        // if it's not in the context path of the application, the security
        // filter will not be triggered
        if (!reqURI.startsWith(req.getServletContext().getContextPath())) {
            chain.doFilter(req, resp);
            return;
        }

        doFilterInternal(req, resp, chain);
    }

    protected abstract void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)  throws IOException, ServletException;

    @Override
    public void destroy() {
    }

}
