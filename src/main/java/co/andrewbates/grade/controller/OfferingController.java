package co.andrewbates.grade.controller;

import java.io.File;

import co.andrewbates.grade.GradePreferences;
import co.andrewbates.grade.data.Database;
import co.andrewbates.grade.dialog.ProgressDialog;
import co.andrewbates.grade.model.Student;
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

public class OfferingController {
    @FXML
    ListView<Student> studentList;

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
    private File testDirectory;

    @FXML
    protected void handleGradeSelected(ActionEvent event) {
        if (selectedStudent == null) {
            new Alert(AlertType.ERROR, "A student must be selected first", ButtonType.OK).showAndWait();
        } else if (testDirectory == null) {
            new Alert(AlertType.ERROR, "No tests have been loaded yet", ButtonType.OK).showAndWait();
        } else {
            GradeTask task = new GradeTask(selectedStudent, new DefaultRubric(testDirectory));
            task.setOnSucceeded((state) -> {
                handleSelectedStudent(selectedStudent);
            });

            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();

            ProgressDialog compileDialog = new ProgressDialog(task);
            compileDialog.show();
        }
    }

    @FXML
    protected void handleGradeAll(ActionEvent event) {
        if (testDirectory == null) {
            new Alert(AlertType.ERROR, "No tests have been loaded yet", ButtonType.OK).showAndWait();
        } else {
            GradeAllTask task = new GradeAllTask(Database.getInstance().students(), new DefaultRubric(testDirectory));
            task.setOnSucceeded((state) -> {
                if (selectedStudent != null) {
                    handleSelectedStudent(selectedStudent);
                }
            });

            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();

            ProgressDialog compileDialog = new ProgressDialog(task);
            compileDialog.show();
        }
    }

    protected void handleSelectedStudent(Student student) {
        // selectedStudent = Database.getInstance().find(studentName);
        // scoreTable.setItems(selectedStudent.getScores());
        scoreOutput.setText("");
    }

    public void initialize() {
        studentList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        studentList.setItems(Database.getInstance().students());
        studentList.getSelectionModel().selectedItemProperty().addListener((observable, oldName, student) -> {
            handleSelectedStudent(student);
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
