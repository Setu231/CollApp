
package io.collapp.web.security.login.oauth;

import com.google.gson.annotations.SerializedName;
import io.collapp.web.security.SecurityConfiguration.SessionHandler;
import io.collapp.web.security.SecurityConfiguration.Users;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;

public class TwitterHandler extends AbstractOAuth1Handler {

    private TwitterHandler(OAuthServiceBuilder serviceBuilder, OAuthRequestBuilder reqBuilder, String apiKey,
			String apiSecret, String callback, Users users, SessionHandler sessionHandler, String errorPage) {
		super("oauth.twitter", "https://api.twitter.com/1.1/account/verify_credentials.json", UserInfo.class,
				"oauth_verifier", users, sessionHandler, errorPage, serviceBuilder.build(new TwitterApi(),
						apiKey, apiSecret, callback), reqBuilder);
	}

	private static class UserInfo implements RemoteUserProfile {

		@SerializedName("screen_name")
		private String screenName;

		@Override
		public boolean valid(Users users, String provider) {
			return users.userExistsAndEnabled(provider, username());
		}

		@Override
		public String username() {
			return screenName;
		}

	}


    public static final OAuthResultHandlerFactory FACTORY = new OAuthResultHandlerFactory.Adapter() {

        @Override
        public OAuthResultHandler build(OAuthServiceBuilder serviceBuilder,
                OAuthRequestBuilder reqBuilder, OAuthProvider provider,
                String callback, Users users, SessionHandler sessionHandler,
                String errorPage) {
            return new TwitterHandler(serviceBuilder, reqBuilder, provider.getApiKey(), provider.getApiSecret(), callback, users, sessionHandler, errorPage);
        }

    };
}
