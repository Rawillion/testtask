package com.fogstream.testtask.configuration;

import com.fogstream.testtask.filter.CaptchaCheckFilter;
import com.fogstream.testtask.service.CustomUserDetailsService;
import com.fogstream.testtask.service.TokenService;
import com.fogstream.testtask.translator.CustomExceptionTranslator;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configuration.ClientDetailsServiceConfiguration;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter
{
	private CustomUserDetailsService userDetailsService;
	private AuthenticationManager authenticationManager;
	private CaptchaCheckFilter captchaCheckFilter;

	public AuthorizationServerConfiguration(CustomUserDetailsService userDetailsService,
											AuthenticationManager authenticationManager,
											CaptchaCheckFilter captchaCheckFilter)
	{
		this.userDetailsService = userDetailsService;
		this.authenticationManager = authenticationManager;
		this.captchaCheckFilter = captchaCheckFilter;
	}

	@Bean
	public FilterRegistrationBean registration(CaptchaCheckFilter filter) {
		FilterRegistrationBean registration = new FilterRegistrationBean(filter);
		registration.setEnabled(false);
		return registration;
	}

	@Bean
	public JwtTokenStore tokenStore() {
		return new JwtTokenStore(tokenEnhancer());
	}

	@Bean
	protected JwtAccessTokenConverter tokenEnhancer() {
		KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("fgkeystore.jks"), "testtaskfg".toCharArray());
		JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
		converter.setKeyPair(keyStoreKeyFactory.getKeyPair("fgalias"));
		return converter;
	}

	@Primary
	@Bean
	public TokenService tokenServices() throws Exception {
		TokenService defaultTokenServices = new TokenService(userDetailsService);
		defaultTokenServices.setTokenStore(tokenStore());
		defaultTokenServices.setTokenEnhancer(tokenEnhancer());
		defaultTokenServices.setClientDetailsService(buildClientDetailsService());
		return defaultTokenServices;
	}

	@Bean
	public ClientDetailsService buildClientDetailsService() throws Exception {
		ClientDetailsServiceConfiguration serviceConfig = new ClientDetailsServiceConfiguration();
		serviceConfig.clientDetailsServiceConfigurer().inMemory()
					 .withClient("fogstream")
					 .secret("$2a$10$GCIrQY/BOfJBviimY3twcO2QDDCS4V5u1Y2KdJCzTApoBUhTXq2u6")//testtask bcrypt
					 .authorizedGrantTypes("password")
					 .scopes("openid")
					 .accessTokenValiditySeconds(60 * 60 *  24)
					 .refreshTokenValiditySeconds(60 * 60 * 24);
		return serviceConfig.clientDetailsService();
	} //Basic dG5hOnRleHJ1cw==

	@Override
	public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
		oauthServer.addTokenEndpointAuthenticationFilter(captchaCheckFilter);
		oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess("permitAll()");
		oauthServer.allowFormAuthenticationForClients();
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints.authenticationManager(authenticationManager)
				 .allowedTokenEndpointRequestMethods(HttpMethod.POST)
				 .tokenStore(tokenStore())
				 .tokenEnhancer(tokenEnhancer())
				 .accessTokenConverter(tokenEnhancer())
				 .userDetailsService(userDetailsService)
				 .exceptionTranslator(new CustomExceptionTranslator()).reuseRefreshTokens(false);
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.withClientDetails(buildClientDetailsService());
	}

}
