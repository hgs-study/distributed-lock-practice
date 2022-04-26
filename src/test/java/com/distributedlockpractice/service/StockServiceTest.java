package com.distributedlockpractice.service;

import com.distributedlockpractice.domain.Stock;
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
    private Stock peenut;

    @BeforeEach
    void 재고_키_세팅(){
        final String name = "peanut";
        final String keyId = "001";
        final int amount = 100;
        final Stock peenut = new Stock(name, keyId, amount);

        this.stockKey = stockService.keyResolver(peenut.getName(), peenut.getKeyId());
        this.peenut = peenut;
        stockService.setStock(this.stockKey, amount);
    }

    @Test
    @Order(1)
    void 상품_수량_확인(){
        final int amount = this.peenut.getAmount();

        final int currentCount = stockService.currentStock(stockKey);

        assertEquals(amount, currentCount);
    }

    @Test
    @Order(2)
    void 상품_재고_카운트만큼_감소(){
        final int amount = this.peenut.getAmount();
        final int count = 2;

        stockService.decrease(this.stockKey, count);

        final int currentCount = stockService.currentStock(stockKey);
        assertEquals(amount - count, currentCount);
    }

    @Test
    @Order(3)
    void 락X_땅콩_100개를_사람_100명이_2개씩_구매() throws InterruptedException {
        final int people = 100;
        final int count = 2;
        final int soldOut = 0;
        final CountDownLatch countDownLatch = new CountDownLatch(people);

        List<Thread> workers = Stream
                                .generate(() -> new Thread(new BuyNoLockWorker(this.stockKey, count, countDownLatch)))
                                .limit(people)
                                .collect(Collectors.toList());
        workers.forEach(Thread::start);
        countDownLatch.await();

        final int currentCount = stockService.currentStock(stockKey);
        assertNotEquals(soldOut, currentCount);
    }

    @Test
    @Order(4)
    void 락O_땅콩_100개를_사람_100명이_2개씩_구매() throws InterruptedException {
        final int people = 100;
        final int count = 2;
        final int soldOut = 0;
        final CountDownLatch countDownLatch = new CountDownLatch(people);

        List<Thread> workers = Stream
                                .generate(() -> new Thread(new BuyWorker(this.stockKey, count, countDownLatch)))
                                .limit(people)
                                .collect(Collectors.toList());
        workers.forEach(Thread::start);
        countDownLatch.await();

        final int currentCount = stockService.currentStock(this.stockKey);
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