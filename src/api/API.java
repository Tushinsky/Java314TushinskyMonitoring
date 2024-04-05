package api;

import in.Request;
import db.DataBase;
import db.IDao;
import entities.IRoleConstants;
import entities.User;
import entities.Reading;
import entities.WaterReading;
import mapping.ImappingConstants;
import java.time.LocalDate;
import java.util.ArrayList;

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
            case ImappingConstants.GET_READING:
                return getReadings(request);
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
        Response response = new Response(success);
        response.getBody()[0][0] = ImappingConstants.REMOVE_ACCOUNT;
        response.getBody()[0][1] = account;// получаем данные по показаниям
        response.getBody()[1][0] = ImappingConstants.USER_NAME;
        response.getBody()[1][1] = request
                .getValueByKey(ImappingConstants.USER_NAME);// получаем данные по показаниям
        response.getBody()[2][0] = ImappingConstants.ROLE;
        response.getBody()[2][1] = request
                .getValueByKey(ImappingConstants.ROLE);// получаем данные по показаниям

        return response;
    }

    private Response addNewReading(Request request) {
        System.out.println(request);
        String account = request.getValueByKey(ImappingConstants.ACCOUNT);
        LocalDate localDate = LocalDate.parse(request
                .getValueByKey(ImappingConstants.LOCAL_DATE));
        int measuring = Integer.parseInt(request
                .getValueByKey(ImappingConstants.MEASURING));
        boolean isHot = request
                .getValueByKey(ImappingConstants.IS_HOT).equals("1");

        // добавляем новые показания в аккаунт пользователя
        WaterReading wr = new WaterReading(localDate, measuring, isHot);
        boolean success = dao
                .addNewReading(account, wr);
        User user = dao.findUserByAccountNumber(account);// ищем пользователя по аккаунту
        Response response = new Response(success);
        response.getBody()[0][0] = ImappingConstants.NEW_READING;
        response.getBody()[0][1] = "";// получаем данные по показаниям
        response.addToBody(user);
        return response;
    }

    private void getAllUsers(Response response) {
        /*
        получаем список всех зарегистрированнх пользователей, фильтруем их
        по правам доступа
        */
        dao.getAllUsers().forEach((user) -> response.addToBody(user));
    }

    /**
     * Возвращает показания по текущему пользователю
     * @param user пользователь для получения данных
     * @return строка, содержащая форматированные данные по показаниям
     */
    private String getCurrentUserReadings(User user) {
        
        // получаем массив показаний текущего пользователя
        ArrayList<Reading> readings = user.getAcc().getReadings();
        StringBuilder builder = new StringBuilder();// построитель строк
        
        if(readings.isEmpty()) {
            // если показаний нет
            return null;
        }
        // преобразуем в строку
        readings.stream().map((r) -> (WaterReading) r).forEachOrdered((wr) -> {
            builder.append(wr.getDate()).append(" | ").append(wr.getMeasuring())
                    .append(" | ").append(wr.isHot()).append(";");
        });
        builder.deleteCharAt(builder.lastIndexOf(";"));// удаляем последний символ;
        return builder.toString();
            
        
    }
    
    
    private Response getReadings(Request request) {
        String account = request.getValueByKey(ImappingConstants.ACCOUNT);
        // проверяем, что такой пользователь есть в нашей базе
        User user = dao.findUserByAccountNumber(account);
        if (user != null) {
            Response response = new Response(true);
            response.getBody()[0][0] = ImappingConstants.GET_READING;
            response.getBody()[0][1] = getCurrentUserReadings(user);// данные по показаниям
            return response;
        } else return new Response(false);
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
//            response.getBody()[3][0] = ImappingConstants.ACCOUNT;
//            response.getBody()[3][1] = currentUser.getAcc().getAccountNumber();
//            response.getBody()[4][0] = ImappingConstants.READINGS;
//            response.getBody()[4][1] = getCurrentUserReadings(currentUser);// данные по показаниям
        } else {
            /*
             Подключился администратор. Для него мы должны передать
             данные по зарегистрированным пользователям вместо аккаунта и показаний
             */
            getAllUsers(response);
        }
        return response;
    }
}
