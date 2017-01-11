package co.andrewbates.grade;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker.StateValue;

public class ImportDialog extends GradeDialog implements ActionListener, PropertyChangeListener {
    private static final long serialVersionUID = 1L;
    private JProgressBar progressBar;
    LogField logField;
    private JButton doneButton;
    private StudentController students;

    public ImportDialog(StudentController students) {
        super("Import");
        this.students = students;

        JPanel mainPanel = new JPanel(new BorderLayout());
        doneButton = new JButton("Done");
        doneButton.setActionCommand("done");
        doneButton.addActionListener(this);

        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);

        logField = new LogField("", 5, 0);

        JPanel panel = new JPanel();
        panel.add(doneButton);
        panel.add(progressBar);

        mainPanel.add(panel, BorderLayout.PAGE_START);
        mainPanel.add(logField, BorderLayout.CENTER);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(mainPanel);
        setModal(true);
        setSize(400, 600);
    }

    public void setVisible(boolean b) {
        if (b == true) {
            JFileChooser chooser = new JFileChooser(GradePreferences.getWorkingDirectory());
            chooser.setDialogTitle("Choose a Folder");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);

            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                doneButton.setEnabled(false);
                progressBar.setValue(0);
                progressBar.setMaximum(100);

                ImportTask task = new ImportTask(chooser.getSelectedFile(), logField, students);
                task.addPropertyChangeListener(this);
                task.execute();
                super.setVisible(true);
            } else {
                super.setVisible(false);
                dispose();
            }
        } else {
            super.setVisible(false);
            dispose();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        dispose();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
        case "progress":
            progressBar.setValue((Integer) evt.getNewValue());
            break;
        case "state":
            StateValue state = (StateValue) evt.getNewValue();
            if (state == StateValue.DONE) {
                progressBar.setValue(100);
                doneButton.setEnabled(true);
            }
            break;
        }
    }
}
