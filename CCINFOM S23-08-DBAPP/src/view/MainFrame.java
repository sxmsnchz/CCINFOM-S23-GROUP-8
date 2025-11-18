package view;
import java.awt.*;
import javax.swing.*;
import model.Session;

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
    private UserMenuPanel userMenuPanel;
    private ReportsMenuPanel reportsMenuPanel;
    private RegistrationServicePanel registrationServicePanel;
    private UserBranchDirectoryPanel userBranchDirectoryPanel;
    private UserPaymentPanel userPaymentPanel;
    private OutstandingViolationsReportPanel outstandingViolationsReportPanel;
    private ReceiptPanel receiptPanel;
    private int lastPaymentId;
    private ReceiptHistoryPanel receiptHistoryPanel;
    private RegistrationsByBranchReportPanel registrationsByBranchReportPanel;
    private ViolationsByOfficerReportPanel violationsByOfficerReportPanel;
    private ViewRegistrationPanel viewRegistrationPanel;
    private RenewRegistrationPanel renewRegistrationPanel;
    private RecordViolationPanel recordViolationPanel;
    private ViewAllViolationsPanel viewAllViolationsPanel;
    private ViewOwnerListPanel viewOwnerListPanel;
    private UserViolationsPanel userViolationsPanel;
    private PaymentRecordsPanel paymentRecordsPanel;


    public MainFrame() {

        //Setting Icon
        ImageIcon icon = new ImageIcon(getClass().getResource("/assets/Logo.svg_.png"));
        Image image = icon.getImage();
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
        userMenuPanel = new UserMenuPanel(this);
        registrationServicePanel = new RegistrationServicePanel(this);
        reportsMenuPanel = new ReportsMenuPanel(this);
        userBranchDirectoryPanel = new UserBranchDirectoryPanel(this);
        outstandingViolationsReportPanel = new OutstandingViolationsReportPanel(this);
        receiptPanel = new ReceiptPanel(this);
        receiptHistoryPanel = new ReceiptHistoryPanel(this);
        registrationsByBranchReportPanel = new RegistrationsByBranchReportPanel(this);
        violationsByOfficerReportPanel = new ViolationsByOfficerReportPanel(this);
        renewRegistrationPanel = new RenewRegistrationPanel(this);
        viewAllViolationsPanel = new ViewAllViolationsPanel(this);
        recordViolationPanel = new RecordViolationPanel(this);
        viewOwnerListPanel = new ViewOwnerListPanel(this);
        userViolationsPanel = new UserViolationsPanel(this);
        //  DO NOT create userPaymentPanel here plz


        // Add panels
        mainPanel.add(homePanel, "home");
        mainPanel.add(officerLoginPanel, "officerLogin");
        mainPanel.add(officerMenuPanel, "officerMenu");
        mainPanel.add(officerRecordsPanel, "officerRecords");
        mainPanel.add(userLoginPanel, "userLogin");
        mainPanel.add(userSignUpPanel, "userSignUp");
        mainPanel.add(branchListPanel, "branchList");
        mainPanel.add(userMenuPanel, "userMenu");
        mainPanel.add(registrationServicePanel, "register");
        mainPanel.add(reportsMenuPanel, "reportsMenu");
        mainPanel.add(userBranchDirectoryPanel, "userBranchDirectory");
        mainPanel.add(outstandingViolationsReportPanel, "outstandingViolationsReport");
        mainPanel.add(receiptPanel, "receipt");
        mainPanel.add(receiptHistoryPanel, "receiptHistory");
        mainPanel.add(registrationsByBranchReportPanel, "registrationsByBranchReport");
        mainPanel.add(violationsByOfficerReportPanel, "violationsByOfficerReport");
        mainPanel.add(renewRegistrationPanel, "renewRegistration");
        mainPanel.add(viewAllViolationsPanel, "viewAllViolations");
        mainPanel.add(recordViolationPanel, "recordViolation");
        mainPanel.add(viewOwnerListPanel, "viewOwnerList");
        mainPanel.add(userViolationsPanel, "userViolations");
        //  DO NOT create userPaymentPanel here plz

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

    public void showUserMenu() {
        userMenuPanel.setOwnerName(Session.loggedInOwnerId);
        cardLayout.show(mainPanel, "userMenu");
    }

    public void showRegistrationPanel() {
        cardLayout.show(mainPanel, "register");
    }

    public void showReportsMenu() {
        cardLayout.show(mainPanel, "reportsMenu");
    }

    public void showUserBranchDirectory() {
        cardLayout.show(mainPanel, "userBranchDirectory");
    }

    public void showUserPayment() { // DO NOT CHANGE !!
        System.out.println("[MainFrame] showUserPayment() called.");
        System.out.println("[MainFrame] Session.loggedInOwnerId = " + Session.loggedInOwnerId);

        if (userPaymentPanel == null) {
            System.out.println("[MainFrame] Creating UserPaymentPanel for the first time.");
            userPaymentPanel = new UserPaymentPanel(this);
            mainPanel.add(userPaymentPanel, "userPayment");
        }

        userPaymentPanel.refreshData();
        cardLayout.show(mainPanel, "userPayment");
    }

    public void showOutstandingViolationsReport() {
        cardLayout.show(mainPanel, "outstandingViolationsReport");
    }

    public void showReceipt(int paymentId) {
        System.out.println("[MainFrame] showReceipt(" + paymentId + ")");

        this.lastPaymentId = paymentId;
        receiptPanel.loadReceipt(paymentId);

        cardLayout.show(mainPanel, "receipt");
    }

    public void showReceiptHistory() {
        receiptHistoryPanel.loadReceipts();
        cardLayout.show(mainPanel, "receiptHistory");
    }

    public void showRegistrationsByBranchReport() {
        cardLayout.show(mainPanel, "registrationsByBranchReport");
    }

    public void showViolationsByOfficerReportPanel() {
        cardLayout.show(mainPanel, "violationsByOfficerReport");
    }

    public void createAndShowViewRegistrationPanel() {
        if (viewRegistrationPanel == null) {
            viewRegistrationPanel = new ViewRegistrationPanel(this);
            mainPanel.add(viewRegistrationPanel, "viewRegistration");
            mainPanel.revalidate();
            mainPanel.repaint();
        }

        viewRegistrationPanel.loadVehicleList();
        showViewRegistrationsPanel();
    }

        public void showViewRegistrationsPanel(){
        cardLayout.show(mainPanel, "viewRegistration");
    }

    public void showRenewRegistration(){
        cardLayout.show(mainPanel, "renewRegistration");
    }

    public void showRecordViolation() {
        cardLayout.show(mainPanel, "recordViolation");
    }

    public void showAllViolations() {
        viewAllViolationsPanel.loadViolations();
        cardLayout.show(mainPanel, "viewAllViolations");
    }

    public void showOwnerList() {
        viewOwnerListPanel.loadOwners();
        cardLayout.show(mainPanel, "viewOwnerList");
    }

    public void showUserViolations() {
        userViolationsPanel.loadViolations();
        cardLayout.show(mainPanel, "userViolations");
    }

    public void showPaymentRecords() {
        if (paymentRecordsPanel == null) {
            paymentRecordsPanel = new PaymentRecordsPanel(this);
            mainPanel.add(paymentRecordsPanel, "paymentRecords");
        }
        paymentRecordsPanel.reloadTable();
        cardLayout.show(mainPanel, "paymentRecords");
    }


    public static void main(String[] args) {
        new MainFrame();
    }
}
