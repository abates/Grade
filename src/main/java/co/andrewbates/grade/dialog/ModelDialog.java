package co.andrewbates.grade.dialog;

import java.io.IOException;
import java.util.Optional;

import co.andrewbates.grade.Main;
import co.andrewbates.grade.controller.ModelController;
import co.andrewbates.grade.data.Info;
import co.andrewbates.grade.data.Model;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

public class ModelDialog<T extends Model> {
    private Dialog<T> dialog = new Dialog<>();
    private ModelController<T> controller;

    private static String FXML_DIR = "/co/andrewbates/grade/fxml";

    public ModelDialog(T model) {
        String path = FXML_DIR + "/" + model.getClass().getSimpleName() + ".fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(path));

        try {
            dialog.getDialogPane().setContent(loader.load());
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        controller = loader.getController();
        controller.setModel(model);

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setHeaderText(model.getClass().getSimpleName());

        if (model.getClass().isAnnotationPresent(Info.class)) {
            Info info = model.getClass().getAnnotation(Info.class);
            if (!"".equals(info.name())) {
                dialog.setContentText(info.name());
            }

            if (!"".equals(info.icon())) {
                FontAwesomeIconView icon = new FontAwesomeIconView();
                icon.setGlyphName(info.icon());
                dialog.setGraphic(icon);
            }
        }

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK && controller.isValid()) {
                return controller.getModel();
            }
            return null;
        });

        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.addEventFilter(ActionEvent.ACTION, event -> {
            if (!controller.isValid()) {
                event.consume();
            }
        });
    }

    public T showAndWait() throws IOException {
        T model = null;
        Optional<T> optional = dialog.showAndWait();
        if (optional.isPresent()) {
            model = optional.get();
            Main.database.save(model);
        }
        return model;
    }
}
