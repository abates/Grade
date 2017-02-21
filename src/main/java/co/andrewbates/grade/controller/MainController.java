package co.andrewbates.grade.controller;

import java.io.File;
import java.io.IOException;

import co.andrewbates.grade.GradePreferences;
import co.andrewbates.grade.ImportWizard;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;

public class MainController {
    @FXML
    BorderPane borderPane;

    @FXML
    TabPane tabPane;

    @FXML
    CheckMenuItem viewStudentsMenuItem;

    @FXML
    CheckMenuItem viewCoursesMenuItem;

    private Tab studentsTab;

    private Tab coursesTab;

    public void setScene(Scene scene) {
        borderPane.prefHeightProperty().bind(scene.heightProperty());
        borderPane.prefWidthProperty().bind(scene.widthProperty());
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
    protected void handleClose(ActionEvent event) {
        Platform.exit();
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
        studentsTab.setOnClosed(ev -> {
            viewStudentsMenuItem.setSelected(false);
        });

        viewStudentsMenuItem.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                tabPane.getTabs().add(studentsTab);
                tabPane.getSelectionModel().select(studentsTab);
            } else {
                tabPane.getTabs().remove(studentsTab);
            }
        });

        coursesTab = new Tab("Courses");
        loader = new FXMLLoader(getClass().getResource("/co/andrewbates/grade/fxml/CoursesTab.fxml"));
        coursesTab.setContent(loader.load());
        coursesTab.setOnClosed(ev -> {
            viewCoursesMenuItem.setSelected(false);
        });

        viewCoursesMenuItem.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                tabPane.getTabs().add(coursesTab);
                tabPane.getSelectionModel().select(coursesTab);
            } else {
                tabPane.getTabs().remove(coursesTab);
            }
        });
    }
}
