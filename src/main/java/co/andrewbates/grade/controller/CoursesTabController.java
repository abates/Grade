package co.andrewbates.grade.controller;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import co.andrewbates.grade.Main;
import co.andrewbates.grade.control.FileContextMenu;
import co.andrewbates.grade.data.Database;
import co.andrewbates.grade.dialog.ProgressDialog;
import co.andrewbates.grade.model.Assignment;
import co.andrewbates.grade.model.BaseModel;
import co.andrewbates.grade.model.Course;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

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

    private CourseController courseController;
    private Stage courseDialog;
    private AssignmentController assignmentController;
    private Stage assignmentDialog;

    @FXML
    protected void handleAddCourse(ActionEvent event) throws IOException {
        BaseModel newCourse = new Course();
        showDialog(courseDialog, newCourse, courseController);
    }

    @FXML
    protected void handleDeleteCourse(ActionEvent event) throws IOException {
        Course course = courseTable.getSelectionModel().getSelectedItem();
        if (course != null) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Confirm Delete");
            alert.setHeaderText("Permanently Delete Course");
            alert.setContentText("Proceed with deleting " + course.getName() + "?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                Database.getInstance().delete(course);
            }
        }
    }

    @FXML
    void handleCourseClicked(MouseEvent event) throws IOException {
        BaseModel course = courseTable.getSelectionModel().getSelectedItem();
        if (course != null && event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
            showDialog(courseDialog, course, courseController);
        }
    }

    @FXML
    void handleAssignmentClicked(MouseEvent event) throws IOException {
        Assignment assignment = assignmentTable.getSelectionModel().getSelectedItem();
        if (assignment != null && event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
            showDialog(assignmentDialog, assignment, assignmentController);
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
        BaseModel course = courseTable.getSelectionModel().getSelectedItem();
        if (course != null) {
            Assignment newAssignment = new Assignment();
            newAssignment.setCourseID(course.getID());
            showDialog(assignmentDialog, newAssignment, assignmentController);
        }
    }

    @FXML
    protected void handleDeleteAssignment(ActionEvent event) throws IOException {
        Assignment assignment = assignmentTable.getSelectionModel().getSelectedItem();
        if (assignment != null) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Confirm Delete");
            alert.setHeaderText("Permanently Delete Assignment");
            alert.setContentText("Proceed with deleting " + assignment.getName() + "?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                Database.getInstance().delete(assignment);
            }
        }
    }

    @FXML
    void handleDeleteTestFile(ActionEvent event) {
        File file = testFileTable.getSelectionModel().getSelectedItem();
        if (file != null) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Confirm Delete");
            alert.setHeaderText("Permanently Delete File");
            alert.setContentText("Proceed with deleting " + file.getName() + "?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
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
                        Database.getInstance().copyFileToAssignment(file,
                                assignmentTable.getSelectionModel().getSelectedItem());
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
        courseController = new CourseController();
        courseDialog = loadStage(courseController, "/co/andrewbates/grade/fxml/Course.fxml");

        courseNameColumn.setCellValueFactory(new PropertyValueFactory<Course, String>("name"));
        initializeTable(courseTable, deleteCourseButton, addAssignmentButton);
        courseTable.setItems(Database.getInstance().courses());
        courseTable.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> {
            if (nv == null) {
                assignmentTable.setItems(FXCollections.observableArrayList());
            } else {
                assignmentTable.setItems(Database.getInstance().assignments(nv));
            }
        });

        assignmentController = new AssignmentController();
        assignmentDialog = loadStage(assignmentController, "/co/andrewbates/grade/fxml/Assignment.fxml");
        assignmentNameColumn.setCellValueFactory(new PropertyValueFactory<Assignment, String>("name"));
        initializeTable(assignmentTable, deleteAssignmentButton, importTestFilesButton);
        assignmentTable.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> {
            if (nv == null) {
                testFileTable.setItems(FXCollections.observableArrayList());
            } else {
                testFileTable.setItems(Database.getInstance().getTestFiles(nv));
            }
        });

        FileContextMenu menu = new FileContextMenu();
        menu.setOnDelete(event -> {
            handleDeleteTestFile(event);
        });

        menu.setOnShowing(event -> {
            menu.setFile(testFileTable.getSelectionModel().getSelectedItem());
        });

        testFileTable.setContextMenu(menu);
        testFileNameColumn.setCellValueFactory(new PropertyValueFactory<File, String>("name"));
        initializeTable(testFileTable, deleteTestFileButton);
    }
}
