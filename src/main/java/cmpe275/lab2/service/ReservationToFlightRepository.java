package cmpe275.lab2.service;

import cmpe275.lab2.domain.ReservationToFlight;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface ReservationToFlightRepository extends CrudRepository<ReservationToFlight, Long> {
    void deleteByReservationIdAndFlightNumber(String reservationId, String flightNumber);
    Iterable<ReservationToFlight> findAllByReservationId(String reservationId);
    Iterable<ReservationToFlight> findAllByFlightNumber(String flightNumber);
    void deleteByFlightNumberAndReservationId(String flightNumber, String reservationId);
}
