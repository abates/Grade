package co.andrewbates.grade;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

public class Main extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;
    MainPanel mainPanel;

    static final StudentController students = new StudentController();

    public Main(String title) {
        super(title);

        MenuBar menuBar = new MenuBar();
        setJMenuBar(menuBar);
        menuBar.addActionListener(this);

        mainPanel = new MainPanel();
        setContentPane(mainPanel);
        pack();
    }

    public static void main(String[] args) {
        Main main = new Main("Program Grader");
        main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        main.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
        case MenuBar.ACTION_IMPORT_FOLDER:
            ImportDialog process = new ImportDialog(students);
            process.setVisible(true);
            break;
        case MenuBar.ACTION_LOAD_TESTS:
            break;
        }
    }
}
