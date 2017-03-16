package co.andrewbates.grade.controller;

import java.io.IOException;
import java.util.HashMap;

import co.andrewbates.grade.data.Database;
import co.andrewbates.grade.model.SchoolYear;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainController extends BaseController {
    @FXML
    private BorderPane borderPane;

    @FXML
    private TabPane tabPane;

    @FXML
    private CheckMenuItem viewStudentsMenuItem;

    @FXML
    private CheckMenuItem viewCoursesMenuItem;

    @FXML
    private Menu yearsMenu;

    private Stage yearDialog;

    private SchoolYearController yearController;

    private HashMap<String, Tab> yearsTabs = new HashMap<>();

    public void setScene(Scene scene) {
        borderPane.prefHeightProperty().bind(scene.heightProperty());
        borderPane.prefWidthProperty().bind(scene.widthProperty());
    }

    @FXML
    public void handleNewYear(ActionEvent event) throws IOException {
        SchoolYear newYear = new SchoolYear();
        showDialog(yearDialog, newYear, yearController);
        if (yearController.completed()) {
            loadYearTab(newYear);
        }
    }

    private void loadYearTab(SchoolYear schoolYear) {
        Tab tab = yearsTabs.get(schoolYear.getName());
        if (tab != null) {
            int index = yearsMenu.getItems().indexOf(schoolYear.getName());
            if (index >= 0) {
                ((CheckMenuItem) yearsMenu.getItems().get(index)).setSelected(true);
            }
            tabPane.getTabs().add(tab);
        }
    }

    @FXML
    public void handleClose(ActionEvent event) {
        Platform.exit();
    }

    @SuppressWarnings("unused")
    public void initialize() throws IOException {
        loadTab("Students", viewStudentsMenuItem, "/co/andrewbates/grade/fxml/StudentsTab.fxml");
        loadTab("Courses", viewCoursesMenuItem, "/co/andrewbates/grade/fxml/CoursesTab.fxml");

        yearController = new SchoolYearController();
        yearDialog = loadStage(yearController, "/co/andrewbates/grade/fxml/SchoolYear.fxml");

        Database.getInstance().schoolYears().addListener((ListChangeListener<SchoolYear>) (change) -> {
            for (; change.next();) {
                int index = change.getFrom();
                for (SchoolYear year : change.getRemoved()) {
                    yearsMenu.getItems().remove(index);
                }

                for (SchoolYear year : change.getAddedSubList()) {
                    final int finalIndex = index;
                    Platform.runLater(() -> {
                        CheckMenuItem menuItem = new CheckMenuItem(year.getName());
                        Tab yearTab = loadTabWithController(year.getName(), menuItem, new SchoolYearTabController(year),
                                "/co/andrewbates/grade/fxml/SchoolYearTab.fxml");
                        yearsMenu.getItems().add(finalIndex, menuItem);
                        yearsTabs.put(year.getName(), yearTab);
                    });
                    index++;
                }
            }
        });
    }

    private Tab loadTabWithController(String name, CheckMenuItem menuItem, Object controller, String path) {
        final Tab tab = new Tab(name);
        FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
        if (controller != null) {
            loader.setController(controller);
        }

        try {
            tab.setContent(loader.load());
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        tab.setOnClosed(ev -> {
            menuItem.setSelected(false);
        });

        menuItem.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                tabPane.getTabs().add(tab);
                tabPane.getSelectionModel().select(tab);
            } else {
                tabPane.getTabs().remove(tab);
            }
        });

        return tab;
    }

    private Tab loadTab(String name, CheckMenuItem menuItem, String path) {
        return loadTabWithController(name, menuItem, null, path);
    }
}
