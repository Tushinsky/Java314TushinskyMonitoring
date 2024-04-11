package in;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * Класс, реализующий запрос на сервер
 * маппинг - адрес "ручки" на сервере, активирующий конкретный нужный нам функционал
 * isAuth - флаг аутентификации
 * тело - массив, в каждой строке по 2 значения -
 * ключ и его значение - параметр запроса и значение, которое мы хотим передать для обработки на сервер
 * */
public class Request {
    private final String mapping;
    private final boolean isAuth;
    private final String[][] body = new String[5][2];
    private final ArrayList<Object> listBody = new ArrayList<>();

    public Request(String mapping, boolean isAuth) {
        this.mapping = mapping;
        this.isAuth = isAuth;
    }

    public String getMapping() {
        return mapping;
    }

    public boolean isAuth() {
        return isAuth;
    }

    public String[][] getBody() {
        return body;
    }

    /**
     * Метод поиска значения по ключу в теле запроса
     *
     * @param key идентификатор (имя ключа), по которому выполняется поиска
     * @return строка, содержащая значение ключа, или пусто, если не найдено
     */
    public String getValueByKey(String key) {
        for (String[] strings : body) {
            if (strings[0].equals(key)) {
                return strings[1];
            }
        }
        return "";
    }

    @Override
    public String toString() {

        return "Request{" +
                "mapping='" + mapping + '\'' +
                ", isAuth=" + isAuth +
                ", body=" + Arrays.toString(body) +
                '}';
    }
    
    /**
     * Добавляет объект в тело запроса
     * @param object объект для добавления
     */
    public void addToBody(Object object) {
        listBody.add(object);
    }
    
    /**
     * Возвращает объект с указанным индексом из тела запроса
     * @param index индекс объекта
     * @return объект с указанным индексом, иначе null
     */
    public Object getFromBody(int index) {
        if(index == listBody.size()) {
            return null;
        }
        return listBody.get(index);
    }
}
