package com.nazir.realtimechat.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return Optional.of("SYSTEM");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return Optional.of(userDetails.getUsername());
        } else if (principal instanceof String s) {
            return Optional.of(s);
        }

        return Optional.of("SYSTEM");
    }
}
