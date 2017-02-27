package co.andrewbates.grade.controller;

import java.io.IOException;

import co.andrewbates.grade.data.Database;
import co.andrewbates.grade.data.Model;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class BaseController {

    public BaseController() {
        super();
    }

    protected void showDialog(Stage stage, Model model, DialogController controller) throws IOException {
        controller.setModel(model);
        stage.showAndWait();
        if (controller.completed()) {
            Database.getInstance().save(model);
        }
    }

    protected Stage loadStage(Object controller, String path) {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
        if (controller != null) {
            loader.setController(controller);
        }

        try {
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(loader.load()));
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        return stage;
    }

    protected void initializeTable(TableView<?> table, Button... buttons) {
        for (Button button : buttons) {
            button.setDisable(true);
        }

        table.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> {
            if (nv != null) {
                for (Button button : buttons) {
                    button.setDisable(false);
                }
            } else {
                for (Button button : buttons) {
                    button.setDisable(true);
                }
            }
        });
    }

}