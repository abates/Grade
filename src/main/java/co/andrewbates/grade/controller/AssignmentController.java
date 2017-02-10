package co.andrewbates.grade.controller;

import java.io.File;
import java.util.HashSet;
import java.util.List;

import co.andrewbates.grade.GradePreferences;
import co.andrewbates.grade.control.FileContextMenu;
import co.andrewbates.grade.dialog.ProgressDialog;
import co.andrewbates.grade.model.Assignment;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class AssignmentController extends DialogController {
    @FXML
    TextField assignmentName;

    @FXML
    TableView<File> testFiles;

    @FXML
    TableColumn<File, String> testFilenameColumn;

    private Assignment assignment;

    private HashSet<File> newTestFiles;

    public Assignment getAssignment() {
        return assignment;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
        assignmentName.setText(assignment.getName());
        newTestFiles = new HashSet<File>();
        testFiles.setItems(assignment.getTestFiles());
    }

    @FXML
    public void handleImportTests(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setSelectedExtensionFilter(new ExtensionFilter("Java Files", ".java"));
        chooser.setTitle("Import Tests");
        chooser.setInitialDirectory(GradePreferences.importDirectory());

        List<File> selectedFiles = chooser.showOpenMultipleDialog(((Node) event.getTarget()).getScene().getWindow());
        if (!selectedFiles.isEmpty()) {
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    newTestFiles.addAll(selectedFiles);
                    updateProgress(0, selectedFiles.size());
                    for (int i = 0; i < selectedFiles.size(); i++) {
                        File file = selectedFiles.get(i);
                        boolean found = false;
                        for (File testFile : testFiles.getItems()) {
                            if (testFile.getName().equals(file.getName())) {
                                found = true;
                                break;
                            }
                        }

                        if (!found) {
                            testFiles.getItems().add(file);
                        }
                        updateProgress(i, selectedFiles.size());
                    }
                    completed();
                    return null;
                }
            };

            ProgressDialog importDialog = new ProgressDialog(task);
            importDialog.setContentText("Importing test files");
            new Thread(task).start();
            importDialog.showAndWait();
        }
    }

    @FXML
    public void handleOK(ActionEvent event) {
        this.assignment.setName(assignmentName.getText());

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                File[] files = newTestFiles.toArray(new File[newTestFiles.size()]);
                updateProgress(0, files.length);
                for (int i = 0; i < files.length; i++) {
                    assignment.copyTestFile(files[i]);
                    updateProgress(i, files.length);
                }
                completed();
                return null;
            }
        };
        ProgressDialog copyDialog = new ProgressDialog(task);
        copyDialog.setContentText("Copying test files...");
        new Thread(task).start();
        copyDialog.showAndWait();

        super.handleOK(event);
    }

    public void initialize() {
        FileContextMenu menu = new FileContextMenu();
        menu.setOnShowing(event -> {
            menu.setFile(testFiles.getSelectionModel().getSelectedItem());
        });

        testFiles.setContextMenu(menu);
        testFilenameColumn.setCellValueFactory(new PropertyValueFactory<File, String>("name"));
    }
}
