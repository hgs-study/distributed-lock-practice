package com.distributedlockpractice.domain;

public class Stock {
    private String name;

    private Long amount;

    public Stock(String name, Long amount) {
        this.name = name;
        this.amount = amount;
    }
}
