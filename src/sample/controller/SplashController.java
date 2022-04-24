package sample.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import sample.Main;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;

public class SplashController implements Initializable {
    private static final Label[][] array = new Label[3][3];
    private static ArrayList<Integer> nos, order;
    private static int i;
    @FXML
    private AnchorPane ap;
    @FXML
    private GridPane gp;
    @FXML
    private ProgressBar pb;

    public void runTask() {
        Thread t = new Thread(() -> {
            try {
                while (true) {
                    Platform.runLater(() -> {
                        pb.setProgress(i / 17.0);
                        if (i >= 0 && i <= 8) {
                            array[order.get(i) / 3][order.get(i) % 3].setText(nos.get(i) + "");
                            i++;
                        } else if (i >= 9 && i <= 17) {
                            array[order.get(i - 9) / 3][order.get(i - 9) % 3].setText("");
                            i++;
                        } else if (i == 18) {
                            Main a = new Main();
                            try {
                                a.openMainApp();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            i = 0;
                            randomise();
                        }
                    });
                    Thread.sleep(250);
                    if (i == 9) Thread.sleep(300);
                }
            } catch (InterruptedException ex) {
            }
        });
        t.start();
    }

    public void randomise() {
        Collections.shuffle(nos);
        Collections.shuffle(order);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ap.setStyle(
                "-fx-padding: 5; " +
                        "-fx-background-color: cornsilk; " +
                        "-fx-border-width:5; " +
                        "-fx-border-color: " +
                        "linear-gradient(" +
                        "to bottom, " +
                        "chocolate, " +
                        "derive(chocolate, 50%)" +
                        ");"
        );
        ap.setEffect(new DropShadow());
        pb.setStyle("-fx-accent: #2dcb56");
        nos = new ArrayList<>(9);
        order = new ArrayList<>(9);
        for (int i = 0; i < 9; i++) {
            nos.add(i + 1);
        }

        for (int i = 0; i < 9; i++) {
            order.add(i);
        }
        randomise();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Label l = new Label();
                l.setPrefSize(50, 50);
                l.setFont(new Font(22.3));
                l.setAlignment(Pos.CENTER);
                l.setStyle("-fx-background-color:#aaccbb;-fx-border-color: #ffffff; -fx-font-family: 'Trebuchet MS';");
                gp.add(l, j, i);
                array[i][j] = l;
            }
        }
        runTask();
    }
}
