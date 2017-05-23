package co.andrewbates.grade.controller;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.List;

import co.andrewbates.grade.Main;
import co.andrewbates.grade.control.FileContextMenu;
import co.andrewbates.grade.dialog.ConfirmationDialog;
import co.andrewbates.grade.dialog.ModelDialog;
import co.andrewbates.grade.dialog.ProgressDialog;
import co.andrewbates.grade.model.Assignment;
import co.andrewbates.grade.model.Course;
import co.andrewbates.grade.model.Offering;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class CoursesTabController extends BaseController {
    @FXML
    private TableView<Course> courseTable;

    @FXML
    private TableColumn<Course, String> courseNameColumn;

    @FXML
    private TableView<Assignment> assignmentTable;

    @FXML
    private TableColumn<Assignment, String> assignmentNameColumn;

    @FXML
    private Button addAssignmentButton;

    @FXML
    private Button deleteAssignmentButton;

    @FXML
    private TableView<File> testFileTable;

    @FXML
    private TableColumn<File, String> testFileNameColumn;

    @FXML
    private Button importTestFilesButton;

    @FXML
    private Button addCourseButton;

    @FXML
    private Button deleteCourseButton;

    @FXML
    private Button deleteTestFileButton;

    @FXML
    protected void handleAddCourse(ActionEvent event) throws IOException {
        Course course = new ModelDialog<>(new Course()).showAndWait();
        if (course != null) {
            courseTable.getItems().add(course);
        }
    }

    @FXML
    protected void handleDeleteCourse(ActionEvent event) throws IOException {
        Course course = courseTable.getSelectionModel().getSelectedItem();
        if (course != null) {
            // check if course is in use by an offering
            List<Offering> offerings = Main.database.getOfferings(course);
            if (offerings.size() > 0) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Course in Use");
                alert.setContentText("The course is in use by a year/offering and cannot be deleted");
                alert.showAndWait();
            } else {
                String message = "Proceed with deleting " + course.getName() + "?";
                if (ConfirmationDialog.confirmDelete(message)) {
                    Main.database.delete(course);
                    courseTable.getItems().remove(course);
                }
            }
        }
    }

    @FXML
    void handleCourseClicked(MouseEvent event) throws IOException {
        Course course = courseTable.getSelectionModel().getSelectedItem();
        if (course != null) {
            assignmentTable.setItems(Main.database.getAssignments(course));
            testFileTable.setItems(FXCollections.observableArrayList());

            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                new ModelDialog<>(course).showAndWait();
            }
        }
    }

    @FXML
    void handleAssignmentClicked(MouseEvent event) throws IOException {
        Assignment assignment = assignmentTable.getSelectionModel().getSelectedItem();
        if (assignment != null) {
            testFileTable.setItems(Main.database.getTestFiles(assignment));
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                new ModelDialog<>(assignment).showAndWait();
            }
        }
    }

    @FXML
    void handleTestFileClicked(MouseEvent event) throws IOException {
        File file = testFileTable.getSelectionModel().getSelectedItem();
        if (file != null && event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
            Desktop.getDesktop().open(file);
        }
    }

    @FXML
    protected void handleAddAssignment(ActionEvent event) throws IOException {
        Course course = courseTable.getSelectionModel().getSelectedItem();
        if (course != null) {
            Assignment assignment = new Assignment();
            assignment.setCourseID(course.getID());
            assignment = new ModelDialog<>(assignment).showAndWait();
            if (assignment != null) {
                assignmentTable.setItems(Main.database.getAssignments(course));
            }
        }
    }

    @FXML
    protected void handleDeleteAssignment(ActionEvent event) throws IOException {
        Assignment assignment = assignmentTable.getSelectionModel().getSelectedItem();
        if (assignment != null) {
            String message = "Deleting " + assignment.getName()
                    + " will permanently delete all test files and any student submissions for this assignment.  Proceed?";
            if (ConfirmationDialog.confirmDelete(message)) {
                Main.database.delete(assignment);
                assignmentTable.getItems().remove(assignment);
            }
        }
    }

    @FXML
    void handleDeleteTestFile(ActionEvent event) {
        File file = testFileTable.getSelectionModel().getSelectedItem();
        if (file != null) {
            String message = "Permanently delete " + file.getName() + "?";
            if (ConfirmationDialog.confirmDelete(message)) {
                if (file.delete()) {
                    testFileTable.getItems().remove(file);
                }
            }
        }
    }

    @FXML
    void handleImportTestFiles(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setSelectedExtensionFilter(new ExtensionFilter("Java Files", ".java"));
        chooser.setTitle("Import Tests");
        chooser.setInitialDirectory(Main.preferences.importDirectory());

        List<File> selectedFiles = chooser.showOpenMultipleDialog(((Node) event.getTarget()).getScene().getWindow());
        if (selectedFiles != null && !selectedFiles.isEmpty()) {
            Main.preferences.setImportDirectory(selectedFiles.get(0).getParentFile());
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    updateProgress(0, selectedFiles.size());
                    for (int i = 0; i < selectedFiles.size(); i++) {
                        File file = selectedFiles.get(i);
                        boolean found = false;
                        for (File testFile : testFileTable.getItems()) {
                            if (testFile.getName().equals(file.getName())) {
                                found = true;
                                break;
                            }
                        }

                        if (!found) {
                            testFileTable.getItems().add(file);
                        }
                        Main.database.copyFileToAssignment(file, assignmentTable.getSelectionModel().getSelectedItem());
                        updateProgress(i, selectedFiles.size());
                    }
                    return null;
                }
            };

            ProgressDialog importDialog = new ProgressDialog(task);
            importDialog.setContentText("Importing test files");
            new Thread(task).start();
            importDialog.showAndWait();
        }

    }

    public void initialize() {
        courseNameColumn.setCellValueFactory(new PropertyValueFactory<Course, String>("name"));
        initializeTable(courseTable, assignmentTable, deleteCourseButton, addAssignmentButton);
        courseTable.setItems(Main.database.courses());

        assignmentNameColumn.setCellValueFactory(new PropertyValueFactory<Assignment, String>("name"));
        initializeTable(assignmentTable, testFileTable, deleteAssignmentButton, importTestFilesButton);

        FileContextMenu menu = new FileContextMenu();
        menu.setOnDelete(event -> {
            handleDeleteTestFile(event);
        });

        menu.setOnShowing(event -> {
            menu.setFile(testFileTable.getSelectionModel().getSelectedItem());
        });

        testFileTable.setContextMenu(menu);
        testFileNameColumn.setCellValueFactory(new PropertyValueFactory<File, String>("name"));
        initializeTable(testFileTable, null, deleteTestFileButton);
    }
}
