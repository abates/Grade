package co.andrewbates.grade.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import org.hildan.fxgson.FxGson;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

public abstract class BaseModelLoader<T extends Model> implements ModelLoader<T> {
    private ObservableList<T> list;
    private Class<T> modelClass;
    private Path basedir;

    public BaseModelLoader(Class<T> modelClass) {
        this.modelClass = modelClass;
        this.list = FXCollections.observableArrayList();
    }

    private T loadFile(File file) throws DataException {
        T model = null;
        Gson gson = FxGson.create();
        try {
            model = gson.fromJson(new FileReader(file), modelClass);
        } catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
            e.printStackTrace(System.err);
        }
        return model;
    }

    public Task<Void> load(Path basedir) throws DataException {
        this.basedir = basedir;
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                list.clear();
                File[] dirs = basedir.resolve(getPath()).toFile().listFiles();
                for (int i = 0; i < dirs.length; i++) {
                    if (dirs[i].isDirectory()) {
                        File file = dirs[i].toPath().resolve("model.json").toFile();
                        if (file.exists()) {
                            updateMessage("Loading " + dirs[i].getName());
                            list.add(loadFile(file));
                        }
                    }
                    updateProgress(i, dirs.length);
                }
                updateProgress(1.0, 1.0);
                succeeded();
                return null;
            }
        };
    }

    public abstract String getPath();

    @Override
    public String getPath(T object) {
        return String.join(File.separator, getPath(), object.getID().toString());
    }

    public void delete(T object) {
        File file = basedir.resolve(getPath(object)).toFile();
        file.delete();
        list.remove(object);
    }

    public ObservableList<T> list() {
        return list;
    }

    public void create(T object) throws IOException {
        list.add(object);
        save(object);
    }

    @Override
    public void save(T object) throws IOException {
        File dir = basedir.resolve(getPath(object)).toFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file = dir.toPath().resolve("model.json").toFile();
        Gson gson = FxGson.create();
        FileWriter writer = new FileWriter(file);
        writer.write(gson.toJson(object));
        writer.close();
    }
}
