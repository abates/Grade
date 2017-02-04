package co.andrewbates.grade.model;

import co.andrewbates.grade.data.BaseModel;

public class Student extends BaseModel {
    private String name;

    public Student(String name) {
        setName(name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public String toString() {
        return name;
    }

    public boolean isCompiled() {
        return false;
    }

    public boolean isValid() {
        return false;
    }
}
