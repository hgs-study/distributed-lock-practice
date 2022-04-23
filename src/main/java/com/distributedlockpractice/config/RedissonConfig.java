package com.distributedlockpractice.config;

import com.distributedlockpractice.property.RedisProperty;
import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Configuration
public class RedissonConfig {

    private final RedisProperty redisProperty;

    @Bean
    public RedissonClient redissonClient(){
        Config config = new Config();
        config.useSingleServer()
              .setAddress("redis://"+ this.redisProperty.getHost()+":"+ this.redisProperty.getPort());

        return Redisson.create(config);
    }
}
