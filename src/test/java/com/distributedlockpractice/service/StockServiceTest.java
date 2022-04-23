package com.distributedlockpractice.service;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
class StockServiceTest {

    @Autowired
    private StockService stockService;
    private String stockKey;

    @BeforeEach
    void 키_세팅(){
        final String name = "peanut";
        final String keyId = name + "001";
        this.stockKey = stockService.keyResolver(name, keyId);
    }

    @Test
    @Order(1)
    void 상품_재고_세팅(){
        final int amount = 10;

        stockService.setStock(this.stockKey, amount);

        final int currentCount = stockService.currentStock(stockKey);
        assertEquals(amount, currentCount);
    }

    @Test
    @Order(2)
    void 상품_재고_카운트만큼_감소(){
        final int amount = 10;
        final int count = 2;

        stockService.setStock(this.stockKey, amount);
        stockService.decrease(this.stockKey, count);

        final int currentCount = stockService.currentStock(stockKey);
        assertEquals(amount - count, currentCount);
    }

    @Test
    @Order(3)
    void 락X_땅콩_100개를_사람_100명이_2개씩_구매() throws InterruptedException {
        final int THREAD_LENGTH = 100;
        final int amount = 100;
        final int count = 2;
        final int soldOut = 0;
        final CountDownLatch countDownLatch = new CountDownLatch(THREAD_LENGTH);
        stockService.setStock(this.stockKey, amount);

        List<Thread> workers = Stream
                                .generate(() -> new Thread(new BuyNoLockWorker(this.stockKey, count, countDownLatch)))
                                .limit(THREAD_LENGTH)
                                .collect(Collectors.toList());
        workers.forEach(Thread::start);
        countDownLatch.await();

        final int currentCount = stockService.currentStock(stockKey);
        assertNotEquals(soldOut, currentCount);
    }

    @Test
    @Order(4)
    void 락O_땅콩_100개를_사람_100명이_2개씩_구매() throws InterruptedException {
        final int THREAD_LENGTH = 100;
        final int amount = 100;
        final int count = 2;
        final int soldOut = 0;
        final CountDownLatch countDownLatch = new CountDownLatch(THREAD_LENGTH);
        stockService.setStock(this.stockKey, amount);

        List<Thread> workers = Stream
                                .generate(() -> new Thread(new BuyWorker(this.stockKey, count, countDownLatch)))
                                .limit(THREAD_LENGTH)
                                .collect(Collectors.toList());
        workers.forEach(Thread::start);
        countDownLatch.await();

        final int currentCount = stockService.currentStock(stockKey);
        assertEquals(soldOut, currentCount);
    }

    private class BuyWorker implements Runnable{
        private String stockKey;
        private int count;
        private CountDownLatch countDownLatch;

        public BuyWorker(String stockKey, int count, CountDownLatch countDownLatch) {
            this.stockKey = stockKey;
            this.count = count;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            stockService.decrease(this.stockKey, count);
            countDownLatch.countDown();
        }
    }

    private class BuyNoLockWorker implements Runnable{
        private String stockKey;
        private int count;
        private CountDownLatch countDownLatch;

        public BuyNoLockWorker(String stockKey, int count, CountDownLatch countDownLatch) {
            this.stockKey = stockKey;
            this.count = count;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            stockService.decreaseNoLock(this.stockKey, count);
            countDownLatch.countDown();
        }
    }
}