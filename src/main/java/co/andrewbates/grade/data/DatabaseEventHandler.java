package co.andrewbates.grade.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;

public class DatabaseEventHandler {
    public static final EventType<DatabaseEvent> DELETE = new EventType<>(ActionEvent.ACTION, "DeleteModel");
    public static final EventType<DatabaseEvent> CREATE = new EventType<>(ActionEvent.ACTION, "CreateModel");
    public static final EventType<DatabaseEvent> SAVE = new EventType<>(ActionEvent.ACTION, "SaveModel");

    public static class DatabaseEvent extends ActionEvent {
        private static final long serialVersionUID = 1L;
        private Model model;

        public DatabaseEvent(Model model) {
            this.model = model;
        }

        public Model getModel() {
            return model;
        }
    }

    HashMap<EventType<DatabaseEvent>, List<EventHandler<DatabaseEvent>>> handlers = new HashMap<>();

    public void register(EventType<DatabaseEvent> type, EventHandler<DatabaseEvent> handler) {
        if (handler == null) {
            throw new NullPointerException();
        }

        List<EventHandler<DatabaseEvent>> handlers = this.handlers.get(type);
        if (handlers == null) {
            handlers = new ArrayList<EventHandler<DatabaseEvent>>();
            this.handlers.put(type, handlers);
        }
        handlers.add(handler);
    }

    public void fire(EventType<?> type, Model target) {
        DatabaseEvent event = new DatabaseEvent(target);
        List<EventHandler<DatabaseEvent>> h = handlers.get(type);
        if (h != null) {
            for (EventHandler<DatabaseEvent> handler : h) {
                handler.handle(event);
            }
        }
    }

}
