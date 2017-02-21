package co.andrewbates.grade;

import java.io.File;

import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;

import javafx.fxml.FXMLLoader;

public class ImportWizard extends Wizard {
    private File sourceDirectory;

    public ImportWizard() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/andrewbates/grade/fxml/ImportSelect.fxml"));
        WizardPane importSelectPane = loader.load();
        loader = new FXMLLoader(getClass().getResource("/co/andrewbates/grade/fxml/ImportProgress.fxml"));
        WizardPane importProgressPane = loader.load();
        setFlow(new LinearFlow(importSelectPane, importProgressPane));
    }

    public void setSourceDirectory(File sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
    }

    public File getSourceDirectory() {
        return sourceDirectory;
    }
}
