package cmpe275.lab2.domain;

import javax.persistence.*;

@Entity
@Table(name = "flight_to_passenger")
public class FlightToPassenger {

    @Id
    @GeneratedValue
    private long id;

    @Column(name = "flight_number")
    private String flightNumber;

    @Column(name = "passenger_id")
    private String passengerId;

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public void setPassengerId(String passengerId) {
        this.passengerId = passengerId;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public String getPassengerId() {
        return passengerId;
    }
}
