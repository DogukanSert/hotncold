package com.clay.hotncold.filter;

/**
 * Created by dogukan on 25.04.16.
 */
public class FilterObject {

    String fbID;
    String name;
    String gender;
    String birthday;

    FilterObject(String name, String gender, String birthday, String fbID) {
        this.name = name;
        this.gender = gender;
        this.birthday = birthday;
        this.fbID =fbID;
    }

    public String getName() {
        return name;
    }
    public String getGender() {
        return gender;
    }
    public String getBirthday() {
        return birthday;
    }

    public String getFbID(){
        return fbID;
    }
}