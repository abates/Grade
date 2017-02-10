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

public class CoursesTabController {

    @FXML
    TableView<Course> courseTable;

    @FXML
    TableColumn<Course, String> courseNameColumn;

    @FXML
    TableView<Assignment> assignmentTable;

    @FXML
    TableColumn<Assignment, String> assignmentNameColumn;

    @FXML
    Button addAssignmentButton;

    @FXML
    Button deleteAssignmentButton;

    private CourseController courseController;

    private AssignmentController assignmentController;

    private Stage assignmentDialog;

    private Stage courseDialog;

    @FXML
    protected void handleAddCourse(ActionEvent event) {
        Course newCourse = new Course();
        courseController.setCourse(newCourse);
        courseDialog.showAndWait();
        if (courseController.completed()) {
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
            courseController.setCourse(course);
            courseDialog.showAndWait();
            if (courseController.completed()) {
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
    void handleAssignmentClicked(MouseEvent event) {
        Assignment assignment = assignmentTable.getSelectionModel().getSelectedItem();
        if (assignment != null && event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
            assignmentController.setAssignment(assignment);
            assignmentDialog.showAndWait();
            if (assignmentController.completed()) {
                try {
                    Database.getInstance().save(assignment);
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
        Course course = courseTable.getSelectionModel().getSelectedItem();
        if (course != null) {
            Assignment newAssignment = new Assignment();
            newAssignment.setCourseID(course.getID());
            assignmentController.setAssignment(newAssignment);
            assignmentDialog.showAndWait();
            if (assignmentController.completed()) {
                try {
                    Database.getInstance().create(newAssignment);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    protected void handleDeleteAssignment(ActionEvent event) {
        Assignment assignment = assignmentTable.getSelectionModel().getSelectedItem();
        if (assignment != null) {
            Database.getInstance().deleteAssignment(assignment);
        }
    }

    public void initialize() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/andrewbates/grade/fxml/Course.fxml"));
        try {
            courseDialog = new Stage();
            courseDialog.initModality(Modality.APPLICATION_MODAL);
            courseDialog.setScene(new Scene(loader.load()));
            courseController = loader.<CourseController> getController();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        loader = new FXMLLoader(getClass().getResource("/co/andrewbates/grade/fxml/Assignment.fxml"));
        try {
            assignmentDialog = new Stage();
            assignmentDialog.initModality(Modality.APPLICATION_MODAL);
            assignmentDialog.setScene(new Scene(loader.load()));
            assignmentController = loader.<AssignmentController> getController();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        courseTable.setItems(Database.getInstance().courses());
        addAssignmentButton.setDisable(true);
        deleteAssignmentButton.setDisable(true);
        courseTable.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> {
            if (nv == null) {
                addAssignmentButton.setDisable(true);
                deleteAssignmentButton.setDisable(true);
                assignmentTable.setItems(FXCollections.observableArrayList());
            } else {
                addAssignmentButton.setDisable(false);
                assignmentTable.setItems(Database.getInstance().assignments(nv));
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
        assignmentNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
    }
}
