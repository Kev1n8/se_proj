package com.codeisright.attendance.security;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;

@Configuration
public class JedisConfig {
//    @Bean
//    JedisConnectionFactory jedisConnectionFactory() {
//        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration("localhost", 6379);
////        redisStandaloneConfiguration.setPassword(RedisPassword.of(""));
//        return new JedisConnectionFactory(redisStandaloneConfiguration);
//    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();

        template.setConnectionFactory(lettuceConnectionFactory());
        return template;
    }

    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory() {
        // 创建RedisStandaloneConfiguration
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration("localhost", 6379);
        // 创建LettuceClientConfiguration
        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .commandTimeout(Duration.ofSeconds(60))
                .shutdownTimeout(Duration.ZERO)
                .clientOptions(
                        ClientOptions.builder()
                                .disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS)
                                .autoReconnect(true)
                                .socketOptions(SocketOptions.builder().keepAlive(true).build())
                                .build())
                .build();
        // 创建LettuceConnectionFactory
        return new LettuceConnectionFactory(redisStandaloneConfiguration, clientConfig);
    }

}
