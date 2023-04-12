package org.example.configuration.sessions;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.session.data.redis.config.annotation.web.server.EnableRedisWebSession;

@Configuration
@EnableRedisWebSession(maxInactiveIntervalInSeconds = 60*60*24)
public class SessionConfiguration {

    public LettuceConnectionFactory redisConnectionFactory() {

        return new LettuceConnectionFactory();
    }
}
