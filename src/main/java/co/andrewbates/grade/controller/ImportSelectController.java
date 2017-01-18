package co.andrewbates.grade.controller;

import java.io.File;

import co.andrewbates.grade.GradePreferences;
import co.andrewbates.grade.ImportWizard;
import co.andrewbates.grade.control.WizardPane.EnteringEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

public class ImportSelectController {
    @FXML
    protected Label errorText;

    @FXML
    protected TextField locationField;
    private ImportWizard wizard;

    @FXML
    protected void handleBrowse(ActionEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Import Folder");
        chooser.setInitialDirectory(GradePreferences.importDirectory());

        File selectedDirectory = chooser.showDialog(((Node) event.getTarget()).getScene().getWindow());
        if (selectedDirectory != null) {
            GradePreferences.setImportDirectory(selectedDirectory.getParentFile());
            locationField.setText(selectedDirectory.getAbsolutePath());
        }
    }

    @FXML
    public void initialize() {
        locationField.textProperty().addListener((observable, oldValue, newValue) -> {
            setLocation(newValue);
        });
    }

    private void setLocation(String location) {
        wizard.setInvalid(true);
        wizard.setSourceDirectory(null);
        errorText.setText("");

        File tmp = new File(location);
        if (tmp.exists()) {
            if (tmp.isDirectory()) {
                wizard.setInvalid(false);
                wizard.setSourceDirectory(tmp);
            } else {
                errorText.setText("Location is not a directory");
            }
        } else {
            errorText.setText("Location does not exist");
        }
    }

    @FXML
    void handleEntering(EnteringEvent event) {
        this.wizard = (ImportWizard) event.getWizard();
        wizard.setInvalid(true);
    }

}
