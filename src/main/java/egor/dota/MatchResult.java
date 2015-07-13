package egor.dota;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Egor on 11.07.2015.
 */
public class MatchResult {
    int radiantId;
    int direId;
    boolean radiantWin;
    long matchId;

    public MatchResult(List<Hero> radiant, List<Hero> dire, boolean radiantWin, long matchId) {
        radiantId = getOrCreateIdForEachTeam(radiant);
        direId = getOrCreateIdForEachTeam(dire);
        this.radiantWin = radiantWin;
        this.matchId = matchId;
    }

    private int getOrCreateIdForEachTeam(List<Hero> team){
        List<Integer> ids = new ArrayList<>();
        for (Hero hero : team) {
            ids.add(hero.getId());
        }
        Collections.sort(ids);
        Iterator<Integer> iterator = ids.iterator();
        StringBuilder sb = new StringBuilder();
        while(iterator.hasNext()){
            sb.append(iterator.next());
            if(iterator.hasNext()){
                sb.append("_");
            }
        }
        String teamCode = sb.toString();
        int teamIdByCode = DataBaseHelper.INSTANCE.getTeamIdByCode(teamCode);
        if( teamIdByCode == -1){
            teamIdByCode = DataBaseHelper.INSTANCE.createTeam(teamCode);
        }
        return teamIdByCode;
    }

}
