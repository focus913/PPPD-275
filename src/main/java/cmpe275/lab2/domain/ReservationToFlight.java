package cmpe275.lab2.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.ControllerAdvice;

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

    @Column(name = "price")
    private double price;

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof  ReservationToFlight)) {
            return false;
        }
        ReservationToFlight other = (ReservationToFlight)o;
        return reservationId.equals(other.reservationId) && flightNumber.equals(other.flightNumber);
    }
}
