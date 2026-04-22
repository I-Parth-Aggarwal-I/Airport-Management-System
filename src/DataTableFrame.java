import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.util.*;
import java.util.List;

public class DataTableFrame extends JFrame {

    private final ModuleConfig cfg;
    private DefaultTableModel model;
    private JTable table;
    private final List<String[]> allRows = new ArrayList<>();

    private JComboBox<String> colBox;
    private JComboBox<String> opBox;
    private JTextField searchTf;

    private JLabel statusLbl;

    private JPanel tagsPanel;
    private final List<SearchTag> activeTags = new ArrayList<>();

    private static class SearchTag {
        int colIdx;
        String colName;
        String operator;
        String value;

        SearchTag(int c, String cn, String o, String v) {
            this.colIdx = c;
            this.colName = cn;
            this.operator = o;
            this.value = v;
        }
    }

    private static final Color C_PRIMARY = new Color(13, 27, 100);
    private static final Color C_BG = new Color(238, 243, 252);
    private static final Color C_STRIPE = new Color(246, 249, 255);

    public DataTableFrame(ModuleConfig cfg) {
        this.cfg = cfg;
        setTitle("Airport MS  –  " + cfg.title);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1150, 680);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        buildUI();
        loadData();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        add(buildHeader(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        JPanel hdr = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0, 0, C_PRIMARY, getWidth(), 0, new Color(26, 82, 160)));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        hdr.setPreferredSize(new Dimension(0, 56));
        hdr.setBorder(new EmptyBorder(0, 20, 0, 20));

