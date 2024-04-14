package api;

import entities.User;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Класс реализует ответ из базы данных
 * @author Sergey
 */
public class Response {
    private final boolean isAuth;
    private final String[][] body = new String[5][2];
    private final ArrayList<User> listBody = new ArrayList <>();
    
    public Response(boolean isAuth) {
        this.isAuth = isAuth;
    }

    /**
     * Возвращает результат получения ответа от базы данных
     * @return true если ответ положительный
     */
    public boolean isAuth() {
        return isAuth;
    }

    /**
     * Возвращает тело ответа от базы данных
     * @return двухмерный массив String, содержащий идентификаторы и их значения
     */
    public String[][] getBody() {
        return body;
    }

    /**
     * Добавляет в тело ответа пользователя, по которому запрашивались данные
     * @param user пользователь, зарегистрированный в базе
     */
    public void addToBody(User user) {
        listBody.add(user);
    }
    
    /**
     * Возвращает пользователя из тела ответа
     * @param index индекс пользователя, который возвращается
     * @return пользователь с определённым индексом или null, если такого
     * пользователя нет
     */
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
