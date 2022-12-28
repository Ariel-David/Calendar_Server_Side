package AwesomeCalendar.CustomEntities;

public class CustomLoginResponse<T> {
    private T response;
    private String token;
    private String message;

    public CustomLoginResponse(T response, String token, String message) {
        this.response = response;
        this.token = token;
        this.message = message;
    }

    public T getResponse() {
        return response;
    }

    public void setResponse(T response) {
        this.response = response;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
