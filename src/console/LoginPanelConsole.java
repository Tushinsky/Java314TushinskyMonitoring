/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package console;

import java.util.Scanner;
import mapping.ImappingConstants;
import query.Request;

/**
 *
 * @author Sergey
 */
public class LoginPanelConsole {
    private Request request;
    private int count = 1;// счётчик попыток ввода
    private String login;
    private String password;
    private final Scanner scanner = new Scanner(System.in, "Windows-1251");
    private String mapping;

    public String getMapping() {
        return mapping;
    }
    
    public LoginPanelConsole() {
        initComponents();
    }
    
    private void initComponents() {
        String title = 
                "|--------------------------------------------------------|\n" +
                "|         Вы находитесь на странице авторизации.         |\n" +
                "|Введите Ваш логин и пароль для доступа в личный кабинет.|\n" + 
                "|Для возврата на начальную страницу введите 0.           |\n" +
                "|--------------------------------------------------------|";
        System.out.println(title);
    }
    
    public void waitForChoice() {
        System.out.println("Введите логин и пароль, разделяя их точкой с запятой:");
        String[] enter;
        enter = scanner.nextLine().split(";");
        
        if(enter.length <= 1 || enter.length > 2) {
            mapping = ImappingConstants.LOG_OUT;// выход
        } else {
            
            login = enter[0];
            password = enter[1];
            mapping = ImappingConstants.LOG_IN;
        }
    }
    
    public Request getRequest() {
        // создаём запрос на вход
        request = new Request(mapping, false);
        request.getBody()[0][0] = ImappingConstants.LOG_IN;// ключ
        request.getBody()[0][1] = login;// значение - логин пользователя
        request.getBody()[1][0] = ImappingConstants.PASSWORD;// ключ
        request.getBody()[1][1] = password;// значение - пароль пользователя
        count++;// увеличиваем счётчик попыток
        if(count > 3) {
            // если количество попыток больше 3
            count = 1;// сбрасываем счётчик
            return null;// возвращаем null в запросе
        }
        System.out.println("request: " + request);
        return request;
    }
}
