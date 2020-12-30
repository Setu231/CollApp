
package io.collapp.web.support;

import io.collapp.common.collappEnvironment;
import io.collapp.web.support.ResourceController;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


//TODO add check
@RunWith(MockitoJUnitRunner.class)
public class ResourceControllerTest {

	@Mock
	private collappEnvironment env;
	@Mock
	private HttpServletRequest request;
	@Mock
	private HttpServletResponse response;
	@Mock
	private ServletContext context;
	@Mock
	private HttpSession session;

	Set<String> s = new HashSet<>();

	@Before
	public void prepare() throws IOException {
		when(env.getActiveProfiles()).thenReturn(new String[] { "dev" });
		s.add("file.js");
		s.add("file.css");
		s.add("file.html");

		when(context.getResourcePaths(anyString())).thenReturn(s);
		when(context.getResourceAsStream(anyString())).thenReturn(new ByteArrayInputStream(new byte[] { 42, 42, 42 }));
		when(response.getOutputStream()).thenReturn(mock(ServletOutputStream.class));
		when(request.getServletContext()).thenReturn(context);
		when(request.getSession()).thenReturn(session);
	}

	@Test
	public void testIndex() throws IOException {
		new ResourceController(env).handleIndex(request, response);
	}

	@Test
	public void testJs() throws IOException {
		new ResourceController(env).handleJs(request, response);
	}

	@Test
	public void testCss() throws IOException {
		new ResourceController(env).handleCss(request, response);
	}
}
