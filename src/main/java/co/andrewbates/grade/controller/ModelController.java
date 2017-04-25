package co.andrewbates.grade.controller;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicBoolean;

import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import co.andrewbates.grade.data.Model;
import javafx.application.Platform;
import javafx.scene.control.Control;

public abstract class ModelController<T extends Model> {
    private ValidationSupport validationSupport = new ValidationSupport();

    public boolean isValid() {
        try {
            Field field = ValidationSupport.class.getDeclaredField("dataChanged");
            field.setAccessible(true);
            AtomicBoolean dataChanged = (AtomicBoolean) field.get(validationSupport);
            dataChanged.set(true);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        validationSupport.redecorate();
        ValidationResult result = validationSupport.getValidationResult();
        return result.getErrors().size() == 0;
    }

    public abstract void setModel(T t);

    public abstract T getModel();

    protected void registerValidator(Control control, Validator<?> validator) {
        Platform.runLater(() -> {
            validationSupport.registerValidator(control, false, validator);
        });
    }
}
