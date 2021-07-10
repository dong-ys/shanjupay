package com.shanjupay.gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.AuthenticationEntryPoint;


@Configuration
public class ResouceServerConfig {

    public static final String RESOURCE_ID = "shanju-resource";
    public static final String XC_RESOURCE_ID = "xuecheng-resource";

    private AuthenticationEntryPoint point = new RestOAuth2AuthExceptionEntryPoint();
    private RestAccessDeniedHandler handler = new RestAccessDeniedHandler();

    /**
     * 统一认证中心 资源拦截
     */
    @Configuration
    @EnableResourceServer
    public class UAAServerConfig extends ResourceServerConfigurerAdapter {
        @Autowired
        private TokenStore tokenStore;

        @Override
        public void configure(ResourceServerSecurityConfigurer resources) {
            resources.tokenStore(tokenStore).resourceId(RESOURCE_ID).stateless(true);
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {

            http.sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                    .and()
                    .authorizeRequests()
                    .antMatchers("/uaa/druid/**").denyAll()
                    .antMatchers("/uaa/**").permitAll();
        }

    }




    /**
     * 运营平台
     */
    @Configuration
    @EnableResourceServer
    public class OperationServerConfig extends ResourceServerConfigurerAdapter {

        @Autowired
        private TokenStore tokenStore;

        @Override
        public void configure(ResourceServerSecurityConfigurer resources) {
            resources.tokenStore(tokenStore).resourceId(RESOURCE_ID).stateless(true);
            resources.authenticationEntryPoint(point).accessDeniedHandler(handler);
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {

            http.sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                    .and()
                    .authorizeRequests()
                    .antMatchers("/operation/swagger-ui.html").denyAll()
                    .antMatchers("/operation/webjars/**").denyAll()
                    .antMatchers("/operation/druid/**").denyAll()
                    .antMatchers("/operation/m/**").access("#oauth2.hasScope('read') and #oauth2.clientHasRole('ROLE_OPERATION')")
                    .antMatchers("/operation/**").permitAll();

        }

    }


    /**
     * 商户平台
     */
    @Configuration
    @EnableResourceServer
    public class MerchantServerConfig extends ResourceServerConfigurerAdapter {

        @Autowired
        private TokenStore tokenStore;

        @Override
        public void configure(ResourceServerSecurityConfigurer resources) {
            resources.tokenStore(tokenStore).resourceId(RESOURCE_ID).stateless(true);
            resources.authenticationEntryPoint(point).accessDeniedHandler(handler);
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {

            http.sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                    .and()
                    .authorizeRequests()
                    .antMatchers("/merchant/druid/**").denyAll()
                    .antMatchers("/merchant/my/**").access("#oauth2.hasScope('read') and #oauth2.clientHasRole('ROLE_MERCHANT')")
                    .antMatchers("/merchant/swagger-ui.html").permitAll()
                    .antMatchers("/merchant/webjars/**").permitAll()
                    .antMatchers("/merchant/**").permitAll();

        }

    }


    /**
     * 门户网站
     */
    @Configuration
    @EnableResourceServer
    public class PortalServerConfig extends ResourceServerConfigurerAdapter {

        @Autowired
        private TokenStore tokenStore;

        @Override
        public void configure(ResourceServerSecurityConfigurer resources) {
            resources.tokenStore(tokenStore).resourceId(RESOURCE_ID).stateless(true);
            resources.authenticationEntryPoint(point).accessDeniedHandler(handler);
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {

            http.sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                    .and()
                    .authorizeRequests()
                    .antMatchers("/portal/swagger-ui.html").denyAll()
                    .antMatchers("/portal/webjars/**").denyAll()
                    .antMatchers("/portal/druid/**").denyAll()
                    .antMatchers("/portal/m/**").access("#oauth2.hasScope('read') and #oauth2.clientHasRole('ROLE_PORTAL')")
                    .antMatchers("/portal/**").permitAll();

        }

    }


    /**
     * 开放API
     */
    @Configuration
    @EnableResourceServer
    public class APIServerConfig extends ResourceServerConfigurerAdapter {

        @Autowired
        private TokenStore tokenStore;

        @Override
        public void configure(ResourceServerSecurityConfigurer resources) {
            resources.tokenStore(tokenStore).resourceId(RESOURCE_ID).stateless(true);
            resources.authenticationEntryPoint(point).accessDeniedHandler(handler);
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {

            http.sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                    .and()
                    .authorizeRequests()
                    .antMatchers("/api/swagger-ui.html").denyAll()
                    .antMatchers("/api/webjars/**").denyAll()
                    .antMatchers("/api/druid/**").denyAll()
                    .antMatchers("/api/**").access("#oauth2.hasScope('read') and #oauth2.clientHasRole('ROLE_API')");
        }

    }


    /**
     * 第三方支付代理服务
     */
    @Configuration
    @EnableResourceServer
    public class PaymentAgentReceiverServerConfig extends ResourceServerConfigurerAdapter {

        @Autowired
        private TokenStore tokenStore;

        @Override
        public void configure(ResourceServerSecurityConfigurer resources) {
            resources.tokenStore(tokenStore).resourceId(RESOURCE_ID).stateless(true);
            resources.authenticationEntryPoint(point).accessDeniedHandler(handler);
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {

            http.sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                    .and()
                    .authorizeRequests()
                    .antMatchers("/api/swagger-ui.html").denyAll()
                    .antMatchers("/api/webjars/**").denyAll()
                    .antMatchers("/api/druid/**").denyAll()
                    .antMatchers("/payment-receiver/**").permitAll();

        }

    }




    /********************************学成在线2.0 ResourceServer配置***********************************/
    /**
     * 因为学成在线 每个微服务都包含有所有接口，不需要登陆的、需要登陆的、需要权限的，不太好在网关层初步鉴别访问权限
     * 所以所有请求都放行， 由后面的微服务去鉴权
     */
    @Configuration
    @EnableResourceServer
    public class XCServiceServerConfig extends ResourceServerConfigurerAdapter {

        @Autowired
        private TokenStore tokenStore;

        @Override
        public void configure(ResourceServerSecurityConfigurer resources) {
            resources.tokenStore(tokenStore).resourceId(XC_RESOURCE_ID).stateless(true);
            resources.authenticationEntryPoint(point).accessDeniedHandler(handler);
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {
            String[] xcContextPaths = new String[]{"/content/**", "/learning/**", "/media/**", "/order/**", "/system/**", "/teaching/**"};
            http.sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                    .and()
                    .authorizeRequests()
                    .antMatchers("/*/swagger-ui.html").denyAll()
                    .antMatchers("/*/druid/**").denyAll()
                    .antMatchers(xcContextPaths).permitAll();
                    //.access("#oauth2.hasScope('read') and #oauth2.clientHasRole('ROLE_XC_API')");

        }

    }


}
