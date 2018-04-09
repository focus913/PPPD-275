package cmpe275.lab2.service;

public class ReservationNotExistException extends RuntimeException {
    private String msg;

    public ReservationNotExistException(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
