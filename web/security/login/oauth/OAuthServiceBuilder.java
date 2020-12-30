
package io.collapp.web.security.login.oauth;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.Api;
import org.scribe.oauth.OAuthService;

public class OAuthServiceBuilder {


    public OAuthService build(Api api, String apiKey, String apiSecret, String callback) {
        return new ServiceBuilder().provider(api).apiKey(apiKey).apiSecret(apiSecret).callback(callback).build();
    }

    public OAuthService build(Api api, String apiKey, String apiSecret, String callback, String scope) {
        return new ServiceBuilder().provider(api).apiKey(apiKey).apiSecret(apiSecret).callback(callback).scope(scope).build();
    }
}
