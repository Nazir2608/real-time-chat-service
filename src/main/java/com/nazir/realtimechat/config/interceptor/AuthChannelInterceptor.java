package com.nazir.realtimechat.config.interceptor;

import com.nazir.realtimechat.common.constants.SecurityConstants;
import com.nazir.realtimechat.common.util.JwtUtil;
import com.nazir.realtimechat.presence.service.PresenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthChannelInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final PresenceService presenceService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            log.info("WebSocket CONNECT command detected. Checking headers...");
            String authHeader = accessor.getFirstNativeHeader(SecurityConstants.AUTHORIZATION_HEADER);
            log.info("Authorization Header: {}", authHeader != null ? "Found" : "Missing");

            if (StringUtils.hasText(authHeader) && authHeader.startsWith(SecurityConstants.BEARER_PREFIX)) {
                String token = authHeader.substring(SecurityConstants.BEARER_PREFIX.length());
                if (jwtUtil.isTokenValid(token)) {
                    String username = jwtUtil.extractSubject(token);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    
                    accessor.setUser(authentication);
                    log.info("WebSocket connection authenticated for user: {}", username);
                    
                    // Mark user as online
                    presenceService.markOnline(username);
                } else {
                    log.error("Invalid JWT token for WebSocket connection");
                    throw new IllegalArgumentException("Invalid token");
                }
            } else {
                log.error("Missing or invalid Authorization header for WebSocket connection");
                throw new IllegalArgumentException("Missing token");
            }
        }

        return message;
    }

    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        if (accessor != null && StompCommand.DISCONNECT.equals(accessor.getCommand())) {
            if (accessor.getUser() != null) {
                String username = accessor.getUser().getName();
                log.info("WebSocket DISCONNECT detected for user: {}", username);
                presenceService.markOffline(username);
            }
        }
    }
}
