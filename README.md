# redisson-distributed-lock-

## 테스트 목록

 <img src="https://user-images.githubusercontent.com/76584547/164909787-dcf40665-59ce-4b03-bdeb-0bad9ea758f3.png">

## (분산락 X) 100개의 땅콩을 100명의 사람이 2개씩 구매했을 경우
<p align="center">
  <img src="https://user-images.githubusercontent.com/76584547/164909459-490781a8-7a31-4ab0-9a43-e300289689ca.png">
</p>

> 100개 쓰레드의 컨커런시 세입하지 않은 모습을 보이고 있다.

## (분산락 O) 100개의 땅콩을 100명의 사람이 2개씩 구매했을 경우

<p align="center">
  <img src="https://user-images.githubusercontent.com/76584547/164909411-e06f8f07-4645-4ec9-a33a-3d2c14f47972.png">
</p>

> 100개 쓰레드의 컨커런시 세입한 모습을 보이고 있으며, 땅콩 재고가 0개 이하로 내려가지 않는 모습을 볼 수 있다.

- Redis distributed-locks
```
https://redis.io/docs/reference/patterns/distributed-locks/
```
- 블로그 정리
```
 https://velog.io/@hgs-study/redisson-distributed-lock
```

