package egor.dota;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Egor on 10.07.2015.
 */
public class Match {
    private long id;
    private List<Hero> dire = new ArrayList<>(5);
    private List<Hero> radiant = new ArrayList<>(5);

    public Match(long id) {
        this.id = id;
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

    //todo use game mode
    public boolean isNormalMatch(){
        return radiant.size() == 5 && dire.size() == 5;
    }

    @Override
    public String toString() {
        return "Match{" +
                "id=" + id +
                ", dire=" + dire +
                ", radiant=" + radiant +
                '}';
    }
}
