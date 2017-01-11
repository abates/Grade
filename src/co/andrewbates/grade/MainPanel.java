package co.andrewbates.grade;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class MainPanel extends JPanel implements ActionListener, ListSelectionListener {
    private static final long serialVersionUID = 1L;
    private JList<String> studentList;
    private JScrollPane studentsScrollPane;
    private JButton compileButton;
    private JButton compileAllButton;
    private Student selectedStudent;
    private StudentPanel studentPanel;

    public final String ACTION_COMPILE = "compile";
    public final String ACTION_COMPILE_ALL = "compileAll";

    public MainPanel() {
        setLayout(new GridLayout(0, 3));
        setBorder(new EmptyBorder(0, 5, 0, 5));

        studentList = new JList<String>();
        studentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentList.setModel(Main.students.getStudentNames());
        studentList.addListSelectionListener(this);

        studentsScrollPane = new JScrollPane(studentList);
        add(studentsScrollPane);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        compileButton = new JButton("Compile Selected");
        compileButton.addActionListener(this);
        compileButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        compileButton.setActionCommand(ACTION_COMPILE);
        centerPanel.add(compileButton);

        compileAllButton = new JButton("Compile All");
        compileAllButton.addActionListener(this);
        compileAllButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        compileAllButton.setActionCommand(ACTION_COMPILE_ALL);
        centerPanel.add(compileAllButton);

        add(centerPanel);

        studentPanel = new StudentPanel();
        add(studentPanel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
        case ACTION_COMPILE:
            if (selectedStudent == null) {
                JOptionPane.showMessageDialog(this, "A student must first be selected", "Invalid Selection",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            break;
        case ACTION_COMPILE_ALL:
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        selectedStudent = Main.students.find(studentList.getSelectedValue());
        studentPanel.selectStudent(selectedStudent);
    }
}
