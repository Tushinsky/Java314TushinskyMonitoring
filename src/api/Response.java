package api;

import entities.User;
import java.util.ArrayList;
import java.util.Arrays;

public class Response {
    private final boolean isAuth;
    private final String[][] body = new String[5][2];
    private final ArrayList<User> listBody = new ArrayList <>();
    
    public Response(boolean isAuth) {
        this.isAuth = isAuth;
    }

    public boolean isAuth() {
        return isAuth;
    }

    public String[][] getBody() {
        return body;
    }

    public void addToBody(User user) {
        listBody.add(user);
    }
    
    public User getFromBody(int index) {
        if(index == listBody.size()) {
            return null;
        }
        return listBody.get(index);
    }
    
    
    @Override
    public String toString() {
        return "Response{" +
                "isAuth=" + isAuth +
                ", body=" + Arrays.toString(body) +
                '}';
    }
}
