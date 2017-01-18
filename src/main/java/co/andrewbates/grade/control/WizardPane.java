package co.andrewbates.grade.control;

import org.controlsfx.dialog.Wizard;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;

public class WizardPane extends org.controlsfx.dialog.WizardPane {
    public static class EnteringEvent extends ActionEvent {
        private static final long serialVersionUID = 1L;
        public static final EventType<EnteringEvent> ACTION = new EventType<EnteringEvent>(ActionEvent.ACTION,
                "entering");
        private Wizard wizard;

        public EnteringEvent(Wizard wizard) {
            this.wizard = wizard;
        }

        public Wizard getWizard() {
            return wizard;
        }

        @Override
        public EventType<? extends ActionEvent> getEventType() {
            return ACTION;
        }
    }

    public static class ExitingEvent extends ActionEvent {
        private static final long serialVersionUID = 1L;
        public static final EventType<ExitingEvent> ACTION = new EventType<ExitingEvent>(ActionEvent.ACTION, "exiting");
        private Wizard wizard;

        public ExitingEvent(Wizard wizard) {
            this.wizard = wizard;
        }

        public Wizard getWizard() {
            return wizard;
        }

        @Override
        public EventType<? extends ActionEvent> getEventType() {
            return ACTION;
        }
    }

    ObjectProperty<EventHandler<EnteringEvent>> onEntering;
    ObjectProperty<EventHandler<ExitingEvent>> onExiting;

    public WizardPane() {
        super();
        onEntering = new SimpleObjectProperty<EventHandler<EnteringEvent>>(this, "onEntering") {
            @Override
            protected void invalidated() {
                setEventHandler(EnteringEvent.ACTION, onEntering.get());
            }
        };

        onExiting = new SimpleObjectProperty<EventHandler<ExitingEvent>>(this, "onExiting") {
            @Override
            protected void invalidated() {
                setEventHandler(ExitingEvent.ACTION, onExiting.get());
            }
        };
    }

    public ObjectProperty<EventHandler<EnteringEvent>> onEnteringProperty() {
        return onEntering;
    }

    public EventHandler<EnteringEvent> getOnEntering() {
        return onEnteringProperty().get();
    }

    public void setOnEntering(EventHandler<EnteringEvent> value) {
        onEnteringProperty().set(value);
    }

    public ObjectProperty<EventHandler<ExitingEvent>> onExitingProperty() {
        return onExiting;
    }

    public EventHandler<ExitingEvent> getOnExiting() {
        return onExitingProperty().get();
    }

    public void setOnExiting(EventHandler<ExitingEvent> value) {
        onExitingProperty().set(value);
    }

    @Override
    public void onEnteringPage(Wizard wizard) {
        fireEvent(new EnteringEvent(wizard));
    }

    @Override
    public void onExitingPage(Wizard wizard) {
        fireEvent(new ExitingEvent(wizard));
    }
}
