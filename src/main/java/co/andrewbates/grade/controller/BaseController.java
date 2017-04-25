package co.andrewbates.grade.controller;

import javafx.scene.control.Button;
import javafx.scene.control.TableView;

public class BaseController {

    public BaseController() {
        super();
    }

    protected void initializeTable(TableView<?> table, TableView<?> dependentTable, Button... buttons) {
        for (Button button : buttons) {
            button.setDisable(true);
        }

        table.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> {
            if (nv != null) {
                for (Button button : buttons) {
                    button.setDisable(false);
                }
            } else {
                if (dependentTable != null) {
                    dependentTable.setItems(null);
                }

                for (Button button : buttons) {
                    button.setDisable(true);
                }
            }
        });

    }

}