        JLabel title = new JLabel(cfg.icon + "   " + cfg.title + " Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);

        JButton back = ghostBtn("← Back to Dashboard");
        back.setPreferredSize(new Dimension(160, 33));
        back.addActionListener(e -> {
            dispose();
            new DashboardFrame().setVisible(true);
        });

        hdr.add(title, BorderLayout.WEST);
        hdr.add(back, BorderLayout.EAST);
        return hdr;
    }

    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(C_BG);
        center.setBorder(new EmptyBorder(14, 18, 8, 18));
        center.add(buildSearchPanel(), BorderLayout.NORTH);
        center.add(buildTablePanel(), BorderLayout.CENTER);
        return center;
    }

    private JPanel buildSearchPanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);
        container.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(180, 195, 225)),
                        " Advanced Search ",
                        TitledBorder.LEFT, TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 12), C_PRIMARY),
                new EmptyBorder(2, 6, 6, 6)));

        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        p.setOpaque(false);

        p.add(lbl("Column:"));
        colBox = new JComboBox<>(cfg.columnHeaders);
        colBox.setPreferredSize(new Dimension(145, 30));
        colBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        p.add(colBox);

        p.add(lbl("Condition:"));
        opBox = new JComboBox<>(new String[] {
                "Contains", "Equals", "Starts With", "Ends With", "Greater Than", "Less Than" });
        opBox.setPreferredSize(new Dimension(120, 30));
        opBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        p.add(opBox);

        p.add(lbl("Value:"));
        searchTf = new JTextField();
        searchTf.setPreferredSize(new Dimension(185, 30));
        searchTf.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        p.add(searchTf);

        JButton searchBtn = actionBtn("＋ Add Filter", new Color(13, 71, 161), Color.WHITE);
        searchBtn.addActionListener(e -> addSearchTag());
        searchTf.addActionListener(e -> addSearchTag());
        p.add(searchBtn);

        JButton clearBtn = actionBtn("✕ Clear Filters", new Color(95, 108, 145), Color.WHITE);
        clearBtn.setPreferredSize(new Dimension(130, 33));
        clearBtn.addActionListener(e -> clearAllTags());
        p.add(clearBtn);

        statusLbl = new JLabel();
        statusLbl.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        statusLbl.setForeground(new Color(95, 108, 145));
        p.add(statusLbl);

        container.add(p, BorderLayout.NORTH);

        tagsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        tagsPanel.setOpaque(false);
        container.add(tagsPanel, BorderLayout.CENTER);

        return container;
    }

    private JPanel buildTablePanel() {
        model = new DefaultTableModel(cfg.columnHeaders, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(30);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(205, 225, 255));
        table.setSelectionForeground(new Color(10, 20, 60));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(225, 232, 250));
        table.getTableHeader().setForeground(C_PRIMARY);
        table.getTableHeader().setPreferredSize(new Dimension(0, 36));
        table.getTableHeader().setReorderingAllowed(false);

        table.getTableHeader().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int col = table.columnAtPoint(e.getPoint());
                    if (col >= 0) {
                        showColumnStats(col);
                    }
                }
            }
        });

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                if (!sel)
                    setBackground(r % 2 == 0 ? Color.WHITE : C_STRIPE);
                setBorder(new EmptyBorder(0, 8, 0, 8));
                return this;
            }
        });

        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(new Color(195, 210, 235)));
        sp.setBackground(Color.WHITE);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.setBorder(new EmptyBorder(10, 0, 0, 0));
        wrap.add(sp);
        return wrap;
    }

    private void showColumnStats(int colIndex) {
        String colName = table.getColumnName(colIndex);
        List<String> values = new ArrayList<>();
        for (int r = 0; r < table.getRowCount(); r++) {
            Object val = table.getValueAt(r, colIndex);
            values.add(val != null ? val.toString() : "");
        }
        ColumnStatsDialog dlg = new ColumnStatsDialog(this, colName, values);
        dlg.setVisible(true);
    }

    private JPanel buildFooter() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bar.setBackground(new Color(225, 232, 248));
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(195, 210, 235)));

        JButton refreshBtn = actionBtn("↻  Refresh", new Color(75, 95, 135), Color.WHITE);
        refreshBtn.addActionListener(e -> loadData());
        bar.add(refreshBtn);

        if (cfg.canAdd) {
            JButton addBtn = actionBtn("＋  Add New", new Color(27, 130, 80), Color.WHITE);
            addBtn.addActionListener(e -> openAdd());
            bar.add(addBtn);
        }
        if (cfg.canEdit) {
            JButton editBtn = actionBtn("✎  Edit", new Color(13, 71, 161), Color.WHITE);
            editBtn.addActionListener(e -> openEdit());
            bar.add(editBtn);
        }
        if (cfg.canDelete) {
            JButton delBtn = actionBtn("✕  Delete", new Color(175, 25, 25), Color.WHITE);
            delBtn.addActionListener(e -> doDelete());
            bar.add(delBtn);
        }

        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(new Color(225, 232, 248));
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(195, 210, 235)));
        JLabel recLbl = new JLabel("  " + cfg.title + "  |  Double-click a row to view details");
        recLbl.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        recLbl.setForeground(new Color(95, 108, 145));
        footer.add(recLbl, BorderLayout.WEST);
        footer.add(bar, BorderLayout.EAST);
        return footer;
    }

    void loadData() {
        allRows.clear();
        try (Connection c = DatabaseConnection.getConnection();
                Statement st = c.createStatement();
                ResultSet rs = st.executeQuery(cfg.selectQuery)) {

            while (rs.next()) {
                String[] row = new String[cfg.columnDbNames.length];
                for (int i = 0; i < cfg.columnDbNames.length; i++) {
                    try {
                        Object v = rs.getObject(cfg.columnDbNames[i]);
                        row[i] = v != null ? v.toString() : "";
                    } catch (SQLException ex) {
                        row[i] = "";
                    }
                }
                allRows.add(row);
            }
            applyFilters();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading data:\n" + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateTable(List<String[]> rows) {
        model.setRowCount(0);
        for (String[] r : rows)
            model.addRow(r);
    }

    private void addSearchTag() {
        String val = searchTf.getText().trim();
        if (val.isEmpty())
            return;

        int colIdx = colBox.getSelectedIndex();
        String colName = (String) colBox.getSelectedItem();
        String op = (String) opBox.getSelectedItem();

        SearchTag tag = new SearchTag(colIdx, colName, op, val);
        activeTags.add(tag);
        searchTf.setText("");

        renderTags();
        applyFilters();
    }

    private void clearAllTags() {
        activeTags.clear();
        searchTf.setText("");
        renderTags();
        applyFilters();
    }

    private void renderTags() {
        tagsPanel.removeAll();
        for (SearchTag tag : activeTags) {
            JPanel tagPill = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 2));
            tagPill.setOpaque(true);
            tagPill.setBackground(new Color(248, 250, 252));
            tagPill.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(210, 220, 230), 1, true),
                    new EmptyBorder(2, 4, 2, 4)));

            JLabel lbl = new JLabel("+ " + tag.colName + " " + tag.operator + " " + tag.value);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lbl.setForeground(new Color(13, 71, 161));

            JLabel closeLbl = new JLabel(" × ");
            closeLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
            closeLbl.setForeground(new Color(100, 110, 130));
            closeLbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            closeLbl.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    activeTags.remove(tag);
                    renderTags();
                    applyFilters();
                }

                public void mouseEntered(java.awt.event.MouseEvent e) {
                    closeLbl.setForeground(Color.RED);
                }

                public void mouseExited(java.awt.event.MouseEvent e) {
                    closeLbl.setForeground(new Color(100, 110, 130));
                }
            });

            tagPill.add(lbl);
            tagPill.add(closeLbl);
            tagsPanel.add(tagPill);
        }
        tagsPanel.revalidate();
        tagsPanel.repaint();
    }

    private void applyFilters() {
        if (activeTags.isEmpty()) {
            populateTable(allRows);
            setStatus("Showing all " + allRows.size() + " records.");
            return;
        }

        Map<Integer, List<SearchTag>> tagsByCol = new HashMap<>();
        for (SearchTag tag : activeTags) {
            tagsByCol.computeIfAbsent(tag.colIdx, k -> new ArrayList<>()).add(tag);
        }

        List<String[]> result = new ArrayList<>();
        for (String[] row : allRows) {
            boolean rowMatches = true;
            for (Map.Entry<Integer, List<SearchTag>> entry : tagsByCol.entrySet()) {
                int colIdx = entry.getKey();
                if (colIdx >= row.length) {
                    rowMatches = false;
                    break;
                }

                String cell = row[colIdx].toLowerCase();
                List<SearchTag> colTags = entry.getValue();

                List<SearchTag> rangeTags = new ArrayList<>();
                List<SearchTag> matchTags = new ArrayList<>();
                for (SearchTag tag : colTags) {
                    if (tag.operator.equals("Greater Than") || tag.operator.equals("Less Than")) {
                        rangeTags.add(tag);
                    } else {
                        matchTags.add(tag);
                    }
                }

                boolean rangeMatch = true;
                for (SearchTag tag : rangeTags) {
                    if (!matchesCondition(row[colIdx], cell, tag.operator, tag.value.toLowerCase(), tag.value)) {
                        rangeMatch = false;
                        break;
                    }
                }

                boolean matchTagsMatch = matchTags.isEmpty();
                for (SearchTag tag : matchTags) {
                    if (matchesCondition(row[colIdx], cell, tag.operator, tag.value.toLowerCase(), tag.value)) {
                        matchTagsMatch = true;
                        break;
                    }
                }

                if (!(rangeMatch && matchTagsMatch)) {
                    rowMatches = false; // AND logic across columns and range checks
                    break;
                }
            }
            if (rowMatches)
                result.add(row);
        }
        populateTable(result);
        setStatus("Found " + result.size() + " matching record(s) out of " + allRows.size() + ".");
    }

    private boolean matchesCondition(String rawCell, String cell, String op, String sv, String rawVal) {
        switch (op) {
            case "Contains":
                return cell.contains(sv);
            case "Equals":
                return cell.equals(sv);
            case "Starts With":
                return cell.startsWith(sv);
            case "Ends With":
                return cell.endsWith(sv);
            case "Greater Than":
                try {
                    return Double.parseDouble(rawCell) > Double.parseDouble(rawVal);
                } catch (NumberFormatException e) {
                    return cell.compareTo(sv) > 0;
                }
            case "Less Than":
                try {
                    return Double.parseDouble(rawCell) < Double.parseDouble(rawVal);
                } catch (NumberFormatException e) {
                    return cell.compareTo(sv) < 0;
                }
        }
        return false;
    }

    private void openAdd() {
        FormDialog dlg = new FormDialog(this, "Add New " + cfg.title, cfg, null);
        dlg.setVisible(true);
        if (dlg.isSaved())
            loadData();
    }

    private void openEdit() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a row to edit.");
            return;
        }
        String[] rowData = new String[cfg.columnDbNames.length];
        for (int i = 0; i < cfg.columnDbNames.length; i++)
            rowData[i] = (String) model.getValueAt(row, i);
        String pkVal = rowData[cfg.getPrimaryKeyIndex()];
        FormDialog dlg = new FormDialog(this, "Edit " + cfg.title + "  [ID: " + pkVal + "]", cfg, rowData);
        dlg.setVisible(true);
        if (dlg.isSaved())
            loadData();
    }

    private void doDelete() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a row to delete.");
            return;
        }
        String pkVal = (String) model.getValueAt(row, cfg.getPrimaryKeyIndex());
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete record with ID = " + pkVal + "?\nThis cannot be undone.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION)
            return;
        try (Connection c = DatabaseConnection.getConnection()) {
            PreparedStatement ps = c.prepareStatement(
                    "DELETE FROM " + cfg.tableName + " WHERE " + cfg.primaryKey + "=?");
            ps.setString(1, pkVal);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Record deleted successfully.");
            loadData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error:\n" + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setStatus(String msg) {
        if (statusLbl != null)
            statusLbl.setText("  " + msg);
    }

    private JLabel lbl(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return l;
    }

    private JButton actionBtn(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = getModel().isPressed() ? bg.darker()
                        : getModel().isRollover() ? bg.brighter() : bg;
                g2.setColor(c);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(fg);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            }
        };
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(118, 33));
        return btn;
    }

    private JButton ghostBtn(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(255, 255, 255, 65) : new Color(255, 255, 255, 30));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            }
        };
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
