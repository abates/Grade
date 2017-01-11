package co.andrewbates.grade;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import co.andrewbates.grade.rubric.Score;

public class StudentPanel extends JPanel implements ListSelectionListener {
    private static final long serialVersionUID = 1L;
    private static final String[] criteriaColumns = new String[] { "Criteria", "Result" };
    private DefaultTableModel criteria;
    private JTable criteriaTable;
    private JTextArea log;
    private Student selectedStudent;

    public StudentPanel() {
        super();
        setLayout(new GridLayout(2, 1));
        criteria = new DefaultTableModel(criteriaColumns, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        criteriaTable = new JTable(criteria);
        criteriaTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        criteriaTable.getSelectionModel().addListSelectionListener(this);
        JScrollPane scrollPane = new JScrollPane(criteriaTable);
        criteriaTable.setFillsViewportHeight(true);
        add(scrollPane);

        log = new JTextArea();
        scrollPane = new JScrollPane();
        scrollPane.setViewportView(log);
        add(scrollPane);
    }

    public void selectStudent(Student student) {
        this.selectedStudent = student;
        criteria.setRowCount(0);
        for (Score score : student.getScores()) {
            criteria.addRow(new Object[] { score.getName(), score.getScore() });
        }
    }

    public void unselectStudent() {
        criteria.setRowCount(0);
        log.setText("");
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        int row = criteriaTable.getSelectedRow();
        if (e.getValueIsAdjusting() && row >= 0) {
            String criteriaName = (String) criteria.getValueAt(row, 0);
            log.setText(selectedStudent.getScore(criteriaName).getMessage());
        }
    }
}
