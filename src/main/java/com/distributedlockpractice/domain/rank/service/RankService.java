package com.distributedlockpractice.domain.rank.service;

import com.distributedlockpractice.domain.rank.entity.RankCount;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RankService {

    private final RedissonClient redissonClient;
    private static final String RANK_COUNT = "redis-count";

    public void countDown(RankCount rankCount){
        final RLock lock = redissonClient.getLock(RANK_COUNT);

        try {
            final boolean isLocked = lock.tryLock(1, 3, TimeUnit.SECONDS);

            if(!isLocked){
                log.info("["+Thread.currentThread().getName()+"] 현재 남은 인원 : "+ rankCount.getCount());
            }

            log.info("현재 진행중인 사람 : "+ Thread.currentThread().getName());
            rankCount.minusCount();
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            if(lock != null && lock.isLocked())
                lock.unlock();
        }
    }
}
