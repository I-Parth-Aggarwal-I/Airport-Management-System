
CREATE DATABASE IF NOT EXISTS airportms;
USE airportms;

CREATE TABLE IF NOT EXISTS Airport (
    airport_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100),
    city VARCHAR(50),
    country VARCHAR(50),
    iata_code VARCHAR(10) UNIQUE
);

CREATE TABLE IF NOT EXISTS Airline (
    airline_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100),
    iata_code VARCHAR(10) UNIQUE
);

CREATE TABLE IF NOT EXISTS Aircraft (
    aircraft_id INT PRIMARY KEY AUTO_INCREMENT,
    model VARCHAR(50),
    capacity INT,
    airline_id INT,
    FOREIGN KEY (airline_id) REFERENCES Airline(airline_id)
);

CREATE TABLE IF NOT EXISTS Gate (
    gate_id INT PRIMARY KEY AUTO_INCREMENT,
    terminal VARCHAR(10),
    gate_number VARCHAR(10)
);

CREATE TABLE IF NOT EXISTS Flight (
    flight_id INT PRIMARY KEY AUTO_INCREMENT,
    flight_number VARCHAR(20),
    departure_time DATETIME,
    arrival_time DATETIME,
    status VARCHAR(20),
    aircraft_id INT,
    source_airport INT,
    destination_airport INT,
    gate_id INT,
    FOREIGN KEY (aircraft_id) REFERENCES Aircraft(aircraft_id),
    FOREIGN KEY (source_airport) REFERENCES Airport(airport_id),
    FOREIGN KEY (destination_airport) REFERENCES Airport(airport_id),
    FOREIGN KEY (gate_id) REFERENCES Gate(gate_id)
);

CREATE TABLE IF NOT EXISTS Passenger (
    passenger_id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    passport_no VARCHAR(50) UNIQUE,
    email VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS AirportStaff (
    staff_id   INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50),
    last_name  VARCHAR(50),
    role       VARCHAR(50),
    airport_id INT,
    FOREIGN KEY (airport_id) REFERENCES Airport(airport_id)
);

CREATE TABLE IF NOT EXISTS FlightStaff (
    staff_id   INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50),
    last_name  VARCHAR(50),
    role       VARCHAR(50),
    flight_id  INT,
    FOREIGN KEY (flight_id) REFERENCES Flight(flight_id)
);

CREATE TABLE IF NOT EXISTS Login (
    login_id        INT PRIMARY KEY AUTO_INCREMENT,
    username        VARCHAR(50)  UNIQUE,
    password        VARCHAR(255),
    role            VARCHAR(50),
    passenger_id    INT  DEFAULT NULL,
    flight_staff_id INT  DEFAULT NULL,
    airport_staff_id INT DEFAULT NULL,
    FOREIGN KEY (passenger_id)     REFERENCES Passenger(passenger_id),
    FOREIGN KEY (flight_staff_id)  REFERENCES FlightStaff(staff_id),
    FOREIGN KEY (airport_staff_id) REFERENCES AirportStaff(staff_id)
);

CREATE TABLE IF NOT EXISTS Booking (
    booking_id INT PRIMARY KEY AUTO_INCREMENT,
    booking_reference VARCHAR(50),
    status VARCHAR(20),
    passenger_id INT,
    flight_id INT,
    FOREIGN KEY (passenger_id) REFERENCES Passenger(passenger_id),
    FOREIGN KEY (flight_id) REFERENCES Flight(flight_id)
);

CREATE TABLE IF NOT EXISTS Seat (
    seat_id INT PRIMARY KEY AUTO_INCREMENT,
    aircraft_id INT,
    seat_number VARCHAR(10),
    FOREIGN KEY (aircraft_id) REFERENCES Aircraft(aircraft_id)
);

CREATE TABLE IF NOT EXISTS Ticket (
    ticket_id INT PRIMARY KEY AUTO_INCREMENT,
    booking_id INT,
    passenger_id INT,
    seat_id INT,
    price DECIMAL(10,2),
    FOREIGN KEY (booking_id) REFERENCES Booking(booking_id),
    FOREIGN KEY (passenger_id) REFERENCES Passenger(passenger_id),
    FOREIGN KEY (seat_id) REFERENCES Seat(seat_id)
);

CREATE TABLE IF NOT EXISTS Payment (
    payment_id INT PRIMARY KEY AUTO_INCREMENT,
    booking_id INT,
    amount DECIMAL(10,2),
    payment_status VARCHAR(20),
    FOREIGN KEY (booking_id) REFERENCES Booking(booking_id)
);

CREATE TABLE IF NOT EXISTS Baggage (
    baggage_id INT PRIMARY KEY AUTO_INCREMENT,
    ticket_id INT,
    weight DECIMAL(5,2),
    FOREIGN KEY (ticket_id) REFERENCES Ticket(ticket_id)
);
