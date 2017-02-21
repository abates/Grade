package co.andrewbates.grade.controller;

import co.andrewbates.grade.data.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

public abstract class DialogController {
    @FXML
    Region mainPane;

    private boolean completed;

    @FXML
    void handleCancel(ActionEvent event) {
        completed = false;
        ((Stage) mainPane.getScene().getWindow()).close();
    }

    @FXML
    void handleOK(ActionEvent event) {
        completed = true;
        ((Stage) mainPane.getScene().getWindow()).close();
    }

    public boolean completed() {
        return completed;
    }

    public boolean canceled() {
        return !completed;
    }

    public abstract void setModel(Model model);
}