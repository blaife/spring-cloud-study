package com.atguigu.cloud.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Blaife
 * @description TODO
 * @date 2020/12/19 10:11
 */
@Configuration
public class GateWayConfig {

    @Bean
    public RouteLocator baiduRouteLocator_guonei(RouteLocatorBuilder routeLocatorBuilder) {
        RouteLocatorBuilder.Builder routes = routeLocatorBuilder.routes();
        routes.route("path_route_baidu_guonei", r -> r.path("/guonei")
                .uri("http://news.baidu.com/guonei")).build();

        return routes.build();
    }

    @Bean
    public RouteLocator baiduRouteLocator_guoji(RouteLocatorBuilder routeLocatorBuilder) {
        RouteLocatorBuilder.Builder routes = routeLocatorBuilder.routes();
        routes.route("path_route_baidu_guoji", r -> r.path("/guoji")
                .uri("http://news.baidu.com/guoji")).build();

        return routes.build();
    }

}
