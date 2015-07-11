package egor.dota;

/**
 * Created by Егор on 10.07.2015.
 */
public class Hero implements Comparable<Hero>{
    private int id;
    private String name;

    public Hero(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public int compareTo(Hero o) {
        return Integer.compare(this.id, o.id);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Hero{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
