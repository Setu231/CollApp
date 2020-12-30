
package io.collapp.web.security.login.oauth;

import io.collapp.web.security.SecurityConfiguration.*;
import io.collapp.web.security.login.oauth.OAuthResultHandler.*;

import com.google.gson.annotations.SerializedName;

public class GoogleHandler extends OAuthResultHandlerAdapter {

    private GoogleHandler(OAuthServiceBuilder serviceBuilder, OAuthRequestBuilder reqBuilder, String apiKey,
			String apiSecret, String callback, Users users, SessionHandler sessionHandler, String errorPage) {
		super("oauth.google",//
				"https://www.googleapis.com/oauth2/v3/userinfo/",//
				UserInfo.class, "code",//
				users,//
				sessionHandler,//
				errorPage,//
				serviceBuilder.build(new Google20Api(), apiKey, apiSecret, callback, "openid email"), reqBuilder);
	}

	static class UserInfo implements RemoteUserProfile {
		private String email;

		@SerializedName("email_verified")
		private boolean emailVerified;

		@Override
		public boolean valid(Users users, String provider) {
			return emailVerified && users.userExistsAndEnabled(provider, email);
		}

		@Override
		public String username() {
			return email;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public boolean isEmailVerified() {
			return emailVerified;
		}

		public void setEmailVerified(boolean emailVerified) {
			this.emailVerified = emailVerified;
		}
	}


    public static final OAuthResultHandlerFactory FACTORY = new OAuthResultHandlerFactory.Adapter() {

        @Override
        public OAuthResultHandler build(OAuthServiceBuilder serviceBuilder,
                OAuthRequestBuilder reqBuilder, OAuthProvider provider,
                String callback, Users users, SessionHandler sessionHandler,
                String errorPage) {
            return new GoogleHandler(serviceBuilder, reqBuilder, provider.getApiKey(), provider.getApiSecret(), callback, users, sessionHandler, errorPage);
        }

    };
}
