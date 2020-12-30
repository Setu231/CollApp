
package io.collapp.web.security.login.oauth;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import org.scribe.exceptions.OAuthException;
import org.scribe.extractors.AccessTokenExtractor;
import org.scribe.model.Token;

class JsonTokenExtractor implements AccessTokenExtractor {

    private static final Gson GSON = new GsonBuilder().serializeNulls().create();

	@Override
	public Token extract(String response) {
		try {
			return new Token(GSON.fromJson(response, JsonTokenExtractor.AccessToken.class).getAccessToken(), "", response);
		} catch (JsonSyntaxException | NullPointerException e) {
			throw new OAuthException("Cannot extract an acces token. Response was: " + response, e);
		}
	}

	static class AccessToken {
		@SerializedName("access_token")
		private String accessToken;

		public String getAccessToken() {
			return accessToken;
		}

		public void setAccessToken(String accessToken) {
			this.accessToken = accessToken;
		}
	}

}
