package co.andrewbates.grade.controller;

import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import co.andrewbates.grade.model.Assignment;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class AssignmentController extends ModelController<Assignment> {
    @FXML
    TextField assignmentName;

    private Assignment assignment;

    @Override
    public boolean isValid() {
        String name = assignmentName.getText();
        return name != null && !name.trim().isEmpty();
    }

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
        Platform.runLater(() -> {
            ValidationSupport validationSupport = new ValidationSupport();
            validationSupport.registerValidator(assignmentName, false,
                    Validator.createEmptyValidator("Name is required"));
        });
    }
}
