package co.andrewbates.grade;

import java.io.File;
import java.nio.file.Paths;
import java.util.prefs.Preferences;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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

    public static Preferences ui(Window window) {
        return root().node("ui").node(window.getClass().getName());
    }

    public static Preferences root() {
        return Preferences.userRoot().node("co.andrewbates.grade");
    }

    public static void bindUIPreferences(Window window) {
        window.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                ui(window).node("dimensions").putInt("width", newValue.intValue());
            }
        });

        window.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                ui(window).node("dimensions").putInt("height", newValue.intValue());
            }
        });

        window.xProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                ui(window).node("position").putInt("x", newValue.intValue());
            }
        });

        window.yProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                ui(window).node("position").putInt("y", newValue.intValue());
            }
        });

        window.setWidth(ui(window).node("dimension").getInt("width", 500));
        window.setHeight(ui(window).node("dimension").getInt("height", 400));

        window.setX(ui(window).node("position").getInt("x", 1));
        window.setY(ui(window).node("position").getInt("y", 1));
    }

    public static File importDirectory() {
        String wd = system().get("workingDirectory", System.getProperty("user.dir"));
        String importDirectory = system().get("importDirectory", wd);
        return new File(importDirectory);
    }

    public static void setImportDirectory(File file) {
        system().put("importDirectory", file.getAbsolutePath());
    }
}
