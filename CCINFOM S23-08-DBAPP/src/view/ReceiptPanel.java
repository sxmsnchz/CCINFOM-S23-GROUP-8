package view;

import database.DatabaseConnection;
import model.Session;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ReceiptPanel extends JPanel {

    private final MainFrame mainFrame;

    private int paymentId;

    private JLabel lblReceiptNum;
    private JLabel lblTransactionType;
    private JLabel lblAmountPaid;
    private JLabel lblDateIssued;
    private JLabel lblOfficer;
    private JLabel lblBranch;
    private JLabel lblPlate;

    public ReceiptPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new GridBagLayout());
        setBackground(new Color(245, 245, 245));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(30, 40, 40, 40));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(420, 500));

        JLabel title = new JLabel("LTO OFFICIAL RECEIPT", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblReceiptNum = createLabel();
        lblTransactionType = createLabel();
        lblAmountPaid = createLabel();
        lblDateIssued = createLabel();
        lblOfficer = createLabel();
        lblBranch = createLabel();
        lblPlate = createLabel();

        JButton doneBtn = new JButton("Done");
        doneBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        doneBtn.setBackground(new Color(0, 90, 200));
        doneBtn.setForeground(Color.white);
        doneBtn.setFocusPainted(false);
        doneBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        doneBtn.addActionListener(e -> mainFrame.showUserMenu());

        card.add(title);
        card.add(Box.createVerticalStrut(20));

        card.add(lblReceiptNum);
        card.add(lblTransactionType);
        card.add(lblPlate);
        card.add(lblAmountPaid);
        card.add(lblDateIssued);
        card.add(lblOfficer);
        card.add(lblBranch);

        card.add(Box.createVerticalStrut(25));
        card.add(doneBtn);

        add(card);
    }

    private JLabel createLabel() {
        JLabel l = new JLabel(" ");
        l.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        return l;
    }


    // =====================================================================================
    // LOAD RECEIPT DATA
    // =====================================================================================
    public void loadReceipt(int paymentId) {
        this.paymentId = paymentId;

        try (Connection conn = DatabaseConnection.getConnection()) {

            String sql = """
                SELECT 
                    r.receipt_number,
                    r.issue_date,
                    p.payment_type,
                    p.amount_paid,
                    p.owner_id,
                    p.date_paid,
                    p.branch_id,
                    p.officer_id,
                    b.branch_name,
                    o.first_name AS officer_fn,
                    o.last_name AS officer_ln,
                    v.plate_number
                FROM receipt r
                JOIN payment p ON r.payment_id = p.payment_id
                JOIN branch b ON p.branch_id = b.branch_id
                JOIN officer o ON p.officer_id = o.officer_id
                LEFT JOIN registration reg ON reg.registration_id = p.payment_id
                LEFT JOIN vehicle v ON reg.vehicle_id = v.vehicle_id
                WHERE r.payment_id = ?;
                """;

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, paymentId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String officerName = rs.getString("officer_ln") + ", " + rs.getString("officer_fn");
                lblReceiptNum.setText("Receipt Number: " + rs.getString("receipt_number"));
                lblTransactionType.setText("Transaction: " + rs.getString("payment_type"));
                lblAmountPaid.setText("Amount Paid: ₱" + rs.getDouble("amount_paid"));
                lblDateIssued.setText("Date Issued: " + rs.getDate("issue_date"));
                lblOfficer.setText("Processed By: " + officerName);
                lblBranch.setText("Branch: " + rs.getString("branch_name"));

                String plate = rs.getString("plate_number");
                if (plate != null) {
                    lblPlate.setText("Plate Number: " + plate);
                } else {
                    lblPlate.setText("Plate Number: N/A");
                }
            } else {
                lblReceiptNum.setText("Error loading receipt.");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            lblReceiptNum.setText("Error loading data.");
        }
    }
}

