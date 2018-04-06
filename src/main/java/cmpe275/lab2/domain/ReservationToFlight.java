package cmpe275.lab2.domain;

import javax.persistence.*;

@Entity
@Table(name = "reservation_to_flight")
public class ReservationToFlight {

    @Id @GeneratedValue
    private long id;

    @Column(name = "reservation_id")
    private String reservationId;

    @Column(name = "flight_number")
    private String flightNumber;

    public String getFlightNumber() {
        return flightNumber;
    }

    public String getReservationId() {
        return reservationId;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }
}
