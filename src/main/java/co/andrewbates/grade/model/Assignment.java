package co.andrewbates.grade.model;

import java.util.UUID;

import co.andrewbates.grade.data.BaseModel;

public class Assignment extends BaseModel {
    private UUID courseID;

    public UUID getCourseID() {
        return courseID;
    }

    public void setCourseID(UUID courseID) {
        this.courseID = courseID;
    }

    public String toString() {
        return getName();
    }
}
