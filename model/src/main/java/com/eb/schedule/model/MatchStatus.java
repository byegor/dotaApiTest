package com.eb.schedule.model;

/**
 * Created by Egor on 12.02.2016.
 */
public enum MatchStatus {
    SCHEDULED((byte)0),
    LIVE((byte)1),
    FINISHED((byte)2);


    public final byte status;

    MatchStatus(byte status) {
        this.status = status;
    }


    public static MatchStatus fromValue(byte status){
        for(MatchStatus matchStatus : MatchStatus.values()){
            if( matchStatus.status == status){
                return matchStatus;
            }
        }
        throw  new IllegalArgumentException("so such match status as " + status);
    }

}
