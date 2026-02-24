package com.nazir.realtimechat.config;

import com.nazir.realtimechat.config.interceptor.AuthChannelInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final AuthChannelInterceptor authChannelInterceptor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable a simple memory-based message broker to carry the messages 
        // back to the client on destinations prefixed with /topic
        config.enableSimpleBroker("/topic", "/queue");
        
        // Designate the /app prefix for messages that are bound for methods 
        // annotated with @MessageMapping
        config.setApplicationDestinationPrefixes("/app");
        
        // For one-to-one messaging
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Use setAllowedOriginPatterns("*") to avoid CORS issues in Spring Boot 3
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*");
        
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // Register our custom authentication interceptor
        registration.interceptors(authChannelInterceptor);
    }
}
