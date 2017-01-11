package co.andrewbates.grade;

import java.io.File;
import java.nio.file.Paths;
import java.util.prefs.Preferences;

public class GradePreferences {
    public static File getWorkingDirectory() {
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

    public static Preferences ui() {
        return root().node("ui");
    }

    public static Preferences root() {
        return Preferences.userRoot().node("co.andrewbates.grade");
    }
}
