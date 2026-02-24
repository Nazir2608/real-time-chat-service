package com.nazir.realtimechat.presence.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class PresenceService {

    private final StringRedisTemplate redisTemplate;
    private static final String PRESENCE_KEY_PREFIX = "user:presence:";
    private static final long PRESENCE_TTL_MINUTES = 5;

    /**
     * Mark a user as online in Redis with a TTL.
     */
    public void markOnline(String username) {
        String key = PRESENCE_KEY_PREFIX + username;
        log.info("Marking user {} as ONLINE in Redis", username);
        redisTemplate.opsForValue().set(key, "online", PRESENCE_TTL_MINUTES, TimeUnit.MINUTES);
    }

    /**
     * Mark a user as offline by deleting their presence key.
     */
    public void markOffline(String username) {
        String key = PRESENCE_KEY_PREFIX + username;
        log.info("Marking user {} as OFFLINE in Redis", username);
        redisTemplate.delete(key);
    }

    /**
     * Check if a user is currently online.
     */
    public boolean isOnline(String username) {
        String key = PRESENCE_KEY_PREFIX + username;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
