package co.andrewbates.grade.controller;

import org.controlsfx.validation.Validator;

import co.andrewbates.grade.model.Course;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class CourseController extends ModelController<Course> {
    Course course;

    @FXML
    TextField courseName;

    @Override
    public void setModel(Course course) {
        this.course = course;
        courseName.setText(course.getName());
    }

    @Override
    public Course getModel() {
        course.setName(courseName.getText());
        return course;
    }

    public void initialize() {
        registerValidator(courseName, Validator.createEmptyValidator("Name is required"));
    }
}
