package com.eb.schedule.shared.bean;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Egor on 26.05.2016.
 */
public class TeamBean implements Serializable {

    public int id;

    @JsonProperty("n")
    private String name;

    @JsonProperty("t")
    private String tag;

    @JsonProperty("l")
    private String logo;

    @JsonProperty("p")
    private List<Player> players;

    public TeamBean() {
    }

    public TeamBean(int id, String name, String tag, long logo) {
        this.id = id;
        this.name = name;
        this.tag = tag;
        this.logo = "" + logo;
    }

    public TeamBean(int id, String name, String tag, long logo, List<Player> players) {
        this.id = id;
        this.name = name;
        this.tag = tag;
        this.logo = "" + logo;
        this.players = players;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("TeamBean{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", tag='").append(tag).append('\'');
        sb.append(", logo='").append(logo).append('\'');
        sb.append('}');
        return sb.toString();
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

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }


}
