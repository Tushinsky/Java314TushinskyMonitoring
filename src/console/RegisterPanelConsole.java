/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package console;

import java.util.Scanner;
import mapping.ImappingConstants;
import static mapping.ImappingConstants.REGISTER;
import query.Request;

/**
 *
 * @author Sergey
 */
public class RegisterPanelConsole {
    private Request request;// запрос на регистрацию
    private String login;
    private String password;
    private String userName;
    private final Scanner scanner = new Scanner(System.in, "Windows-1251");
    private String mapping;

    public String getMapping() {
        return mapping;
    }
    
    public RegisterPanelConsole() {
        initComponents();
    }
    
    public Request getRequest() {
        request.getBody()[0][0] = ImappingConstants.USER_NAME;// ключ
        request.getBody()[0][1] = userName;// значение - логин пользователя
        request.getBody()[1][0] = ImappingConstants.LOG_IN;// ключ
        request.getBody()[1][1] = login;// значение - логин пользователя
        request.getBody()[2][0] = ImappingConstants.PASSWORD;// ключ
        request.getBody()[2][1] = password;// значение - пароль пользователя
        return request;
    }
    
    public void waitForChoice() {
        System.out.println("Введите имя, логин и пароль, разделяя их точкой с запятой:");
        String[] enter;
        enter = scanner.nextLine().split(";");
        if(enter.length <= 1 || enter.length > 3) {
            mapping = ImappingConstants.LOG_OUT;// выход
        } else {
            userName = enter[0];
            login = enter[1];
            password = enter[2];
            mapping = ImappingConstants.REGISTER;
        }
        request = new Request(REGISTER, false);// запрос на сервер для входа
        
    }
    
    private void initComponents() {
        String title = "Вы находитесь на странице регистрации.\n" +
                "Задайте Ваши имя, логин и " +
                "пароль для регистрации на сайте и " +
                "получения доступа в личный кабинет.\n" +
                "Или введите 0 для возврата на начальную страницу.";
        System.out.println(title);
    }
    
    
}
