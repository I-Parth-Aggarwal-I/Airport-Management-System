import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.sql.*;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel msgLabel;

    private static final Color C_DARK = new Color(13, 27, 100);
    private static final Color C_MID = new Color(26, 82, 160);

    public LoginFrame() {
        setTitle("Airport Management System – Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 560);
        setLocationRelativeTo(null);
        setResizable(false);
        buildUI();
    }

    private void buildUI() {
        Image bgImage = new ImageIcon("assets/login_bg.jpg").getImage();

        JPanel root = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (bgImage != null) {
                    g2.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g2.setPaint(new GradientPaint(0, 0, C_DARK, 0, getHeight(), C_MID));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBorder(new EmptyBorder(52, 20, 24, 20));

        JLabel title = new JLabel("Airport Management System", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Sign in to your account", SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        sub.setForeground(new Color(230, 240, 255));
        sub.setAlignmentX(CENTER_ALIGNMENT);

        top.add(title);
        top.add(Box.createVerticalStrut(6));
        top.add(sub);

        JPanel cardWrap = new JPanel(new GridBagLayout());
        cardWrap.setOpaque(false);
        cardWrap.setBorder(new EmptyBorder(0, 36, 48, 36));

        JPanel card = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 45));
                g2.fillRoundRect(4, 4, getWidth() - 2, getHeight() - 2, 22, 22);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 5, getHeight() - 5, 22, 22);
            }
        };
        card.setOpaque(false);
        // By relying on the components' preferred sizes and insets, GridBagLayout will
        // wrap them nicely
        card.setBorder(new EmptyBorder(15, 5, 5, 5));

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(6, 24, 6, 24);

        addLabel(card, "Username", g, 0);
        usernameField = new JTextField();
        styleField(usernameField);
        g.gridy = 1;
        card.add(usernameField, g);

        g.insets = new Insets(12, 24, 6, 24);
        addLabel(card, "Password", g, 2);
        passwordField = new JPasswordField();
        styleField(passwordField);
        g.insets = new Insets(6, 24, 4, 24);
        g.gridy = 3;
        card.add(passwordField, g);

        msgLabel = new JLabel(" ", SwingConstants.CENTER);
        msgLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        msgLabel.setForeground(new Color(210, 50, 50));
        g.gridy = 4;
        g.insets = new Insets(2, 24, 2, 24);
        card.add(msgLabel, g);

        JButton loginBtn = buildLoginButton();
        g.gridy = 5;
        g.insets = new Insets(4, 24, 22, 24);
        card.add(loginBtn, g);

        cardWrap.add(card);
        root.add(top, BorderLayout.NORTH);
        root.add(cardWrap, BorderLayout.CENTER);
        setContentPane(root);

        usernameField.addActionListener(e -> passwordField.requestFocus());
        passwordField.addActionListener(e -> loginBtn.doClick());
    }

    private void addLabel(JPanel p, String text, GridBagConstraints g, int row) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(50, 60, 100));
        g.gridy = row;
        p.add(lbl, g);
    }

    private void styleField(JTextField f) {
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(190, 205, 230)),
                new EmptyBorder(8, 10, 8, 10)));
        f.setPreferredSize(new Dimension(280, 38));
    }

    private JButton buildLoginButton() {
        JButton btn = new JButton("LOGIN") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = getModel().isPressed() ? C_DARK.darker()
                        : getModel().isRollover() ? new Color(20, 100, 185)
                                : C_DARK;
                g2.setColor(c);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                        (getWidth() - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            }
        };
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(0, 42));
        btn.addActionListener(e -> doLogin());
        return btn;
    }

    private void doLogin() {
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            setMsg("Please enter username and password.", false);
            return;
        }
        setMsg("Signing in …", true);

        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                try (Connection conn = DatabaseConnection.getConnection()) {
                    PreparedStatement ps = conn.prepareStatement(
                            "SELECT * FROM Login WHERE username=? AND password=?");
                    ps.setString(1, user);
                    ps.setString(2, pass);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        UserSession s = UserSession.getInstance();
                        s.loginId = rs.getInt("login_id");
                        s.username = rs.getString("username");
                        s.role = rs.getString("role");
                        s.passengerId = getInt(rs, "passenger_id");
                        s.flightStaffId = getInt(rs, "flight_staff_id");
                        s.airportStaffId = getInt(rs, "airport_staff_id");
                        return true;
                    }
                    return false;
                }
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        dispose();
                        new DashboardFrame().setVisible(true);
                    } else
                        setMsg("Invalid username or password.", false);
                } catch (Exception ex) {
                    setMsg("Connection error: " + ex.getMessage(), false);
                }
            }
        }.execute();
    }

    private Integer getInt(ResultSet rs, String col) {
        try {
            Object v = rs.getObject(col);
            return v != null ? rs.getInt(col) : null;
        } catch (Exception e) {
            return null;
        }
    }

    private void setMsg(String text, boolean info) {
        msgLabel.setForeground(info ? new Color(30, 120, 200) : new Color(210, 50, 50));
        msgLabel.setText(text);
    }
}
