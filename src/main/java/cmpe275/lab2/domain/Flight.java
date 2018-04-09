package cmpe275.lab2.domain;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import javax.persistence.*;
import java.util.Date;
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

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "flights")
    private List<Reservation> reservations = new LinkedList();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "flight_to_passenger",
                joinColumns = { @JoinColumn(name = "flight_number")},
                inverseJoinColumns = { @JoinColumn(name = "passenger_id")})
    @JacksonXmlElementWrapper(localName = "passengers")
    @JacksonXmlProperty(localName = "passenger")
    private List<Passenger> passengers = new LinkedList<>();

    @JsonView(Views.Public.class)
    public Date getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Date arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    @JsonView(Views.Public.class)
    public Date getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Date departureTime) {
        this.departureTime = departureTime;
    }

    @JsonView(Views.Public.class)
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @JsonView(Views.Public.class)
    public int getSeatsLeft() {
        return seatsLeft;
    }

    @JsonView(Views.Private3.class)
    public List<Passenger> getPassengers() {
        return passengers;
    }

    @JsonView(Views.Public.class)
    public Plane getPlane() {
        return plane;
    }

    @JsonView(Views.Public.class)
    public String getDescription() {
        return description;
    }

    @JsonView(Views.Public.class)
    public String getFlightNumber() {
        return flightNumber;
    }

    @JsonView(Views.Public.class)
    public String getOrigin() {
        return origin;
    }

    @JsonView(Views.Public.class)
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
