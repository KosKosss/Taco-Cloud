package tacos.authorization;


import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.web.SecurityFilterChain;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

@Configuration(proxyBeanMethods = false)
public class AuthorizationServerConfig {


    /**
     * Метод authenticationServerSecurityFilterChain() определяет SecurityFilterChain, который настраивает некоторое поведение по
     * умолчанию для сервера авторизации OAuth 2 и  страницу входа по
     * умолчанию. Аннотация @Order присваивает Ordered.HIGHEST_PRECEDENCE, гарантируя, что если по какой-то причине будут объявлены
     * другие bean-компоненты этого же типа, то данный компонент будет
     * иметь приоритет над другими.
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain
    authorizationServerSecurityFilterChain(HttpSecurity http) throws
            Exception {
        OAuth2AuthorizationServerConfiguration
                .applyDefaultSecurity(http);
        return http
                .formLogin(Customizer.withDefaults())
                .build();
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository(PasswordEncoder passwordEncoder){
        //ID (withId) – случайный уникальный идентификатор
        RegisteredClient registeredClient =
                RegisteredClient.withId(UUID.randomUUID().toString())
                //идентификатор клиента (clientId) – аналог имени пользователя, только в роли пользователя выступает клиент. В данном случае "taco-admin-client"
                        .clientId("taco-admin-client")
                //секрет клиента (clientSecret)  – аналог пароля для клиента;
                //здесь используется слово "secret"
                        .clientSecret(passwordEncoder.encode("secret"))
                        .clientAuthenticationMethod(
                                ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                //тип авторизации (authorizationGrantType) – типы разрешений
                //OAuth 2, поддерживаемые клиентом. В данном случае используются код авторизации и токен обновления
                        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                        .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                //URL переадресации (redirectUri) – один или несколько зарегистрированных URL, куда сервер авторизации может переадресовать клиента после предоставления авторизации. Этот аспект
                //добавляет еще один уровень безопасности, предотвращая получение кода авторизации произвольным приложением, который
                //оно может обменять на токен
                        .redirectUri(
                                "http://127.0.0.1:9090/login/oauth2/code/taco-admin-client")
                //область действия (scope)  – одна или несколько областей действия OAuth 2, которые разрешено запрашивать этому клиенту.
                //Здесь используются три области: "writeIngredients", "deleteIngredients" и константа OidcScopes.OPENID, которая соответствует области "openid". Область "openid" потребуется позже, когда
                //мы будем использовать сервер авторизации в качестве решения
                //единого входа для приложения администратора Taco Cloud
                        .scope("writeIngredients")
                        .scope("deleteIngredients")
                        .scope(OidcScopes.OPENID)
                //параметры клиента (clientSettings) – это лямбда-выражение,
                //позволяющее настраивать параметры клиента. В данном случае
                //мы требуем явного согласия пользователя перед предоставлением доступа к запрошенной области. Без этого доступ к области
                //предоставлялся бы неявно после входа пользователя
                        .clientSettings(
                clientSettings -> clientSettings.requireUserConsent(true))
                .build();
        return new InMemoryRegisteredClientRepository(registeredClient);
    }


    /**
     * JWKSource создает пары 2048-битных ключей RSA, которые будут использоваться для подписи токена. Токен подписывается с использованием закрытого ключа.
     * Сервер ресурсов сможет проверить достоверность токена, указанного
     * в запросе, получив открытый ключ от сервера авторизации.
     */
    @Bean
    public JWKSource<SecurityContext> jwkSource()
            throws NoSuchAlgorithmException {
        RSAKey rsaKey = generateRsa();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }

    private static RSAKey generateRsa() throws NoSuchAlgorithmException {
        KeyPair keyPair = generateRsaKey();RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        return new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
    }
    private static KeyPair generateRsaKey() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }
    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }
}
