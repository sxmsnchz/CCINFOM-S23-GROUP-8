package view;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    // Panels
    private HomePanel homePanel;
    private OfficerLoginPanel officerLoginPanel;
    private UserLoginPanel userLoginPanel;
    private UserSignUpPanel signUpPanel;

    public MainFrame() {

        setTitle("LTO Vehicle Registration Portal");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // CardLayout container
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Create panels + pass this MainFrame
        homePanel = new HomePanel(this);
        officerLoginPanel = new OfficerLoginPanel(this);
        userLoginPanel = new UserLoginPanel(this);
        signUpPanel = new UserSignUpPanel(this);

        // Add them
        mainPanel.add(homePanel, "home");
        mainPanel.add(officerLoginPanel, "officerLogin");
        mainPanel.add(userLoginPanel, "userLogin");
        mainPanel.add(signUpPanel, "signUp");

        add(mainPanel);
        setVisible(true);
    }

    // Panel switching methods
    public void showHome() {
        cardLayout.show(mainPanel, "home");
    }

    public void showOfficerLogin() {
        cardLayout.show(mainPanel, "officerLogin");
    }

    public void showUserLogin() {
        cardLayout.show(mainPanel, "userLogin");
    }

    public void showSignUp() {
        cardLayout.show(mainPanel, "signUp");
    }

    public static void main(String[] args) {
        new MainFrame();
    }
}
