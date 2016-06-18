package com.eb.schedule.shared.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Egor on 26.05.2016.
 */
public class TeamBean {

    public int id;

    @SerializedName("n")
    public String name;

    @SerializedName("t")
    public String tag;

    @SerializedName("l")
    public String logo;

    public TeamBean(int id, String name, String tag, long logo) {
        this.id = id;
        this.name = name;
        this.tag = tag;
        this.logo = "" + logo;
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
}
