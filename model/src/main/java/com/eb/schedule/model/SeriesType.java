package com.eb.schedule.model;

/**
 * Created by Egor on 13.02.2016.
 */
public enum SeriesType {
    NO_SERIES(0, 1),
    BO3(1, 3),
    BO5(2, 5);

    public final int code;
    public final int gamesCount;

    SeriesType(int code, int gamesCount) {
        this.code = code;
        this.gamesCount = gamesCount;
    }


    public static SeriesType fromCode(int code){
        switch(code){
            case 1: return BO3;
            case 2: return BO5;
            default: return NO_SERIES;
        }
    }
}
