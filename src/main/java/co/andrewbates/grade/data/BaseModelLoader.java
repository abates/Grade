package co.andrewbates.grade.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.UUID;

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
    private Path path;
    private HashMap<UUID, T> index;

    public BaseModelLoader(Class<T> modelClass) {
        this.modelClass = modelClass;
        this.list = FXCollections.observableArrayList();
        this.index = new HashMap<UUID, T>();
    }

    protected void initialize(T model) {

    }

    public T get(UUID id) {
        return index.get(id);
    }

    T load(File file) throws DataException {
        if (file.isDirectory()) {
            file = file.toPath().resolve("model.json").toFile();
        }

        if (!file.exists()) {
            return null;
        }

        T model = null;
        Gson gson = FxGson.create();
        try {
            model = gson.fromJson(new FileReader(file), modelClass);
            initialize(model);
            list.add(model);
            index.put(model.getID(), model);
        } catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
            // TODO this needs to throw something
            e.printStackTrace(System.err);
        }
        return model;
    }

    T load(Path path) throws DataException {
        return load(path.toFile());
    }

    T load(String name) throws DataException {
        File file = getPath().resolve(name).toFile();
        if (file.exists()) {
            return load(file);
        }
        return null;
    }

    public Task<Void> loadAll(Path path) throws DataException {
        setPath(path);
        return loadAll();
    }

    public Task<Void> loadAll() throws DataException {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                list.clear();
                File[] files = getPath().toFile().listFiles();
                for (int i = 0; i < files.length; i++) {
                    load(files[i]);
                    updateProgress(i, files.length);
                }
                updateProgress(1.0, 1.0);
                succeeded();
                return null;
            }
        };
    }

    void setPath(Path path) {
        this.path = path;
    }

    public Path getPath() {
        return path;
    }

    @Override
    public Path getPath(T object) {
        return getPath().resolve(object.getID().toString());
    }

    public void delete(T object) throws IOException {
        Files.walkFileTree(getPath(object), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
        list.remove(object);
    }

    public ObservableList<T> list() {
        return list;
    }

    @Override
    public void save(T object) throws IOException {
        if (object.getID() == null) {
            object.setID(UUID.randomUUID());
            list.add(object);
            FXCollections.sort(list);
        }

        File dir = getPath(object).toFile();
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
