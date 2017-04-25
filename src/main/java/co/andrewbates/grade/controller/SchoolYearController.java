package co.andrewbates.grade.controller;

import org.controlsfx.validation.Validator;

import co.andrewbates.grade.model.SchoolYear;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class SchoolYearController extends ModelController<SchoolYear> {
    @FXML
    private TextField yearName;

    private SchoolYear schoolYear;

    @Override
    public void setModel(SchoolYear schoolYear) {
        this.schoolYear = schoolYear;
        yearName.setText(schoolYear.getName());
    }

    @Override
    public SchoolYear getModel() {
        schoolYear.setName(yearName.getText());
        return schoolYear;
    }

    public void initialize() {
        registerValidator(yearName, Validator.createEmptyValidator("Name is required"));
    }
}
