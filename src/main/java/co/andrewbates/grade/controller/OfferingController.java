package co.andrewbates.grade.controller;

import co.andrewbates.grade.data.Database;
import co.andrewbates.grade.data.Model;
import co.andrewbates.grade.model.Course;
import co.andrewbates.grade.model.Offering;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;

public class OfferingController extends DialogController {
    private Offering offering;

    @FXML
    private TextField offeringName;

    @FXML
    private ComboBox<Course> courseList;

    @Override
    public void setModel(Model model) {
        offering = (Offering) model;
        if (offering != null) {
            courseList.getSelectionModel().select(Database.getInstance().getCourse(offering.getCourseID()));
        }
    }

    @Override
    void handleOK(ActionEvent event) {
        offering.setCourseID(courseList.getSelectionModel().getSelectedItem().getID());
        offering.setName(offeringName.getText());
        super.handleOK(event);
    }

    public void initialize() {
        courseList.setItems(Database.getInstance().courses());
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
}
