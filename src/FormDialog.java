import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.sql.*;
import java.util.*;

public class FormDialog extends JDialog {

    private final ModuleConfig        cfg;
    private final String[]            existingData;   
    private final Map<String,JTextField> fields = new LinkedHashMap<>();
    private boolean saved = false;

    private static final Color C_PRIMARY = new Color(13, 27, 100);

    public FormDialog(JFrame parent, String title, ModuleConfig cfg, String[] existingData) {
        super(parent, title, true);
        this.cfg          = cfg;
        this.existingData = existingData;

        int fieldCount = cfg.editableColumns.length;
        int height = Math.min(110 + fieldCount * 62, 620);
        setSize(500, height);
        setLocationRelativeTo(parent);
        setResizable(false);
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout());

        JPanel hdr = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0,0,C_PRIMARY,getWidth(),0,new Color(26,82,160)));
                g2.fillRect(0,0,getWidth(),getHeight());
            }
        };
        hdr.setPreferredSize(new Dimension(0,50));
        hdr.setBorder(new EmptyBorder(0,18,0,18));
        JLabel titleLbl = new JLabel(getTitle());
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLbl.setForeground(Color.WHITE);
        hdr.add(titleLbl, BorderLayout.CENTER);
        add(hdr, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(16, 24, 12, 24));

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(6, 4, 6, 4);

        for (int i = 0; i < cfg.editableColumns.length; i++) {
            String col  = cfg.editableColumns[i];
            String head = cfg.editableHeaders[i];

            JLabel lbl = new JLabel(head + ":");
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lbl.setForeground(new Color(50, 62, 108));
            g.gridx=0; g.gridy=i; g.weightx=0.32;
            form.add(lbl, g);

            JTextField tf = new JTextField();
            tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(185,200,225)),
                new EmptyBorder(7,9,7,9)));
            tf.setPreferredSize(new Dimension(0,36));

            if (existingData != null) {
                for (int j = 0; j < cfg.columnDbNames.length; j++) {
                    if (cfg.columnDbNames[j].equalsIgnoreCase(col)) {
                        tf.setText(existingData[j] != null ? existingData[j] : "");
                        break;
                    }
                }
            }

            g.gridx=1; g.weightx=0.68;
            form.add(tf, g);
            fields.put(col, tf);
        }

        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        btns.setBackground(new Color(228, 234, 250));
        btns.setBorder(BorderFactory.createMatteBorder(1,0,0,0,new Color(195,210,235)));

        JButton cancel = new JButton("Cancel");
        cancel.setFont(new Font("Segoe UI",Font.PLAIN,12));
        cancel.setPreferredSize(new Dimension(90,34));
        cancel.addActionListener(e -> dispose());
        btns.add(cancel);

        String saveLabel = existingData == null ? "Add Record" : "Save Changes";
        JButton save = new JButton(saveLabel) {
            @Override protected void paintComponent(Graphics g2) {
                Graphics2D g = (Graphics2D) g2;
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(getModel().isRollover() ? new Color(18,96,178) : C_PRIMARY);
                g.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
                g.setColor(Color.WHITE);
                g.setFont(new Font("Segoe UI",Font.BOLD,12));
                FontMetrics fm=g.getFontMetrics();
                g.drawString(getText(),(getWidth()-fm.stringWidth(getText()))/2,(getHeight()+fm.getAscent()-fm.getDescent())/2);
            }
        };
        save.setOpaque(false); save.setContentAreaFilled(false); save.setBorderPainted(false);
        save.setFocusPainted(false); save.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        save.setPreferredSize(new Dimension(130,34));
        save.addActionListener(e -> doSave());
        btns.add(save);

        add(btns, BorderLayout.SOUTH);

        for (JTextField tf : fields.values())
            tf.addActionListener(e -> doSave());
    }

    private void doSave() {
        Map<String,String> vals = new LinkedHashMap<>();
        for (Map.Entry<String,JTextField> e : fields.entrySet())
            vals.put(e.getKey(), e.getValue().getText().trim());

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (existingData == null) {
                String cols  = String.join(",", vals.keySet());
                String marks = String.join(",", Collections.nCopies(vals.size(),"?"));
                PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO " + cfg.tableName + " (" + cols + ") VALUES (" + marks + ")");
                int idx=1;
                for (String v : vals.values()) {
                    if (v.isEmpty()) ps.setNull(idx++, Types.NULL);
                    else             ps.setString(idx++, v);
                }
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this,"Record added successfully! ✔","Success",JOptionPane.INFORMATION_MESSAGE);

            } else {
                String pkVal = existingData[cfg.getPrimaryKeyIndex()];
                java.util.List<String> cols = new ArrayList<>(vals.keySet());
                StringBuilder sb = new StringBuilder();
                for (int i=0;i<cols.size();i++){
                    if(i>0) sb.append(", ");
                    sb.append(cols.get(i)).append("=?");
                }
                PreparedStatement ps = conn.prepareStatement(
                    "UPDATE " + cfg.tableName + " SET " + sb + " WHERE " + cfg.primaryKey + "=?");
                int idx=1;
                for (String v : vals.values()){
                    if (v.isEmpty()) ps.setNull(idx++, Types.NULL);
                    else             ps.setString(idx++, v);
                }
                ps.setString(idx, pkVal);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this,"Record updated successfully! ✔","Success",JOptionPane.INFORMATION_MESSAGE);
            }
            saved = true;
            dispose();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error saving record:\n" + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() { return saved; }
}
