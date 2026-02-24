package com.nazir.realtimechat.presence.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class PresenceService {

    private final StringRedisTemplate redisTemplate;
    private static final String PRESENCE_KEY_PREFIX = "user:presence:";
    private static final String LAST_SEEN_KEY_PREFIX = "user:lastSeen:";
    private static final long PRESENCE_TTL_MINUTES = 5;

    /**
     * Mark a user as online in Redis with a TTL.
     */
    public void markOnline(String username) {
        String key = PRESENCE_KEY_PREFIX + username;
        log.debug("Marking user {} as ONLINE in Redis", username);
        redisTemplate.opsForValue().set(key, "online", PRESENCE_TTL_MINUTES, TimeUnit.MINUTES);
    }

    /**
     * Mark a user as offline by deleting their presence key and storing last seen.
     */
    public void markOffline(String username) {
        String presenceKey = PRESENCE_KEY_PREFIX + username;
        String lastSeenKey = LAST_SEEN_KEY_PREFIX + username;
        
        log.info("Marking user {} as OFFLINE in Redis and updating last seen", username);
        redisTemplate.delete(presenceKey);
        
        // Store current epoch milliseconds as last seen
        redisTemplate.opsForValue().set(lastSeenKey, String.valueOf(System.currentTimeMillis()));
    }

    /**
     * Check if a user is currently online.
     */
    public boolean isOnline(String username) {
        String key = PRESENCE_KEY_PREFIX + username;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * Get the last seen timestamp for a user.
     * Returns null if no last seen info exists.
     */
    public Long getLastSeen(String username) {
        String key = LAST_SEEN_KEY_PREFIX + username;
        String val = redisTemplate.opsForValue().get(key);
        return val != null ? Long.parseLong(val) : null;
    }

    /**
     * Get presence for all users by scanning keys.
     */
    public Map<String, Map<String, Object>> getAllPresence() {
        Set<String> keys = redisTemplate.keys(PRESENCE_KEY_PREFIX + "*");
        Map<String, Map<String, Object>> result = new HashMap<>();
        
        if (keys != null) {
            for (String key : keys) {
                String username = key.substring(PRESENCE_KEY_PREFIX.length());
                result.put(username, Map.of(
                    "username", username,
                    "status", "online",
                    "lastSeen", ""
                ));
            }
        }
        
        // Also add last seen for known offline users? 
        // For now, only return online users for simplicity, or 
        // just let the frontend assume missing users are offline.
        return result;
    }
}
