package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import sample.controller.*;

import java.io.IOException;

public class Main extends Application {
    public static Stage ps;
    public static FXMLLoader splashLoader;
    public static Stage splashStage;
    public static int stageCount;
    private static boolean done;
    public FXMLLoader loader;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        splashLoader = new FXMLLoader(getClass().getResource("view/splash.fxml"));
        Parent splashRoot = splashLoader.load();
        splashStage = new Stage(StageStyle.UNDECORATED);
        splashStage.setScene(new Scene(splashRoot));
        splashStage.getIcons().add(new Image("logo.png"));
        splashStage.show();
    }

    public void openMainApp() throws IOException {
        if (done) return;
        done = true;
        splashStage.close();
        loader = new FXMLLoader(getClass().getResource("view/play.fxml"));
        Parent root = loader.load();
        ps = new Stage();
        ps.getIcons().add(new Image("logo.png"));
        ps.setTitle("Sudoku++");
        Scene scene = new Scene(root);
        ps.setScene(scene);
        ps.setResizable(false);
        ps.setOnCloseRequest(this::checkAllDown);
        stageCount++;
        ((PlayController) loader.getController()).setCurrent(ps);
        ps.show();
    }

    public void openAnother() throws Exception {
        loader = new FXMLLoader(getClass().getResource("view/play.fxml"));
        Parent root = loader.load();
        Stage s = new Stage();
        s.getIcons().add(new Image("logo.png"));
        s.setTitle("Sudoku++");
        Scene scene = new Scene(root);
        s.setScene(scene);
        s.show();
        s.setResizable(false);
        s.setOnCloseRequest(this::checkAllDown);
        stageCount++;
        ((PlayController) loader.getController()).setCurrent(s);
    }

    public void openDetails(PlayController parent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("view/details.fxml"));
            Parent root = loader.load();
            ((DetailsController) loader.getController()).setParent(parent);
            ((DetailsController) loader.getController()).init();
            Stage stage = new Stage();
            stage.setTitle("Details");
            stage.getIcons().add(new Image("logo.png"));
            stage.setScene(new Scene(root));
            stage.initOwner(parent.getCurrent());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.show();
        } catch (IOException ex) {
            System.out.println("details.fxml cannot be found");
        }
    }

    public void openComputer(PlayController parent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("view/computer.fxml"));
            Parent root = loader.load();
            ((ComputerController) loader.getController()).setPc(parent);
            Stage stage = new Stage();
            stage.setTitle("Generate");
            stage.getIcons().add(new Image("logo.png"));
            stage.setScene(new Scene(root));
            stage.initOwner(parent.getCurrent());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.show();
        } catch (IOException ex) {
            System.out.println("computer.fxml cannot be found");
        }
    }

    public void openScheme(PlayController parent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("view/scheme.fxml"));
            Parent root = loader.load();
            ((SchemeController) loader.getController()).setPc(parent);
            Stage stage = new Stage();
            stage.setTitle("Colour Scheme");
            stage.setResizable(false);
            stage.getIcons().add(new Image("logo.png"));
            stage.setScene(new Scene(root));
            stage.initOwner(parent.getCurrent());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("scheme.fxml cannot be found");
        }
    }
    public void openKiller(PlayController parent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("view/killer.fxml"));
            Parent root = loader.load();
            ((KillerController) loader.getController()).setPc(parent);
            ((KillerController) loader.getController()).init();
            Stage stage = new Stage();
            stage.setTitle("Add Modifiers");
            stage.setResizable(false);
            stage.getIcons().add(new Image("logo.png"));
            stage.setScene(new Scene(root));
            stage.initOwner(parent.getCurrent());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("killer.fxml cannot be found");
        }
    }
    public void checkAllDown(WindowEvent e) {
        stageCount--;
        if (stageCount == 0) {
            System.exit(0);
        }
    }
}
