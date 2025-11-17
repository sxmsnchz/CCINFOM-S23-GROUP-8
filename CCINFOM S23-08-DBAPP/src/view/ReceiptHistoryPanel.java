package view;

import database.DatabaseConnection;
import model.Session;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ReceiptHistoryPanel extends JPanel {

    private final MainFrame mainFrame;
    private JPanel listContainer;

    public ReceiptHistoryPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout());
        setBackground(new Color(243, 246, 251));

        // TOP BAR
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        topBar.setBackground(new Color(243, 246, 251));

        JButton backBtn = new JButton("← Back");
        styleSecondary(backBtn);
        backBtn.addActionListener(e -> mainFrame.showUserMenu());
        topBar.add(backBtn);

        JLabel title = new JLabel("Transaction History");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(20, 50, 100));
        topBar.add(title);

        add(topBar, BorderLayout.NORTH);

        // LIST AREA
        listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        listContainer.setOpaque(false);

        JScrollPane scroll = new JScrollPane(listContainer);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        add(scroll, BorderLayout.CENTER);
    }

    // Load receipts whenever the panel is opened
    public void loadReceipts() {
        listContainer.removeAll();

        try (Connection conn = DatabaseConnection.getConnection()) {

            String sql = """
                SELECT
                    r.receipt_number,
                    r.issue_date,
                    r.payment_id,
                    p.payment_type,
                    p.amount_paid,
                    b.branch_name,
                    o.first_name AS officer_fn,
                    o.last_name AS officer_ln,

                    COALESCE(
                        (SELECT ve.plate_number FROM violation v
                         JOIN vehicle ve ON ve.vehicle_id=v.vehicle_id
                         WHERE v.payment_id=p.payment_id LIMIT 1),
                         
                        (SELECT ve.plate_number FROM registration reg
                         JOIN vehicle ve ON ve.vehicle_id=reg.vehicle_id
                         WHERE reg.payment_id=p.payment_id LIMIT 1),

                        (SELECT ve.plate_number FROM renewal re
                         JOIN registration reg2 ON reg2.registration_id=re.registration_id
                         JOIN vehicle ve ON ve.vehicle_id=reg2.vehicle_id
                         WHERE re.payment_id=p.payment_id LIMIT 1),

                        'N/A'
                    ) AS plate_number
                FROM receipt r
                JOIN payment p ON r.payment_id = p.payment_id
                JOIN branch b ON p.branch_id = b.branch_id
                JOIN officer o ON p.officer_id = o.officer_id
                WHERE p.owner_id = ?
                ORDER BY r.issue_date DESC;
                """;

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, Session.loggedInOwnerId);

            ResultSet rs = ps.executeQuery();

            boolean hasAny = false;

            while (rs.next()) {
                hasAny = true;
                listContainer.add(createReceiptCard(rs));
                listContainer.add(Box.createVerticalStrut(10));
            }

            if (!hasAny) {
                JLabel lbl = new JLabel("No receipts found.", SwingConstants.CENTER);
                lbl.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                lbl.setForeground(Color.DARK_GRAY);
                listContainer.add(Box.createVerticalStrut(40));
                listContainer.add(lbl);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        listContainer.revalidate();
        listContainer.repaint();
    }

    // === Create ONE receipt card in the list ===
    private JPanel createReceiptCard(ResultSet rs) throws Exception {

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        card.setBackground(Color.WHITE);

        int paymentId = rs.getInt("payment_id");

        JLabel title = new JLabel(rs.getString("payment_type") +
                " • Receipt " + rs.getString("receipt_number"));
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        title.setForeground(new Color(20, 50, 100));

        JLabel plateLbl = new JLabel("Plate: " + rs.getString("plate_number"));
        JLabel amountLbl = new JLabel("Amount: ₱" + rs.getDouble("amount_paid"));
        JLabel dateLbl = new JLabel("Issued: " + rs.getDate("issue_date"));
        JLabel officerLbl = new JLabel("Officer: " +
                rs.getString("officer_ln") + ", " + rs.getString("officer_fn"));
        JLabel branchLbl = new JLabel("Branch: " + rs.getString("branch_name"));

        plateLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        amountLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dateLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        officerLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        branchLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JButton viewBtn = new JButton("View Receipt");
        stylePrimary(viewBtn);
        viewBtn.addActionListener(e -> mainFrame.showReceipt(paymentId));

        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(plateLbl);
        card.add(amountLbl);
        card.add(dateLbl);
        card.add(officerLbl);
        card.add(branchLbl);

        card.add(Box.createVerticalStrut(10));
        card.add(viewBtn);

        return card;
    }

    // Styling
    private void stylePrimary(JButton b) {
        b.setBackground(new Color(0, 90, 200));
        b.setForeground(Color.white);
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
    }

    private void styleSecondary(JButton b) {
        b.setBackground(new Color(230, 230, 230));
        b.setForeground(Color.darkGray);
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    }
}
