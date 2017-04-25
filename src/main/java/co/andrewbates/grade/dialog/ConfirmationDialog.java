package co.andrewbates.grade.dialog;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class ConfirmationDialog {
    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return the headerText
     */
    public String getHeaderText() {
        return headerText;
    }

    /**
     * @return the contentText
     */
    public String getContentText() {
        return contentText;
    }

    /**
     * @param title
     *            the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @param headerText
     *            the headerText to set
     */
    public void setHeaderText(String headerText) {
        this.headerText = headerText;
    }

    /**
     * @param contentText
     *            the contentText to set
     */
    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    private String title = "";
    private String headerText = "";
    private String contentText = "";

    public static boolean confirmDelete(String message) {
        ConfirmationDialog dialog = new ConfirmationDialog();
        dialog.setTitle("Confirm Delete");
        dialog.setContentText(message);
        return dialog.showAndWait();
    }

    public ConfirmationDialog() {

    }

    public ConfirmationDialog(String title, String headerText, String contentText) {
        setTitle(title);
        setHeaderText(headerText);
        setContentText(contentText);
    }

    public boolean showAndWait() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.OK;
    }
}
