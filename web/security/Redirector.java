
package io.collapp.web.security;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

public final class Redirector {

	private Redirector() {
	}

	public static String cleanupRequestedUrl(String r, HttpServletRequest request) {
		try {
			return (r == null || !r.startsWith("/")) ? (request.getContextPath() + "/") : URLDecoder.decode(r, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return r;
		}
	}


	public static void sendRedirect(HttpServletRequest req, HttpServletResponse resp, String page, Map<String, List<String>> params) throws IOException {
		UriComponents urlToRedirect = UriComponentsBuilder.fromPath(page).queryParams(new LinkedMultiValueMap<>(params)).build();
		resp.sendRedirect(urlToRedirect.toUriString());
	}
}
