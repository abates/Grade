package co.andrewbates.grade.model;

import co.andrewbates.grade.data.BaseModel;

public class SchoolYear extends BaseModel {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
