package api;

import query.Response;
import query.Request;
import db.DataBase;
import db.IDao;
import entities.Account;
import mapping.IRoleConstants;
import entities.User;
import entities.WaterReading;
import mapping.ImappingConstants;

/**
 * Класс, реализующий функционал сервера
 * В поле класса кладем (пока что) сущность IDao (пока что, одна, и, пока что, это база данных),
 * извлекающая из базы данных ответы
 * Храним, в качестве "cookie", флаг аутентификации;
 * единственный "контрактный" метод response() будет внутри вызывать, в зависимости от
 * пришедшего mapping (те самые "ручки", к которым привязан соответствующий функционал серверной части),
 * соответствующий метод, реализующий нужный функционал.
 * Вызываемые методы должны быть приватными
 * */
public class API implements Iapi{
    private final IDao dao = new DataBase();
//    private User currentUser = null;

    @Override
    public Response response(Request request) {
        String mapping = request.getMapping();
        switch (mapping) {
            case ImappingConstants.LOG_IN:
                return loginResponse(request);// вход на сервер

            case ImappingConstants.REGISTER:
                return newUserResponse(request);// регистрация нового пользователя

            case ImappingConstants.REMOVE_ACCOUNT:
                return removeAccountResponse(request);// удаление аккаунта текущего пользователя

            case ImappingConstants.NEW_READING:
                return addNewReadingResponse(request);// добавление новых показаний
            case ImappingConstants.CHANGE_READING:
                return changeReadingsResponse(request);
            case ImappingConstants.REMOVE_READING:
                return removeReadingResponse(request);
            default:
                return new Response(request.isAuth());
        }
    }

    /**
     * Возвращает ответ для входящего пользователя
     * @param request запрос на вход
     * @return ответ, содержащий данные, запрашиваемые пользователем
     */
    private Response loginResponse(Request request) {
        System.out.println(request);
        // получаем из тела запроса логин и пароль пользователя, который подключается
        String login = request.getValueByKey(ImappingConstants.LOG_IN);
        String password = request.getValueByKey(ImappingConstants.PASSWORD);
        // проверяем, что такой пользователь есть в нашей базе
        boolean success = dao.authorize(login, password);
        if (success) {
            // если успешно, заполняем тело ответа
            User currentUser = dao.getCurrentUser();
            return getDataResponse(currentUser);
        } else return new Response(false);
    }

    /**
     * Возвращает ответ для добавления нового пользователя
     * @param request запрос на добавление
     * @return ответ, содержащий данные, запрашиваемые пользователем
     */
    private Response newUserResponse(Request request) {
//        System.out.println(request);
        String username = request.getValueByKey(ImappingConstants.USER_NAME);
        String login = request.getValueByKey(ImappingConstants.LOG_IN);
        String password = request.getValueByKey(ImappingConstants.PASSWORD);
        int id = dao.addNewUser(username, login, password);
        if (id != 0) {
            // если вернулся код, не равный 0, возвращаем пользователя
            User currentUser = dao.getCurrentUser();
            return getDataResponse(currentUser);
        } else return new Response(false);

    }

    /**
     * Возвращает ответ на удаление аккаунта выбранног пользователя
     * @param request запрос на удаление
     * @return ответ, содержащий данные, запрашиваемые пользователем
     */
    private Response removeAccountResponse(Request request) {
        Account account = (Account) request.getFromBody(0);
        Response response = new Response(dao.removeAccount(account));
        // тело не заполняется, возвращается только результат удаления
        response.getBody()[0][0] = ImappingConstants.REMOVE_ACCOUNT;
        response.getBody()[0][1] = "";// получаем данные по показаниям
        return response;

    }

    /**
     * Возвращает ответ добавление новых показаний пользователем
     * @param request запрос на добавление
     * @return ответ, содержащий данные, запрашиваемые пользователем
     */
    private Response addNewReadingResponse(Request request) {
        System.out.println(request);
        String account = request.getValueByKey(ImappingConstants.ACCOUNT);
        String role = request.getValueByKey(ImappingConstants.ROLE);
        // вытаскиваем новые показания из тела запроса
        WaterReading wr = (WaterReading) request.getFromBody(0);
        int id = dao.addNewReading(role, account, wr);// добавляем их
        // возвращаем результат добавления новых показаний
        Response response = new Response(id != 0);
        response.getBody()[0][0] = ImappingConstants.NEW_READING;
        response.getBody()[0][1] = String.valueOf(id);
        return response;
        
        
    }

    /**
     * Возвращает ответ на получение данных по всем пользователям
     * @param request запрос на всех пользователей
     * @return ответ, содержащий данные, запрашиваемые пользователем
     */
    private void getAllUsers(Response response) {
        /*
        получаем список всех зарегистрированнх пользователей, добавляем их
        в тело ответа
        */
        dao.getAllUsers().forEach((user) -> response.addToBody(user));
    }
    
    /**
     * Возвращает ответ изменение показаний пользователя
     * @param request запрос на изменение показаний
     * @return ответ, содержащий данные, запрашиваемые пользователем
     */
    private Response changeReadingsResponse(Request request) {
        // создаём объект показаний и передаём в базу данных
        WaterReading wr = (WaterReading) request.getFromBody(0);
        boolean success = dao.changeReading(wr);
        // если изменения приняты, возвращаем результат
        Response response = new Response(success);
        response.getBody()[0][0] = ImappingConstants.CHANGE_READING;
        response.getBody()[0][1] = "";// данные по показаниям
        return response;
        
    }

    /**
     * Заполняет и возвращает ответ с данными для входящего пользователя
     * @param user пользовалеь, запрашивающий данные
     * @return ответ, содержащий данные, запрашиваемые пользователем
     */
    private Response getDataResponse(User user) {
        Response response = new Response(true);
        response.getBody()[0][0] = ImappingConstants.USER_NAME;
        response.getBody()[0][1] = user.getUsername();
        response.getBody()[1][0] = ImappingConstants.ROLE;
        response.getBody()[1][1] = user.getRole();
        // в зависимости от прав пользователя формируем тело ответа
        if (user.getRole().equals(IRoleConstants.USER)) {
            // подключился обычный пользователь
            response.addToBody(user);
        } else {
            /*
             Подключился администратор. Для него мы должны передать
             данные по зарегистрированным пользователям вместо аккаунта и показаний
             */
            getAllUsers(response);
        }
        return response;
    }

    /**
     * Возвращает ответ на удаление показаний пользователя
     * @param request запрос на удаление показаний
     * @return ответ, содержащий данные, запрашиваемые пользователем
     */
    private Response removeReadingResponse(Request request) {
        // создаём объект показаний и передаём в базу данных
        WaterReading wr = (WaterReading) request.getFromBody(0);
        boolean success = dao.removeReading(wr);
        // если изменения приняты, возвращаем результат
        Response response = new Response(success);
        response.getBody()[0][0] = ImappingConstants.REMOVE_READING;
        response.getBody()[0][1] = "";// данные по показаниям
        return response;
    }
}
