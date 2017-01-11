package co.andrewbates.grade;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.prefs.Preferences;

import javax.swing.JDialog;

public class GradeDialog extends JDialog implements ComponentListener {
    private static final long serialVersionUID = 1L;

    public GradeDialog(String title) {
        super();
        setTitle(title);
        addComponentListener(this);
        setSize(prefs().node("dimension").getInt("width", 500), prefs().node("dimension").getInt("height", 400));

        setLocation(prefs().node("position").getInt("x", 1), prefs().node("position").getInt("y", 1));
    }

    protected Preferences prefs() {
        return GradePreferences.ui().node(getClass().getName());
    }

    @Override
    public void componentResized(ComponentEvent e) {
        prefs().node("dimension").putInt("width", getWidth());
        prefs().node("dimension").putInt("height", getHeight());
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        prefs().node("position").putInt("x", getX());
        prefs().node("position").putInt("y", getY());
    }

    @Override
    public void componentShown(ComponentEvent e) {
    }

    @Override
    public void componentHidden(ComponentEvent e) {
    }

}