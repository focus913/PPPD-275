package cmpe275.lab2.service;

import org.springframework.http.HttpStatus;

public class AirlineReservationError {
    private int code;
    private String msg;

    AirlineReservationError(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
