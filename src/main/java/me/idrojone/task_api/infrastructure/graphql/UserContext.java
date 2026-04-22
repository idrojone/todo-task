package me.idrojone.task_api.infrastructure.graphql;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * Extracts user info from headers and stores in ThreadLocal/RequestContext.
 * GraphQL resolvers can access current user via this component.
 */
@Component
public class UserContext implements WebFilter {

    private static final ThreadLocal<UserInfo> CURRENT_USER = new ThreadLocal<>();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        HttpHeaders headers = exchange.getRequest().getHeaders();
        
        String userId = headers.getFirst("X-User-Id");
        String userEmail = headers.getFirst("X-User-Email");

        if (userId != null) {
            UserInfo userInfo = new UserInfo(userId, userEmail);
            CURRENT_USER.set(userInfo);
            
            return chain.filter(exchange).doFinally(signal -> CURRENT_USER.remove());
        }
        
        return chain.filter(exchange);
    }

    /**
     * Get current user info from anywhere in the request thread.
     */
    public static Optional<UserInfo> getCurrentUser() {
        return Optional.ofNullable(CURRENT_USER.get());
    }

    /**
     * User info extracted from JWT at gateway.
     */
    public record UserInfo(String userId, String email) {}
}