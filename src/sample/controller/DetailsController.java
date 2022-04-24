package sample.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DetailsController {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    @FXML
    private TextField authorTF;
    @FXML
    private TextField sourceTF;
    @FXML
    private TextField dateTF;
    @FXML
    private TextArea descriptionTF;
    @FXML
    private TextArea commentTF;
    private PlayController pc;

    public void save(ActionEvent e) {
        try {
            pc.setPuzzleInfo(authorTF.getText(), descriptionTF.getText(), commentTF.getText(), sdf.parse(dateTF.getText()), sourceTF.getText());

        } catch (ParseException pe) {
            PlayController.invokeError("thats not a valid date!");
        }
    }

    public void init() {
        authorTF.setText(pc.getSudoku().getAuthor());
        try {
            dateTF.setText(sdf.format(pc.getSudoku().getPublished()));
        } catch (NullPointerException ex) {
            dateTF.setText("");
        }
        commentTF.setText(pc.getSudoku().getComment());
        descriptionTF.setText(pc.getSudoku().getDescription());
        sourceTF.setText(pc.getSudoku().getSource());
    }

    public void setParent(PlayController pc) {
        this.pc = pc;
    }
}
