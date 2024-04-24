/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package console;

import api.API;
import mapping.IRoleConstants;
import mapping.ImappingConstants;
import query.Request;
import query.Response;

/**
 *
 * @author Sergey
 */
public class StartAppConsole {
    private final API api;// класс для связи с базой данных
    
    public StartAppConsole() {
        api = new API();
        
    }
    
    public void display() {
        StartPanelConsole startPanel = new StartPanelConsole();
        startPanel.waitForChoice();
        switch(startPanel.getMapping()) {
            case ImappingConstants.LOG_IN:
                createLoginPanel();
                break;
            case ImappingConstants.REGISTER:
                createRegisterPanel();
                break;
            case ImappingConstants.LOG_OUT:
                System.exit(0);
        }
    }
    
    private void createLoginPanel() {
        LoginPanelConsole loginPanel = new LoginPanelConsole();
        boolean enter = true;
        while(enter) {
            loginPanel.waitForChoice();
            switch(loginPanel.getMapping()) {
                case ImappingConstants.LOG_OUT:
                    display();
                    enter = false;
                    break;
                case ImappingConstants.LOG_IN:
                    // получаем тело запроса
                    Request request = loginPanel.getRequest();
                    // проверяем запрос
                    if(request != null) {
                        if(!signIn(request)) {
                            System.out.println("Проверьте правильность ввода логина или пароля!");
                            
                        } else {
                            enter = false;
                        }
                    } else {
                        System.out.println("Превышен лимит попыток для входа. Доступ закрыт.\n" +
                                            "Обратитесь к администратору!");
                        display();
                        enter = false;
                    }
            }
        }
    }
    
    private void createRegisterPanel() {
        RegisterPanelConsole registerPanel = new RegisterPanelConsole();
        registerPanel.waitForChoice();
        switch(registerPanel.getMapping()) {
            case ImappingConstants.REGISTER:
                signIn(registerPanel.getRequest());
                break;
            case ImappingConstants.LOG_OUT:
                display();
        }
    }
    
    private void createAdminPanel(Response responseInit) {
        AdminPagePanelConsole adminPanel = new AdminPagePanelConsole(responseInit);
        boolean exit = true;
        while(exit) {
            adminPanel.waitForChoice();
            switch(adminPanel.getName()) {
                case ImappingConstants.LOG_OUT:
                    display();
                    exit = false;
                    break;
                case ImappingConstants.CHANGE_READING:
                case ImappingConstants.NEW_READING:
                case ImappingConstants.REMOVE_ACCOUNT:
                case ImappingConstants.REMOVE_READING:
                    Request request = adminPanel.getRequest();
                    if(request != null) {
                        Response response = api.response(request);
                        adminPanel.setResponse(response);
                    }
            }
        }
    }
    
    private void createHomePanel(Response responseInit) {
        HomePagePanelConsole homePanel = new HomePagePanelConsole(responseInit);
        boolean exit = true;
        while(exit) {
            homePanel.waitForChoice();
            System.out.println("name: " + homePanel.getName());
            switch(homePanel.getName()) {
                case ImappingConstants.LOG_OUT:
                    display();
                    exit = false;
                    break;
                case ImappingConstants.NEW_READING:
                    Request request = homePanel.getRequest();
                    if(request != null) {
                        Response response = api.response(request);
                        homePanel.setResponse(response);
                    }
                    break;
                default:
                    
            }
            
        }
//        homePanel.waitForChoice();
    }
    
    private boolean signIn(Request request) {
        // отправляем запрос на вход
        Response response = api.response(request);
        boolean retValue = response.isAuth();// получаем результат ответа
        if (retValue) {
            // если запрос на вход подтверждён, переходим на домашнюю страницу
            String role = response.getValueByKey(ImappingConstants.ROLE);
            if(role.equals(IRoleConstants.USER)) {
                // если вошёл обычный пользователь создаём панель домашней страницы
                createHomePanel(response);
//                homePagePanel.setResponse(response);
            } else {
                // создаём панель администратора
                createAdminPanel(response);
            }
        }
        return retValue;
    }
}
