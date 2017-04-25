package co.andrewbates.grade;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.SplitPane;
import javafx.scene.control.SplitPane.Divider;
import javafx.stage.Window;

public class GradePreferences {
    private File propertyFile;
    private Properties props;
    private long lastUpdate;

    public GradePreferences(File inputFile) throws IOException {
        this.propertyFile = inputFile;
        props = new Properties();

        try {
            props.load(new FileInputStream(inputFile));
        } catch (FileNotFoundException e) {
            System.err.println("Warning: No preferences file found: " + inputFile.getAbsolutePath());
        }
    }

    private synchronized void setProperty(String key, String value) {
        props.setProperty(key, value);
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdate > 1000) {
            try {
                props.store(new FileOutputStream(propertyFile), null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            lastUpdate = currentTime;
        }
    }

    private String getProperty(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    };

    public void bindWindow(String name, Window window) {
        window.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                setProperty("ui." + name + ".dimensions.width", newValue.toString());
            }
        });

        window.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                setProperty("ui." + name + ".dimensions.height", newValue.toString());
            }
        });

        window.xProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                setProperty("ui." + name + ".position.x", newValue.toString());
            }
        });

        window.yProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                setProperty("ui." + name + ".position.y", newValue.toString());
            }
        });

        double width = Double.valueOf(getProperty("ui." + name + ".dimensions.width", "640"));
        if (width > 0) {
            window.setWidth(width);
        }

        double height = Double.valueOf(getProperty("ui." + name + ".dimensions.height", "640"));
        if (height > 0) {
            window.setHeight(height);
        }

        double x = Double.valueOf(getProperty("ui." + name + "position.x", "1"));
        double y = Double.valueOf(getProperty("ui." + name + "position.y", "1"));
        window.setX(x);
        window.setY(y);
    }

    public File importDirectory() {
        String wd = getProperty("system.workingDirectory", System.getProperty("user.dir"));
        String importDirectory = getProperty("system.importDirectory", wd);
        return new File(importDirectory);
    }

    public void setImportDirectory(File file) {
        setProperty("system.importDirectory", file.getAbsolutePath());
    }

    public void bindSplitPane(String name, SplitPane splitPane) {
        splitPane.sceneProperty().addListener((o1, oldScene, scene) -> {
            if (scene != null) {
                scene.windowProperty().addListener((o2, oldWindow, window) -> {
                    if (oldWindow == null && window != null) {
                        window.showingProperty().addListener((o, ov, showing) -> {
                            ObservableList<Divider> dividers = splitPane.getDividers();
                            if (showing) {
                                for (int i = 0; i < dividers.size(); i++) {
                                    Divider divider = dividers.get(i);
                                    final String path = "ui." + name + ".panes." + i + ".position";
                                    divider.positionProperty().addListener((observable, oldPosition, newPosition) -> {
                                        setProperty(path, newPosition.toString());
                                    });

                                    double position = Double.valueOf(getProperty(path, "0.5"));
                                    splitPane.setDividerPosition(i, position);
                                }
                            } else {
                                for (int i = 0; i < dividers.size(); i++) {
                                    String path = "ui." + name + ".panes." + i + ".position";
                                    setProperty(path, "" + splitPane.getDividerPositions()[i]);
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    private static Path getPath(Path path) {
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return path;
    }

    public static Path dataDirectory() {
        return getPath(new File(System.getProperty("user.home")).toPath().resolve(".grade"));
    }
}
