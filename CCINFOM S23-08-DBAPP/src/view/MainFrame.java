package view;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    private HomePanel homePanel;

    public MainFrame() {

        setTitle("LTO Vehicle Registration Portal");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // CardLayout container
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Only HomePanel for now
        homePanel = new HomePanel(this);
        mainPanel.add(homePanel, "home");

        add(mainPanel);
        setVisible(true);
    }

    // Stub methods (will work once panels are added)
    public void showHome() {
        cardLayout.show(mainPanel, "home");
    }

    public void showUserLogin() {
        JOptionPane.showMessageDialog(this, "UserLoginPanel not created yet.");
    }

    public void showOfficerLogin() {
        JOptionPane.showMessageDialog(this, "OfficerLoginPanel not created yet.");
    }

    public void showSignUp() {
        JOptionPane.showMessageDialog(this, "SignUpPanel not created yet.");
    }

    public static void main(String[] args) {
        new MainFrame();
    }
}

