package AwesomeCalendar.CustomEntities;

public class CustomResponse<T> {
    private T response;
    private String message;

    public CustomResponse(T response, String message) {
        this.response = response;
        this.message = message;
    }

    public T getResponse() {
        return response;
    }

    public void setResponse(T response) {
        this.response = response;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
