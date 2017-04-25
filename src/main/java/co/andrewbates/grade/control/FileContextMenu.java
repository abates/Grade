package co.andrewbates.grade.control;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class FileContextMenu extends ContextMenu {
    public class FileContextEvent extends ActionEvent {
        private static final long serialVersionUID = 1L;

        private File file;

        public FileContextEvent(File file) {
            this.file = file;
        }

        public File getFile() {
            return file;
        }
    }

    private ObjectProperty<EventHandler<FileContextEvent>> onOpen = new SimpleObjectProperty<>();

    private ObjectProperty<EventHandler<FileContextEvent>> onDelete = new SimpleObjectProperty<>();

    private ObjectProperty<EventHandler<FileContextEvent>> onShowFolder = new SimpleObjectProperty<>();

    private File file;

    public FileContextMenu() {
        MenuItem openItem = new MenuItem("Open");
        openItem.setOnAction(event -> {
            onOpenProperty().get().handle(new FileContextEvent(file));
        });
        getItems().add(openItem);
        setOnOpen(event -> open(event.getFile()));

        MenuItem showItem = new MenuItem("Show Folder");
        showItem.setOnAction(event -> {
            onShowFolderProperty().get().handle(new FileContextEvent(file));
        });
        getItems().add(showItem);
        setOnShowFolder(event -> open(event.getFile().getParentFile()));

        MenuItem deleteItem = new MenuItem("Delete");
        deleteItem.setOnAction(event -> {
            onDeleteProperty().get().handle(new FileContextEvent(file));
        });
        getItems().add(deleteItem);
        setOnDelete(event -> delete(event.getFile()));
    }

    public void open(File file) {
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

    public void delete(File file) {
        try {
            Files.delete(file.toPath());
        } catch (IOException e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Delete Failed");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    public void setFile(File file) {
        this.file = file;
    }

    public final ObjectProperty<EventHandler<FileContextEvent>> onOpenProperty() {
        return this.onOpen;
    }

    public final EventHandler<FileContextEvent> getOnOpen() {
        return this.onOpenProperty().get();
    }

    public final void setOnOpen(final EventHandler<FileContextEvent> onOpen) {
        this.onOpenProperty().set(onOpen);
    }

    public final ObjectProperty<EventHandler<FileContextEvent>> onDeleteProperty() {
        return this.onDelete;
    }

    public final EventHandler<FileContextEvent> getOnDelete() {
        return this.onDeleteProperty().get();
    }

    public final void setOnDelete(final EventHandler<FileContextEvent> onDelete) {
        this.onDeleteProperty().set(onDelete);
    }

    public final ObjectProperty<EventHandler<FileContextEvent>> onShowFolderProperty() {
        return this.onShowFolder;
    }

    public final EventHandler<FileContextEvent> getOnShowFolder() {
        return this.onShowFolderProperty().get();
    }

    public final void setOnShowFolder(final EventHandler<FileContextEvent> onShowFolder) {
        this.onShowFolderProperty().set(onShowFolder);
    }
}
