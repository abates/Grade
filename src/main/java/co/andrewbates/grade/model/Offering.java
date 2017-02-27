package co.andrewbates.grade.model;

import java.util.UUID;

import co.andrewbates.grade.data.BaseModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class Offering extends BaseModel {
    private ObjectProperty<UUID> courseID = new SimpleObjectProperty<>();
    private ObjectProperty<UUID> schoolYearID = new SimpleObjectProperty<>();

    public ObjectProperty<UUID> courseIDProperty() {
        return courseID;
    }

    public ObjectProperty<UUID> schoolYearIDProperty() {
        return schoolYearID;
    }

    public void setCourseID(UUID courseID) {
        this.courseID.set(courseID);
    }

    public void setSchoolYearID(UUID schoolYearID) {
        this.schoolYearID.set(schoolYearID);
    }

    public UUID getSchoolYearID() {
        return schoolYearID.get();
    }

    public UUID getCourseID() {
        return courseID.get();
    }
}
