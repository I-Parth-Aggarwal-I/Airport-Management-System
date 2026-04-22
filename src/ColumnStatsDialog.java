import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ColumnStatsDialog extends JDialog {

    private static final Color C_PRIMARY = new Color(13, 27, 100);

    public ColumnStatsDialog(JFrame parent, String colName, List<String> values) {
        super(parent, "Column Statistics", true);
        setSize(360, 420);
        setLocationRelativeTo(parent);
        setResizable(false);
        buildUI(colName, values);
    }

    private void buildUI(String colName, List<String> values) {
        setLayout(new BorderLayout());

        JPanel hdr = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0,0,C_PRIMARY,getWidth(),0,new Color(26,82,160)));
                g2.fillRect(0,0,getWidth(),getHeight());
            }
        };
        hdr.setPreferredSize(new Dimension(0, 50));
        hdr.setBorder(new EmptyBorder(0, 18, 0, 18));
        JLabel titleLbl = new JLabel("Statistics: " + colName);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLbl.setForeground(Color.WHITE);
        hdr.add(titleLbl, BorderLayout.CENTER);
        add(hdr, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(20, 24, 20, 24));

        calculateAndDisplayStats(content, colName, values);

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        btns.setBackground(new Color(228, 234, 250));
        btns.setBorder(BorderFactory.createMatteBorder(1,0,0,0,new Color(195,210,235)));

        JButton close = new JButton("Close") {
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
        close.setOpaque(false); close.setContentAreaFilled(false); close.setBorderPainted(false);
        close.setFocusPainted(false); close.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        close.setPreferredSize(new Dimension(100,34));
        close.addActionListener(e -> dispose());
        btns.add(close);

        add(btns, BorderLayout.SOUTH);
    }

    private void calculateAndDisplayStats(JPanel p, String colName, List<String> values) {
        int count = 0;
        Set<String> unique = new HashSet<>();
        boolean allNumeric = true;
        double sum = 0;
        double min = Double.MAX_VALUE;
        double max = -Double.MAX_VALUE;

        for (String v : values) {
            if (v == null || v.trim().isEmpty()) continue;
            count++;
            unique.add(v);
            try {
                double d = Double.parseDouble(v);
                sum += d;
                if (d < min) min = d;
                if (d > max) max = d;
            } catch (NumberFormatException e) {
                allNumeric = false;
            }
        }

        boolean isId = colName.toLowerCase().contains("id") || colName.toLowerCase().contains("no");

        addStatLine(p, "Total Count", String.valueOf(count));
        addStatLine(p, "Unique Values", String.valueOf(unique.size()));

        if (count > 0 && allNumeric && !isId) {
            addStatLine(p, "Sum", formatNumber(sum));
            addStatLine(p, "Average", formatNumber(sum / count));
            addStatLine(p, "Minimum", formatNumber(min));
            addStatLine(p, "Maximum", formatNumber(max));
        } else if (count == 0) {
            JLabel info = new JLabel("No data available in this column.");
            info.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            info.setForeground(Color.GRAY);
            p.add(info);
        }
        
        p.add(Box.createVerticalGlue());
    }

    private String formatNumber(double value) {
        if (value == (long) value) {
            return String.format("%d", (long) value);
        }
        return String.format("%.2f", value);
    }

    private void addStatLine(JPanel p, String label, String value) {
        JPanel line = new JPanel(new BorderLayout());
        line.setOpaque(false);
        line.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        line.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 235, 245)),
            new EmptyBorder(10, 0, 10, 0)
        ));

        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(new Color(50, 62, 108));

        JLabel v = new JLabel(value);
        v.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        v.setForeground(new Color(10, 20, 40));

        line.add(l, BorderLayout.WEST);
        line.add(v, BorderLayout.EAST);
        p.add(line);
    }
}
