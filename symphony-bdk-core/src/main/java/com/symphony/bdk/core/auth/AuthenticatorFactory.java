package com.symphony.bdk.core.auth;

import com.symphony.bdk.core.api.invoker.ApiClient;
import com.symphony.bdk.core.auth.exception.AuthInitializationException;
import com.symphony.bdk.core.auth.impl.BotAuthenticatorRsaImpl;
import com.symphony.bdk.core.auth.impl.OboAuthenticatorRsaImpl;
import com.symphony.bdk.core.auth.jwt.JwtHelper;
import com.symphony.bdk.core.config.model.BdkConfig;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apiguardian.api.API;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;

import javax.annotation.Nonnull;

/**
 * Factory class that provides new instances for the main authenticators :
 * <ul>
 *   <li>{@link BotAuthenticator} : to authenticate the main Bot service account</li>
 *   <li>{@link OboAuthenticator} : to perform on-behalf-of authentication</li>
 * </ul>
 */
@Slf4j
@API(status = API.Status.STABLE)
public class AuthenticatorFactory {

  private final BdkConfig config;
  private final ApiClient loginApiClient;
  private final ApiClient relayApiClient;

  private final JwtHelper jwtHelper = new JwtHelper();

  public AuthenticatorFactory(@Nonnull BdkConfig bdkConfig, @Nonnull ApiClient loginClient, @Nonnull ApiClient relayClient) {
    this.config = bdkConfig;
    this.loginApiClient = loginClient;
    this.relayApiClient = relayClient;
  }

  /**
   * Creates a new instance of a {@link BotAuthenticator} service.
   *
   * @return a new {@link BotAuthenticator} instance.
   */
  public @Nonnull BotAuthenticator getBotAuthenticator() throws AuthInitializationException {

    return new BotAuthenticatorRsaImpl(
        this.config.getBot().getUsername(),
        this.loadPrivateKeyFromPath(this.config.getBot().getPrivateKeyPath()),
        this.loginApiClient,
        this.relayApiClient
    );
  }

  /**
   * Creates a new instance of an {@link OboAuthenticator} service.
   *
   * @return a new {@link OboAuthenticator} instance.
   */
  public @Nonnull OboAuthenticator getOboAuthenticator() throws AuthInitializationException {

    return new OboAuthenticatorRsaImpl(
        this.config.getApp().getAppId(),
        this.loadPrivateKeyFromPath(this.config.getApp().getPrivateKeyPath()),
        this.loginApiClient
    );
  }

  private PrivateKey loadPrivateKeyFromPath(String privateKeyPath) throws AuthInitializationException {
    log.debug("Loading RSA privateKey from path : {}", privateKeyPath);
    try {
      return this.jwtHelper.parseRsaPrivateKey(IOUtils.toString(new FileInputStream(privateKeyPath), StandardCharsets.UTF_8));
    } catch (GeneralSecurityException e) {
      final String message = "Unable to parse RSA Private Key located at " + privateKeyPath;
      log.error(message, e);
      throw new AuthInitializationException(message, e);
    } catch (IOException e) {
      final String message = "Unable to read or find RSA Private Key from path " + privateKeyPath;
      log.error(message, e);
      throw new AuthInitializationException(message, e);
    }
  }
}
