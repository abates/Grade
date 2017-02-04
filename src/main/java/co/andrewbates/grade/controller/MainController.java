package co.andrewbates.grade.controller;

import java.io.File;
import java.io.IOException;

import co.andrewbates.grade.GradePreferences;
import co.andrewbates.grade.ImportWizard;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.DirectoryChooser;

public class MainController {
    @FXML
    TabPane tabPane;

    @FXML
    CheckMenuItem viewStudentsMenuItem;

    @FXML
    CheckMenuItem viewCoursesMenuItem;

    Tab studentsTab;
    Tab coursesTab;

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
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Import Folder");
        chooser.setInitialDirectory(GradePreferences.testDirectory());

        File testDirectory = chooser.showDialog(tabPane.getScene().getWindow());
        if (testDirectory != null) {
            GradePreferences.setTestDirectory(testDirectory.getParentFile());
        }
    }

    public void initialize() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/andrewbates/grade/fxml/StudentsTab.fxml"));

        studentsTab = new Tab("Students");
        studentsTab.setContent(loader.load());

        loader = new FXMLLoader(getClass().getResource("/co/andrewbates/grade/fxml/CoursesTab.fxml"));
        coursesTab = new Tab("Courses");
        coursesTab.setContent(loader.load());

        viewStudentsMenuItem.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                tabPane.getTabs().add(studentsTab);
            } else {
                tabPane.getTabs().remove(studentsTab);
            }
        });

        viewCoursesMenuItem.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                tabPane.getTabs().add(coursesTab);
            } else {
                tabPane.getTabs().remove(coursesTab);
            }
        });
    }
}
