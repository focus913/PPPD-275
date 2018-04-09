package cmpe275.lab2.domain;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import javax.persistence.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "reservation")
@JacksonXmlRootElement(localName = "reservation")
public class Reservation {
    @Id
    @Column(name = "reservation_id")
    private String reservationNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passenger_id")
    private Passenger passenger;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "reservation_to_flight",
               joinColumns = { @JoinColumn(name = "reservation_id")},
               inverseJoinColumns = {@JoinColumn(name = "flight_number")})
    @JacksonXmlElementWrapper(localName = "flights")
    @JacksonXmlProperty(localName = "flight")
    private List<Flight> flights = new LinkedList<>();

    private double price;

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    @JsonView(Views.Public.class)
    public String getReservationNumber() {
        return reservationNumber;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    @JsonView(Views.Private2.class)
    public Passenger getPassenger() {
        return passenger;
    }

    @JsonView(Views.Public.class)
    public List<Flight> getFlights() {
        return flights;
    }

    public void setFlights(List<Flight> flights) {
        this.flights = flights;
    }

    public Reservation() {
        this.reservationNumber = "reservation" + UUID.randomUUID().toString().replaceAll("-", "");
    }
}
