package co.andrewbates.grade.controller;

import java.io.IOException;

import co.andrewbates.grade.Main;
import co.andrewbates.grade.data.DatabaseEventHandler;
import co.andrewbates.grade.dialog.ModelDialog;
import co.andrewbates.grade.model.SchoolYear;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;

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

    private ObservableList<SchoolYear> schoolYears;

    public void setScene(Scene scene) {
        borderPane.prefHeightProperty().bind(scene.heightProperty());
        borderPane.prefWidthProperty().bind(scene.widthProperty());
    }

    @FXML
    public void handleNewYear(ActionEvent event) throws IOException {
        SchoolYear schoolYear = new ModelDialog<>(new SchoolYear()).showAndWait();
        if (schoolYear != null) {
            schoolYears.add(schoolYear);
            CheckMenuItem menuItem = addYearTab(schoolYear);
            menuItem.setSelected(true);
        }
    }

    @FXML
    public void handleClose(ActionEvent event) {
        Platform.exit();
    }

    public void initialize() throws IOException {
        loadTab("Courses", viewCoursesMenuItem, "/co/andrewbates/grade/fxml/CoursesTab.fxml");

        Task<ObservableList<SchoolYear>> task = Main.database.schoolYearsTask();

        schoolYears = task.getValue();

        task.setOnSucceeded(event -> {
            for (SchoolYear schoolYear : schoolYears) {
                addYearTab(schoolYear);
            }
        });

        Main.database.addHandler(SchoolYear.class, DatabaseEventHandler.DELETE, event -> {
            SchoolYear year = (SchoolYear) event.getModel();
            for (MenuItem item : yearsMenu.getItems()) {
                CheckMenuItem checkItem = (CheckMenuItem) item;
                if (checkItem.getText() == year.getName() || checkItem.getText().equals(year.getName())) {
                    yearsMenu.getItems().remove(checkItem);
                    if (checkItem.isSelected()) {
                        tabPane.getTabs().remove(checkItem.getUserData());
                    }
                    break;
                }
            }
        });
    }

    private CheckMenuItem addYearTab(SchoolYear year) {
        final CheckMenuItem menuItem = new CheckMenuItem(year.getName());

        yearsMenu.getItems().add(menuItem);

        loadTabWithController(year.getName(), menuItem, new SchoolYearTabController(year),
                "/co/andrewbates/grade/fxml/SchoolYearTab.fxml");

        year.nameProperty().addListener((o, ov, nv) -> {
            menuItem.setText(nv);
            Tab yearTab = (Tab) menuItem.getUserData();
            yearTab.setText(nv);
        });

        return menuItem;
    }

    private void loadTabWithController(String name, CheckMenuItem menuItem, Object controller, String path) {
        menuItem.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                Tab tab = new Tab(name);
                menuItem.setUserData(tab);
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

                tabPane.getTabs().add(tab);
                tabPane.getSelectionModel().select(tab);
            } else {
                tabPane.getTabs().remove((Tab) menuItem.getUserData());
            }
        });
    }

    private void loadTab(String name, CheckMenuItem menuItem, String path) {
        loadTabWithController(name, menuItem, null, path);
    }
}
