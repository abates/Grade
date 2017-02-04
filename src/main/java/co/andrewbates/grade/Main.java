package co.andrewbates.grade;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {
    public static final StudentController students = new StudentController();
    private Stage mainStage;

    @Override
    public void start(final Stage mainStage) throws Exception {
        this.mainStage = mainStage;
        showMainStage();
        showSplash();
    }

    private void showMainStage() throws Exception {
        GradePreferences.bindWindow("Main", mainStage);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/andrewbates/grade/fxml/Main.fxml"));
        Scene scene = new Scene(loader.load());
        mainStage.setScene(scene);
        mainStage.showingProperty();
        mainStage.toBack();

        mainStage.show();
    }

    private void showSplash() throws Exception {
        Stage initStage = new Stage(StageStyle.DECORATED);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/andrewbates/grade/fxml/Splash.fxml"));
        Scene scene = new Scene(loader.load());
        initStage.initOwner(mainStage);
        initStage.initModality(Modality.APPLICATION_MODAL);
        initStage.initStyle(StageStyle.UNDECORATED);
        initStage.setScene(scene);
        initStage.setAlwaysOnTop(true);
        initStage.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
