package co.andrewbates.grade;

import java.io.IOException;

import org.controlsfx.dialog.ExceptionDialog;

import co.andrewbates.grade.controller.MainController;
import co.andrewbates.grade.data.Database;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    public static Database database;
    private Stage mainStage;
    public static GradePreferences preferences;

    @Override
    public void start(final Stage mainStage) throws IOException {
        this.mainStage = mainStage;
        preferences = new GradePreferences(GradePreferences.dataDirectory().resolve("preferences.properties").toFile());
        database = new Database(GradePreferences.dataDirectory());

        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            handleException(throwable);
        });

        try {
            showMainStage();
        } catch (Exception ex) {
            handleException(ex);
        }
    }

    public static void handleException(Throwable throwable) {
        System.err.println("Encountered an exception: ");
        throwable.printStackTrace(System.err);
        ExceptionDialog dialog = new ExceptionDialog(throwable);
        dialog.showAndWait();
    }

    private void showMainStage() throws Exception {
        preferences.bindWindow("Main", mainStage);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/andrewbates/grade/fxml/Main.fxml"));
        Scene scene = new Scene(loader.load());
        ((MainController) loader.getController()).setScene(scene);
        mainStage.setScene(scene);
        mainStage.showingProperty();
        mainStage.toBack();

        mainStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
