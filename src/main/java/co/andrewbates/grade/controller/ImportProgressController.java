package co.andrewbates.grade.controller;

import org.controlsfx.dialog.ExceptionDialog;

import co.andrewbates.grade.GradePreferences;
import co.andrewbates.grade.ImportWizard;
import co.andrewbates.grade.control.WizardPane.EnteringEvent;
import co.andrewbates.grade.task.ImportTask;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;

public class ImportProgressController {

    private ImportWizard wizard;

    @FXML
    private ProgressBar progress;

    @FXML
    private TextArea log;

    @FXML
    void handleEntering(EnteringEvent event) {
        wizard = (ImportWizard) event.getWizard();
        wizard.setInvalid(true);
        GradePreferences.setImportDirectory(wizard.getSourceDirectory().getParentFile());
        ImportTask task = new ImportTask(wizard.getSourceDirectory());
        progress.progressProperty().bind(task.progressProperty());
        log.textProperty().bind(task.valueProperty());

        task.setOnSucceeded((state) -> {
            wizard.setInvalid(false);
        });

        task.setOnFailed((state) -> {
            new ExceptionDialog(task.getException()).showAndWait();
        });

        new Thread(task).start();
    }
}
