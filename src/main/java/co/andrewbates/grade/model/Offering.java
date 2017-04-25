package co.andrewbates.grade.model;

import java.util.UUID;

import co.andrewbates.grade.data.Info;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

@Info(icon = "FILE", path = "offerings")
public class Offering extends BaseModel {
    private static final long serialVersionUID = 1L;
    private ObjectProperty<UUID> courseID = new SimpleObjectProperty<>();
    private ObjectProperty<UUID> schoolYearID = new SimpleObjectProperty<>();

    public ObjectProperty<UUID> courseIDProperty() {
        return courseID;
    }

    public void setCourseID(UUID courseID) {
        this.courseID.set(courseID);
    }

    public UUID getCourseID() {
        return courseID.get();
    }

    public final ObjectProperty<UUID> schoolYearIDProperty() {
        return this.schoolYearID;
    }

    public final UUID getSchoolYearID() {
        return this.schoolYearIDProperty().get();
    }

    public final void setSchoolYearID(final UUID schoolYearID) {
        this.schoolYearIDProperty().set(schoolYearID);
    }
}
