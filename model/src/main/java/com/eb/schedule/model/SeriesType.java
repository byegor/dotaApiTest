package com.eb.schedule.model;

/**
 * Created by Egor on 13.02.2016.
 */
public enum SeriesType {
    BO1((byte) 0, 1),
    BO2((byte) 11, 2),
    BO3((byte) 1, 3),
    BO5((byte) 2, 5);

    public final byte code;
    public final int gamesCount;

    SeriesType(byte code, int gamesCount) {
        this.code = code;
        this.gamesCount = gamesCount;
    }


    public static SeriesType fromCode(byte code) {
        switch (code) {
            case 1:
                return BO3;
            case 2:
                return BO5;
            case 11:
                return BO2;
            default:
                return BO1;
        }
    }
}
