package com.eb.schedule.shared.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Egor on 26.05.2016.
 */
public class Match implements Serializable {
    @SerializedName("id")
    private Long matchId;

    @SerializedName("st")
    private Long startTime;
    @SerializedName("bo")
    private String seriesType;
    @SerializedName("d")
    private String duration;
    @SerializedName("win")
    private Boolean radiantWin;

    @SerializedName("rad")
    private TeamBean radiantTeam;
    @SerializedName("dire")
    private TeamBean direTeam;
    @SerializedName("l")
    private LeagueBean league;

    @SerializedName("msc")
    private String matchScore;
    @SerializedName("nw")
    private List<Double> networth;

    @SerializedName("gn")
    private int gameNumber;

    @SerializedName("rpic")
    private List<HeroBean> radianPicks;

    @SerializedName("rban")
    private List<HeroBean> radianBans;

    @SerializedName("dpic")
    private List<HeroBean> direPicks;

    @SerializedName("dban")
    private List<HeroBean> direBans;


    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public String getSeriesType() {
        return seriesType;
    }

    public void setSeriesType(String seriesType) {
        this.seriesType = seriesType;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Boolean getRadiantWin() {
        return radiantWin;
    }

    public void setRadiantWin(Boolean radiantWin) {
        this.radiantWin = radiantWin;
    }

    public TeamBean getRadiantTeam() {
        return radiantTeam;
    }

    public void setRadiantTeam(TeamBean radiantTeam) {
        this.radiantTeam = radiantTeam;
    }

    public TeamBean getDireTeam() {
        return direTeam;
    }

    public void setDireTeam(TeamBean direTeam) {
        this.direTeam = direTeam;
    }

    public LeagueBean getLeague() {
        return league;
    }

    public void setLeague(LeagueBean league) {
        this.league = league;
    }

    public String getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(String matchScore) {
        this.matchScore = matchScore;
    }

    public List<Double> getNetworth() {
        return networth;
    }

    public void setNetworth(List<Double> networth) {
        this.networth = networth;
    }

    public int getGameNumber() {
        return gameNumber;
    }

    public void setGameNumber(int gameNumber) {
        this.gameNumber = gameNumber;
    }

    public List<HeroBean> getRadianPicks() {
        return radianPicks;
    }

    public void setRadianPicks(List<HeroBean> radianPicks) {
        this.radianPicks = radianPicks;
    }

    public List<HeroBean> getDirePicks() {
        return direPicks;
    }

    public void setDirePicks(List<HeroBean> direPicks) {
        this.direPicks = direPicks;
    }

    public List<HeroBean> getRadianBans() {
        return radianBans;
    }

    public void setRadianBans(List<HeroBean> radianBans) {
        this.radianBans = radianBans;
    }

    public List<HeroBean> getDireBans() {
        return direBans;
    }

    public void setDireBans(List<HeroBean> direBans) {
        this.direBans = direBans;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Match{");
        sb.append("matchId=").append(matchId);
        sb.append(", startTime=").append(startTime);
        sb.append(", seriesType='").append(seriesType).append('\'');
        sb.append(", radiantTeam=").append(radiantTeam);
        sb.append(", direTeam=").append(direTeam);
        sb.append(", league=").append(league);
        sb.append(", gameNumber=").append(gameNumber);
        sb.append('}');
        return sb.toString();
    }
}
