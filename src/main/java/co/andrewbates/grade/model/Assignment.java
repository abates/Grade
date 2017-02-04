package co.andrewbates.grade.model;

import java.util.UUID;

import co.andrewbates.grade.data.BaseModel;

public class Assignment extends BaseModel {
    private UUID courseID;
    private String name;

    public UUID getCourseID() {
        return courseID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
