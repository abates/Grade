package co.andrewbates.grade;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {
    public static final StudentController students = new StudentController();

    @Override
    public void start(Stage primaryStage) throws Exception {
        GradePreferences.bindUIPreferences(primaryStage);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/andrewbates/grade/resources/fxml/Main.fxml"));
        BorderPane borderPane = loader.load();
        Scene scene = new Scene(borderPane, 560, 240);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
