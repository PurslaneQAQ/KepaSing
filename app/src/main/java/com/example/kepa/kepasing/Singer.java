package com.example.kepa.kepasing;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/10/30 0030.
 */

public class Singer {
    private String index;
    private String name;

    public Singer(String index, String name) {
        this.index = index;
        this.name = name;
    }

    public String getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }
    public static List<Singer> getSingers() {
        List<Singer> singers = new ArrayList<>();
        singers.add(new Singer("A", "Avril Lavigne"));
        singers.add(new Singer("B", "B.O.B"));
        singers.add(new Singer("C", "Carrie Underwood"));
        singers.add(new Singer("D", "迪玛希"));
        singers.add(new Singer("E", "Ellie Goulding"));
        singers.add(new Singer("E", "Eminem"));
        singers.add(new Singer("F", "Fall Out Boy"));
        singers.add(new Singer("I", "Imagine Dragons"));
        singers.add(new Singer("J", "Jessica Simpson"));
        singers.add(new Singer("K", "Katy Perry"));
        singers.add(new Singer("L", "Lana Del Rey"));
        singers.add(new Singer("P", "Paul"));
        singers.add(new Singer("P", "Peter"));
        singers.add(new Singer("S", "Sarah Brightman"));
        singers.add(new Singer("R", "Rihanna"));
        singers.add(new Singer("T", "Taylor Swift"));
        singers.add(new Singer("T", "Tonya Mitchell"));
        singers.add(new Singer("W", "王菲"));
        singers.add(new Singer("X", "薛之谦"));
        singers.add(new Singer("Y", "杨宗纬"));
        singers.add(new Singer("Z", "张杰"));
        return singers;
    }
}
