package view;

import database.DatabaseConnection;
import model.Session;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserLoginGUI extends JFrame {

	private JTextField ownerIdField;
	private JPasswordField passwordField;
	private JLabel statusLabel;

	public UserLoginGUI() {
		setTitle("User Login - LTO Portal");
		setSize(420, 300);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());

		JPanel card = new JPanel();
		card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
		card.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
		card.setBackground(Color.WHITE);

		JLabel title = new JLabel("Vehicle Owner Login");
		title.setFont(new Font("Segoe UI", Font.BOLD, 18));
		title.setAlignmentX(Component.CENTER_ALIGNMENT);

		JLabel subtitle = new JLabel("Enter your Owner ID and password");
		subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		subtitle.setForeground(Color.DARK_GRAY);
		subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

		card.add(title);
		card.add(subtitle);
		card.add(Box.createVerticalStrut(20));

		JLabel idLabel = new JLabel("Owner ID:");
		ownerIdField = new JTextField(20);

		JLabel passLabel = new JLabel("Password:");
		passwordField = new JPasswordField(20);

		statusLabel = new JLabel(" ");
		statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
		statusLabel.setForeground(new Color(160, 0, 0));

		JPanel formPanel = new JPanel(new GridBagLayout());
		formPanel.setOpaque(false);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 0, 5, 0);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0; gbc.gridy = 0;
		formPanel.add(idLabel, gbc);
		gbc.gridy++;
		formPanel.add(ownerIdField, gbc);
		gbc.gridy++;
		formPanel.add(passLabel, gbc);
		gbc.gridy++;
		formPanel.add(passwordField, gbc);
		gbc.gridy++;
		formPanel.add(statusLabel, gbc);

		card.add(formPanel);
		card.add(Box.createVerticalStrut(15));

		JPanel btnPanel = new JPanel();
		btnPanel.setOpaque(false);

		JButton loginBtn = new JButton("Login");
		JButton cancelBtn = new JButton("Back");

		stylePrimaryButton(loginBtn);
		styleSecondaryButton(cancelBtn);

		btnPanel.add(loginBtn);
		btnPanel.add(cancelBtn);

		card.add(btnPanel);

		add(card, BorderLayout.CENTER);

		loginBtn.addActionListener(e -> doLogin());
		cancelBtn.addActionListener(e -> {
			dispose();
			new VehicleRegistrationHomeGUI();
		});
		passwordField.addActionListener(e -> doLogin());

		setVisible(true);
	}

	private void doLogin() {
		String ownerStr = ownerIdField.getText().trim();
		String password = new String(passwordField.getPassword()).trim();

		if (ownerStr.isEmpty() || password.isEmpty()) {
			statusLabel.setText("Please fill in all fields.");
			return;
		}

		if (!ownerStr.matches("\\d+")) {
			statusLabel.setText("Owner ID must be numeric.");
			return;
		}

		try {
			Connection conn = DatabaseConnection.getConnection();
			if (conn == null) {
				JOptionPane.showMessageDialog(this,
						"Database connection error.",
						"Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			int ownerId = Integer.parseInt(ownerStr);
			PreparedStatement ps = conn.prepareStatement(
					"SELECT * FROM owner WHERE owner_id = ? AND password = ?");
			ps.setInt(1, ownerId);
			ps.setString(2, password);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				Session.loggedInOwnerId = ownerId;
				Session.loggedInRole = "owner";

				String name = rs.getString("first_name") + " " + rs.getString("last_name");
				JOptionPane.showMessageDialog(this,
						"Login successful!\nWelcome, " + name + ".",
						"Success",
						JOptionPane.INFORMATION_MESSAGE);

				dispose();
				new UserMenu().viewUserMenu();
			} else {
				statusLabel.setText("Invalid credentials.");
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this,
					"Error during login: " + ex.getMessage(),
					"Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void stylePrimaryButton(JButton btn) {
		btn.setFocusPainted(false);
		btn.setBackground(new Color(0, 90, 200));
		btn.setForeground(Color.WHITE);
		btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
	}

	private void styleSecondaryButton(JButton btn) {
		btn.setFocusPainted(false);
		btn.setBackground(new Color(230, 230, 230));
		btn.setForeground(Color.DARK_GRAY);
		btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
	}

	public static void main(String[] args) {
		new UserLoginGUI();
	}
}
