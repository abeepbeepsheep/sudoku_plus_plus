package sample.model;

import sample.controller.ModifierController;

import java.util.ArrayList;

public abstract class Modifier extends ModifierController implements Cloneable {
    public abstract boolean equals(Modifier m);

    public abstract ArrayList<Integer> getPoints();

    public abstract int getSum();

    //what is a modifier
    public Object clone() {
        try {
            Object clone = super.clone();
            return clone;
        } catch (CloneNotSupportedException ex) {
        }
        return null;
    }
    public String toString(){
        String out="";
        for (int i:getPoints()){
            out+=i+",";
        }
        out+=getSum();
        return out;
    }
}
