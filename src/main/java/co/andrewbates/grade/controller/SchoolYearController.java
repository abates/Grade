package co.andrewbates.grade.controller;

import co.andrewbates.grade.data.Model;
import co.andrewbates.grade.model.SchoolYear;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class SchoolYearController extends DialogController {
    @FXML
    private TextField yearName;

    private SchoolYear schoolYear;

    @Override
    public void setModel(Model model) {
        this.schoolYear = (SchoolYear) model;
        yearName.setText(schoolYear.getName());
    }

    public void handleOK(ActionEvent event) {
        schoolYear.setName(yearName.getText());
        super.handleOK(event);
    }

}
