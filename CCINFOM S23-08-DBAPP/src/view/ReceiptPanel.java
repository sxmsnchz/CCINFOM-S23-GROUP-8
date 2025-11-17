package view;

import database.DatabaseConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ReceiptPanel extends JPanel {

    private final MainFrame mainFrame;

    private JLabel lblReceiptNum;
    private JLabel lblTransactionType;
    private JLabel lblAmountPaid;
    private JLabel lblDateIssued;
    private JLabel lblOfficer;
    private JLabel lblBranch;
    private JLabel lblPlate;

    private float fadeOpacity = 0f;     // FADE-IN ANIMATION VARIABLE
    private Timer fadeTimer;            // FADE-IN TIMER
    private Image watermarkImg;         // WATERMARK IMAGE

    public ReceiptPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        // Load watermark image
        watermarkImg = new ImageIcon(getClass().getResource("/assets/lto.jpg")).getImage();

        // White background
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        // ---------------------------
        // LEFT CONTAINER
        // ---------------------------
        JPanel leftContainer = new JPanel(new GridBagLayout());
        leftContainer.setOpaque(false);
        leftContainer.setBorder(BorderFactory.createEmptyBorder(0, 350, 0, 0));

        // ---------------------------
        // RECEIPT CARD (with fade)
        // ---------------------------
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;

                // Apply fade opacity
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fadeOpacity));

                // Background shadow
                g2.setColor(new Color(0, 0, 0, 35));
                g2.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 20, 20);

                super.paintComponent(g);
            }
        };

        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2),
                BorderFactory.createEmptyBorder(30, 40, 40, 40)
        ));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(450, 550));

        // TITLE
        JLabel title = new JLabel("LTO OFFICIAL RECEIPT", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lineTop = makeLine();
        JLabel lineBottom1 = makeLine();
        JLabel lineBottom2 = makeLine();

        lblReceiptNum = createText();
        lblTransactionType = createText();
        lblPlate = createText();
        lblAmountPaid = createText();
        lblDateIssued = createText();
        lblOfficer = createText();
        lblBranch = createText();

        JButton backBtn = new JButton("Back");
        styleSecondary(backBtn);
        backBtn.addActionListener(e -> mainFrame.showReceiptHistory());

        JButton doneBtn = new JButton("Done");
        stylePrimary(doneBtn);
        doneBtn.addActionListener(e -> mainFrame.showUserMenu());

        card.add(title);
        card.add(Box.createVerticalStrut(15));
        card.add(lineTop);
        card.add(Box.createVerticalStrut(15));

        card.add(lblReceiptNum);
        card.add(lblTransactionType);
        card.add(lblPlate);
        card.add(lblAmountPaid);
        card.add(lblDateIssued);
        card.add(lblOfficer);
        card.add(lblBranch);

        card.add(Box.createVerticalStrut(15));
        card.add(lineBottom1);
        card.add(Box.createVerticalStrut(10));

        JLabel thankYou = new JLabel("Thank you for your payment!", SwingConstants.CENTER);
        thankYou.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        thankYou.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(thankYou);
        card.add(Box.createVerticalStrut(10));
        card.add(lineBottom2);
        card.add(Box.createVerticalStrut(20));

        JPanel btnRow = new JPanel(new FlowLayout());
        btnRow.setOpaque(false);
        btnRow.add(backBtn);
        btnRow.add(doneBtn);

        card.add(btnRow);

        leftContainer.add(card);
        add(leftContainer, BorderLayout.WEST);

        startFadeAnimation(card);
    }

    // WATERMARK BACKGROUND
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        // Center watermark
        int w = getWidth();
        int h = getHeight();

        int imgW = watermarkImg.getWidth(null);
        int imgH = watermarkImg.getHeight(null);

        int x = (w - imgW) / 2;
        int y = (h - imgH) / 2;

        // Transparent watermark
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.12f));
        g2.drawImage(watermarkImg, x, y, null);
    }

    // FADE-IN ANIMATION
    private void startFadeAnimation(JPanel card) {
        fadeOpacity = 0f;
        if (fadeTimer != null && fadeTimer.isRunning()) fadeTimer.stop();

        fadeTimer = new Timer(20, e -> {
            fadeOpacity += 0.05f;
            if (fadeOpacity >= 1f) {
                fadeOpacity = 1f;
                fadeTimer.stop();
            }
            card.repaint();
        });

        fadeTimer.start();
    }

    private JLabel makeLine() {
        JLabel l = new JLabel("---------------------------------------------");
        l.setFont(new Font("Monospaced", Font.PLAIN, 12));
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        return l;
    }

    private JLabel createText() {
        JLabel l = new JLabel(" ");
        l.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        return l;
    }

    // LOAD RECEIPT CONTENT
    public void loadReceipt(int paymentId) {
        startFadeAnimation(this); // restart fade each time the panel loads

        try (Connection conn = DatabaseConnection.getConnection()) {

            String sql = """
                SELECT
                    r.receipt_number,
                    r.issue_date,
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
                WHERE r.payment_id = ?;
                """;

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, paymentId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                lblReceiptNum.setText("Receipt Number: " + rs.getString("receipt_number"));
                lblTransactionType.setText("Transaction: " + rs.getString("payment_type"));
                lblPlate.setText("Plate Number: " + rs.getString("plate_number"));
                lblAmountPaid.setText("Amount Paid: ₱" + rs.getDouble("amount_paid"));
                lblDateIssued.setText("Date Issued: " + rs.getDate("issue_date"));

                String officerName = rs.getString("officer_ln") + ", " + rs.getString("officer_fn");
                lblOfficer.setText("Processed By: " + officerName);
                lblBranch.setText("Branch: " + rs.getString("branch_name"));
            } else {
                lblReceiptNum.setText("Error loading receipt");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            lblReceiptNum.setText("Error loading receipt.");
        }
    }

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
}
