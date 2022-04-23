package com.distributedlockpractice.service;

import com.distributedlockpractice.property.StockProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockService {

    private final RedissonClient redissonClient;
    private final StockProperty stockProperty;
    private static final int EMPTY = 0;

    public String keyResolver(String domain, String keyId){
        final String prefix = stockProperty.getPrefix()+":"+domain+":%s";
        return String.format(prefix, keyId);
    }

    public void decrease(final String key, final int count){
        final String lockName = key + ":lock";
        final RLock lock = redissonClient.getLock(lockName);
        final String worker = Thread.currentThread().getName();

        try {
            if(!lock.tryLock(1, 3, TimeUnit.SECONDS)){
                log.info("["+worker+"] 현재 남은 재고 : "+ currentStock(key));
            }

            final int stock = currentStock(key);
            if(stock <= EMPTY){
                log.info("["+worker+"] 현재 남은 재고가 없습니다. ("+stock+"개)");
                return;
            }

            log.info("현재 진행중인 사람 : "+ worker +" & 현재 남은 재고 : " +stock+"개");
            setStock(key, stock - count);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if(lock != null && lock.isLocked()) {
                lock.unlock();
            }
        }
    }

    public void decreaseNoLock(final String key, final int count){
        final String worker = Thread.currentThread().getName();
        final int stock = currentStock(key);
        log.info("["+worker+"] 현재 남은 재고 : "+ currentStock(key));

        if(stock <= EMPTY){
            log.info("["+worker+"] 현재 남은 재고가 없습니다. ("+stock+"개)");
            return;
        }

        log.info("현재 진행중인 사람 : "+ worker +" & 현재 남은 재고 : " +stock+"개");
        setStock(key, stock - count);
    }

    public void setStock(String key, int amount){
        redissonClient.getBucket(key).set(amount);
    }

    public int currentStock(String key){
        return (int) redissonClient.getBucket(key).get();
    }
}
