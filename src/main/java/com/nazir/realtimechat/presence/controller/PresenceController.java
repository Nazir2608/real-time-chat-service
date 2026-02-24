package com.nazir.realtimechat.presence.controller;

import com.nazir.realtimechat.presence.service.PresenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/presence")
@RequiredArgsConstructor
public class PresenceController {

    private final PresenceService presenceService;

    @GetMapping("/{username}")
    public ResponseEntity<Map<String, Object>> getPresence(@PathVariable String username) {
        log.info("Checking presence for user: {}", username);
        boolean online = presenceService.isOnline(username);
        return ResponseEntity.ok(Map.of("username", username, "status", online ? "online" : "offline"));
    }
}
