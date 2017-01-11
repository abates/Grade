package co.andrewbates.grade;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class MenuBar extends JMenuBar implements ActionListener {
    private ArrayList<ActionListener> actionListeners = new ArrayList<ActionListener>();
    private static final long serialVersionUID = 1L;

    public static final String ACTION_IMPORT_FOLDER = "importFolder";
    public static final String ACTION_LOAD_TESTS = "loadTests";

    public MenuBar() {
        JMenu fileMenu = new JMenu("File");
        JMenuItem folderItem = new JMenuItem("Import Folder");
        folderItem.setActionCommand(ACTION_IMPORT_FOLDER);
        folderItem.addActionListener(this);

        JMenuItem loadItem = new JMenuItem("Load Tests");
        loadItem.setActionCommand(ACTION_LOAD_TESTS);
        loadItem.addActionListener(this);

        fileMenu.add(folderItem);
        fileMenu.add(loadItem);
        add(fileMenu);
    }

    public void addActionListener(ActionListener listener) {
        actionListeners.add(listener);
    }

    public void removeActionListner(ActionListener listener) {
        actionListeners.remove(listener);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (ActionListener listener : actionListeners) {
            listener.actionPerformed(e);
        }
    }
}
