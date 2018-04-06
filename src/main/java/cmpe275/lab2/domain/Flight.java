package cmpe275.lab2.domain;

import javax.persistence.*;
import java.sql.Date;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "flight")
public class Flight {

    @Id
    @Column(name = "flight_number")
    private String flightNumber;

    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "origin", nullable = false)
    private String origin;

    @Column(name = "destination", nullable = false)
    private String to;

    @Column(name = "departure_time", nullable = false)
    private Date departureTime;

    @Column(name = "arrival_time", nullable = false)
    private Date arrivalTime;

    @Column(name = "seats_left", nullable = false)
    private int seatsLeft;

    @Column(name = "description")
    private String description;

    @Embedded
    private Plane plane;

    @ManyToMany(mappedBy = "flights")
    private List<Reservation> reservations = new LinkedList();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "flight_to_passenger",
                joinColumns = { @JoinColumn(name = "flight_number")},
                inverseJoinColumns = { @JoinColumn(name = "passenger_id")})
    private List<Passenger> passengers = new LinkedList<>();

    public Date getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Date arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public Date getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Date departureTime) {
        this.departureTime = departureTime;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getSeatsLeft() {
        return seatsLeft;
    }

    public List<Passenger> getPassengers() {
        return passengers;
    }

    public Plane getPlane() {
        return plane;
    }

    public String getDescription() {
        return description;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public String getOrigin() {
        return origin;
    }

    public String getTo() {
        return to;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public void setPlane(Plane plane) {
        this.plane = plane;
    }

    public void setPassengers(List<Passenger> passengers) {
        this.passengers = passengers;
    }

    public void setSeatsLeft(int seatsLeft) {
        this.seatsLeft = seatsLeft;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Flight() {
    }
}
