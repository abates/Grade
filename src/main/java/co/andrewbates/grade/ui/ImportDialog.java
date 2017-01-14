package co.andrewbates.grade.ui;

import java.io.File;

import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;

import co.andrewbates.grade.GradePreferences;
import co.andrewbates.grade.Main;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;

public class ImportDialog extends Wizard {
    class SourcePane extends WizardPane implements EventHandler<ActionEvent>, ChangeListener<String> {
        File sourceDirectory;
        TextField location;
        Label errorLabel;

        public SourcePane() {
            setHeaderText("Import Directory");
            GridPane grid = new GridPane();
            grid.setAlignment(Pos.CENTER);
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(25, 25, 25, 25));
            grid.add(new Label("Choose a source directory"), 0, 0, 3, 1);
            grid.add(new Label("Location:"), 0, 1);
            grid.setPrefHeight(200);
            grid.setPrefWidth(400);

            location = new TextField();
            location.textProperty().addListener(this);
            grid.add(location, 1, 1);

            Button browseButton = new Button("Browse");
            browseButton.setOnAction(this);
            grid.add(browseButton, 2, 1);

            errorLabel = new Label("");
            errorLabel.setTextFill(Color.web("#BF0018"));
            grid.add(errorLabel, 0, 3, 3, 1);

            setContent(grid);
        }

        @Override
        public void handle(ActionEvent event) {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Import Folder");
            chooser.setInitialDirectory(GradePreferences.importDirectory());

            File selectedDirectory = chooser.showDialog(getScene().getWindow());
            location.setText(selectedDirectory.getAbsolutePath());
        }

        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            File tmp = new File(newValue);
            if (tmp.exists()) {
                if (tmp.isDirectory()) {
                    errorLabel.setText("");
                    sourceDirectory = tmp;
                } else {
                    errorLabel.setText("Location is not a directory");
                }
            } else {
                errorLabel.setText("Location does not exist");
            }
        }
    }

    class ImportPane extends WizardPane {
        public ImportPane() {
            setHeaderText("Import Directory");
            GridPane grid = new GridPane();
            ProgressBar progressBar = new ProgressBar();
            grid.add(progressBar, 0, 0);

            TextArea text = new TextArea();
            grid.add(text, 0, 1);
            grid.setPrefHeight(200);
            grid.setPrefWidth(400);
            setContent(grid);
            ImportTask task = new ImportTask(null);
            task.messageProperty().addListener((observable, oldValue, newValue) -> {
                text.appendText(newValue);
                text.setScrollTop(Double.MAX_VALUE);
            });

            task.progressProperty().addListener((observable, oldValue, newValue) -> {
                progressBar.setProgress(newValue.doubleValue());
            });
        }
    }

    class ImportTask extends Task<Void> {
        File folder;

        public ImportTask(File folder) {
            this.folder = folder;
        }

        @Override
        protected Void call() throws Exception {
            updateProgress(0, 1.0);
            File[] files = folder.listFiles();
            for (int i = 0; i < files.length; i++) {
                String filename = files[i].getName();
                if (filename.indexOf("assignsubmission_file") > 0) {
                    String[] tokens = files[i].getName().split("_");

                    File destination = new File(folder.getAbsolutePath() + "/" + tokens[0]);
                    if (!destination.exists()) {
                        if (destination.mkdirs()) {
                            updateMessage("Creating directory " + destination.getPath());
                            String studentName = tokens[0];
                            Main.students.findOrCreate(studentName, destination);
                        } else {
                            updateMessage("Failed to create " + destination.getAbsolutePath() + "\n");
                        }
                    }

                    if (destination.exists()) {
                        File newFile = new File(destination.getAbsolutePath() + "/" + tokens[4]);
                        if (!files[i].renameTo(newFile)) {
                            updateMessage("Failed to rename " + files[i].getName() + "\n");
                        }
                    }
                } else if (files[i].isDirectory()) {
                    String studentName = files[i].getName();
                    Main.students.findOrCreate(studentName, files[i]);
                    updateMessage("Imported " + studentName);
                }
                updateProgress((double) i / (files.length - 1), 1.0);
            }
            return null;
        }
    }

    public ImportDialog() {
        SourcePane sourcePane = new SourcePane();
        WizardPane importPane = new ImportPane();

        setFlow(new LinearFlow(sourcePane, importPane));
    }
}
