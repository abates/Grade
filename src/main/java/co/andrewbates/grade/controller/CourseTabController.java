package co.andrewbates.grade.controller;

import java.io.IOException;

import co.andrewbates.grade.data.Database;
import co.andrewbates.grade.model.Assignment;
import co.andrewbates.grade.model.Course;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CourseTabController {
    @FXML
    TableView<Course> courseTable;

    @FXML
    TableColumn<Course, String> courseNameColumn;

    @FXML
    TableView<Assignment> assignmentTable;

    @FXML
    Button addAssignmentButton;

    @FXML
    Button deleteAssignmentButton;

    private EditCourseController editCourseController;

    private Stage editCourseDialog;

    @FXML
    protected void handleAddCourse(ActionEvent event) {
        Course newCourse = new Course();
        editCourseController.setCourse(newCourse);
        editCourseDialog.showAndWait();
        if (editCourseController.completed()) {
            try {
                Database.getInstance().create(newCourse);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @FXML
    void handleCourseClicked(MouseEvent event) {
        Course course = courseTable.getSelectionModel().getSelectedItem();

        if (course != null && event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
            editCourseController.setCourse(course);
            editCourseDialog.showAndWait();
            if (editCourseController.completed()) {
                try {
                    Database.getInstance().save(course);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    protected void handleDeleteCourse(ActionEvent event) {
    }

    @FXML
    protected void handleAddAssignment(ActionEvent event) {
    }

    @FXML
    protected void handleDeleteAssignment(ActionEvent event) {
        Database.getInstance().deleteAssignment(assignmentTable.getSelectionModel().getSelectedItem());
    }

    public void initialize() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/andrewbates/grade/fxml/Course.fxml"));
        try {
            editCourseDialog = new Stage();
            editCourseDialog.initModality(Modality.APPLICATION_MODAL);
            editCourseDialog.setScene(new Scene(loader.load()));
            editCourseController = loader.<EditCourseController> getController();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        courseTable.setItems(Database.getInstance().courses());
        courseTable.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> {
            if (nv == null) {
                addAssignmentButton.setDisable(true);
                deleteAssignmentButton.setDisable(true);
                assignmentTable.setItems(FXCollections.observableArrayList());
            } else {
                addAssignmentButton.setDisable(false);
                deleteAssignmentButton.setDisable(false);
                assignmentTable.setItems(Database.getInstance().getAssignments(nv));
            }
        });

        courseNameColumn.setCellValueFactory(new PropertyValueFactory<Course, String>("name"));
        assignmentTable.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> {
            if (nv == null) {
                deleteAssignmentButton.setDisable(true);
            } else {
                deleteAssignmentButton.setDisable(false);
            }
        });
    }
}
