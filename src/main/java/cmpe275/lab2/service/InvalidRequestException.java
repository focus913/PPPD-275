package cmpe275.lab2.service;

public class InvalidRequestException extends RuntimeException {
    private String msg;

    public InvalidRequestException(String msg) {
        this.msg = msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
