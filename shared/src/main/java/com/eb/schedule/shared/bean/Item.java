package com.eb.schedule.shared.bean;

import java.io.Serializable;

/**
 * Created by Egor on 07.07.2016.
 */
public class Item  implements Serializable {

    private int id;
    private String name;

    public Item() {
    }

    public Item(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
