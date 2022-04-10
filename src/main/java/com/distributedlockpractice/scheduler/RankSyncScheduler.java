package com.distributedlockpractice.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.ObjectUtils;

@Slf4j
@RequiredArgsConstructor
public class RankSyncScheduler {

    @Value("${redis.rank.key}")
    private String key;

    @Value("${redis.rank.refresh-range}")
    private long refreshRange;

    private final RedissonClient redissonClient;

    @Scheduled(fixedRate = 1000)
    public void minusCount(){
        Long count = redissonClient.getScoredSortedSet(key).stream().count();

        if(!ObjectUtils.isEmpty(count))
            removeQueue(count);
    }

    private void removeQueue(Long count) {
        int endCount = (int) Math.min(count, refreshRange);
        int executeCount = redissonClient.getScoredSortedSet(key).removeRangeByRank(0, endCount);
        log.info("대기 풀린 사람 수 :" + executeCount);
    }
}
