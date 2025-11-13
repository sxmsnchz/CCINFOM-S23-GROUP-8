package view;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    private HomePanel homePanel;
    private OfficerLoginPanel officerLoginPanel;
    // add for user log in etc

    public MainFrame() {

        setTitle("LTO Vehicle Registration Portal");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Panels
        homePanel = new HomePanel(this);
        officerLoginPanel = new OfficerLoginPanel(this);

        // Add panels
        mainPanel.add(homePanel, "home");
        mainPanel.add(officerLoginPanel, "officerLogin");

        add(mainPanel);
        setVisible(true);
    }


    // ===== Panel Switching =====
    public void showHome() {
        cardLayout.show(mainPanel, "home");
    }

    public void showUserLogin() {
        JOptionPane.showMessageDialog(this, "UserLoginPanel not created yet.");
    }

    public void showOfficerLogin() {
        cardLayout.show(mainPanel, "officerLogin");
    }

    public void showSignUp() {
        JOptionPane.showMessageDialog(this, "SignUpPanel not created yet.");
    }


    public static void main(String[] args) {
        new MainFrame();
    }
}
