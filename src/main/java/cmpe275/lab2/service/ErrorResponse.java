package cmpe275.lab2.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;

public class ErrorResponse {
    @JsonProperty("BadRequest")
    private AirlineReservationError error;

    public AirlineReservationError getError() {
        return this.error;
    }

    public void setError(AirlineReservationError error) {
        this.error = error;
    }

    ErrorResponse(int code, String msg) {
        this.error = new AirlineReservationError(code, msg);
    }
}
