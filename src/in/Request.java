package in;

import api.Response;
import java.util.Arrays;


/**
 * Класс, реализующий запрос на сервер
 * маппинг - адрес "ручки" на сервере, активирующий конкретный нужный нам функционал
 * isAuth - флаг аутентификации
 * тело - массив, в каждой строке по 2 значения -
 * ключ и его значение - параметр запроса и значение, которое мы хотим передать для обработки на сервер
 */
public class Request extends Response{
    private final String mapping;
    
    /**
     * Конструктор нового запроса к базе данных
     * @param mapping идентификатор запроса (одна из констант ImappingConstants)
     * @param isAuth флаг выполнения запроса
     */
    public Request(String mapping, boolean isAuth) {
        super(isAuth);
        this.mapping = mapping;
    }

    /**
     * Возвращает идентификатор запроса к базе данных
     * @return строка - идентификатор запроса
     */
    public String getMapping() {
        return mapping;
    }

    
    
    @Override
    public String toString() {

        return "Request{" +
                "mapping='" + mapping + '\'' +
                ", isAuth=" + super.isAuth() +
                ", body=" + Arrays.toString(super.getBody()) +
                '}';
    }
    
    
}
