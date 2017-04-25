package co.andrewbates.grade.controller;

import org.controlsfx.validation.Validator;

import co.andrewbates.grade.model.Assignment;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class AssignmentController extends ModelController<Assignment> {
    @FXML
    TextField assignmentName;

    private Assignment assignment;

    @Override
    public void setModel(Assignment assignment) {
        this.assignment = assignment;
        assignmentName.setText(assignment.getName());
    }

    @Override
    public Assignment getModel() {
        assignment.setName(assignmentName.getText());
        return assignment;
    }

    public void initialize() {
        registerValidator(assignmentName, Validator.createEmptyValidator("Name is required"));
    }
}
