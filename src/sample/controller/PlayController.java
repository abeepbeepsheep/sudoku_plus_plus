package sample.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.*;
import sample.Main;
import sample.model.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class PlayController implements Initializable {
    private final TextField[][] array = new TextField[9][9];
    private final boolean[][] correct = new boolean[9][9];
    private final boolean[][] preset = new boolean[9][9];
    private final TextFieldStyle[][] tfStyleArray = new TextFieldStyle[9][9];
    private final ArrayList<ArrayList<Sudoku>> backupSudokus = new ArrayList<>();
    private final ArrayList<Sudoku> lookupSudokuType = new ArrayList<>();
    @FXML
    private GridPane grid;
    @FXML
    private RadioButton kingSudoku;
    @FXML
    private RadioButton xSudoku;
    @FXML
    private RadioButton normalSudoku;
    @FXML
    private Button autocomplete;
    @FXML
    private AnchorPane centrePane;
    @FXML
    private Label timeTaken;
    @FXML
    private Button newPuzzle;
    @FXML
    private TextArea puzzleInfo;
    @FXML
    private Button clearButton;
    @FXML
    private Button editInfo;
    @FXML
    private Button mode;
    @FXML
    private Label typeLabel;
    @FXML
    private Label variantLabel;
    @FXML
    private Button addMods;
    private Sudoku sudoku;
    private TextField prevTF;
    private ToggleGroup tg;
    private boolean hitOnce;
    private String prevNum = "";
    private boolean invokeEdit = true;
    private int SECONDS = 0;
    private int MINUTES = 0;
    private int HOURS = 0;
    private AtomicBoolean go;
    private String strMode = "play";
    private Stage current;
    private File sudokuFile;
    private GridPane background;
    private String wrongColour, normalColour, presetColour;
    private boolean saved;
    private ArrayList<Modifier> alm;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        saved = true;
        alm=new ArrayList<>();
        tg = new ToggleGroup();
        normalSudoku.setToggleGroup(tg);
        xSudoku.setToggleGroup(tg);
        kingSudoku.setToggleGroup(tg);
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                tfStyleArray[i][j] = new TextFieldStyle();
                correct[i][j] = true;
                TextField tf = new TextField();
                tf.setAlignment(Pos.CENTER);
                tf.setPrefSize(50, 50);
                tf.setFont(new Font(22.3));
                tf.setOnKeyPressed(e -> handleDelete(e));
                tf.setOnKeyTyped(e -> handleType(e));
                tf.setOnMouseClicked(e -> handleClick(e));
                tf.requestFocus();
                grid.add(tf, j, i);
                array[i][j] = tf;
            }
        }
        prevTF = array[0][0];
        hitOnce = true;
        sudoku = new Sudoku();
        formatErrors(prevTF);
        go = new AtomicBoolean(false);
        try {
            Scanner colourScan = new Scanner(new File("settings.txt"));
            setColours(colourScan.nextLine(), colourScan.nextLine(), colourScan.nextLine());
            colourScan.close();
        } catch (FileNotFoundException ex) {
            invokeError("settings.txt not found");
        }
        centrePane.getChildren().add(1,new GridPane());
        startTask();
    }
    public ArrayList<Modifier> getALM(){
        return alm;
    }
    public void setColours(String a, String b, String c) {
        normalColour = a;
        presetColour = b;
        wrongColour = c;
        formatErrors(prevTF);
    }

    // for the centre grid
    public void handleClick(MouseEvent e) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                array[i][j].setStyle("-fx-border-color: #43ac2b;" +
                        "-fx-display-caret: false;");
            }
        }
        TextField square = (TextField) (e.getSource());
        formatErrors(square);
    }

    public int[] findCoordinates(TextField tf) {
        int row = -1, col = -1;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (array[i][j].equals(tf)) {
                    row = i;
                    col = j;
                    return new int[]{row, col};
                }
            }
        }
        return null;
    }

    private void findNeighbours(TextField tf) {
        int row, col;
        int[] coords = findCoordinates(tf);
        row = coords[0];
        col = coords[1];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                tfStyleArray[i][j].setBackgroundColour("#ffffff");
            }
        }
        for (int i = 0; i < 9; i++) {
            tfStyleArray[row][i].setBackgroundColour("#cbbbbc");
            tfStyleArray[i][col].setBackgroundColour("#cbbbbc");
            tfStyleArray[row / 3 * 3 + i / 3][col / 3 * 3 + i % 3].setBackgroundColour("#cbbbbc");
        }
        tfStyleArray[row][col].setBackgroundColour("#edddde");
    }

    public void handleDelete(KeyEvent e) {
        if (!e.getCode().isWhitespaceKey() && !e.getCode().isLetterKey() && !e.getCode().isDigitKey() && !e.getCode().equals(KeyCode.BACK_SPACE) && !e.getCode().equals(KeyCode.DELETE))
            return;
        TextField square = (TextField) (e.getSource());
        int row = findCoordinates(square)[0];
        int col = findCoordinates(square)[1];
        if (preset[row][col]) return;
        prevNum = square.getText();
        invokeEdit = !e.getCode().equals(KeyCode.BACK_SPACE) && !e.getCode().equals(KeyCode.DELETE);
        square.setText("");
    }

    public void handleType(KeyEvent e) {
        try {
            saved = false;
            if (!invokeEdit) {
                formatErrors(prevTF);
                return;
            }
            TextField square = (TextField) (e.getSource());
            int row = findCoordinates(square)[0];
            int col = findCoordinates(square)[1];
            if (preset[row][col]) return;
            String s = square.getText();
            square.setText("");
            if (s.length() != 0 && s.charAt(0) != '0' && Character.isDigit(s.charAt(0))) {
                square.setText(s);
            } else {
                square.setText(prevNum);
            }
            formatErrors(square);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public int[][] getGrid() {
        int[][] grid = new int[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                try {
                    grid[i][j] = Integer.parseInt(array[i][j].getText());
                } catch (NumberFormatException ex) {
                    grid[i][j] = 0;
                }
            }
        }
        return grid;
    }

    public void setGrid(int[][] grid) {
        sudoku.setGrid(grid);
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (sudoku.getSquare(i, j) != 0) {
                    array[i][j].setText("" + sudoku.getSquare(i, j));
                    array[i][j].setEditable(false);
                    preset[i][j] = true;
                } else {
                    array[i][j].setText("");
                    array[i][j].setEditable(true);
                    preset[i][j] = false;
                }
            }
        }
        formatErrors(prevTF);
    }

    public void formatErrors(TextField tf) {
        prevTF = tf;
        try {
            if (tf.getText().length() == 1 && Character.isDigit(tf.getText().charAt(0)))
                sudoku.setSquare(Integer.parseInt(tf.getText()), findCoordinates(tf)[0], findCoordinates(tf)[1]);
            else if (tf.getText().equals("")) {
                sudoku.setSquare(0, findCoordinates(tf)[0], findCoordinates(tf)[1]);
            }
        } catch (NumberFormatException ex) {
        }//you expect to get NFE because sometimes there isnt text so its okay
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                correct[i][j] = sudoku.isSquareValid(i, j, sudoku.getGrid());
            }
        }
        findNeighbours(tf);
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (!(preset[i][j] || correct[i][j])) {
                    tfStyleArray[i][j].setTextColour(wrongColour);
                    array[i][j].setEditable(true);
                } else if (preset[i][j]) {
                    tfStyleArray[i][j].setTextColour(presetColour);
                    array[i][j].setEditable(false);
                } else {
                    tfStyleArray[i][j].setTextColour(normalColour);
                    array[i][j].setEditable(true);
                }
                array[i][j].setStyle(tfStyleArray[i][j].toString());
            }
        }
        if (sudoku.isSolvedValid() && hitOnce) {
            go.set(false);
            Alert infomation = new Alert(Alert.AlertType.INFORMATION);
            infomation.setHeaderText("Congratulations!");
            infomation.setContentText("You have solved the Sudoku in " + timeTaken.getText());
            infomation.showAndWait();
            hitOnce = false;
        }
    }

    public void changeRules(ActionEvent e) {
        saved = false;
        if (e.getSource() == kingSudoku) {
            setTypeLabel("King Sudoku");
            sudoku = new KingSudoku(sudoku);
        }
        if (e.getSource() == normalSudoku) {
            setTypeLabel("Sudoku");
            sudoku = new Sudoku(sudoku);
        }
        if (e.getSource() == xSudoku) {
            setTypeLabel("X-Sudoku");
            sudoku = new XSudoku(sudoku);
        }
        formatErrors(prevTF);
    }

    public void generateNewPuzzle(ActionEvent e) {
        go.set(false);
        Main a = new Main();
        a.openComputer(this);
        constructKiller();
    }
    public void clearMods(){
        sudoku.clearMods();
        alm.clear();
        constructKiller();
    }
    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public void setHitOnce(boolean state) {
        hitOnce = state;
    }

    public void solvePuzzle(ActionEvent e) {
        if (alm.size()!=0){
            for (Modifier m:alm) {
                if (m.getPoints().size()!=0) {
                    invokeError("Sorry, this app does not yet support autosolving Killer Sudokus. However, you still can solve it yourself.");
                    return;
                }
            }
        }
        if (mode.getText().equals("Play")) {
            int[][] grid = new int[9][9];
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    if (preset[i][j]) {
                        grid[i][j] = Integer.parseInt(array[i][j].getText());
                    } else grid[i][j] = 0;
                }
            }
            sudoku.setGrid(grid);
            sudoku.solveSudoku();
            if (sudoku.getGrid() == null) {
                sudoku.setGrid(grid);
                invokeError("no possible solution!");
                return;
            }
        } else {
            int[][] grid = new int[9][9];
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    try {
                        grid[i][j] = Integer.parseInt(array[i][j].getText());
                    } catch (NumberFormatException ex) {
                        grid[i][j] = 0;
                    }
                }
            }
            sudoku.setGrid(grid);
            sudoku.solveSudoku();
            if (sudoku.getGrid() == null) {
                sudoku.setGrid(grid);
                invokeError("no possible solution!");
                return;
            }
            hitOnce=false;
        }
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                array[i][j].setText("" + sudoku.getSquare(i, j));
                preset[i][j] = mode.getText().equals("Play");
            }
        }
        saved = false;
        formatErrors(prevTF);
    }

    public void setInfo() {
        saved = false;
        puzzleInfo.setText(sudoku.puzzleInfo());
    }

    public void resetTime() {
        SECONDS = MINUTES = HOURS = 0;
        timeTaken.setText("00:00:00");
    }

    private void startTask() {
        Runnable task = this::handleTimer;
        Runnable task2 = this::handleCreation;
        Thread t2 = new Thread(task2);
        Thread t = new Thread(task);
        t.setDaemon(true);
        t2.setDaemon(true);
        t.start();
        //t2.start();
    }

    public void handleCreation() {
        while (true) {
            try {
                Platform.runLater(() -> {
                    for (int i = 0; i < lookupSudokuType.size(); i++) {
                        if (backupSudokus.get(i).size() > 3) {
                            //Main.openMainApp();
                            return;
                        }
                        Sudoku s = new Sudoku();
                        s.genPuzzle(50, lookupSudokuType.get(i));
                        backupSudokus.get(i).add(s);
                    }
                });
            } catch (Exception e) {
            }
        }
    }

    private void handleTimer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        Platform.runLater((new Runnable() {
                            @Override
                            public void run() {
                                if (go.get()) {
                                    SECONDS++;
                                    if (SECONDS == 60) {
                                        MINUTES++;
                                        SECONDS = 0;

                                    }
                                    if (MINUTES == 60) {
                                        HOURS++;
                                        MINUTES = 0;
                                    }
                                    timeTaken.setText(String.format("%02d:%02d:%02d", HOURS, MINUTES, SECONDS));
                                }
                            }
                        }));
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }).start();

    }

    public void switchMode(ActionEvent e) {
        puzzleInfo.setText(sudoku.puzzleInfo());
        if (strMode.equals("play")) {
            mode.setText("Create");
            strMode = "create";
            go.set(false);
            timeTaken.setText("");
            createMode();
        } else {
            if (!sudoku.isSolvedValid()) hitOnce=true;
            mode.setText("Play");
            strMode = "play";
            playMode();
            timeTaken.setText("00:00:00");
            go.set(true);
        }
    }

    private void createMode() {
        if (typeLabel.getText().equals("X-Sudoku")){
            xSudoku.setSelected(true);
        } else if (typeLabel.getText().equals("King Sudoku")){
            kingSudoku.setSelected(true);
        } else{
            normalSudoku.setSelected(true);
        }
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                preset[i][j] = false;

            }
        }
        formatErrors(prevTF);
        toggleVisibility(true);
    }

    private void playMode() {
        HOURS = MINUTES = SECONDS = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                preset[i][j] = !array[i][j].getText().equals("");
            }
        }
        formatErrors(prevTF);
        toggleVisibility(false);
    }

    public void clearAll() {
        saved = false;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (!preset[i][j]) {
                    array[i][j].setText("");
                }
                sudoku.setSquare(0, i, j);
            }
        }
    }

    private void toggleVisibility(boolean state) {
        editInfo.setVisible(state);
        newPuzzle.setVisible(!state);
        xSudoku.setVisible(state);
        normalSudoku.setVisible(state);
        kingSudoku.setVisible(state);
        variantLabel.setVisible(state);
        addMods.setVisible(state);
        typeLabel.setVisible(!state);
    }

    public void exitApp(ActionEvent e) {
        Window window= current.getScene().getWindow();
        window.fireEvent(new WindowEvent(window,WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    public Stage getCurrent() {
        return current;
    }

    public void setCurrent(Stage s) {
        current = s;
        current.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, (e) -> {
            if (!saved) {
                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                confirmation.setHeaderText("You have unsaved work!");
                confirmation.setContentText("Would you like to save it?");
                ButtonType save = new ButtonType("Save");
                ButtonType dont = new ButtonType("Don't Save");
                ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
                confirmation.getButtonTypes().setAll(save, dont, cancel);
                Optional<ButtonType> result = confirmation.showAndWait();
                if (result.get() == save) {
                    savePuzzle(null);
                }
                if (result.get() ==cancel) e.consume();
            }
        });
    }

    public void openNew(ActionEvent e) {
        Main main = new Main();
        try {
            main.openAnother();
        } catch (Exception ex) {
            invokeError("exception at openAnother in main");
        }

    }

    public void openPuzzle(ActionEvent e) {
        saved = true;
        hitOnce = true;
        FileChooser fc = new FileChooser();
        fc.setTitle("Open");
        File cwd = new File(System.getProperty("user.dir") + "\\Sudokus");
        fc.setInitialDirectory(cwd);
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Sudoku files", "*.sdk*"));
        File chosen = fc.showOpenDialog(current);
        sudokuFile = chosen;
        SECONDS = HOURS = MINUTES = 0;
        timeTaken.setText(String.format("%02d:%02d:%02d", HOURS, MINUTES, SECONDS));
        try {
            Scanner cin = new Scanner(chosen);
            String var = cin.nextLine();
            if (var.equals("k")) {
                sudoku = new KingSudoku(chosen);
                setTypeLabel("King Sudoku");
                kingSudoku.setSelected(true);
            } else if (var.equals("x")) {
                sudoku = new XSudoku(chosen);
                setTypeLabel("X-Sudoku");
                xSudoku.setSelected(true);
            } else {
                sudoku = new Sudoku(chosen);
                setTypeLabel("Sudoku");
                normalSudoku.setSelected(true);
            }
            cin.close();
            go.set(true);
            puzzleInfo.setText(sudoku.puzzleInfo());
            setGrid(sudoku.getGrid());
            String temp = sudokuFile + "";
            current.setTitle(temp.substring(temp.lastIndexOf("\\") + 1));
            go.set(true);
        } catch (FileNotFoundException ex) {
            invokeError("impossible");
        } catch (NullPointerException npe) {
            go.set(false);
            invokeError("no file chosen");
            current.setTitle("Sudoku++");
        }
        constructKiller();
    }

    public void savePuzzle(ActionEvent e) {
        saved = true;
        if (sudokuFile == null) savePuzzleAs(e);
        else {
            try {
                PrintWriter cout = new PrintWriter(sudokuFile);
                cout.println(sudoku.fileString());
                cout.close();
            } catch (IOException ex) {
            }
        }
    }

    public void savePuzzleAs(ActionEvent e) {
        saved = true;
        FileChooser fc = new FileChooser();
        fc.setTitle("Save");
        try {
            File cwd = new File(System.getProperty("user.dir") + "\\Sudokus");
            fc.setInitialDirectory(cwd);
        } catch (Exception ex) {
            invokeError("cannot find sudoku folder!");
        }
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Sudoku files", "*.sdk*"));
        File chosen = fc.showSaveDialog(current);
        if (chosen == null){if (sudokuFile==null) saved=false;return;}
        try {
            PrintWriter cout = new PrintWriter(chosen + ".sdk");
            if (!(sudoku instanceof KingSudoku) && !(sudoku instanceof XSudoku)){
                cout.println("s");
            }
            cout.println(sudoku.fileString());
            cout.close();
            sudokuFile = new File(chosen + ".sdk");
        } catch (IOException ex) {
        }
        String temp = sudokuFile + "";
        current.setTitle(temp.substring(temp.lastIndexOf("\\") + 1) + "");
    }

    public void openAbout() {
        AnchorPane ap = new AnchorPane();
        TextArea ta = new TextArea();
        ta.setWrapText(true);
        ta.setPrefSize(700, 500);
        ta.setFont(new Font(25));
        ta.setEditable(false);
        ta.setStyle("-fx-font-family: 'Trebuchet MS';");
        try {
            Scanner cin = new Scanner(new File("about.txt"));
            while (cin.hasNext()) {
                ta.appendText(cin.nextLine() + "\n");
            }
            cin.close();
        } catch (FileNotFoundException ex) {
            invokeError("about.txt not found");
        }
        ap.getChildren().add(ta);
        Stage about = new Stage();
        about.getIcons().add(new Image("logo.png"));
        about.initOwner(current);
        about.initModality(Modality.APPLICATION_MODAL);
        about.setTitle("About Sudoku++");
        about.setResizable(false);
        about.setScene(new Scene(ap));
        about.showAndWait();
    }

    public void openDetails(ActionEvent e) {
        Main a = new Main();
        a.openDetails(this);
    }

    public void openScheme(ActionEvent e) {
        Main a = new Main();
        a.openScheme(this);
    }

    public void openMods(ActionEvent e) {
        Main a = new Main();
        a.openKiller(this);
    }

    public Sudoku getSudoku() {
        return sudoku;
    }

    public void setSudoku(Sudoku sdk) {
        sudoku = sdk;
    }

    public void setTypeLabel(String type) {
        typeLabel.setText(type);
    }

    public void setPuzzleInfo(String author, String description, String comment
            , Date published, String source) {
        sudoku.setInfo(author, description, comment, published, source);
        puzzleInfo.setText(sudoku.puzzleInfo());
    }

    public static void invokeError(String text) {
        Alert error = new Alert(Alert.AlertType.ERROR);
        error.setHeaderText(text);
        error.showAndWait();
    }

    public void setGo(boolean state) {
        go.set(state);
    }

    public void constructKiller() {
        ModifierController mc = new ModifierController();
        mc.addAllModifier(alm);
        mc.construct(DetermineColour.solve(alm));
        sudoku.setMods(alm);
        background=mc.getGp();
        background.setLayoutX(47);
        background.setLayoutY(58);
        background.setOpacity(0.83);
        centrePane.getChildren().remove(1);
        centrePane.getChildren().add(1,background);
    }
}
