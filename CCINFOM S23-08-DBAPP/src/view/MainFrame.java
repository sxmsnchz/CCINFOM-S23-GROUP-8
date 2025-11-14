package view;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    private HomePanel homePanel;
    private OfficerLoginPanel officerLoginPanel;
    private OfficerMenuPanel officerMenuPanel;
    private OfficerRecordsPanel officerRecordsPanel;
    private UserLoginPanel userLoginPanel;
    private UserSignUpPanel userSignUpPanel;
    private BranchListPanel branchListPanel;

    public MainFrame() {

        //Setting Icon
        ImageIcon icon = new ImageIcon(getClass().getResource("/assets/Logo.svg_.png"));
        // Get the Image object from the ImageIcon
        Image image = icon.getImage();
        // Set the icon for the JFrame
        setIconImage(image);
        
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
        officerMenuPanel = new OfficerMenuPanel(this);
        officerRecordsPanel = new OfficerRecordsPanel(this);
        userLoginPanel = new UserLoginPanel(this);
        userSignUpPanel = new UserSignUpPanel(this);
        branchListPanel = new BranchListPanel(this);

        // Add panels
        mainPanel.add(homePanel, "home");
        mainPanel.add(officerLoginPanel, "officerLogin");
        mainPanel.add(officerMenuPanel, "officerMenu");
        mainPanel.add(officerRecordsPanel, "officerRecords");
        mainPanel.add(userLoginPanel, "userLogin");
        mainPanel.add(userSignUpPanel, "userSignUp");
        mainPanel.add(branchListPanel, "branchList");
        add(mainPanel);
        setVisible(true);
    }


    // ===== Panel Switching =====
    public void showHome() {
        cardLayout.show(mainPanel, "home");
    }

    public void showUserLogin() {
        cardLayout.show(mainPanel, "userLogin");
    }

    public void showOfficerMenu() {
        cardLayout.show(mainPanel, "officerMenu");
    }

    public void showOfficerRecords() {
        cardLayout.show(mainPanel, "officerRecords");
    }

    public void showOfficerLogin() {
        cardLayout.show(mainPanel, "officerLogin");
    }

    public void showSignUp() {
        cardLayout.show(mainPanel, "userSignUp");
    }

    public void showBranchList() {
        cardLayout.show(mainPanel, "branchList");
    }


    public static void main(String[] args) {
        new MainFrame();
    }
}
