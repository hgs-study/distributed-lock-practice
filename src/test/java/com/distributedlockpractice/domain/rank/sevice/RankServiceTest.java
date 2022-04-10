package com.distributedlockpractice.domain.rank.sevice;

import com.distributedlockpractice.domain.rank.entity.RankCount;
import com.distributedlockpractice.domain.rank.service.RankService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootTest
public class RankServiceTest {

    @Autowired
    private RankService rankService;

    private static int THREAD_LENGTH = 100;

    @Test
    public void 랭크_카운트다운_테스트() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(THREAD_LENGTH);
        RankCount rankCount = new RankCount(THREAD_LENGTH);

        List<Thread> workers = Stream
                                .generate(() -> new Thread(new RankCountDownWorker(countDownLatch, rankCount)))
                                .limit(THREAD_LENGTH)
                                .collect(Collectors.toList());

        workers.forEach(Thread::start);
        countDownLatch.await();
    }

    public class RankCountDownWorker implements Runnable{
        private CountDownLatch countDownLatch;
        private RankCount rankCount;

        public RankCountDownWorker(CountDownLatch countDownLatch, RankCount rankCount) {
            this.countDownLatch = countDownLatch;
            this.rankCount = rankCount;
        }

        @Override
        public void run() {
            rankService.countDown(rankCount);
            countDownLatch.countDown();
        }
    }
}