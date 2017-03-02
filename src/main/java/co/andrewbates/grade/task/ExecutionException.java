package co.andrewbates.grade.task;

import java.util.List;
import java.util.stream.Collectors;

public class ExecutionException extends Exception {
    private static final long serialVersionUID = 1L;
    private List<Throwable> throwables;

    public ExecutionException(List<Throwable> throwables) {
        super();
        this.throwables = throwables;
    }

    @Override
    public String getMessage() {
        List<String> messages = throwables.stream().map(i -> i.toString()).collect(Collectors.toList());
        return String.join("\n", messages);
    }
}
