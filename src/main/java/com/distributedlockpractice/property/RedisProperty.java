package com.distributedlockpractice.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties("spring.redis")
public class RedisProperty {
    private String host;
    private int port;
}
