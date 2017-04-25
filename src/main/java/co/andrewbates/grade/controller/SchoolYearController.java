package co.andrewbates.grade.controller;

import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import co.andrewbates.grade.model.SchoolYear;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class SchoolYearController extends ModelController<SchoolYear> {
    @FXML
    private TextField yearName;

    private SchoolYear schoolYear;

    @Override
    public void setModel(SchoolYear model) {
        this.schoolYear = (SchoolYear) model;
        yearName.setText(schoolYear.getName());
    }

    @Override
    public boolean isValid() {
        String name = yearName.getText();
        return name != null && !name.trim().isEmpty();
    }

    @Override
    public SchoolYear getModel() {
        schoolYear.setName(yearName.getText());
        return schoolYear;
    }

    public void initialize() {
        Platform.runLater(() -> {
            ValidationSupport validationSupport = new ValidationSupport();
            validationSupport.registerValidator(yearName, false, Validator.createEmptyValidator("Name is required"));
        });
    }
}
