package sample.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

public class SchemeController implements Initializable {
    @FXML
    private AnchorPane background;
    @FXML
    private TextField normalTF;
    @FXML
    private TextField wrongTF;
    @FXML
    private TextField presetTF;
    @FXML
    private ColorPicker normalCP;
    @FXML
    private ColorPicker presetCP;
    @FXML
    private ColorPicker wrongCP;
    private PlayController pc;

    public void initialize(URL url, ResourceBundle rb) {
        try {
            Scanner cin = new Scanner(new File("settings.txt"));
            String color = cin.nextLine();
            normalTF.setStyle("-fx-text-fill: " + color);
            normalCP.setValue(Color.web(color));
            color = cin.nextLine();
            presetTF.setStyle("-fx-text-fill: " + color);
            presetCP.setValue(Color.web(color));
            color = cin.nextLine();
            wrongTF.setStyle("-fx-text-fill: " + color);
            wrongCP.setValue(Color.web(color));
            cin.close();
        } catch (FileNotFoundException ex) {
            System.out.println("cannot find settings");
        }
    }

    public void setPc(PlayController pc) {
        this.pc = pc;
    }

    public void change(ActionEvent e) {
        ColorPicker source = (ColorPicker) e.getSource();
        if (source.equals(normalCP)) {
            normalTF.setStyle("-fx-text-fill: " + rgbStr(normalCP.getValue()));
        }
        if (source.equals(presetCP)) {
            presetTF.setStyle("-fx-text-fill: " + rgbStr(presetCP.getValue()));
        }
        if (source.equals(wrongCP)) {
            wrongTF.setStyle("-fx-text-fill: " + rgbStr(wrongCP.getValue()));
        }
    }

    private String rgbStr(Color c) {
        return "rgb(" + (int) (c.getRed() * 255) + "," + (int) (c.getGreen() * 255) + "," + (int) (c.getBlue() * 255) + ")";

    }

    public void save(ActionEvent e) {
        try {
            PrintWriter cout = new PrintWriter("settings.txt");
            cout.println(rgbStr(normalCP.getValue()));
            cout.println(rgbStr(presetCP.getValue()));
            cout.println(rgbStr(wrongCP.getValue()));
            cout.close();
            pc.setColours(rgbStr(normalCP.getValue()), rgbStr(presetCP.getValue()), rgbStr(wrongCP.getValue()));
        } catch (IOException ex) {
            System.out.println("cannot write to settings.txt");
        }
    }
}
