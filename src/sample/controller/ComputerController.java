package sample.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import sample.model.KingSudoku;
import sample.model.Sudoku;
import sample.model.XSudoku;

import java.util.Date;
import java.util.Random;

public class ComputerController {
    @FXML
    private AnchorPane background;
    @FXML
    private RadioButton easy;
    @FXML
    private ToggleGroup difficulty;
    @FXML
    private RadioButton medium;
    @FXML
    private RadioButton hard;
    @FXML
    private RadioButton sudoku;
    @FXML
    private ToggleGroup type;
    @FXML
    private RadioButton kingSudoku;
    @FXML
    private RadioButton xSudoku;
    private Sudoku sdk;
    private PlayController pc;

    public void generate(ActionEvent e) {
        Random r = new Random();
        if (type.getSelectedToggle().equals(kingSudoku)) {
            sdk = new KingSudoku();
            pc.setTypeLabel("King Sudoku");
        }
        if (type.getSelectedToggle().equals(sudoku)) {
            sdk = new Sudoku();
            pc.setTypeLabel("Sudoku");
        }
        if (type.getSelectedToggle().equals(xSudoku)) {
            sdk = new XSudoku();
            pc.setTypeLabel("X-Sudoku");
        }
        if (difficulty.getSelectedToggle().equals(easy)) {
            sdk.genPuzzle(45 + r.nextInt(6), sdk);
            sdk.setInfo("Sudoku++", "This puzzle is generated by the program", "Easy"
                    , new Date(), "Sudoku++");
        } else if (difficulty.getSelectedToggle().equals(medium)) {
            sdk.genPuzzle(35 + r.nextInt(6), sdk);
            sdk.setInfo("Sudoku++", "This puzzle is generated by the program", "Medium"
                    , new Date(), "Sudoku++");
        } else {
            sdk.genPuzzle(25 + r.nextInt(6), sdk);
            sdk.setInfo("Sudoku++", "This puzzle is generated by the program", "Hard"
                    , new Date(), "Sudoku++");
        }

        setInMain();
    }

    public void setPc(PlayController pc) {
        this.pc = pc;
    }

    private void setInMain() {
        pc.setGo(true);
        pc.setSudoku(sdk);
        pc.setHitOnce(true);
        pc.setGrid(sdk.getGrid());
        pc.setInfo();
        pc.resetTime();
        pc.clearMods();
    }
}
