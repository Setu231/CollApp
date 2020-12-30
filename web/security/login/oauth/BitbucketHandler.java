
package io.collapp.web.security.login.oauth;

import io.collapp.web.security.SecurityConfiguration.SessionHandler;
import io.collapp.web.security.SecurityConfiguration.Users;
import io.collapp.web.security.login.oauth.OAuthResultHandler.OAuthResultHandlerAdapter;
import org.scribe.builder.ServiceBuilder;

public class BitbucketHandler extends OAuthResultHandlerAdapter {

	private BitbucketHandler(OAuthServiceBuilder serviceBuilder, OAuthRequestBuilder reqBuilder, String apiKey,
			String apiSecret, String callback, Users users, SessionHandler sessionHandler, String errorPage) {
		super("oauth.bitbucket",//
				"https://api.bitbucket.org/2.0/user",//
				UserInfo.class, "code", //
				users,//
				sessionHandler,//
				errorPage,//
				serviceBuilder.build(new Bitbucket20Api(), apiKey, apiSecret, callback), reqBuilder);
	}

	private static class UserInfo implements RemoteUserProfile {

	    String username;

		@Override
		public boolean valid(Users users, String provider) {
			return users.userExistsAndEnabled(provider, username);
		}

		@Override
		public String username() {
			return username;
		}
	}

	public static final OAuthResultHandlerFactory FACTORY = new OAuthResultHandlerFactory.Adapter() {

        @Override
        public OAuthResultHandler build(OAuthServiceBuilder serviceBuilder,
                OAuthRequestBuilder reqBuilder, OAuthProvider provider,
                String callback, Users users, SessionHandler sessionHandler,
                String errorPage) {
            return new BitbucketHandler(serviceBuilder, reqBuilder, provider.getApiKey(), provider.getApiSecret(), callback, users, sessionHandler, errorPage);
        }
    };
}
