package sample.model;

import java.util.ArrayList;
import java.util.Collections;

public class Killer extends Modifier {
    private final ArrayList<Integer> points;
    //points are stored in base 9
    private final int sum;

    public Killer(ArrayList<Integer> points, int sum) {
        this.points = new ArrayList<>();
        this.points.addAll(points);
        this.sum = sum;
        Collections.sort(this.points);
    }

    public boolean equals(Modifier k) {
        if (!(k instanceof Killer)) return false;
        Killer p = (Killer) k;
        for (int i = 0; i < points.size(); i++) {
            if (!points.get(i).equals(p.points.get(i))) {
                return false;
            }
        }
        return sum == p.sum;
    }

    public ArrayList<Integer> getPoints() {
        return points;
    }

    public int getSum() {
        return sum;
    }
}
