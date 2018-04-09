package cmpe275.lab2.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class AirlineReservationExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(PassengerNotExistException.class)
    public final ResponseEntity<ErrorResponse>
    handlePassengerNotExistException(PassengerNotExistException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMsg());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ReservationNotExistException.class)
    public final ResponseEntity<ErrorResponse>
    handleReservationNotExistException(ReservationNotExistException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMsg());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FlightNotExistException.class)
    public final ResponseEntity<ErrorResponse>
    handleFlightNotExistException(FlightNotExistException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMsg());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidRequestException.class)
    public final ResponseEntity<ErrorResponse>
    handleUpdateFlightException(InvalidRequestException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMsg());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
