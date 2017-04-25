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
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Stream;

import org.hildan.fxgson.FxGson;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

public class ModelLoader<T extends Model> {
    class LoadTask extends Task<ObservableList<T>> {
        private ObservableList<T> collection = FXCollections.observableArrayList();

        public LoadTask() {
            updateValue(collection);
        }

        @Override
        protected ObservableList<T> call() throws Exception {
            File[] files = getPath().toFile().listFiles();
            for (int i = 0; i < files.length; i++) {
                collection.add(load(files[i]));
                updateProgress(i, files.length);
            }
            updateProgress(1.0, 1.0);
            succeeded();
            return collection;
        }

    }

    private Class<T> modelClass;
    private Path basePath;

    public ModelLoader(Class<T> modelClass, Path basePath) {
        this.modelClass = modelClass;
        this.basePath = basePath;
    }

    public T get(UUID id) throws IOException {
        return load(getPath(id).toFile());
    }

    private T load(File file) {
        if (file.isDirectory()) {
            file = file.toPath().resolve("model.json").toFile();
        }

        if (!file.exists()) {
            return null;
        }

        T model = null;
        Gson gson = FxGson.create();
        try {
        	FileReader reader = new FileReader(file);
            model = gson.fromJson(reader, modelClass);
            reader.close();
        } catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
            // TODO this needs to throw something
            System.err.println("Failed parsing " + file);
            e.printStackTrace(System.err);
        } catch (IOException e) {
        	// we don't care if close() caused an IOException
        }
        return model;
    }

    public Class<T> modelClass() {
        return modelClass;
    }

    void setPath(Path path) {
        this.basePath = path;
    }

    public Path getPath() throws IOException {
        Path path = this.basePath;
        if (modelClass.isAnnotationPresent(Info.class)) {
            Info info = modelClass.getAnnotation(Info.class);
            if (!"".equals(info.path())) {
                path = path.resolve(info.path());
            }
        } else {
            path = path.resolve(modelClass.getSimpleName());
        }

        if (!Files.exists(path)) {
            Files.createDirectory(path);
        }
        return path;
    }

    public Path getPath(T object) throws IOException {
        return getPath(object.getID());
    }

    public Path getPath(UUID id) throws IOException {
        return getPath().resolve(id.toString());
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
    }

    public Stream<T> stream() throws IOException {
        return Arrays.asList(getPath().toFile().listFiles()).stream().filter(f -> {
            return f.isDirectory();
        }).map(f -> load(f));
    }

    public Task<ObservableList<T>> allTask() {
        Task<ObservableList<T>> t = new LoadTask();
        new Thread(t).start();
        return t;
    }

    public ObservableList<T> all() {
        Task<ObservableList<T>> t = allTask();
        return t.getValue();
    }

    public void save(T object) throws IOException {
        if (object.getID() == null) {
            object.setID(UUID.randomUUID());
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
