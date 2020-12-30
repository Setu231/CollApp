
package io.collapp.web.security.login.oauth;

import io.collapp.web.security.SecurityConfiguration.SessionHandler;
import io.collapp.web.security.SecurityConfiguration.Users;
import io.collapp.web.security.login.oauth.OAuthResultHandler.OAuthRequestBuilder;

public interface OAuthResultHandlerFactory {

    OAuthResultHandler build(OAuthServiceBuilder serviceBuilder,
            OAuthRequestBuilder reqBuilder,
            OAuthProvider oauthProvider,
            String callback,
            Users users,
            SessionHandler sessionHandler,
            String errorPage);

    boolean hasConfigurableBaseUrl();

    boolean isConfigurableInstance();

    public abstract class Adapter implements OAuthResultHandlerFactory {
        @Override
        public boolean hasConfigurableBaseUrl() {
            return false;
        }

        @Override
        public boolean isConfigurableInstance() {
            return false;
        }
    }
}
