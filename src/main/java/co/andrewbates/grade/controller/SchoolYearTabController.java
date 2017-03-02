package co.andrewbates.grade.controller;

import java.io.File;
import java.io.IOException;

import co.andrewbates.grade.Main;
import co.andrewbates.grade.control.FileContextMenu;
import co.andrewbates.grade.control.FileContextMenu.FileContextEvent;
import co.andrewbates.grade.data.Database;
import co.andrewbates.grade.dialog.LoggingProgressDialog;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class SchoolYearTabController extends BaseController {
    private SchoolYear schoolYear;

    public SchoolYearTabController(SchoolYear schoolYear) {
        this.schoolYear = schoolYear;
    }

    @FXML
    private Button addOfferingButton;

    @FXML
    private Button deleteOfferingButton;

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
    private Button importAssignmentsButton;

    @FXML
    private Button deleteStudentButton;

    @FXML
    private TableView<Submission> submissionsTable;

    @FXML
    private TableColumn<Submission, String> submissionNameColumn;

    @FXML
    private TableColumn<Submission, Submission.Status> submissionGradedColumn;

    @FXML
    private TableColumn<Submission, GlyphIcon<FontAwesomeIcon>> submissionScoreColumn;

    @FXML
    private Button deleteSubmissionButton;

    @FXML
    private TableView<File> filesTable;

    @FXML
    private TableColumn<File, String> fileNameColumn;

    @FXML
    private Button gradeAssignmentsButton;

    @FXML
    private TextArea logArea;

    private Stage offeringDialog;
    private OfferingController offeringController;
    private Course selectedCourse;
    private Assignment selectedAssignment;
    private Offering selectedOffering;

    private Submission selectedSubmission;

    @FXML
    void handleAddOffering(ActionEvent event) throws IOException {
        Offering offering = new Offering();
        offering.setSchoolYearID(schoolYear.getID());
        showDialog(offeringDialog, offering, offeringController);
    }

    @FXML
    void handleAssignmentClicked(MouseEvent event) {
        selectedAssignment = assignmentsTable.getSelectionModel().getSelectedItem();
        if (selectedAssignment == null) {
            submissionsTable.setItems(null);
            gradeAssignmentsButton.setDisable(true);
        } else {
            ObservableList<Submission> submissions = Database.getInstance().submissions(selectedOffering,
                    selectedAssignment);
            submissionsTable.setItems(submissions);
        }
    }

    @FXML
    void handleOfferingClicked(MouseEvent event) {
        selectedOffering = offeringsTable.getSelectionModel().getSelectedItem();
        if (selectedOffering == null) {
            selectedCourse = null;
            assignmentsTable.setItems(null);
            submissionsTable.setItems(null);
        } else {
            selectedCourse = Database.getInstance().getCourse(selectedOffering.getCourseID());
            assignmentsTable.setItems(Database.getInstance().assignments(selectedCourse));
            submissionsTable.setItems(null);
        }
    }

    @FXML
    void handleImportAssignments(ActionEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Import Folder");
        chooser.setInitialDirectory(Main.preferences.importDirectory());

        File selectedDirectory = chooser.showDialog(((Node) event.getTarget()).getScene().getWindow());
        if (selectedDirectory != null) {
            Main.preferences.setImportDirectory(selectedDirectory.getParentFile());
            ImportTask task = new ImportTask(selectedDirectory, selectedOffering, selectedAssignment);

            LoggingProgressDialog importDialog = new LoggingProgressDialog(task);
            importDialog.setContentText("Importing test files");
            new Thread(task).start();
            importDialog.showAndWait();
        }
    }

    @FXML
    void handleSubmissionClicked(MouseEvent event) {
        selectedSubmission = submissionsTable.getSelectionModel().getSelectedItem();
        if (selectedSubmission == null) {
            filesTable.setItems(null);
        } else {
            filesTable.setItems(Database.getInstance().getSubmissionFiles(selectedSubmission));
            logArea.setText(selectedSubmission.getLog());
        }
    }

    @FXML
    void handleDeleteOffering(ActionEvent event) {

    }

    @FXML
    void handleDeleteAssignment(ActionEvent event) {

    }

    @FXML
    void handleTestFileClicked(MouseEvent event) {

    }

    @FXML
    void handleGradeAssignments(ActionEvent event) {
        ObservableList<Submission> submissions = submissionsTable.getItems();
        if (submissions != null && submissions.size() > 0) {
            DefaultRubric rubric = new DefaultRubric(Database.getInstance().getTestPath(selectedAssignment));
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

    private void handleDeleteFile(FileContextEvent event) {
        // TODO Auto-generated method stub

    }

    public void initialize() {
        offeringController = new OfferingController();
        offeringDialog = loadStage(offeringController, "/co/andrewbates/grade/fxml/Offering.fxml");

        initializeTable(offeringsTable, deleteOfferingButton);
        offeringNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        courseNameColumn.setCellValueFactory(tv -> {
            return Database.getInstance().getCourse(tv.getValue().getCourseID()).nameProperty();
        });

        offeringsTable.setItems(Database.getInstance().offerings(schoolYear));

        initializeTable(assignmentsTable, importAssignmentsButton, gradeAssignmentsButton);
        assignmentNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        initializeTable(submissionsTable, deleteStudentButton);

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
        FileContextMenu menu = new FileContextMenu();
        menu.setOnDelete(event -> {
            handleDeleteFile(event);
        });

        menu.setOnShowing(event -> {
            menu.setFile(filesTable.getSelectionModel().getSelectedItem());
        });
        filesTable.setContextMenu(menu);
        fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
    }
}
