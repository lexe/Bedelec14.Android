package com.lexecc.wk2014;

public class User {

    private Integer mID;
    private String mName;
    private Integer mScore;

    public User(Integer id, String name, Integer score) {
        mID = id;
        mName = name;
        mScore = score;
    }

    public Integer getID() {
        return mID;
    }

    public String getName() {
        return mName;
    }

    public Integer getScore() {
        return mScore;
    }
}
