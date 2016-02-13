package egor.dota.model.entity;

/**
 * Created by Egor on 13.02.2016.
 */
public enum SeriesType {
    NO_SERIES(0),
    BO3(1),
    BO5(2);

    public final int code;

    SeriesType(int code) {
        this.code = code;
    }


    public static SeriesType fromCode(int code){
        switch(code){
            case 1: return BO3;
            case 2: return BO5;
            default: return NO_SERIES;
        }
    }
}
