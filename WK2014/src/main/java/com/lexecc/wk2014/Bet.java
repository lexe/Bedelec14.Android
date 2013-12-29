package com.lexecc.wk2014;

public class Bet {

    private Integer mScoreTeam1;
    private Integer mScoreTeam2;
    private String mUserName;
    private Integer mPoints;

    public Bet(String userName, Integer scoreTeam1, Integer scoreTeam2, Integer points) {
        mUserName = userName;
        mScoreTeam1 = scoreTeam1;
        mScoreTeam2 = scoreTeam2;
        mPoints = points;
    }

    public Integer getScoreTeam1() {
        return mScoreTeam1;
    }

    public Integer getScoreTeam2() {
        return mScoreTeam2;
    }

    public String getUserName() {
        return mUserName;
    }

    public Integer getPoints() {
        return mPoints;
    }
}
