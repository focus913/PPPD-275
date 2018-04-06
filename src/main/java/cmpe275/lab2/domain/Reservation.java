package cmpe275.lab2.domain;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "reservation")
public class Reservation {
    @Id
    @Column(name = "reservation_id")
    private String reservationId;

    @ManyToOne
    @JoinColumn(name = "passenger_id")
    private Passenger passenger;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "reservation_to_flight",
               joinColumns = { @JoinColumn(name = "reservation_id")},
               inverseJoinColumns = {@JoinColumn(name = "flight_number")})
    private List<Flight> flights = new LinkedList<>();

    public String getReservationId() {
        return reservationId;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public List<Flight> getFlights() {
        return flights;
    }

    public void setFlights(List<Flight> flights) {
        this.flights = flights;
    }

    public Reservation() {
        this.reservationId = "reservation" + UUID.randomUUID().toString().replaceAll("-", "");
    }
}
