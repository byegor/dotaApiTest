package egor.dota;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by EGOR on 1.07.2015.
 */
public class DotaApi {

    public static void main(String[] args) throws IOException {
        new Thread(new RetrieveStatisticsBasedOnSequenceJob()).start();
    }


}
