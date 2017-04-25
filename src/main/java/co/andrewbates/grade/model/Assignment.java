package co.andrewbates.grade.model;

import java.util.UUID;

import co.andrewbates.grade.data.Info;
import javafx.beans.property.SimpleObjectProperty;

@Info(icon = "FILE_ALT", path = "assignments")
public class Assignment extends BaseModel {
    private static final long serialVersionUID = 1L;
    private SimpleObjectProperty<UUID> courseID = new SimpleObjectProperty<>();

    public String toString() {
        return getName();
    }

    public final SimpleObjectProperty<UUID> courseIDProperty() {
        return this.courseID;
    }

    public final UUID getCourseID() {
        return this.courseIDProperty().get();
    }

    public final void setCourseID(final UUID courseID) {
        this.courseIDProperty().set(courseID);
    }

}
