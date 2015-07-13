package egor.dota;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Egor on 10.07.2015.
 */
public class MatchSequence {
    public static final long TWENTY_MINUTES = TimeUnit.MINUTES.toSeconds(20);
    private long id;
    private long sequence;
    private boolean radiantWin;
    private int duration;
    private List<Hero> dire = new ArrayList<>(5);
    private List<Hero> radiant = new ArrayList<>(5);

    public MatchSequence(long id, long sequence, boolean radiantWin, int duration) {
        this.id = id;
        this.sequence = sequence;
        this.radiantWin = radiantWin;
        this.duration = duration;
    }

    public void addDire(Hero hero) {
        if( hero != null){
            dire.add(hero);
        }
    }

    public void addRadiant(Hero hero) {
        if( hero != null){
            radiant.add(hero);
        }
    }

    public long getId() {
        return id;
    }

    public List<Hero> getDire() {
        return dire;
    }

    public List<Hero> getRadiant() {
        return radiant;
    }

    public boolean isNormalMatch(){
        return radiant.size() == 5 && dire.size() == 5 && duration > TWENTY_MINUTES;
    }

    public long getSequence() {
        return sequence;
    }

    public boolean isRadiantWin() {
        return radiantWin;
    }

    public int getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return "MatchSequence{" +
                "id=" + id +
                ", sequence=" + sequence +
                ", radiantWin=" + radiantWin +
                '}';
    }
}
