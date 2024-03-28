package api;

import mapping.ImappingConstants;
import in.Request;
import db.DataBase;
import db.IDao;
import entities.IRoleConstants;
import entities.User;
import entities.Reading;
import entities.WaterReading;
import in.IRequestResponseConstants;

import java.time.LocalDate;

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
                User user = dao.findUserByAccountNumber(request
                        .getValueByKey(IRequestResponseConstants.ACCOUNT));
                System.out.println(user.getUsername() + " " + user.getRole());
                Response response = new Response(true);
                response.getBody()[0][0] = mapping;
                response.getBody()[0][1] = getReadings(user);
                return response;
            default:
                return new Response(request.isAuth());
        }
    }

    private Response login(Request request) {
        System.out.println(request);
        String login = request.getValueByKey(IRequestResponseConstants.LOGIN);
        String password = request.getValueByKey(IRequestResponseConstants.PASSWORD);
        boolean success = dao.authorize(login, password);
        if (success) {
            currentUser = dao.findUserByUsername(login);
            System.out.println("currentuser:" + currentUser);
            return dataResponse();
        } else return new Response(false);
    }

    private Response newUser(Request request) {
        System.out.println(request);
        String username = request.getValueByKey(IRequestResponseConstants.USER_NAME);
        String login = request.getValueByKey(IRequestResponseConstants.LOGIN);
        String password = request.getValueByKey(IRequestResponseConstants.PASSWORD);
        boolean success = dao.addNewUser(username, login, password);
        if (success) {
            currentUser = dao.findUserByUsername(login);
            System.out.println("user:" + currentUser);
            return dataResponse();
        } else return new Response(false);

    }

    private Response removeAccount(Request request) {
        String account = request.getValueByKey(IRequestResponseConstants.ACCOUNT);
//        User user = dao.findUserByAccountNumber(account);// ищем пользователя по аккаунту
        boolean success = dao.removeAccount(account);// удаляем его аккаунт
        Response response = new Response(success);
        response.getBody()[0][0] = ImappingConstants.REMOVE_ACCOUNT;
        response.getBody()[0][1] = account;// получаем данные по показаниям
        response.getBody()[1][0] = IRequestResponseConstants.USER_NAME;
        response.getBody()[1][1] = request
                .getValueByKey(IRequestResponseConstants.USER_NAME);// получаем данные по показаниям
        response.getBody()[2][0] = IRequestResponseConstants.ROLE;
        response.getBody()[2][1] = request
                .getValueByKey(IRequestResponseConstants.ROLE);// получаем данные по показаниям

        return response;
    }

    private Response addNewReading(Request request) {
        System.out.println(request);
        String account = request.getValueByKey(IRequestResponseConstants.ACCOUNT);
        LocalDate localDate = LocalDate.parse(request
                .getValueByKey(IRequestResponseConstants.LOCAL_DATE));
        int measuring = Integer.parseInt(request
                .getValueByKey(IRequestResponseConstants.MEASURING));
        boolean isHot = request
                .getValueByKey(IRequestResponseConstants.IS_HOT).equals("1");

        // добавляем новые показания в аккаунт пользователя
        boolean success = dao
                .addNewReading(account, new WaterReading(localDate, measuring, isHot));
        User user = dao.findUserByAccountNumber(account);// ищем пользователя по аккаунту
        Response response = new Response(success);
        response.getBody()[0][0] = ImappingConstants.NEW_READING;
        response.getBody()[0][1] = getReadings(user);// получаем данные по показаниям
        return response;
    }

    private String getAllUsers() {
        // преобразуем его в строку для передачи в ответе
        StringBuilder builder = new StringBuilder();
        dao.getAllUsers().stream().filter((u) -> u.getRole().equals(IRoleConstants.USER))
                .forEach((user) -> {
            builder.append(user.getUsername()).append(" | ").append(user.getAcc()
                    .getAccountNumber()).append(";");
            System.out.println("user:" + user.getUsername());
//            builder.append(user.getUsername()).append(";");
        });
        builder.deleteCharAt(builder.lastIndexOf(";"));// удаляем последний символ;

        return builder.toString();
    }

    private String getReadings(User user) {
        StringBuilder builder = new StringBuilder();
        Reading[] readings = user.getAcc().getReadings();// получаем массив показаний текущего пользователя
        if(readings.length > 0) {
            // преобразуем в строку
            for(Reading r : readings) {
                if (r != null) {
                    WaterReading wr = (WaterReading) r;
                    builder.append(wr.getDate()).append(" | ").append(wr.getMeasuring())
                            .append(" | ").append(wr.isHot()).append(";");
                } else {
                    break;
                }
            }
            if(builder.length() >0) {
                builder.deleteCharAt(builder.lastIndexOf(";"));// удаляем последний символ;
                return builder.toString();
            } else {
                return null;
            }
        }
        return null;
    }

    private Response dataResponse() {
        Response response = new Response(true);
        response.getBody()[0][0] = IRequestResponseConstants.USER_NAME;
        response.getBody()[0][1] = currentUser.getUsername();
        response.getBody()[1][0] = IRequestResponseConstants.PASSWORD;
        response.getBody()[1][1] = currentUser.getPassword();
        response.getBody()[2][0] = IRequestResponseConstants.ROLE;
        response.getBody()[2][1] = currentUser.getRole();
        // в зависимости от прав пользователя формируем тело ответа
        if (currentUser.getRole().equals(IRoleConstants.USER)) {
            // подключился обычный пользователь
            response.getBody()[3][0] = IRequestResponseConstants.ACCOUNT;
            response.getBody()[3][1] = currentUser.getAcc().getAccountNumber();
            response.getBody()[4][0] = IRequestResponseConstants.READINGS;
            response.getBody()[4][1] = getReadings(currentUser);
        } else {
            /*
             Подключился администратор. Для него мы должны передать
             данные по зарегистрированным пользователям вместо аккаунта и показаний
             */
            response.getBody()[3][0] = IRequestResponseConstants.USERS;
            response.getBody()[3][1] = getAllUsers();
        }
        return response;
    }
}
