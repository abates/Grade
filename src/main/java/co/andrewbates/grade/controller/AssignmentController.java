package co.andrewbates.grade.controller;

import co.andrewbates.grade.data.Model;
import co.andrewbates.grade.model.Assignment;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class AssignmentController extends DialogController {
    @FXML
    TextField assignmentName;

    private Assignment assignment;

    public Assignment getAssignment() {
        return assignment;
    }

    @FXML
    public void handleOK(ActionEvent event) {
        this.assignment.setName(assignmentName.getText());
        super.handleOK(event);
    }

    public void setModel(Model model) {
        this.assignment = (Assignment) model;
        assignmentName.setText(assignment.getName());

    }
}
