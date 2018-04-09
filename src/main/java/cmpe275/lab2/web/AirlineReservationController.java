package cmpe275.lab2.web;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import cmpe275.lab2.domain.*;
import cmpe275.lab2.service.*;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class AirlineReservationController {

    static AirlineReservationService service = new AirlineReservationService();

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

    @GetMapping(path = "/passenger/{id}", produces = {"application/json", "application/xml"})
    @JsonView(Views.Private1.class)
    public @ResponseBody Passenger getPassenger(@PathVariable("id") String passengerId) {
        return service.getPassenger(passengerId);
    }

    @PostMapping(path = "/passenger", produces = {"application/json", "application/xml"})
    @JsonView(Views.Private1.class)
    public @ResponseBody Passenger createPassenger(
            @RequestParam String firstname,
            @RequestParam String lastname,
            @RequestParam int age,
            @RequestParam String gender,
            @RequestParam String phone) {
        String passengerId = service.createPassenger(firstname, lastname, age, gender, phone);
        return service.getPassenger(passengerId);
    }

    @PutMapping(path = "/passenger/{id}", produces = {"application/json", "application/xml"})
    @JsonView(Views.Private1.class)
    public @ResponseBody Passenger updatePassenger(
            @PathVariable("id") String passengerId,
            @RequestParam String firstname,
            @RequestParam String lastname,
            @RequestParam int age,
            @RequestParam String gender,
            @RequestParam String phone) {
        service.updatePassenger(passengerId, firstname, lastname, age, gender, phone);
        return service.getPassenger(passengerId);
    }

    @DeleteMapping(path = "/passenger/{id}")
    public @ResponseBody String deletePassenger(@PathVariable("id") String passengerId) {
        service.deletePassenger(passengerId);
        String msg = "Passenger with id " + passengerId + " is deleted successfully";
        SuccessResponse response = new SuccessResponse(200, msg);
        ObjectMapper objectMapper = new XmlMapper();
        try {
            return objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException ex) {
            throw new InvalidRequestException(ex.getMessage());
        }
    }

    @GetMapping(path = "/reservation/{number}")
    @JsonView(Views.Private2.class)
    public @ResponseBody Reservation getReservation(
            @PathVariable("number") String reservationNum) {
        return service.getReservation(reservationNum);
    }

    @PostMapping(path = "/reservation")
    public @ResponseBody String createReservation(
            @RequestParam String passengerId,
            @RequestParam List<String> flightLists) {
        String reservationNum = service.createReservation(passengerId, flightLists);
        Reservation reservation = service.getReservation(reservationNum);
        ObjectMapper objectMapper = new XmlMapper();
        try {
            return objectMapper.writerWithView(Views.Private2.class).writeValueAsString(reservation);
        } catch (JsonProcessingException ex) {
            throw new InvalidRequestException(ex.getMessage());
        }
    }

    @PostMapping(path = "/reservation/{number}")
    @JsonView(Views.Private2.class)
    public @ResponseBody Reservation updateReservation(
            @PathVariable("number") String reservationNum,
            @RequestParam List<String> flightsAdded,
            @RequestParam List<String> flightsRemoved) {
        service.updateReservation(reservationNum, flightsAdded, flightsRemoved);
        return service.getReservation(reservationNum);
    }

    @GetMapping(path = "/reservation")
    public @ResponseBody String searchForReservation(
            @RequestParam String passengerId,
            @RequestParam String origin,
            @RequestParam String to,
            @RequestParam String flightNumber) {
        Iterable<Reservation> reservations =
                service.searchReservation(passengerId, origin, to, flightNumber);
        ObjectMapper objectMapper = new XmlMapper();
        try {
            return objectMapper.writerWithView(Views.Private2.class).writeValueAsString(reservations);
        } catch (JsonProcessingException ex) {
            throw new InvalidRequestException(ex.getMessage());
        }
    }

    @DeleteMapping(path = "/reservation/{number}")
    public @ResponseBody String deleteReservation(
            @PathVariable("number") String reservationNumber) {
        service.deleteReservation(reservationNumber);
        String msg = "Reservation with number " + reservationNumber + " is canceled successfully";
        SuccessResponse response = new SuccessResponse(200, msg);
        ObjectMapper objectMapper = new XmlMapper();
        try {
            return objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException ex) {
            throw new InvalidRequestException(ex.getMessage());
        }
    }

    @GetMapping(path = "/flight/{flightNumber}")
    @JsonView(Views.Private3.class)
    public @ResponseBody Flight getFlight(@PathVariable("flightNumber") String flightNumber) {
        return service.getFlight(flightNumber);
    }

    @PostMapping(path = "/flight/{flightNumber}")
    public @ResponseBody String createFlight(
            @PathVariable("flightNumber") String flightNumber,
            @RequestParam double price,
            @RequestParam String origin,
            @RequestParam String to,
            @RequestParam String departuretime,
            @RequestParam String arrivalTime,
            @RequestParam String description,
            @RequestParam int capacity,
            @RequestParam String model,
            @RequestParam String manufacturer,
            @RequestParam int year) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH");
        try {
            Date departTime = format.parse(departuretime);
            Date arriveTime = format.parse(arrivalTime);
            service.createFlight(
                    flightNumber, price, origin, to, departTime,
                    arriveTime, description, capacity, model, manufacturer, year);
            Flight flight = service.getFlight(flightNumber);
            ObjectMapper objectMapper = new XmlMapper();
            return objectMapper.writerWithView(Views.Private3.class).writeValueAsString(flight);
        } catch (ParseException ex) {
            throw new InvalidRequestException(ex.getMessage());
        } catch (JsonProcessingException ex) {
            throw new InvalidRequestException(ex.getMessage());
        }
    }

    @PostMapping(path = "/flight/{flightNumber}")
    public @ResponseBody String updateFlight(
            @PathVariable("flightNumber") String flightNumber,
            @RequestParam double price,
            @RequestParam String origin,
            @RequestParam String to,
            @RequestParam String departuretime,
            @RequestParam String arrivalTime,
            @RequestParam String description,
            @RequestParam int capacity,
            @RequestParam String model,
            @RequestParam String manufacturer,
            @RequestParam int year) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH");
        try {
            Date departTime = format.parse(departuretime);
            Date arriveTime = format.parse(arrivalTime);
            service.updateFlight(
                    flightNumber, price, origin, to, departTime,
                    arriveTime, description, capacity, model, manufacturer, year);
            Flight flight = service.getFlight(flightNumber);
            ObjectMapper objectMapper = new XmlMapper();
            return objectMapper.writerWithView(Views.Private3.class).writeValueAsString(flight);
        } catch (ParseException ex) {
            throw new InvalidRequestException(ex.getMessage());
        } catch (JsonProcessingException ex) {
            throw new InvalidRequestException(ex.getMessage());
        }
    }

    @DeleteMapping(path = "/airline/{flightNumber}")
    public @ResponseBody String deleteFlight(@PathVariable("flightNumber") String flightNumber) {
        service.deleteFlight(flightNumber);
        String msg = "Flight with id " + flightNumber + " is deleted successfully";
        SuccessResponse response = new SuccessResponse(200, msg);
        ObjectMapper objectMapper = new XmlMapper();
        try {
            return objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException ex) {
            throw new InvalidRequestException(ex.getMessage());
        }
    }
}
