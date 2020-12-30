
package io.collapp.web.security.login.oauth;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

final class Utils {

	private Utils() {
	}

	static String encode(String string) {
		try {
			return URLEncoder.encode(string, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}
}
