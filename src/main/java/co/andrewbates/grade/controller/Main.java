package co.andrewbates.grade.controller;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import co.andrewbates.grade.ui.ImportDialog;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;

public class Main implements Initializable {
    @FXML
    File sourceDirectory;

    @FXML
    TextField location;

    @FXML
    Label errorLabel;

    @FXML
    ListView<String> studentList;

    @FXML
    protected void handleCompileSelectedButtonAction(ActionEvent event) {
    }

    @FXML
    protected void handleCompileAllButtonAction(ActionEvent event) {
    }

    @FXML
    protected void importMenuItemAction(ActionEvent event) {
        ImportDialog dialog = new ImportDialog();
        dialog.showAndWait();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        studentList = new ListView<String>();
        studentList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        studentList.setItems(co.andrewbates.grade.Main.students.studentNames());
    }

}
