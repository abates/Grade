package co.andrewbates.grade.controller;

import java.io.File;
import java.io.IOException;

import co.andrewbates.grade.Main;
import co.andrewbates.grade.control.FileContextMenu;
import co.andrewbates.grade.dialog.ConfirmationDialog;
import co.andrewbates.grade.dialog.LoggingProgressDialog;
import co.andrewbates.grade.dialog.ModelDialog;
import co.andrewbates.grade.model.Assignment;
import co.andrewbates.grade.model.Course;
import co.andrewbates.grade.model.Offering;
import co.andrewbates.grade.model.SchoolYear;
import co.andrewbates.grade.model.Submission;
import co.andrewbates.grade.rubric.DefaultRubric;
import co.andrewbates.grade.task.GradeAllTask;
import co.andrewbates.grade.task.ImportTask;
import de.jensd.fx.glyphs.GlyphIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;

public class SchoolYearTabController extends BaseController {
    private Assignment selectedAssignment;

    private Course selectedCourse;

    private File selectedFile;

    private FileContextMenu fileMenu;

    private Offering selectedOffering;

    private Submission selectedSubmission;

    private SchoolYear schoolYear;

    @FXML
    private Button addOfferingButton;

    @FXML
    private Button deleteFileButton;

    @FXML
    private Button deleteOfferingButton;

    @FXML
    private Button deleteStudentButton;

    @FXML
    private Button deleteSubmissionButton;

    @FXML
    private Button gradeAssignmentsButton;

    @FXML
    private Button importAssignmentsButton;

    @FXML
    private TableView<Offering> offeringsTable;

    @FXML
    private TableColumn<Offering, String> offeringNameColumn;

    @FXML
    private TableColumn<Offering, String> courseNameColumn;

    @FXML
    private TableView<Assignment> assignmentsTable;

    @FXML
    private TableColumn<Assignment, String> assignmentNameColumn;

    @FXML
    private TableView<Submission> submissionsTable;

    @FXML
    private TableColumn<Submission, String> submissionNameColumn;

    @FXML
    private TableColumn<Submission, Submission.Status> submissionGradedColumn;

    @FXML
    private TableColumn<Submission, GlyphIcon<FontAwesomeIcon>> submissionScoreColumn;

    @FXML
    private TableView<File> filesTable;

    @FXML
    private TableColumn<File, String> fileNameColumn;

    @FXML
    private TextArea logArea;

    public SchoolYearTabController(SchoolYear schoolYear) {
        this.schoolYear = schoolYear;
    }

    @FXML
    void handleAddOffering(ActionEvent event) throws IOException {
        Offering offering = new Offering();
        offering.setSchoolYearID(schoolYear.getID());
        offering = new ModelDialog<>(offering).showAndWait();
        if (offering != null) {
            offeringsTable.getItems().add(offering);
        }
    }

    @FXML
    void handleAssignmentClicked(MouseEvent event) throws IOException {
        selectedAssignment = assignmentsTable.getSelectionModel().getSelectedItem();
        if (selectedAssignment == null) {
            submissionsTable.setItems(null);
            gradeAssignmentsButton.setDisable(true);
        } else {
            ObservableList<Submission> submissions = Main.database.getSubmissions(selectedOffering, selectedAssignment);
            submissionsTable.setItems(submissions);
        }
        filesTable.setItems(null);
        logArea.setText("");
    }

    @FXML
    void handleDeleteFile(ActionEvent event) {
        String message = "Delete " + selectedFile.getName() + "from " + selectedSubmission.getStudentName()
                + "'s submission?";

        if (ConfirmationDialog.confirmDelete(message)) {
            fileMenu.delete(selectedFile);
            filesTable.getItems().remove(selectedFile);
        }
    }

    @FXML
    void handleDeleteOffering(ActionEvent event) throws IOException {
        String message = "This will permanently delete the offering and all associated student submissions.  Proceed?";
        if (ConfirmationDialog.confirmDelete(message)) {
            Main.database.delete(selectedOffering);
            offeringsTable.getItems().remove(selectedOffering);
        }
    }

    @FXML
    void handleDeleteSchoolYear(ActionEvent event) throws IOException {
        String message = schoolYear.getName() + " and all related data (offerings and submissions) will be deleted";

        if (ConfirmationDialog.confirmDelete(message)) {
            Main.database.delete(schoolYear);
        }
    }

    @FXML
    void handleDeleteSubmission(ActionEvent event) throws IOException {
        String message = "Permanently delete all files for " + selectedSubmission.getStudentName() + "'s submission?";

        if (ConfirmationDialog.confirmDelete(message)) {
            Main.database.delete(selectedSubmission);
            submissionsTable.getItems().remove(selectedSubmission);
        }
    }

