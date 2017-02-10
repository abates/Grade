package co.andrewbates.grade.model;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import co.andrewbates.grade.GradePreferences;
import co.andrewbates.grade.data.BaseModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Assignment extends BaseModel {
    private UUID courseID;
    private StringProperty name = new SimpleStringProperty();

    public UUID getCourseID() {
        return courseID;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setCourseID(UUID courseID) {
        this.courseID = courseID;
    }

    public ObservableList<File> getTestFiles() {
        File[] files = new File[] {};
        Path testDir = GradePreferences.dataDirectory().resolve("assignments").resolve(getID().toString())
                .resolve("tests");

        if (testDir.toFile().exists()) {
            files = testDir.toFile().listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.getName().endsWith(".java");
                }
            });
        }
        return FXCollections.observableArrayList(files);
    }

    public void copyTestFile(File file) throws IOException {
        Path testDir = GradePreferences.dataDirectory().resolve("assignments").resolve(getID().toString())
                .resolve("tests");
        if (!testDir.toFile().exists()) {
            testDir.toFile().mkdirs();
        }
        Files.copy(file.toPath(), testDir.resolve(file.getName()), StandardCopyOption.REPLACE_EXISTING);
    }
}
