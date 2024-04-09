package api;

import in.Request;
import db.DataBase;
import db.IDao;
import entities.IRoleConstants;
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
    private User currentUser = null;

    @Override
    public Response response(Request request) {
        String mapping = request.getMapping();
        switch (mapping) {
            case ImappingConstants.LOG_IN:
                return login(request);// вход на сервер

            case ImappingConstants.REGISTER:
                return newUser(request);// регистрация нового пользователя

            case ImappingConstants.REMOVE_ACCOUNT:
                return removeAccount(request);// удаление аккаунта текущего пользователя

            case ImappingConstants.NEW_READING:
                return addNewReading(request);// добавление новых показаний
            case ImappingConstants.CHANGE_READING:
                return changeReadings(request);
            case ImappingConstants.REMOVE_READING:
                return removeReading(request);
            default:
                return new Response(request.isAuth());
        }
    }

    private Response login(Request request) {
        System.out.println(request);
        // получаем из тела запроса логин и пароль пользователя, который подключается
        String login = request.getValueByKey(ImappingConstants.LOG_IN);
        String password = request.getValueByKey(ImappingConstants.PASSWORD);
        // проверяем, что такой пользователь есть в нашей базе
        boolean success = dao.authorize(login, password);
        if (success) {
            currentUser = dao.getCurrentUser();
            System.out.println("currentuser:" + currentUser);
            return dataResponse();
        } else return new Response(false);
    }

    private Response newUser(Request request) {
        System.out.println(request);
        String username = request.getValueByKey(ImappingConstants.USER_NAME);
        String login = request.getValueByKey(ImappingConstants.LOG_IN);
        String password = request.getValueByKey(ImappingConstants.PASSWORD);
        boolean success = dao.addNewUser(username, login, password);
        if (success) {
            currentUser = dao.getCurrentUser();
            System.out.println("user:" + currentUser);
            return dataResponse();
        } else return new Response(false);

    }

    private Response removeAccount(Request request) {
        String account = request.getValueByKey(ImappingConstants.ACCOUNT);
//        User user = dao.findUserByAccountNumber(account);// ищем пользователя по аккаунту
        boolean success = dao.removeAccount(account);// удаляем его аккаунт
        if(success) {
            Response response = new Response(success);
            response.getBody()[0][0] = ImappingConstants.REMOVE_ACCOUNT;
            response.getBody()[0][1] = "";// получаем данные по показаниям
            return response;
        }
        return new Response(false);
    }

    private Response addNewReading(Request request) {
        System.out.println(request);
        String account = request.getValueByKey(ImappingConstants.ACCOUNT);
        // добавляем новые показания в аккаунт пользователя
        WaterReading wr = (WaterReading) request.getFromBody(0);
        boolean success = dao.addNewReading(account, wr);
        Response response = new Response(success);
        if(success) {
            // ищем пользователя по аккаунту
            response.getBody()[0][0] = ImappingConstants.NEW_READING;
            response.getBody()[0][1] = "";// получаем данные по показаниям
        }
        return response;
        
        
    }

    private void getAllUsers(Response response) {
        /*
        получаем список всех зарегистрированнх пользователей, фильтруем их
        по правам доступа
        */
        dao.getAllUsers().forEach((user) -> response.addToBody(user));
    }
    
    
    private Response changeReadings(Request request) {
        // разбираем тело запроса на аккауант, измерение, тип
        String account = request.getValueByKey(ImappingConstants.ACCOUNT);
        // создаём объект показаний и передаём в базу данных
        WaterReading wr = (WaterReading) request.getFromBody(0);
        boolean succes = dao.changeReading(account, wr);
        // если изменения приняты, возвращаем результат
        Response response = new Response(succes);
        response.getBody()[0][0] = ImappingConstants.CHANGE_READING;
        response.getBody()[0][1] = "";// данные по показаниям
        return response;
        
    }

    private Response dataResponse() {
        Response response = new Response(true);
        response.getBody()[0][0] = ImappingConstants.USER_NAME;
        response.getBody()[0][1] = currentUser.getUsername();
        response.getBody()[1][0] = ImappingConstants.PASSWORD;
        response.getBody()[1][1] = currentUser.getPassword();
        response.getBody()[2][0] = ImappingConstants.ROLE;
        response.getBody()[2][1] = currentUser.getRole();
        // в зависимости от прав пользователя формируем тело ответа
        if (currentUser.getRole().equals(IRoleConstants.USER)) {
            // подключился обычный пользователь
            response.addToBody(currentUser);
        } else {
            /*
             Подключился администратор. Для него мы должны передать
             данные по зарегистрированным пользователям вместо аккаунта и показаний
             */
            getAllUsers(response);
        }
        return response;
    }

    private Response removeReading(Request request) {
        // разбираем тело запроса на аккауант, измерение, тип
        String account = request.getValueByKey(ImappingConstants.ACCOUNT);
        // создаём объект показаний и передаём в базу данных
        WaterReading wr = (WaterReading) request.getFromBody(0);
        boolean succes = dao.removeReading(account, wr);
        // если изменения приняты, возвращаем результат
        Response response = new Response(succes);
        response.getBody()[0][0] = ImappingConstants.CHANGE_READING;
        response.getBody()[0][1] = "";// данные по показаниям
        return response;
    }
}
