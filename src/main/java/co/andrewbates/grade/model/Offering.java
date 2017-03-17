package co.andrewbates.grade.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class Offering extends BaseModel {
    private static final long serialVersionUID = 1L;
    private ObjectProperty<Long> courseID = new SimpleObjectProperty<>();
    private ObjectProperty<Long> schoolYearID = new SimpleObjectProperty<>();

    public ObjectProperty<Long> courseIDProperty() {
        return courseID;
    }

    public ObjectProperty<Long> schoolYearIDProperty() {
        return schoolYearID;
    }

    public void setCourseID(long courseID) {
        this.courseID.set(courseID);
    }

    public void setSchoolYearID(long schoolYearID) {
        this.schoolYearID.set(schoolYearID);
    }

    public long getSchoolYearID() {
        return schoolYearID.get();
    }

    public long getCourseID() {
        return courseID.get();
    }
}
