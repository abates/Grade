package co.andrewbates.grade.dialog;

import org.controlsfx.dialog.ExceptionDialog;

import javafx.concurrent.Task;
import javafx.stage.Modality;

public class ProgressDialog extends org.controlsfx.dialog.ProgressDialog {
    public ProgressDialog(Task<?> task) {
        super(task);
        initModality(Modality.APPLICATION_MODAL);
        task.setOnFailed(event -> {
            System.err.println("Failed: " + task.getException());
            event.getSource().getException().printStackTrace(System.err);
            new ExceptionDialog(event.getSource().getException()).showAndWait();
        });
    }

}
