package co.andrewbates.grade;

import java.io.File;

import co.andrewbates.grade.rubric.Scoreable;

public class Student extends Scoreable {
    private String name;
    private File dir;

    public Student(String name, File dir) {
        this.name = name;
        this.dir = dir;
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

    public File getDir() {
        return dir;
    }
}
