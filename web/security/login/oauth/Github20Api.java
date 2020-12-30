
package io.collapp.web.security.login.oauth;

import org.scribe.builder.api.DefaultApi20;
import org.scribe.model.OAuthConfig;

import static io.collapp.web.security.login.oauth.Utils.encode;

class Github20Api extends DefaultApi20 {

	@Override
	public String getAccessTokenEndpoint() {
		return "https://github.com/login/oauth/access_token";
	}

	@Override
	public String getAuthorizationUrl(OAuthConfig config) {
		return "https://github.com/login/oauth/authorize?client_id=" + encode(config.getApiKey()) + "&redirect_uri="
				+ encode(config.getCallback());
	}
}
