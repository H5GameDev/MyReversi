package com.casualgame.reversi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class WebConfig  implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
        		.allowedOriginPatterns("*")
                .allowedMethods("GET", "HEAD", "POST","PUT", "DELETE", "OPTIONS")
                .allowCredentials(false)
                .maxAge(3600);
    }
	@Override
    public void addViewControllers(ViewControllerRegistry registry) {
		//registry.addViewController("/").setViewName("forward:/index.html");
		registry.addViewController("/index").setViewName("index");
		registry.addViewController("/").setViewName("index");
		

	}
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {

		
	}
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        AntPathMatcher matcher = new AntPathMatcher();
        matcher.setCaseSensitive(false);
        configurer.setPathMatcher(matcher);
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
		//Execute order are the same as register order.
		//registry.addInterceptor(new ValidPathInterceptor())
		//	.addPathPatterns("/api/**")
		//	.addPathPatterns("/api/**");
		/*registry.addInterceptor(setBeanLogin())
			.addPathPatterns("/api/**");
		registry.addInterceptor(setBeanPrivilege())
			.addPathPatterns("/**");*/
    }
    
    /*@Bean
    TomcatServletWebServerFactory tomcatServletWebServerFactory() {
        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                // TODO Auto-generated method stub
                SecurityConstraint constraint = new SecurityConstraint();
                constraint.setUserConstraint("CONFIDENTIAL");
                SecurityCollection collection = new SecurityCollection();
                collection.addPattern("/");
                constraint.addCollection(collection);
                context.addConstraint(constraint);
            }            
        };
        
        factory.addAdditionalTomcatConnectors(createTomcatConnector());
        return factory;        
    }*/
    
    /*private Connector createTomcatConnector() {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setScheme("http");
        connector.setPort(80);
        connector.setSecure(false);
        connector.setRedirectPort(443);
        return connector;
    }*/
    
}