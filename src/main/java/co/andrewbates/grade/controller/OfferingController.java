package co.andrewbates.grade.controller;

import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import co.andrewbates.grade.Main;
import co.andrewbates.grade.model.Course;
import co.andrewbates.grade.model.Offering;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;

public class OfferingController extends ModelController<Offering> {
    private Offering offering;

    @FXML
    private TextField offeringName;

    @FXML
    private ComboBox<Course> courseList;

    public void initialize() {
        Platform.runLater(() -> {
            ValidationSupport validationSupport = new ValidationSupport();
            validationSupport.registerValidator(offeringName, false,
                    Validator.createEmptyValidator("Name is required"));
            validationSupport.registerValidator(courseList, false,
                    Validator.createEmptyValidator("Course must be selected"));
        });

        courseList.setItems(Main.database.courses());
        courseList.setCellFactory(lv -> {
            return new ListCell<Course>() {
                @Override
                public void updateItem(Course item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null) {
                        setText(null);
                    } else {
                        setText(item.getName());
                    }
                }
            };
        });
    }

    @Override
    public boolean isValid() {
        String name = offeringName.getText();
        Course course = courseList.getSelectionModel().getSelectedItem();
        return name != null && course != null && !name.trim().isEmpty();
    }

    @Override
    public void setModel(Offering offering) {
        this.offering = offering;

    }

    @Override
    public Offering getModel() {
        offering.setName(offeringName.getText());
        offering.setCourseID(courseList.getSelectionModel().getSelectedItem().getID());
        return offering;
    }
}
