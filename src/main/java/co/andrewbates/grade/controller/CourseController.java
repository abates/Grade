package co.andrewbates.grade.controller;

import co.andrewbates.grade.data.Model;
import co.andrewbates.grade.model.Course;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class CourseController extends DialogController {
    Course course;

    @FXML
    TextField courseName;

    public void handleOK(ActionEvent event) {
        course.setName(courseName.getText());
        super.handleOK(event);
    }

    @Override
    public void setModel(Model model) {
        this.course = (Course) model;
        courseName.setText(course.getName());
    }
}
