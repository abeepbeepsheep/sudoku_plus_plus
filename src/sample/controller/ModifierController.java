package sample.controller;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import sample.model.DetermineColour;
import sample.model.Modifier;

import java.util.ArrayList;

public class ModifierController{
    private final GridPane gp;
    private final ArrayList<Modifier> structure;
    private final Label[][] array;

    public ModifierController() {
        gp = new GridPane();
        array = new Label[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                array[i][j] = new Label();
                array[i][j].setPrefSize(50, 50);
                array[i][j].setAlignment(Pos.BOTTOM_RIGHT);
                array[i][j].setTextFill(Color.WHITE);
                gp.add(array[i][j], j, i);
            }
        }
        gp.setPrefSize(450, 450);
        structure = new ArrayList<>();
    }


    public void addAllModifier(ArrayList<Modifier> arrayList) {
        structure.addAll(arrayList);
    }

    public void addModifier(Modifier m) {
        structure.add(m);
    }

    public void deleteModifier(Modifier m) {
        structure.removeIf(m::equals);
    }

    public void construct(int[] order) {
        if (order == null) return;
        String[] colours = new String[6];
        colours[0] = "#378ad7";
        colours[1] = "#7819cb";
        colours[2] = "#19cbbc";
        colours[3] = "#1fcb19";
        colours[4] = "#bfcb19";
        colours[5] = "#cb6c19";
        gp.setPrefSize(450, 450);
        int cnt = 0;
        for (Modifier m : structure) {
            for (int i : m.getPoints()) {
                Label l = array[i / 9][i % 9];
                l.setPrefSize(50, 50);
                l.setStyle("-fx-background-color: " + colours[order[cnt]]);
                if (i == m.getPoints().get(m.getPoints().size() - 1)) {
                    l.setText(m.getSum() + "");
                }
            }
            cnt++;
        }
    }

    public GridPane getGp() {
        return gp;
    }

}
