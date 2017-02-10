package co.andrewbates.grade.control;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class FileContextMenu extends ContextMenu {
    private File file;

    public FileContextMenu() {
        MenuItem openItem = new MenuItem("Open");
        openItem.setOnAction(event -> {
            open(file);
        });
        getItems().add(openItem);

        MenuItem showItem = new MenuItem("Show Folder");
        showItem.setOnAction(event -> {
            open(file.getParentFile());
        });
        getItems().add(showItem);
    }

    private void open(File file) {
        try {
            Desktop.getDesktop().open(file);
        } catch (IOException ex) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Open File");
            alert.setHeaderText("Failed to open file");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
    }

    public void setFile(File file) {
        this.file = file;
    }
}
