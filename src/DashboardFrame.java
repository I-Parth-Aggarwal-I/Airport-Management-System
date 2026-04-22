import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class DashboardFrame extends JFrame {

    private static final Color C_PRIMARY = new Color(13, 27, 100);
    private static final Color C_SECOND = new Color(26, 82, 160);
    private static final Color C_BG = new Color(238, 243, 252);

    public DashboardFrame() {
        setTitle("Airport Management System – Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        add(buildHeader(), BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(buildContent());
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBackground(C_BG);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel hdr = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0, 0, C_PRIMARY, getWidth(), 0, C_SECOND));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        hdr.setPreferredSize(new Dimension(0, 66));
        hdr.setBorder(new EmptyBorder(0, 22, 0, 22));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);
        JLabel ico = new JLabel("✈");
        ico.setFont(new Font("Arial", Font.PLAIN, 30));
        ico.setForeground(Color.WHITE);
        JPanel tb = new JPanel();
        tb.setOpaque(false);
        tb.setLayout(new BoxLayout(tb, BoxLayout.Y_AXIS));
        JLabel t1 = new JLabel("Airport Management System");
        t1.setFont(new Font("Segoe UI", Font.BOLD, 17));
        t1.setForeground(Color.WHITE);
        JLabel t2 = new JLabel("Dashboard");
        t2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        t2.setForeground(new Color(180, 205, 255));
        tb.add(t1);
        tb.add(t2);
        left.add(ico);
        left.add(tb);

        UserSession s = UserSession.getInstance();
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);
        JPanel ub = new JPanel();
        ub.setOpaque(false);
        ub.setLayout(new BoxLayout(ub, BoxLayout.Y_AXIS));
        JLabel u1 = new JLabel(s.username);
        u1.setFont(new Font("Segoe UI", Font.BOLD, 13));
        u1.setForeground(Color.WHITE);
        u1.setAlignmentX(RIGHT_ALIGNMENT);
        JLabel u2 = new JLabel(s.getRoleDisplay());
        u2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        u2.setForeground(new Color(180, 205, 255));
        u2.setAlignmentX(RIGHT_ALIGNMENT);
        ub.add(u1);
        ub.add(u2);

        JButton logoutBtn = makeFlatBtn("⏻  Logout");
        logoutBtn.setPreferredSize(new Dimension(105, 34));
        logoutBtn.addActionListener(e -> {
            s.clear();
            dispose();
            new LoginFrame().setVisible(true);
        });

        right.add(ub);
        right.add(logoutBtn);
        hdr.add(left, BorderLayout.WEST);
        hdr.add(right, BorderLayout.EAST);
        return hdr;
    }

    private JPanel buildContent() {
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(C_BG);
        content.setBorder(new EmptyBorder(26, 30, 30, 30));

        UserSession s = UserSession.getInstance();
        JLabel welcome = new JLabel("Welcome, " + s.username + "   |   " + s.getRoleDisplay());
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 22));
        welcome.setForeground(C_PRIMARY);
        welcome.setBorder(new EmptyBorder(0, 0, 5, 0));

        JLabel hint = new JLabel("Click any module card to view, search, or manage records.");
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        hint.setForeground(new Color(95, 108, 145));
        hint.setBorder(new EmptyBorder(0, 0, 22, 0));

        JPanel topSec = new JPanel();
        topSec.setOpaque(false);
        topSec.setLayout(new BoxLayout(topSec, BoxLayout.Y_AXIS));
        topSec.add(welcome);
        topSec.add(hint);
        content.add(topSec, BorderLayout.NORTH);

        List<ModuleConfig> modules = buildModules(s);

        int cols = 4;
        int rows = (int) Math.ceil((double) modules.size() / cols);
        JPanel grid = new JPanel(new GridLayout(rows, cols, 18, 18));
        grid.setOpaque(false);
        for (ModuleConfig m : modules)
            grid.add(buildCard(m));

        for (int i = modules.size(); i < rows * cols; i++) {
            JPanel placeholder = new JPanel();
            placeholder.setOpaque(false);
            grid.add(placeholder);
        }

        JPanel gridWrapper = new JPanel(new BorderLayout());
        gridWrapper.setOpaque(false);
        gridWrapper.add(grid, BorderLayout.NORTH);
        content.add(gridWrapper, BorderLayout.CENTER);
        return content;
    }

    private JPanel buildCard(ModuleConfig cfg) {
        JPanel card = new JPanel() {
            boolean hover = false;
            {
                setOpaque(false);
                setPreferredSize(new Dimension(220, 175));
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) {
                        hover = true;
                        repaint();
                    }

                    public void mouseExited(MouseEvent e) {
                        hover = false;
                        repaint();
                    }

                    public void mouseClicked(MouseEvent e) {
                        dispose();
                        new DataTableFrame(cfg).setVisible(true);
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, hover ? 40 : 22));
                g2.fillRoundRect(3, 3, getWidth() - 2, getHeight() - 2, 18, 18);
                g2.setColor(hover ? new Color(244, 248, 255) : Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 18, 18);
                g2.setColor(cfg.color);
                g2.fillRoundRect(0, 0, getWidth() - 4, 7, 5, 5);
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
                FontMetrics fi = g2.getFontMetrics();
                int iw = fi.stringWidth(cfg.icon);
                g2.drawString(cfg.icon, ((getWidth() - 4) - iw) / 2, 74);
                g2.setColor(new Color(28, 40, 80));
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                FontMetrics ft = g2.getFontMetrics();
                int tw = ft.stringWidth(cfg.title);
                g2.drawString(cfg.title, ((getWidth() - 4) - tw) / 2, 100);
                String[] badges = cfg.getBadges();
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                FontMetrics fb = g2.getFontMetrics();
                int totalW = 0;
                for (String b : badges)
                    totalW += fb.stringWidth(b) + 14;
                totalW += (badges.length - 1) * 4;
                int bx = ((getWidth() - 4) - totalW) / 2;
                for (String b : badges) {
                    int bw = fb.stringWidth(b) + 14;
                    g2.setColor(cfg.color);
                    g2.fillRoundRect(bx, 115, bw, 18, 8, 8);
                    g2.setColor(Color.WHITE);
                    g2.drawString(b, bx + 7, 128);
                    bx += bw + 4;
                }
            }
        };
        return card;
    }

    private List<ModuleConfig> buildModules(UserSession s) {
        List<ModuleConfig> list = new ArrayList<>();

        if (s.isAdmin()) {
            list.add(airports(true, true, true));
            list.add(airlines(true, true, true));
            list.add(aircraft(true, true, true));
            list.add(gates(true, true, true));
            list.add(flights(true, true, true, null));
            list.add(passengers(true, true, true));
            list.add(bookings(true, true, true, null));
            list.add(tickets(true, true, true, null));
            list.add(payments(true, true, true, null));
            list.add(baggage(true, true, true, null));
            list.add(seats(true, true, true));
            list.add(airportStaff(true, true, true));
            list.add(flightStaff(true, true, true));
            list.add(users());

        } else if (s.isPassenger()) {
            list.add(flights(false, false, false, "WHERE f.status='Scheduled'"));
            Integer pid = s.passengerId;
            String pidFilter = pid != null ? "WHERE b.passenger_id=" + pid : "WHERE 1=0";
            String tktFilter = pid != null ? "WHERE t.passenger_id=" + pid : "WHERE 1=0";
            String payFilter = pid != null
                    ? "WHERE p.booking_id IN(SELECT booking_id FROM Booking WHERE passenger_id=" + pid + ")"
                    : "WHERE 1=0";
            String bagFilter = pid != null
                    ? "WHERE bg.ticket_id IN(SELECT ticket_id FROM Ticket WHERE passenger_id=" + pid + ")"
                    : "WHERE 1=0";
            list.add(bookings(true, true, false, pidFilter));
            list.add(tickets(false, false, false, tktFilter));
            list.add(payments(false, false, false, payFilter));
            list.add(baggage(false, false, false, bagFilter));

        } else if (s.isAirportStaff()) {
            list.add(airports(false, true, false));
            list.add(gates(true, true, true));
            list.add(flights(false, true, false, null));
            list.add(passengers(false, false, false));
            list.add(bookings(false, false, false, null));

        } else if (s.isFlightStaff()) {
            String myFlight = s.flightStaffId != null
                    ? "WHERE f.flight_id=(SELECT flight_id FROM FlightStaff WHERE staff_id=" + s.flightStaffId + ")"
                    : null;
            list.add(flights(false, true, false, myFlight));
            list.add(aircraft(false, false, false));
            list.add(airlines(false, false, false));
            list.add(seats(false, false, false));
        }
        return list;
    }

    private ModuleConfig airports(boolean a, boolean e, boolean d) {
        return new ModuleConfig("Airports", "🛫", "Airport", "airport_id",
                "SELECT airport_id,name,city,country,iata_code FROM Airport",
                new String[] { "ID", "Name", "City", "Country", "IATA" },
                new String[] { "airport_id", "name", "city", "country", "iata_code" },
                new String[] { "name", "city", "country", "iata_code" },
                new String[] { "Airport Name", "City", "Country", "IATA Code" },
                new Color(63, 81, 181), a, e, d);
    }

    private ModuleConfig airlines(boolean a, boolean e, boolean d) {
        return new ModuleConfig("Airlines", "🏢", "Airline", "airline_id",
                "SELECT airline_id,name,iata_code FROM Airline",
                new String[] { "ID", "Name", "IATA" },
                new String[] { "airline_id", "name", "iata_code" },
                new String[] { "name", "iata_code" },
                new String[] { "Airline Name", "IATA Code" },
                new Color(0, 150, 136), a, e, d);
    }

    private ModuleConfig aircraft(boolean a, boolean e, boolean d) {
        return new ModuleConfig("Aircraft", "✈", "Aircraft", "aircraft_id",
                "SELECT ac.aircraft_id,ac.model,ac.capacity,ac.airline_id,al.name AS airline_name" +
                        " FROM Aircraft ac LEFT JOIN Airline al ON ac.airline_id=al.airline_id",
                new String[] { "ID", "Model", "Capacity", "Airline ID", "Airline" },
                new String[] { "aircraft_id", "model", "capacity", "airline_id", "airline_name" },
                new String[] { "model", "capacity", "airline_id" },
                new String[] { "Model", "Capacity", "Airline ID" },
                new Color(33, 150, 243), a, e, d);
    }

    private ModuleConfig gates(boolean a, boolean e, boolean d) {
        return new ModuleConfig("Gates", "🚪", "Gate", "gate_id",
                "SELECT gate_id,terminal,gate_number FROM Gate",
                new String[] { "ID", "Terminal", "Gate No" },
                new String[] { "gate_id", "terminal", "gate_number" },
                new String[] { "terminal", "gate_number" },
                new String[] { "Terminal", "Gate Number" },
                new Color(156, 39, 176), a, e, d);
    }

    private ModuleConfig flights(boolean a, boolean e, boolean d, String extra) {
        String q = "SELECT f.flight_id,f.flight_number,f.departure_time,f.arrival_time,f.status," +
                "f.aircraft_id,a1.iata_code AS src,a2.iata_code AS dst,f.gate_id" +
                " FROM Flight f" +
                " LEFT JOIN Airport a1 ON f.source_airport=a1.airport_id" +
                " LEFT JOIN Airport a2 ON f.destination_airport=a2.airport_id" +
                (extra != null ? " " + extra : "");
        return new ModuleConfig("Flights", "✈️", "Flight", "flight_id", q,
                new String[] { "ID", "Flight No", "Departure", "Arrival", "Status", "Aircraft ID", "From", "To",
                        "Gate ID" },
                new String[] { "flight_id", "flight_number", "departure_time", "arrival_time", "status", "aircraft_id",
                        "src", "dst", "gate_id" },
                new String[] { "flight_number", "departure_time", "arrival_time", "status", "aircraft_id",
                        "source_airport", "destination_airport", "gate_id" },
                new String[] { "Flight Number", "Departure (YYYY-MM-DD HH:MM:SS)", "Arrival (YYYY-MM-DD HH:MM:SS)",
                        "Status", "Aircraft ID", "Source Airport ID", "Destination Airport ID", "Gate ID" },
                new Color(233, 30, 99), a, e, d);
    }

    private ModuleConfig passengers(boolean a, boolean e, boolean d) {
        return new ModuleConfig("Passengers", "👤", "Passenger", "passenger_id",
                "SELECT passenger_id,first_name,last_name,passport_no,email FROM Passenger",
                new String[] { "ID", "First Name", "Last Name", "Passport No", "Email" },
                new String[] { "passenger_id", "first_name", "last_name", "passport_no", "email" },
                new String[] { "first_name", "last_name", "passport_no", "email" },
                new String[] { "First Name", "Last Name", "Passport No", "Email" },
                new Color(255, 87, 34), a, e, d);
    }

    private ModuleConfig bookings(boolean a, boolean e, boolean d, String filter) {
        String q = "SELECT b.booking_id,b.booking_reference,b.status,b.passenger_id,b.flight_id,f.flight_number" +
                " FROM Booking b LEFT JOIN Flight f ON b.flight_id=f.flight_id" + (filter != null ? " " + filter : "");
        return new ModuleConfig("Bookings", "📋", "Booking", "booking_id", q,
                new String[] { "ID", "Reference", "Status", "Passenger ID", "Flight ID", "Flight No" },
                new String[] { "booking_id", "booking_reference", "status", "passenger_id", "flight_id",
                        "flight_number" },
                new String[] { "booking_reference", "status", "passenger_id", "flight_id" },
                new String[] { "Booking Reference", "Status (Confirmed/Cancelled)", "Passenger ID", "Flight ID" },
                new Color(255, 152, 0), a, e, d);
    }

    private ModuleConfig tickets(boolean a, boolean e, boolean d, String filter) {
        String q = "SELECT t.ticket_id,t.booking_id,t.passenger_id,t.seat_id,t.price,s.seat_number" +
                " FROM Ticket t LEFT JOIN Seat s ON t.seat_id=s.seat_id" + (filter != null ? " " + filter : "");
        return new ModuleConfig("Tickets", "🎫", "Ticket", "ticket_id", q,
                new String[] { "ID", "Booking ID", "Passenger ID", "Seat ID", "Price", "Seat No" },
                new String[] { "ticket_id", "booking_id", "passenger_id", "seat_id", "price", "seat_number" },
                new String[] { "booking_id", "passenger_id", "seat_id", "price" },
                new String[] { "Booking ID", "Passenger ID", "Seat ID", "Price" },
                new Color(0, 188, 212), a, e, d);
    }

    private ModuleConfig payments(boolean a, boolean e, boolean d, String filter) {
        String q = "SELECT payment_id,booking_id,amount,payment_status FROM Payment"
                + (filter != null ? " " + filter : "");
        return new ModuleConfig("Payments", "💳", "Payment", "payment_id", q,
                new String[] { "ID", "Booking ID", "Amount", "Status" },
                new String[] { "payment_id", "booking_id", "amount", "payment_status" },
                new String[] { "booking_id", "amount", "payment_status" },
                new String[] { "Booking ID", "Amount", "Status (Paid/Pending/Failed)" },
                new Color(76, 175, 80), a, e, d);
    }

    private ModuleConfig baggage(boolean a, boolean e, boolean d, String filter) {
        String q = "SELECT baggage_id,ticket_id,weight FROM Baggage" + (filter != null ? " " + filter : "");
        return new ModuleConfig("Baggage", "🧳", "Baggage", "baggage_id", q,
                new String[] { "ID", "Ticket ID", "Weight (kg)" },
                new String[] { "baggage_id", "ticket_id", "weight" },
                new String[] { "ticket_id", "weight" },
                new String[] { "Ticket ID", "Weight (kg)" },
                new Color(121, 85, 72), a, e, d);
    }

    private ModuleConfig seats(boolean a, boolean e, boolean d) {
        return new ModuleConfig("Seats", "💺", "Seat", "seat_id",
                "SELECT s.seat_id,s.aircraft_id,s.seat_number,ac.model FROM Seat s LEFT JOIN Aircraft ac ON s.aircraft_id=ac.aircraft_id",
                new String[] { "ID", "Aircraft ID", "Seat No", "Aircraft" },
                new String[] { "seat_id", "aircraft_id", "seat_number", "model" },
                new String[] { "aircraft_id", "seat_number" },
                new String[] { "Aircraft ID", "Seat Number" },
                new Color(96, 125, 139), a, e, d);
    }

    private ModuleConfig airportStaff(boolean a, boolean e, boolean d) {
        return new ModuleConfig("Airport Staff", "👷", "AirportStaff", "staff_id",
                "SELECT ast.staff_id,ast.first_name,ast.last_name,ast.role,ast.airport_id,ap.name AS airport_name" +
                        " FROM AirportStaff ast LEFT JOIN Airport ap ON ast.airport_id=ap.airport_id",
                new String[] { "ID", "First Name", "Last Name", "Role", "Airport ID", "Airport" },
                new String[] { "staff_id", "first_name", "last_name", "role", "airport_id", "airport_name" },
                new String[] { "first_name", "last_name", "role", "airport_id" },
                new String[] { "First Name", "Last Name", "Role", "Airport ID" },
                new Color(63, 81, 181), a, e, d);
    }

    private ModuleConfig flightStaff(boolean a, boolean e, boolean d) {
        return new ModuleConfig("Flight Staff", "👨‍✈️", "FlightStaff", "staff_id",
                "SELECT fs.staff_id,fs.first_name,fs.last_name,fs.role,fs.flight_id,f.flight_number" +
                        " FROM FlightStaff fs LEFT JOIN Flight f ON fs.flight_id=f.flight_id",
                new String[] { "ID", "First Name", "Last Name", "Role", "Flight ID", "Flight No" },
                new String[] { "staff_id", "first_name", "last_name", "role", "flight_id", "flight_number" },
                new String[] { "first_name", "last_name", "role", "flight_id" },
                new String[] { "First Name", "Last Name", "Role", "Flight ID" },
                new Color(0, 150, 136), a, e, d);
    }

    private ModuleConfig users() {
        return new ModuleConfig("Users", "🔑", "Login", "login_id",
                "SELECT login_id,username,role,passenger_id,flight_staff_id,airport_staff_id FROM Login",
                new String[] { "ID", "Username", "Role", "Passenger ID", "Flight Staff ID", "Airport Staff ID" },
                new String[] { "login_id", "username", "role", "passenger_id", "flight_staff_id", "airport_staff_id" },
                new String[] { "username", "password", "role", "passenger_id", "flight_staff_id", "airport_staff_id" },
                new String[] { "Username", "Password", "Role", "Passenger ID", "Flight Staff ID", "Airport Staff ID" },
                new Color(103, 58, 183), true, true, true);
    }

    private JButton makeFlatBtn(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(200, 50, 50, 190) : new Color(255, 255, 255, 40));
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
