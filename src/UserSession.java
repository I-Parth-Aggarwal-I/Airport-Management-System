public class UserSession {
    private static UserSession instance;

    public int     loginId;
    public String  username;
    public String  role;
    public Integer passengerId;
    public Integer flightStaffId;
    public Integer airportStaffId;

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) instance = new UserSession();
        return instance;
    }

    public void clear() { instance = null; }

    public boolean isAdmin()        { return "admin".equalsIgnoreCase(role); }
    public boolean isPassenger()    { return "passenger".equalsIgnoreCase(role); }
    public boolean isAirportStaff() { return "airport_staff".equalsIgnoreCase(role); }
    public boolean isFlightStaff()  { return "flight_staff".equalsIgnoreCase(role); }

    public String getRoleDisplay() {
        if (role == null) return "Unknown";
        String r = role.replace("_", " ");
        return Character.toUpperCase(r.charAt(0)) + r.substring(1);
    }
}
