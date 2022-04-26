package com.distributedlockpractice.domain;

public class Stock {
    private String name;
    private String keyId;
    private int amount;

    public Stock(String name, String keyId, int amount) {
        this.name = name;
        this.keyId = keyId;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public String getKeyId() {
        return keyId;
    }

    public int getAmount() {
        return amount;
    }
}
