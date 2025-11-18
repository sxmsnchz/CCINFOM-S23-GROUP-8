package view;

import database.DatabaseConnection;
import model.Session;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UserPaymentPanel extends JPanel {

    private final MainFrame mainFrame;

    private List<PaymentItem> unpaidList = new ArrayList<>();

    private JPanel cardContainer;
    private CardLayout cardLayout;

    private JPanel listCard;
    private JPanel paymentCard;

    private JLabel amountLabel;
    private JLabel dateLabel;
    private JLabel itemTitleLabel;

    private JTextField cardNameField;
    private JTextField cardNumberField;
    private JTextField expiryField;
    private JTextField zipField;
    private JTextField cvvField;

    private PaymentItem selectedItem;

    public UserPaymentPanel(MainFrame mainFrame) {
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

        JLabel title = new JLabel("Payment Hub");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(20, 50, 100));
        topBar.add(title);

        add(topBar, BorderLayout.NORTH);

        // CARD CONTAINER
        cardLayout = new CardLayout();
        cardContainer = new JPanel(cardLayout);

        listCard = createListCard();
        paymentCard = createPaymentCard();

        cardContainer.add(listCard, "list");
        cardContainer.add(paymentCard, "payment");

        add(cardContainer, BorderLayout.CENTER);
    }

    // CALL WHEN OPENING
    public void refreshData() {
        loadUnpaidTransactions();
        populateListCard();
        cardLayout.show(cardContainer, "list");
    }

    // LOAD UNPAID TRANSACTIONS WITH DATE
    private void loadUnpaidTransactions() {
        unpaidList.clear();

        int ownerId = Session.loggedInOwnerId;
        if (ownerId == 0) return;

        String sql = """
            SELECT * FROM (
                -- UNPAID VIOLATIONS
                SELECT
                    'Violation' AS type,
                    v.violation_id AS id,
                    ve.plate_number AS plate,
                    v.violation_type AS description,
                    v.fine_amount AS amount,
                    v.violation_date AS dateValue
                FROM violation v
                JOIN vehicle ve ON v.vehicle_id = ve.vehicle_id
                WHERE v.owner_id = ?
                  AND (v.payment_status = 'Unpaid' OR v.payment_id IS NULL)

                UNION

                -- NEW REGISTRATION (first-time)
                SELECT
                    'Registration' AS type,
                    r.registration_id AS id,
                    v.plate_number AS plate,
                    'New Registration' AS description,
                    7410 AS amount,
                    r.first_date_registered AS dateValue
                FROM registration r
                JOIN vehicle v ON r.vehicle_id = v.vehicle_id
                WHERE r.owner_id = ?
                  AND r.payment_id IS NULL
                  AND r.first_date_registered IS NULL
                  AND r.status = 'INACTIVE'

                UNION

                -- RENEWALS (ONLY if renewal row exists)
                SELECT
                    'Renewal' AS type,
                    r.registration_id AS id,
                    v.plate_number AS plate,
                    CASE
                        WHEN r.expiry_date < CURDATE() THEN 'Renewal (Expired)'
                        ELSE 'Renewal (Expiring Soon)'
                    END AS description,
                    1500 AS amount,
                    r.expiry_date AS dateValue
                FROM registration r
                JOIN renewal re ON re.registration_id = r.registration_id
                JOIN vehicle v ON r.vehicle_id = v.vehicle_id
                WHERE r.owner_id = ?
                  AND (re.payment_id IS NULL OR re.last_renewal_date IS NULL)
                  AND (r.expiry_date < CURDATE()
                     OR r.expiry_date <= DATE_ADD(CURDATE(), INTERVAL 60 DAY))
            ) t;
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, ownerId); // violations
            ps.setInt(2, ownerId); // new registrations
            ps.setInt(3, ownerId); // renewals

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                unpaidList.add(new PaymentItem(
                        rs.getString("type"),
                        rs.getInt("id"),
                        rs.getString("plate"),
                        rs.getString("description"),
                        rs.getDouble("amount"),
                        rs.getString("dateValue")
                ));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // LIST CARD
    private JPanel createListCard() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JPanel container = new JPanel();
        container.setOpaque(false);
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        JScrollPane scroll = new JScrollPane(container);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        panel.add(scroll, BorderLayout.CENTER);
        panel.putClientProperty("listContainer", container);

        return panel;
    }

    private void populateListCard() {
        JPanel container = (JPanel) listCard.getClientProperty("listContainer");
        container.removeAll();

        if (unpaidList.isEmpty()) {
            JLabel none = new JLabel("You have no unpaid transactions.", SwingConstants.CENTER);
            none.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            none.setForeground(Color.DARK_GRAY);
            container.add(Box.createVerticalStrut(40));
            container.add(none);
        } else {
            for (PaymentItem item : unpaidList) {
                container.add(createPaymentCardItem(item));
                container.add(Box.createVerticalStrut(12));
            }
        }

        container.revalidate();
        container.repaint();
    }

    private JPanel createPaymentCardItem(PaymentItem item) {

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));
        card.setBackground(Color.WHITE);

        JLabel title = new JLabel(item.type + " • " + item.description);
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        title.setForeground(new Color(20, 50, 100));

        JLabel plate = new JLabel("Plate: " + item.plate);
        JLabel amount = new JLabel("Amount: ₱" + item.amount);

        plate.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        amount.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        // Only show date for Violations
        JLabel dateLbl = null;
        if (item.type.equalsIgnoreCase("Violation")) {
            dateLbl = new JLabel("Date Issued: " + item.dateIssued);
            dateLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        }

        JButton payBtn = new JButton("Pay Now");
        stylePrimary(payBtn);
        payBtn.addActionListener(e -> openPaymentForm(item));

        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(plate);
        card.add(amount);
        if (dateLbl != null) {
            card.add(dateLbl);
        }
        card.add(Box.createVerticalStrut(8));
        card.add(payBtn);

        return card;
    }

    // PAYMENT FORM — 2 COLUMN PROFESSIONAL
    private JPanel createPaymentCard() {

        JPanel outer = new JPanel(new GridBagLayout());
        outer.setOpaque(false);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(25, 35, 35, 35));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(460, 420));

        // Title
        itemTitleLabel = new JLabel("Transaction");
        itemTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        itemTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        dateLabel = new JLabel("Date Issued: N/A");
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        amountLabel = new JLabel("Amount Due: ₱0.00");
        amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        amountLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel formGrid = new JPanel(new GridLayout(3, 2, 12, 12));
        formGrid.setOpaque(false);

        cardNameField = new JTextField();
        cardNumberField = new JTextField();
        expiryField = new JTextField();
        zipField = new JTextField();
        cvvField = new JTextField();

        formGrid.add(labeledField("Cardholder Name", cardNameField));
        formGrid.add(labeledField("Card Number", cardNumberField));

        formGrid.add(labeledField("Expiry (MM/YY)", expiryField));
        formGrid.add(labeledField("ZIP Code", zipField));

        formGrid.add(labeledField("CVV", cvvField));
        formGrid.add(new JLabel());

        JButton confirmBtn = new JButton("Confirm Payment");
        stylePrimary(confirmBtn);
        confirmBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        confirmBtn.addActionListener(e -> processPayment());

        JButton backBtn = new JButton("Back");
        styleSecondary(backBtn);
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backBtn.addActionListener(e -> {
            // clear fields when going back
            cardNameField.setText("");
            cardNumberField.setText("");
            expiryField.setText("");
            zipField.setText("");
            cvvField.setText("");
            cardLayout.show(cardContainer, "list");
        });

        card.add(itemTitleLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(dateLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(amountLabel);
        card.add(Box.createVerticalStrut(25));
        card.add(formGrid);
        card.add(Box.createVerticalStrut(25));
        card.add(confirmBtn);
        card.add(Box.createVerticalStrut(10));
        card.add(backBtn);

        outer.add(card);
        return outer;
    }

    private JPanel labeledField(String label, JTextField field) {
        JPanel panel = new JPanel(new BorderLayout(3, 3));
        panel.setOpaque(false);

        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        panel.add(l, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);

        return panel;
    }

    private void openPaymentForm(PaymentItem item) {
        this.selectedItem = item;

        itemTitleLabel.setText(item.type + " • " + item.description);
        amountLabel.setText("Amount Due: ₱" + item.amount);

        // Show date label ONLY for violations
        if (item.type.equalsIgnoreCase("Violation")) {
            dateLabel.setText("Date Issued: " + item.dateIssued);
            dateLabel.setVisible(true);
        } else {
            dateLabel.setVisible(false);
        }

        // clear previous card details
        cardNameField.setText("");
        cardNumberField.setText("");
        expiryField.setText("");
        zipField.setText("");
        cvvField.setText("");

        cardLayout.show(cardContainer, "payment");
    }

    // PROCESS PAYMENT WITH CARD VALIDATION
    private void processPayment() {

        if (selectedItem == null) {
            JOptionPane.showMessageDialog(this, "No transaction selected.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String cardName = cardNameField.getText().trim();
        String cardNum = cardNumberField.getText().trim();
        String expiry = expiryField.getText().trim();
        String zip = zipField.getText().trim();
        String cvv = cvvField.getText().trim();

        if (cardName.isEmpty() || cardNum.isEmpty() || expiry.isEmpty() ||
                zip.isEmpty() || cvv.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please complete all fields.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!cardNum.matches("\\d+")) {
            JOptionPane.showMessageDialog(this,
                    "Card number must contain digits only.",
                    "Invalid Card Number", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!(cardNum.length() == 15 || cardNum.length() == 16)) {
            JOptionPane.showMessageDialog(this,
                    "Card number must be 15 or 16 digits.",
                    "Invalid Card Number", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // expiry MM/YY
        if (!expiry.matches("^(0[1-9]|1[0-2])/\\d{2}$")) {
            JOptionPane.showMessageDialog(this,
                    "Expiry must be in MM/YY format.",
                    "Invalid Expiry", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!cvv.matches("\\d{3,4}")) {
            JOptionPane.showMessageDialog(this,
                    "CVV must be 3–4 digits.",
                    "Invalid CVV", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (zip.length() < 4) {
            JOptionPane.showMessageDialog(this,
                    "ZIP code must be at least 4 characters.",
                    "Invalid ZIP Code", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // If all validations pass, save to DB
        savePaymentToDatabase(selectedItem);

        // Clear fields & reload list
        cardNameField.setText("");
        cardNumberField.setText("");
        expiryField.setText("");
        zipField.setText("");
        cvvField.setText("");

        refreshData();
    }

    private void savePaymentToDatabase(PaymentItem item) {

        int ownerId = Session.loggedInOwnerId;

        try (Connection conn = DatabaseConnection.getConnection()) {

            int officerId = 0;
            int branchId = 0;
            double amount = item.amount;
            String paymentType = item.type; // Violation / Registration / Renewal

            // VIOLATION
            if (item.type.equalsIgnoreCase("Violation")) {

                PreparedStatement ps = conn.prepareStatement(
                        "SELECT officer_id, branch_id FROM violation WHERE violation_id = ?"
                );
                ps.setInt(1, item.id);

                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    officerId = rs.getInt("officer_id");
                    branchId = rs.getInt("branch_id");
                } else {
                    JOptionPane.showMessageDialog(this, "Error: Violation not found.");
                    return;
                }
            }

            // REGISTRATION or RENEWAL
            else {

                PreparedStatement ps = conn.prepareStatement(
                        "SELECT officer_id, branch_id, payment_id, expiry_date, first_date_registered " +
                                "FROM registration WHERE registration_id = ?"
                );
                ps.setInt(1, item.id);
                ResultSet rs = ps.executeQuery();

                if (!rs.next()) {
                    JOptionPane.showMessageDialog(this, "Error: Registration not found.");
                    return;
                }

                officerId = rs.getInt("officer_id");
                branchId = rs.getInt("branch_id");

                if (item.type.equalsIgnoreCase("Registration")) {
                    paymentType = "Registration";
                    amount = 7410;
                } else {
                    paymentType = "Renewal";
                    amount = 1500;
                }
            }

            // INSERT INTO PAYMENT
            PreparedStatement insertPay = conn.prepareStatement(
                    "INSERT INTO payment (officer_id, branch_id, owner_id, payment_type, amount_paid, date_paid) " +
                            "VALUES (?, ?, ?, ?, ?, ?)",
                    PreparedStatement.RETURN_GENERATED_KEYS
            );

            insertPay.setInt(1, officerId);
            insertPay.setInt(2, branchId);
            insertPay.setInt(3, ownerId);
            insertPay.setString(4, paymentType);
            insertPay.setDouble(5, amount);
            insertPay.setDate(6, java.sql.Date.valueOf(java.time.LocalDate.now()));

            insertPay.executeUpdate();

            ResultSet gen = insertPay.getGeneratedKeys();
            int paymentId = 0;
            if (gen.next()) paymentId = gen.getInt(1);

            // STEP 3 — CREATE RECEIPT ROW

            String prefix = paymentType.equalsIgnoreCase("Violation") ? "V" : "R";

            // Get latest receipt number with same prefix
            PreparedStatement psLast = conn.prepareStatement(
        "SELECT receipt_number FROM receipt WHERE receipt_number LIKE ? ORDER BY receipt_id DESC LIMIT 1"
            );

            psLast.setString(1, prefix + "%");
            ResultSet rsLast = psLast.executeQuery();

            String nextReceipt;
            if (rsLast.next()) {
                String last = rsLast.getString("receipt_number"); // e.g., R024
                int num = Integer.parseInt(last.substring(1)) + 1;
                nextReceipt = prefix + String.format("%03d", num);
            } else {
                nextReceipt = prefix + "001";
            }

        // Insert new receipt
        PreparedStatement psReceipt = conn.prepareStatement(
        "INSERT INTO receipt (payment_id, receipt_number, issue_date, printed_by) VALUES (?, ?, CURDATE(), ?)"
        );

        psReceipt.setInt(1, paymentId);
        psReceipt.setString(2, nextReceipt);

        // officer name for printed_by
        PreparedStatement getOfficer = conn.prepareStatement(
        "SELECT last_name, first_name FROM officer WHERE officer_id = ?"
        );
        getOfficer.setInt(1, officerId);
        ResultSet rsOff = getOfficer.executeQuery();

        String printedBy = "Unknown";
        if (rsOff.next()) {
            printedBy = rsOff.getString("last_name") + ", " + rsOff.getString("first_name");
        }   

        psReceipt.setString(3, printedBy);
        psReceipt.executeUpdate();

            // UPDATE TABLES
            if (item.type.equalsIgnoreCase("Violation")) {
                PreparedStatement upd = conn.prepareStatement(
                        "UPDATE violation SET payment_status='Cleared', payment_id=? WHERE violation_id=?"
                );
                upd.setInt(1, paymentId);
                upd.setInt(2, item.id);
                upd.executeUpdate();
            } else if (paymentType.equalsIgnoreCase("Registration")) {

                PreparedStatement upd = conn.prepareStatement("""
                        UPDATE registration
                        SET payment_id = ?,
                            first_date_registered = CURDATE(),
                            current_date_registered = CURDATE(),
                            expiry_date = DATE_ADD(CURDATE(), INTERVAL 3 YEAR),
                            status = 'ACTIVE'
                        WHERE registration_id = ?
                        """);

                upd.setInt(1, paymentId);
                upd.setInt(2, item.id);
                upd.executeUpdate();

            } else if (paymentType.equalsIgnoreCase("Renewal")) {

                PreparedStatement updRenew = conn.prepareStatement("""
                        UPDATE renewal
                        SET last_renewal_date = CURDATE(),
                            payment_id = ?
                        WHERE registration_id = ?
                        """);

                updRenew.setInt(1, paymentId);
                updRenew.setInt(2, item.id);
                updRenew.executeUpdate();

                PreparedStatement updReg = conn.prepareStatement("""
                        UPDATE registration
                        SET current_date_registered = CURDATE(),
                            expiry_date = DATE_ADD(CURDATE(), INTERVAL 1 YEAR),
                            status = 'ACTIVE'
                        WHERE registration_id = ?
                        """);

                updReg.setInt(1, item.id);
                updReg.executeUpdate();
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Payment successfully processed!\nGenerating receipt...",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            mainFrame.showReceipt(paymentId);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Payment failed:\n" + ex.getMessage());
        }
    }

    // STYLE
    private void stylePrimary(JButton b) {
        b.setBackground(new Color(0, 90, 200));
        b.setForeground(Color.white);
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
    }

    private void styleSecondary(JButton b) {
        b.setBackground(new Color(230, 230, 230));
        b.setForeground(Color.darkGray);
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    }

    // MODEL
    private static class PaymentItem {
        String type;
        int id;
        String plate;
        String description;
        double amount;
        String dateIssued;

        PaymentItem(String type, int id, String plate, String description,
                    double amount, String dateIssued) {
            this.type = type;
            this.id = id;
            this.plate = plate;
            this.description = description;
            this.amount = amount;
            this.dateIssued = dateIssued;
        }
    }
}
