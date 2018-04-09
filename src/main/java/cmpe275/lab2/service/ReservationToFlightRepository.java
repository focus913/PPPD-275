package cmpe275.lab2.service;

import cmpe275.lab2.domain.ReservationToFlight;
import org.springframework.data.repository.CrudRepository;

public interface ReservationToFlightRepository extends CrudRepository<ReservationToFlight, Long> {
    void deleteAllByReservationIdAndFlightNumber(String reservationId, String flightNumber);
    Iterable<ReservationToFlight> findAllByReservationId(String reservationId);
}
