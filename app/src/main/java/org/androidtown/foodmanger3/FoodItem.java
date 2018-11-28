package org.androidtown.foodmanger3;

public class FoodItem {

    String Id;
    String name;
    String date;
    int remainDay;
    int duration;
    int resId;

    public FoodItem(String foodid, String name, String date, int duration, int resId, int day) {
        this.Id = foodid;
        this.name = name;
        this.date = date;
        this.resId = resId;
        this.duration = duration;
        this.remainDay = day;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getDay() {
        return remainDay;
    }

    public void setDay(int day) {
        this.remainDay = day;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }
}
