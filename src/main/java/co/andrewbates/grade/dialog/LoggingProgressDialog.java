package co.andrewbates.grade.dialog;

import javafx.concurrent.Task;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class LoggingProgressDialog extends ProgressDialog {
    public LoggingProgressDialog(Task<?> task) {
        super(task);

        TextArea log = new TextArea();
        log.textProperty().bind(task.messageProperty());
        ((VBox) getDialogPane().getContent()).getChildren().add(log);
    }
}
