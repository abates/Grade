package co.andrewbates.grade;

import java.io.File;
import java.nio.file.Paths;
import java.util.prefs.Preferences;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.SplitPane;
import javafx.scene.control.SplitPane.Divider;
import javafx.stage.Window;

public class GradePreferences {
    public static File workingDirectory() {
        String wd = system().get("workingDirectory", System.getProperty("user.dir"));
        return new File(wd);
    }

    public static void setWorkingDirectory(File file) {
        system().put("workingDirectory", file.getAbsolutePath());
    }

    public static File getTestsDirectory() {
        String wd = system().get("workingDirectory",
                Paths.get(System.getProperty("user.dir"), "testdata", "criteria").toString());
        return new File(wd);
    }

    public static Preferences system() {
        return root().node("system");
    }

    public static Preferences ui(String name) {
        return root().node("ui").node(name);
    }

    public static Preferences root() {
        return Preferences.userRoot().node("co.andrewbates.grade");
    }

    public static void bindWindow(String name, Window window) {
        window.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                ui(name).node("dimensions").putDouble("width", newValue.doubleValue());
            }
        });

        window.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                ui(name).node("dimensions").putDouble("height", newValue.doubleValue());
            }
        });

        window.xProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                ui(name).node("position").putDouble("x", newValue.doubleValue());
            }
        });

        window.yProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                ui(name).node("position").putDouble("y", newValue.doubleValue());
            }
        });

        double width = ui(name).node("dimensions").getDouble("width", 640);
        if (width > 0) {
            window.setWidth(width);
        }

        double height = ui(name).node("dimensions").getDouble("height", 480);
        if (height > 0) {
            window.setHeight(height);
        }

        window.setX(ui(name).node("position").getDouble("x", 1));
        window.setY(ui(name).node("position").getDouble("y", 1));
    }

    public static File importDirectory() {
        String wd = system().get("workingDirectory", System.getProperty("user.dir"));
        String importDirectory = system().get("importDirectory", wd);
        return new File(importDirectory);
    }

    public static void setImportDirectory(File file) {
        system().put("importDirectory", file.getAbsolutePath());
    }

    public static void bindSplitPane(String name, SplitPane splitPane) {
        splitPane.sceneProperty().addListener((o1, oldScene, scene) -> {
            if (scene != null) {
                scene.windowProperty().addListener((o2, oldWindow, window) -> {
                    if (oldWindow == null && window != null) {
                        window.showingProperty().addListener((o, ov, showing) -> {
                            ObservableList<Divider> dividers = splitPane.getDividers();
                            if (showing) {
                                for (int i = 0; i < dividers.size(); i++) {
                                    Divider divider = dividers.get(i);
                                    final Preferences paneNode = ui(name).node("panes").node("" + i);
                                    divider.positionProperty().addListener((observable, oldPosition, newPosition) -> {
                                        paneNode.putDouble("position", newPosition.doubleValue());
                                    });
                                }

                                for (int i = 0; i < dividers.size(); i++) {
                                    splitPane.setDividerPosition(i, ui(name).node("panes").node("" + i)
                                            .getDouble("position", splitPane.getDividerPositions()[i]));
                                }
                            } else {
                                for (int i = 0; i < dividers.size(); i++) {
                                    Preferences paneNode = ui(name).node("panes").node("" + i);
                                    paneNode.putDouble("position", splitPane.getDividerPositions()[i]);
                                }
                            }
                        });
                    }
                });
            }
        });
    }
}
