package com.distributedlockpractice.domain.rank.entity;

public class RankCount {
    private int count;

    public RankCount(int count) {
        this.count = count;
    }

    public int getCount(){
        return this.count;
    }

    public int minusCount(){
        this.count = this.count - 1;
        return this.count;
    }
}
