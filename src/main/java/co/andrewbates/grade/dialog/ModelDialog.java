package co.andrewbates.grade.dialog;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.stage.Stage;

public class ModelDialog extends Stage {
    private BooleanProperty canceled;

    public ModelDialog() {
        this.canceled = new SimpleBooleanProperty(false);

    }

    public BooleanProperty canceledProperty() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled.set(canceled);
    }

    public boolean getCanceled() {
        return canceled.get();
    }
}
