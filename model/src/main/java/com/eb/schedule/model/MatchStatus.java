package com.eb.schedule.model;

/**
 * Created by Egor on 12.02.2016.
 */
public enum MatchStatus {
    SCHEDULED(0),
    LIVE(1),
    FINISHED(2);


    public final int status;

    MatchStatus(int status) {
        this.status = status;
    }


    public static MatchStatus fromValue(int status){
        for(MatchStatus matchStatus : MatchStatus.values()){
            if( matchStatus.status == status){
                return matchStatus;
            }
        }
        throw  new IllegalArgumentException("so such match status as " + status);
    }

}
