package co.andrewbates.grade.controller;

import co.andrewbates.grade.model.Course;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EditCourseController {
    private Course course;

    @FXML
    TextField courseName;

    private boolean completed;

    public void setCourse(Course course) {
        this.course = course;
        courseName.setText(course.getName());
    }

    @FXML
    void handleCancel(ActionEvent event) {
        completed = false;
        ((Stage) courseName.getScene().getWindow()).close();
    }

    @FXML
    void handleOK(ActionEvent event) {
        completed = true;
        course.setName(courseName.getText());
        ((Stage) courseName.getScene().getWindow()).close();
    }

    public boolean completed() {
        return completed;
    }

    public boolean canceled() {
        return !completed;
    }
}
