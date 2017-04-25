package co.andrewbates.grade.controller;

import co.andrewbates.grade.data.Model;

public abstract class ModelController<T extends Model> {

    public abstract boolean isValid();

    public abstract void setModel(T t);

    public abstract T getModel();

}
