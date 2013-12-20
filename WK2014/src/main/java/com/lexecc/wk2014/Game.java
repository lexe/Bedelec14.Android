package com.lexecc.wk2014;

import java.util.Date;

public class Game {

    private Integer mID;
    private Date mDate;
    private String mTeam1;
    private String mTeam2;
    private Integer mScoreTeam1;
    private Integer mScoreTeam2;
    private Integer mPronoTeam1;
    private Integer mPronoTeam2;
    private Integer mPoints;

    public Game(Integer id, Date date, String team1, String team2, Integer scoreTeam1, Integer scoreTeam2, Integer pronoTeam1, Integer pronoTeam2, Integer points) {
        this.mID = id;
        this.mDate = date;
        this.mTeam1 = team1;
        this.mTeam2 = team2;
        this.mScoreTeam1 = scoreTeam1;
        this.mScoreTeam2 = scoreTeam2;
        this.mPronoTeam1 = pronoTeam1;
        this.mPronoTeam2 = pronoTeam2;
        this.mPoints = points;
    }

    public Integer getID() {
        return mID;
    }

    public Date getDate() {
        return mDate;
    }

    public String getTeam1() {
        return mTeam1;
    }

    public String getTeam2() {
        return mTeam2;
    }

    public Integer getScoreTeam1() {
        return mScoreTeam1;
    }

    public Integer getScoreTeam2() {
        return mScoreTeam2;
    }

    public Integer getPronoTeam1() {
        return mPronoTeam1;
    }

    public Integer getPronoTeam2() {
        return mPronoTeam2;
    }

    public Integer getPoints() { return mPoints; }

    public String getImage(Integer team) {
        String retVal = "";

        if (team == 1) retVal = getTeam1();
        else if (team == 2) retVal = getTeam2();

        retVal = retVal.replace(" ", "_");
        retVal = retVal.replace("ë", "e");
        retVal = retVal.replace("-", "_");
        retVal = retVal.toLowerCase();

        return retVal;
    }

}
