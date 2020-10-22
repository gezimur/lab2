package com.gena.service;

import javafx.util.Pair;

import java.util.ArrayList;

public class City {
    public String name;
    public ArrayList<Pair<City, Integer>> ways;
    public Double x;
    public Double y;
    public Integer lengthToGoal;

    public City(String name){
        this.name = name;
        ways = new ArrayList<>();
        x = 0.0;
        y = 0.0;
        lengthToGoal = 0;
    }

    public void setCoord(double x, double y){
        this.x = x * Math.PI / 180;
        this.y = y * Math.PI / 180;
    }

    public int getLength(City b){
        return (int)Math.round(6371 * 2 * Math.asin(Math.sqrt(
                Math.pow(Math.sin( (x - b.x)/2 ), 2) +
                        Math.cos(x) * Math.cos(b.x) *
                                Math.pow(Math.sin( (y - b.y)/2 ), 2)) ));
    }

    public void setLengthToGoal(City goal){
        lengthToGoal = getLength(goal);
    }

    public void addWay(City to, Integer distance){
        ways.add(new Pair<>(to, distance));
        to.ways.add(new Pair<>(this, distance));
    }
}
