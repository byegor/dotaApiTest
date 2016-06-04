package com.eb.schedule.shared.bean;

import java.util.List;

/**
 * Created by Egor on 26.05.2016.
 */
public class Match {

    private Long matchId;
    private Long startTime;
    private String seriesType;
    private Double duration;
    private Boolean radiantWin;

    private TeamBean radiantTeam;
    private TeamBean direTeam;
    private LeagueBean league;

    private int radiantScore;
    private int direScore;
    private List<Double> networht;

    private int gameNumber;

}
