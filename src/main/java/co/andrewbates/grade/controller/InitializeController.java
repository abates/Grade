package co.andrewbates.grade.controller;

import org.controlsfx.dialog.ExceptionDialog;

import co.andrewbates.grade.Main;
import co.andrewbates.grade.data.Database;
import co.andrewbates.grade.data.Database.Loader;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import javafx.util.Duration;

public class InitializeController {
    private static final long SPLASH_DELAY = 1000;
    @FXML
    ProgressBar progressBar;

    @FXML
    Label progressText;

    Loader loader;

    long startTime;

    private void start(Stage stage) {

        stage.showingProperty().addListener((observable, ov, nv) -> {
            if (nv) {
                startTime = System.currentTimeMillis();
                stage.toFront();
            }
        });

        loader.valueProperty().addListener((o, ov, nv) -> {
            progressText.setText(nv);
        });

        loader.setOnSucceeded((state) -> {
            long finish = System.currentTimeMillis();
            if (finish - startTime < SPLASH_DELAY) {
                PauseTransition delay = new PauseTransition(new Duration(SPLASH_DELAY - (finish - startTime)));
                delay.setOnFinished(ev -> {
                    stage.hide();
                });
                delay.play();
            } else {
                stage.hide();
            }
        });

        loader.setOnFailed((state) -> {
            new ExceptionDialog(loader.getException()).showAndWait();
            stage.hide();
        });

    }

    public void initialize() {
        loader = Database.load(Main.preferences.dataDirectory());
        progressBar.progressProperty().bind(loader.progressProperty());
        progressBar.sceneProperty().addListener((os, osv, nsv) -> {
            if (nsv != null) {
                nsv.windowProperty().addListener((ow, owv, nwv) -> {
                    if (nwv != null) {
                        start((Stage) nwv);
                    }
                });
            }
        });
        new Thread(loader).start();
    }

}
