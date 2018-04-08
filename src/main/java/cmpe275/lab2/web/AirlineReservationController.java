package cmpe275.lab2.web;

import java.sql.Date;
import java.util.Optional;

import cmpe275.lab2.domain.*;
import cmpe275.lab2.service.*;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class AirlineReservationController {

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private ReservationToFlightRepository reservationToFlightRepository;

    @Autowired
    private FlightToPassengerRepository flightToPassengerRepository;

    /*public @ResponseBody Passenger getPassenger() {

    }*/

    @GetMapping(path = "/add")
    public @ResponseBody String addPassenger(
            @RequestParam String firstname,
            @RequestParam String lastname,
            @RequestParam int age,
            @RequestParam String gender,
            @RequestParam String phone) {
        Passenger passenger = new Passenger();
        passenger.setFirstName(firstname);
        passenger.setLastName(lastname);
        passenger.setAge(age);
        passenger.setGender(gender);
        passenger.setPhone(phone);
        passengerRepository.save(passenger);
        return "Saved";
    }

    @GetMapping(path = "/get", produces = {"application/json", "application/xml"})
    @JsonView(Views.Private1.class)
    public @ResponseBody Passenger getPassenger(@RequestParam String passengerId) throws JsonProcessingException {
        Optional<Passenger> passenger =  passengerRepository.findById(passengerId);
        //ObjectMapper objectMapper = new ObjectMapper();
        //return objectMapper.writerWithView(Views.Private1.class).writeValueAsString(passenger);
        if (!passenger.isPresent()) {
            String errMsg = "Sorry, the requested passenger with id " + passengerId + " does not exist";
            throw new PassengerNotExistException(errMsg);
        }
        return passenger.get();
    }

    @GetMapping(path = "/all")
    public @ResponseBody Iterable<Passenger> getAllPassengers() {
        return passengerRepository.findAll();
    }

    @GetMapping(path = "/reservation/add")
    public @ResponseBody String addReservation(
            @RequestParam String passengerId) {
        Reservation reservation = new Reservation();
        Passenger passenger = passengerRepository.findById(passengerId).get();
        reservation.setPassenger(passenger);
        reservationRepository.save(reservation);
        for (int i = 0; i < 2; ++i) {
            ReservationToFlight reservationToFlight = new ReservationToFlight();
            reservationToFlight.setFlightNumber(String.valueOf(i));
            reservationToFlight.setReservationId(reservation.getReservationNumber());
            reservationToFlightRepository.save(reservationToFlight);
            FlightToPassenger flightToPassenger = new FlightToPassenger();
            flightToPassenger.setFlightNumber(String.valueOf(i));
            flightToPassenger.setPassengerId(passengerId);
            flightToPassengerRepository.save(flightToPassenger);
        }
        return "Saved";
    }

    @GetMapping(path = "/reservation/get")
    public @ResponseBody
    @JsonView(Views.Private2.class)
    Optional<Reservation> getReservation(@RequestParam String reservationId) {
        return reservationRepository.findById(reservationId);
    }

    @GetMapping(path = "/flight/add")
    public @ResponseBody String addFlight(
            @RequestParam String flightNumber,
            @RequestParam double price,
            @RequestParam String origin,
            @RequestParam String to,
            //@RequestParam Date departuretime,
            //@RequestParam Date arrivalTime,
            @RequestParam String description,
            @RequestParam int capacity,
            @RequestParam String model,
            @RequestParam String manufacturer,
            @RequestParam int year) {

        Flight flight = new Flight();
        flight.setFlightNumber(flightNumber);
        flight.setArrivalTime(Date.valueOf("1990-09-01"));
        flight.setDepartureTime(Date.valueOf("1990-09-01"));
        flight.setDescription(description);
        flight.setOrigin(origin);
        flight.setTo(to);
        flight.setPrice(price);
        Plane plane = new Plane();
        plane.setCapacity(capacity);
        plane.setManufacturer(manufacturer);
        plane.setModel(model);
        plane.setYear(year);
        flight.setPlane(plane);
        flightRepository.save(flight);
        return "Saved";
    }

    @GetMapping(path = "/flight/get")
    @JsonView(Views.Private3.class)
    public @ResponseBody Optional<Flight> getFlight(@RequestParam String flightNumber) {
        return flightRepository.findById(flightNumber);
    }
}
