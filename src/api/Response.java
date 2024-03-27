package api;

import java.util.Arrays;

public class Response {
    private final boolean isAuth;
    private final String[][] body = new String[5][2];

    public Response(boolean isAuth) {
        this.isAuth = isAuth;
    }

    public boolean isAuth() {
        return isAuth;
    }

    public String[][] getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "Response{" +
                "isAuth=" + isAuth +
                ", body=" + Arrays.toString(body) +
                '}';
    }
}
