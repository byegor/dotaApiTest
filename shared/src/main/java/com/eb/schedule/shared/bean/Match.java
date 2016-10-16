package com.eb.schedule.shared.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Egor on 26.05.2016.
 */
public class Match implements Serializable {
    @JsonProperty("id")
    private Long matchId;

    @JsonProperty("st")
    private Long startTime;
    @JsonProperty("d")
    private String duration;
    @JsonProperty("mst")
    private int matchStatus;

    @JsonProperty("rad")
    private TeamBean radiantTeam;
    @JsonProperty("dire")
    private TeamBean direTeam;

    @JsonProperty("msc")
    private String matchScore;
    @JsonProperty("nw")
    private List<Integer> networth;

    @JsonProperty("gn")
    private int gameNumber;

    @JsonProperty("rpic")
    private List<HeroBean> radianPicks;

    @JsonProperty("rban")
    private List<HeroBean> radianBans;

    @JsonProperty("dpic")
    private List<HeroBean> direPicks;

    @JsonProperty("dban")
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

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getMatchStatus() {
        return matchStatus;
    }

    public void setMatchStatus(int matchStatus) {
        this.matchStatus = matchStatus;
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

    public String getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(String matchScore) {
        this.matchScore = matchScore;
    }

    public List<Integer> getNetworth() {
        return networth;
    }

    public void setNetworth(List<Integer> networth) {
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
        sb.append(", radiantTeam=").append(radiantTeam);
        sb.append(", direTeam=").append(direTeam);
        sb.append(", gameNumber=").append(gameNumber);
        sb.append('}');
        return sb.toString();
    }
}
