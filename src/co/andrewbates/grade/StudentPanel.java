package co.andrewbates.grade;

import java.awt.GridLayout;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;

public class StudentPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private JList<Submission> submissions;
    private LogField logField;

    public StudentPanel() {
        super();
        setLayout(new GridLayout(2, 1));

        submissions = new JList<Submission>();
        submissions.setVisibleRowCount(10);
        add(submissions);

        logField = new LogField("", 25, 0);
        add(logField);
    }

    public void selectStudent(Student student) {
        submissions.setListData(student.getSubmissions());
    }

    public void unselectStudent() {
        submissions.setModel(new DefaultListModel<Submission>());
    }
}