    @FXML
    void handleEditSchoolYear(ActionEvent event) throws IOException {
        new ModelDialog<>(schoolYear).showAndWait();
    }

    @FXML
    void handleFileClicked(MouseEvent event) {
        selectedFile = filesTable.getSelectionModel().getSelectedItem();
        if (selectedFile != null && event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
            fileMenu.open(selectedFile);
        }
    }

    @FXML
    void handleGradeAssignments(ActionEvent event) throws IOException {
        ObservableList<Submission> submissions = submissionsTable.getItems();
        if (submissions != null && submissions.size() > 0) {
            DefaultRubric rubric = new DefaultRubric(Main.database.getTestPath(selectedAssignment));
            GradeAllTask task = new GradeAllTask(submissions, rubric);
            task.onFailedProperty().addListener(state -> {
                if (task.getException() != null) {
                    Main.handleException(task.getException());
                }
            });
            LoggingProgressDialog dialog = new LoggingProgressDialog(task);
            new Thread(task).start();
            dialog.showAndWait();
        }
    }

    @FXML
    void handleImportAssignments(ActionEvent event) throws IOException {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Import Folder");
        chooser.setInitialDirectory(Main.preferences.importDirectory());

        File selectedDirectory = chooser.showDialog(((Node) event.getTarget()).getScene().getWindow());
        if (selectedDirectory != null) {
            Main.preferences.setImportDirectory(selectedDirectory);
            Main.preferences.setImportDirectory(selectedDirectory.getParentFile());
            ImportTask task = new ImportTask(selectedDirectory, selectedOffering, selectedAssignment);

            LoggingProgressDialog importDialog = new LoggingProgressDialog(task);
            importDialog.setContentText("Importing test files");
            new Thread(task).start();
            importDialog.showAndWait();
            submissionsTable.setItems(Main.database.getSubmissions(selectedOffering, selectedAssignment));
        }
    }

    @FXML
    void handleOfferingClicked(MouseEvent event) throws IOException {
        selectedOffering = offeringsTable.getSelectionModel().getSelectedItem();
        if (selectedOffering != null) {
            selectedCourse = Main.database.getCourse(selectedOffering.getCourseID());
            assignmentsTable.setItems(Main.database.getAssignments(selectedCourse));
        }
    }

    @FXML
    void handleSubmissionClicked(MouseEvent event) throws IOException {
        selectedSubmission = submissionsTable.getSelectionModel().getSelectedItem();
        if (selectedSubmission != null) {
            filesTable.setItems(Main.database.getSubmissionFiles(selectedSubmission));
            logArea.setText(selectedSubmission.getLog());
        }
    }

    public void initialize() throws IOException {
        initializeTable(offeringsTable, assignmentsTable, deleteOfferingButton);
        offeringNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        courseNameColumn.setCellValueFactory(tv -> {
            StringProperty name;
            try {
                name = Main.database.getCourse(tv.getValue().getCourseID()).nameProperty();
            } catch (Exception e) {
                name = new SimpleStringProperty("Error - Unknown");
                e.printStackTrace();
            }
            return name;
        });

        offeringsTable.setItems(Main.database.getOfferings(schoolYear));

        initializeTable(assignmentsTable, submissionsTable, importAssignmentsButton, gradeAssignmentsButton);
        assignmentNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        initializeTable(submissionsTable, filesTable, deleteStudentButton);
        submissionsTable.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> {
            if (nv == null) {
                logArea.setText("");
            }
        });

        submissionNameColumn.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        submissionGradedColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        submissionScoreColumn.setCellValueFactory(new PropertyValueFactory<>("grade"));

        submissionGradedColumn.setCellFactory(tc -> {
            return new TableCell<Submission, Submission.Status>() {
                @Override
                protected void updateItem(Submission.Status status, boolean empty) {
                    super.updateItem(status, empty);
                    if (status == null || empty) {
                        setText("");
                        setGraphic(null);
                    } else {
                        GlyphIcon<FontAwesomeIcon> icon = null;
                        switch (status) {
                        case PASSED:
                            icon = new FontAwesomeIconView(FontAwesomeIcon.CHECK);
                            icon.setFill(Color.web("#00ff00"));
                            break;
                        case FAILED:
                            icon = new FontAwesomeIconView(FontAwesomeIcon.CLOSE);
                            icon.setFill(Color.web("#ff0000"));
                            break;
                        case NOTGRADED:
                            break;
                        }
                        setGraphic(icon);
                    }
                }
            };
        });
        fileMenu = new FileContextMenu();
        fileMenu.setOnDelete(event -> {
            handleDeleteFile(event);
        });

        fileMenu.setOnShowing(event -> {
            fileMenu.setFile(filesTable.getSelectionModel().getSelectedItem());
        });

        initializeTable(filesTable, null, deleteFileButton);
        filesTable.setContextMenu(fileMenu);
        fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
    }
}
