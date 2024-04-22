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
    private final Scanner scanner = new Scanner(System.in);
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
        System.out.println("Введите имя:");
        String enter;
        enter = scanner.nextLine();
        if(enter.equals("0")) {
            mapping = ImappingConstants.LOG_OUT;// выход
        } else {
            userName = enter;
            System.out.println("Введите логин:");
            login = scanner.nextLine();
            System.out.println("Введите пароль:");
            password = scanner.nextLine();
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
