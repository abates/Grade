package co.andrewbates.grade.controller;

import org.controlsfx.dialog.ExceptionDialog;
import org.controlsfx.dialog.ProgressDialog;

import co.andrewbates.grade.GradePreferences;
import co.andrewbates.grade.ImportWizard;
import co.andrewbates.grade.Main;
import co.andrewbates.grade.Student;
import co.andrewbates.grade.rubric.DefaultRubric;
import co.andrewbates.grade.rubric.Score;
import co.andrewbates.grade.task.GradeAllTask;
import co.andrewbates.grade.task.GradeTask;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;

public class MainController {
    @FXML
    ListView<String> studentList;

    @FXML
    SplitPane splitPane;

    @FXML
    TableView<Score> scoreTable;

    @FXML
    TableColumn<Score, String> criteriaColumn;

    @FXML
    TableColumn<Score, String> scoreColumn;

    @FXML
    TextArea scoreOutput;

    private Student selectedStudent;

    @FXML
    protected void handleGradeSelected(ActionEvent event) {
        if (selectedStudent == null) {
            new Alert(AlertType.ERROR, "A student must be selected first", ButtonType.OK).showAndWait();
        } else {
            GradeTask task = new GradeTask(selectedStudent, new DefaultRubric());
            task.setOnFailed((state) -> {
                System.err.println("Failed: " + task.getException());
                task.getException().printStackTrace(System.err);
                new ExceptionDialog(task.getException()).showAndWait();
            });

            task.setOnSucceeded((state) -> {
                handleSelectedStudent(selectedStudent.getName());
            });

            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();

            ProgressDialog compileDialog = new ProgressDialog(task);
            compileDialog.initModality(Modality.APPLICATION_MODAL);
            compileDialog.show();
        }
    }

    @FXML
    protected void handleGradeAll(ActionEvent event) {
        GradeAllTask task = new GradeAllTask(Main.students.students(), new DefaultRubric());
        task.setOnFailed((state) -> {
            System.err.println("Failed: " + task.getException());
            task.getException().printStackTrace(System.err);
            new ExceptionDialog(task.getException()).showAndWait();
        });

        task.setOnSucceeded((state) -> {
            if (selectedStudent != null) {
                handleSelectedStudent(selectedStudent.getName());
            }
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();

        ProgressDialog compileDialog = new ProgressDialog(task);
        compileDialog.initModality(Modality.APPLICATION_MODAL);
        compileDialog.show();
    }

    @FXML
    protected void handleImport(ActionEvent event) {
        try {
            ImportWizard wizard = new ImportWizard();
            wizard.showAndWait();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    @FXML
    protected void handleLoadTests(ActionEvent event) {

    }

    protected void handleSelectedStudent(String studentName) {
        selectedStudent = Main.students.find(studentName);
        scoreTable.setItems(selectedStudent.getScores());
    }

    public void initialize() {
        studentList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        studentList.setItems(co.andrewbates.grade.Main.students.studentNames());
        studentList.getSelectionModel().selectedItemProperty().addListener((observable, oldName, studentName) -> {
            handleSelectedStudent(studentName);
        });

        scoreTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        scoreTable.getSelectionModel().selectedItemProperty().addListener((observable, old, score) -> {
            if (score != null) {
                scoreOutput.setText(score.getMessage());
            }
        });

        criteriaColumn.setCellValueFactory(new PropertyValueFactory<Score, String>("name"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<Score, String>("score"));

        GradePreferences.bindSplitPane("MainSPlitPane", splitPane);
    }

}
