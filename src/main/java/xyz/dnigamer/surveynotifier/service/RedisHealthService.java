package xyz.dnigamer.surveynotifier.service;

import io.lettuce.core.api.StatefulRedisConnection;
import org.springframework.stereotype.Service;

@Service
public class RedisHealthService {

    private final StatefulRedisConnection<String, String> redisConnection;

    public RedisHealthService(StatefulRedisConnection<String, String> redisConnection) {
        this.redisConnection = redisConnection;
    }

    public long getRedisPing() {
        long startTime = System.currentTimeMillis();
        try {
            redisConnection.sync().ping();
        } catch (Exception e) {
            return -1;
        }
        return System.currentTimeMillis() - startTime;
    }
}