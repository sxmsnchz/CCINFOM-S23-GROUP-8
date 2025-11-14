package view;

import service.OfficerService;
import model.Officer;
import model.Registration;
import model.Violation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class OfficerRecordsPanel extends JPanel {

    private final MainFrame mainFrame;
    private final OfficerService officerService = new OfficerService();

    private DefaultTableModel officersModel;
    private DefaultTableModel registrationsModel;
    private DefaultTableModel violationsModel;

    private DefaultComboBoxModel<String> officerComboModel;
    private JComboBox<String> regsOfficerSelector;
    private JComboBox<String> violOfficerSelector;

    public OfficerRecordsPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new GridBagLayout());
        setOpaque(false);

        JPanel card = new RoundedPanel(18, new Color(255,255,255,235));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(22, 26, 22, 26));
        card.setPreferredSize(new Dimension(760, 520));

        // Header
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Officer Records");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(20,50,100));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.add(title);
        header.add(Box.createVerticalStrut(6));
        JLabel subtitle = new JLabel("Browse officers, registrations processed and violations issued");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(Color.DARK_GRAY);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.add(subtitle);

        card.add(header);
        card.add(Box.createVerticalStrut(12));

        // Tabbed content
        JTabbedPane tabs = new JTabbedPane();

        // --- All Officers tab ---
        officersModel = new DefaultTableModel(new Object[]{"ID","Name"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable officersTable = new JTable(officersModel);
        JScrollPane officersScroll = new JScrollPane(officersTable);
        JPanel officersPanel = new JPanel(new BorderLayout());
        officersPanel.setOpaque(false);
        officersPanel.add(officersScroll, BorderLayout.CENTER);
        JButton refreshOfficers = createPrimaryButton("Refresh");
        refreshOfficers.addActionListener(e -> loadOfficers());
        JPanel ofBtn = new JPanel(); ofBtn.setOpaque(false); ofBtn.add(refreshOfficers);
        officersPanel.add(ofBtn, BorderLayout.SOUTH);
        tabs.addTab("All Officers", officersPanel);

        // --- Registrations by Officer ---
        registrationsModel = new DefaultTableModel(new Object[]{"RegID","VehID","OwnerID","FirstReg","CurrentReg","Expiry","Status"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable regsTable = new JTable(registrationsModel);
        JScrollPane regsScroll = new JScrollPane(regsTable);
        JPanel regsPanel = new JPanel(new BorderLayout());
        regsPanel.setOpaque(false);
        JPanel regsTop = new JPanel(); regsTop.setOpaque(false);
        officerComboModel = new DefaultComboBoxModel<>();
        regsOfficerSelector = new JComboBox<>(officerComboModel);
        regsOfficerSelector.setPrototypeDisplayValue("000 - Lastname, Firstname");
        JButton loadRegs = createPrimaryButton("Load Registrations Processed");
        loadRegs.addActionListener(e -> loadRegistrationsForSelectedOfficer());
        regsTop.add(new JLabel("Officer:")); regsTop.add(regsOfficerSelector); regsTop.add(loadRegs);
        regsPanel.add(regsTop, BorderLayout.NORTH);
        regsPanel.add(regsScroll, BorderLayout.CENTER);
        tabs.addTab("Registrations Processed", regsPanel);

        // --- Violations by Officer ---
        violationsModel = new DefaultTableModel(new Object[]{"ViolationID","VehicleID","OwnerID","Type","Date","Fine","Status"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable violTable = new JTable(violationsModel);
        JScrollPane violScroll = new JScrollPane(violTable);
        JPanel violPanel = new JPanel(new BorderLayout());
        violPanel.setOpaque(false);
        JPanel violTop = new JPanel(); violTop.setOpaque(false);
        // separate JComboBox backed by same model so both tabs show identical items
        violOfficerSelector = new JComboBox<>(officerComboModel);
        violOfficerSelector.setPrototypeDisplayValue("000 - Lastname, Firstname");
        JButton loadViol = createPrimaryButton("Load Violations Issued by Officer");
        loadViol.addActionListener(e -> loadViolationsForSelectedOfficer());
        violTop.add(new JLabel("Officer:")); violTop.add(violOfficerSelector); violTop.add(loadViol);
        violPanel.add(violTop, BorderLayout.NORTH);
        violPanel.add(violScroll, BorderLayout.CENTER);
        tabs.addTab("Violations Issued", violPanel);

        card.add(tabs);

        // Back button
        card.add(Box.createVerticalStrut(12));
        JPanel bottom = new JPanel(); bottom.setOpaque(false);
        JButton back = createSecondaryButton("Back to Dashboard");
        back.addActionListener(e -> mainFrame.showOfficerMenu());
        bottom.add(back);
        card.add(bottom);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.CENTER;
        add(card, gbc);

        // initial load
        loadOfficers();
    }

    private void loadOfficers() {
        officersModel.setRowCount(0);
        officerComboModel.removeAllElements();
        List<Officer> officers = officerService.getAllOfficers();
        for (Officer o : officers) {
            String name = (o.getFirstName()==null?"":o.getFirstName()) + " " + (o.getLastName()==null?"":o.getLastName());
            officersModel.addRow(new Object[]{o.getOfficerId(), name.trim()});
            officerComboModel.addElement(o.getOfficerId() + " - " + name.trim());
        }
        // Select the first officer in the list (do not auto-load registrations/violations)
        if (officerComboModel.getSize() > 0) {
            regsOfficerSelector.setSelectedIndex(0);
            violOfficerSelector.setSelectedIndex(0);
        }
    }

    private Integer getSelectedOfficerId(JComboBox<String> selector) {
        String sel = (String) selector.getSelectedItem();
        if (sel == null) return null;
        try {
            String[] parts = sel.split("\\s*[-]\\s*");
            return Integer.parseInt(parts[0].trim());
        } catch (Exception e) {
            return null;
        }
    }

    private void loadRegistrationsForSelectedOfficer() {
        registrationsModel.setRowCount(0);
        try {
            Integer id = getSelectedOfficerId(regsOfficerSelector);
            if (id == null) {
                JOptionPane.showMessageDialog(this, "Please select an officer first.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            List<Registration> regs = officerService.getRegistrationsByOfficer(id);
            if (regs == null || regs.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No registrations found for officer " + id + ".", "No Results", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            for (Registration r : regs) {
                String first = r.getFirstDateRegistered()==null?"":r.getFirstDateRegistered().toString();
                String current = r.getCurrentDateRegistered()==null?"":r.getCurrentDateRegistered().toString();
                String expiry = r.getExpiryDate()==null?"":r.getExpiryDate().toString();
                registrationsModel.addRow(new Object[]{r.getRegistrationId(), r.getVehicleId(), r.getOwnerId(), first, current, expiry, r.getStatus()});
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading registrations: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadViolationsForSelectedOfficer() {
        violationsModel.setRowCount(0);
        try {
            Integer id = getSelectedOfficerId(violOfficerSelector);
            if (id == null) {
                JOptionPane.showMessageDialog(this, "Please select an officer first.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            List<Violation> vs = officerService.getViolationsByOfficer(id);
            if (vs == null || vs.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No issued violations found for officer " + id + ".", "No Results", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            for (Violation v : vs) {
                String date = v.getViolationDate()==null?"":v.getViolationDate().toString();
                violationsModel.addRow(new Object[]{v.getViolationId(), v.getVehicleId(), v.getOwnerId(), v.getViolationType(), date, String.format("%.2f", v.getFineAmount()), v.getPaymentStatus()});
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading violations: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    class RoundedPanel extends JPanel {
        private final int radius;
        private final Color bgColor;

        RoundedPanel(int radius, Color bgColor) {
            this.radius = radius;
            this.bgColor = bgColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        }
    }

    // UI helpers to match the app theme
    private JButton createPrimaryButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(new Color(0, 90, 200));
        b.setForeground(Color.white);
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return b;
    }

    private JButton createSecondaryButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(new Color(230, 230, 230));
        b.setForeground(Color.darkGray);
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return b;
    }

    private JButton createDangerButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(new Color(180, 40, 40));
        b.setForeground(Color.white);
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return b;
    }
}

