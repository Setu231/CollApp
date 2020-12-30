
package io.collapp.web.security;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RunWith(MockitoJUnitRunner.class)
public class CSRFFilterTest {


    @Mock
    private FilterConfig filterConfig;


    @Test
    public void testGET() throws IOException, ServletException {
        CSFRFilter filter = new CSFRFilter();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = Mockito.mock(FilterChain.class);

        filter.init(filterConfig);
        filter.doFilterInternal(request, response, chain);

        Mockito.verify(chain).doFilter(request, response);
    }

    @Test
    public void testPOSTWebSocket() throws IOException, ServletException {
        CSFRFilter filter = new CSFRFilter();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/socket/test");
        request.setMethod("POST");

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = Mockito.mock(FilterChain.class);

        filter.init(filterConfig);
        filter.doFilterInternal(request, response, chain);

        Mockito.verify(chain).doFilter(request, response);
    }

    @Test
    public void testPOSTWebSocketWithAnotherContextPath() throws IOException, ServletException {
        CSFRFilter filter = new CSFRFilter();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/my-context-path/api/socket/test");
        request.setMethod("POST");
        request.setContextPath("/my-context-path");

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = Mockito.mock(FilterChain.class);

        filter.init(filterConfig);
        filter.doFilterInternal(request, response, chain);

        Mockito.verify(chain).doFilter(request, response);
    }

    @Test
    public void testPOSTWithoutToken() throws IOException, ServletException {
        CSFRFilter filter = new CSFRFilter();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = Mockito.mock(FilterChain.class);

        filter.init(filterConfig);
        filter.doFilterInternal(request, response, chain);
        Assert.assertEquals(HttpServletResponse.SC_FORBIDDEN, response.getStatus());
    }

    @Test
    public void testPOSTWithToken() throws IOException, ServletException {
        CSFRFilter filter = new CSFRFilter();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = Mockito.mock(FilterChain.class);

        filter.init(filterConfig);
        filter.doFilterInternal(request, response, chain);
        Mockito.verify(chain).doFilter(request, response);


        request.setMethod("POST");
        request.setParameter("_csrf", response.getHeader("X-CSRF-TOKEN"));
        filter.doFilterInternal(request, response, chain);
        Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

    @Test
    public void testPOSTHeaderWithToken() throws IOException, ServletException {
        CSFRFilter filter = new CSFRFilter();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = Mockito.mock(FilterChain.class);

        filter.init(filterConfig);
        filter.doFilterInternal(request, response, chain);
        Mockito.verify(chain).doFilter(request, response);


        request.setMethod("POST");
        request.addHeader("X-CSRF-TOKEN", response.getHeader("X-CSRF-TOKEN"));
        filter.doFilterInternal(request, response, chain);
        Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

    @Test
    public void testPOSTWithWrongToken() throws IOException, ServletException {
        CSFRFilter filter = new CSFRFilter();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = Mockito.mock(FilterChain.class);

        filter.init(filterConfig);
        filter.doFilterInternal(request, response, chain);
        Mockito.verify(chain).doFilter(request, response);


        request.setMethod("POST");
        request.setParameter("_csrf", response.getHeader("X-CSRF-TOKEN"));
        CSRFToken.setToken(request, "plop");
        filter.doFilterInternal(request, response, chain);
        Assert.assertEquals(HttpServletResponse.SC_FORBIDDEN, response.getStatus());
    }

}
