/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package console;

import entities.Account;
import entities.Reading;
import entities.User;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;
import static mapping.ImappingConstants.CHANGE_READING;
import static mapping.ImappingConstants.NEW_READING;
import static mapping.ImappingConstants.REMOVE_ACCOUNT;
import static mapping.ImappingConstants.REMOVE_READING;
import query.Request;
import query.Response;

/**
 *
 * @author Sergey
 */
public class AdminPagePanelConsole {
    private Request request;
    private final Scanner scanner = new Scanner(System.in);
    private String mapping;
    private String name;
    private final ArrayList<User> userList;
    private ArrayList<Reading> readingList;
    private String accountNumber;// номер аккаунта выбранного пользователя
    
    public AdminPagePanelConsole(Response response) {
        // инициализация компонентов пользовательского интерфейса
        userList = new ArrayList<>();
        readingList = new ArrayList<>();
        initComponents(response);
    }
    
    public Request getRequest() {
        switch (this.getName()) {
            case REMOVE_ACCOUNT:
                return getRemoveAccountRequest();
            case REMOVE_READING:
                return getRemoveReadingsRequest();
            case NEW_READING:
                return getAddNewReadingRequest();
            case CHANGE_READING:
                return getChangeReadingRequest();
        }
        return null;
    }

    public void setResponse(Response response) {
        // передаются другие данные (новые показания, на удаление аккаунта)
        switch (response.getBody()[0][0]) {
            case REMOVE_ACCOUNT:
                removeAccount(response);
                break;
            case REMOVE_READING:
                removeReading(response);
                break;
            case NEW_READING:
                addNewReading(response);
                break;
            case CHANGE_READING:
                changeReading(response);
                break;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMapping() {
        return mapping;
    }
    
    public void waitForChoice() {
        System.out.println("Введите порядковый номер пользователя:");
        int number = scanner.nextInt();
        if(number <= 0 || number > userList.size()) {
            System.out.println("Ошибка! Сделайте свой выбор");
            waitForChoice();
        }
        printReadingList(number);
    }
    
    private void initComponents(Response response) {
        String userName = response.getBody()[0][1];
        String title = "Вы вошли на страницу с правами администратора.\n" + 
                "Здесь можно удалить аккаунт выбранного пользователя, добавить,\n" + 
                "удалить или изменить показания выбранного пользователя.\n" +
                "Администратор " + userName + ". Сегодня " + LocalDate.now();
        System.out.println(title);
        printUserList(response);
    }
    
    private void printUserList(Response response) {
        int index = 0;
        User user;
        System.out.println("|№ п/п|Имя пользователя|");
        while((user = (User) response.getFromBody(index))!= null) {
            // Печатаем данные из списка
            System.out.println("|--" + user.getIdNumber() + "--|" + user.getUsername() + "|");
            userList.add(user);
            index++;
            
        }
        
        
    }
    
    private void printReadingList(int index) {
        User u = userList.get(index - 1);
        Account acc = u.getAcc();
        accountNumber = acc.getAccountNumber();
        System.out.println("Пользователь " + u.getUsername() + ", account " + 
                accountNumber + "\n|-№-|Показания|");
        
        readingList = acc.getReadings();
        readingList.forEach((Reading r) -> {
            System.out.println("| " + r.getIdNumber() +
                    " |" + r.getDate() + "|" + r.getMeasuring() + "|");
        });
    }

    private Request getRemoveAccountRequest() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private Request getRemoveReadingsRequest() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private Request getAddNewReadingRequest() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private Request getChangeReadingRequest() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void removeAccount(Response response) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void removeReading(Response response) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void addNewReading(Response response) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void changeReading(Response response) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
