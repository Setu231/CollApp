
package io.collapp.web.security.login.oauth;

import org.scribe.builder.api.DefaultApi20;
import org.scribe.extractors.AccessTokenExtractor;
import org.scribe.model.*;
import org.scribe.oauth.OAuth20ServiceImpl;
import org.scribe.oauth.OAuthService;
import org.scribe.services.Base64Encoder;

import java.nio.charset.StandardCharsets;

import static io.collapp.web.security.login.oauth.Utils.encode;

/**
 * <pre>
 * https://confluence.atlassian.com/display/BITBUCKET/OAuth+on+Bitbucket
 * </pre>
 */
class Bitbucket20Api extends DefaultApi20 {

    @Override
    public String getAccessTokenEndpoint() {
        return "https://bitbucket.org/site/oauth2/access_token";
    }

    @Override
    public String getAuthorizationUrl(OAuthConfig config) {
        return "https://bitbucket.org/site/oauth2/authorize?client_id=" + encode(config.getApiKey()) + "&redirect_uri="
                + encode(config.getCallback()) + "&response_type=code";
    }

    @Override
    public Verb getAccessTokenVerb() {
        return Verb.POST;
    }

    @Override
    public AccessTokenExtractor getAccessTokenExtractor() {
        return new JsonTokenExtractor();
    }

    @Override
    public OAuthService createService(final OAuthConfig config) {
        return new OAuth20ServiceImpl(this, config) {
            @Override
            public Token getAccessToken(Token requestToken, Verifier verifier) {
                //basic auth, as described at https://developer.atlassian.com/static/bitbucket/concepts/oauth2.html
                OAuthRequest request = new OAuthRequest(getAccessTokenVerb(), getAccessTokenEndpoint());

                //basic auth
                request.addHeader("Authorization", "Basic "+Base64Encoder.getInstance().encode((config.getApiKey()+":"+config.getApiSecret()).getBytes(StandardCharsets.UTF_8)));

                request.addBodyParameter(OAuthConstants.CODE, verifier.getValue());
                request.addBodyParameter(OAuthConstants.REDIRECT_URI, config.getCallback());

                request.addBodyParameter("grant_type", "authorization_code");
                Response response = request.send();
                return getAccessTokenExtractor().extract(response.getBody());
            }
        };
    }
}
