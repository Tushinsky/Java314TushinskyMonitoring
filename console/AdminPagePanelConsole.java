/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package console;

import entities.Account;
import entities.Reading;
import entities.User;
import entities.WaterReading;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;
import mapping.IRoleConstants;
import mapping.ImappingConstants;
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
    private final Scanner scanner = new Scanner(System.in);
    private String mapping;
    private String name;
    private final ArrayList<User> userList;
    private ArrayList<Reading> readingList;
    private String accountNumber;// номер аккаунта выбранного пользователя
    private int numberIndex;
        
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
        numberIndex = scanner.nextInt();
        if(numberIndex <= 0 || numberIndex > userList.size()) {
            System.out.println("Ошибка! Сделайте свой выбор");
            waitForChoice();
        }
        printReadingList(numberIndex);// печать показаний
        choiceOperation(numberIndex);// выбор операции
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
            System.out.println("|  " + user.getIdNumber() + " |" + user.getUsername() + "|");
            userList.add(user);
            index++;
            
        }
        
        
    }
    
    private void printReadingList(int index) {
        User u = userList.get(index - 1);
        Account acc = u.getAcc();
        accountNumber = acc.getAccountNumber();
        System.out.println("Пользователь " + u.getUsername() + ", account " + 
                accountNumber + "\n| № |Показания|Горячая|");
        
        readingList = acc.getReadings();
        readingList.stream().map((r) -> (WaterReading) r).forEachOrdered((wr) -> {
                System.out.println("| " + wr.getIdNumber() +
                        " | " + wr.getDate() + " | " + wr.getMeasuring() +
                        " | " + wr.isHot() + " |");
            });
    }
    
    private void choiceOperation(int number) {
        System.out.println("1 - удалить аккаунт;\n2 - добавить показания;" +
                "\n3 - удалить показания;\n4 - изменить показания." +
                "\n0 - для возврата на стартовую страницу.\nВведите:");
        number = scanner.nextInt();
        switch(number) {
            case 0:
                name = ImappingConstants.LOG_OUT;
                break;
            case 1:
                // удалить аккаунт
                name = REMOVE_ACCOUNT;
                break;
            case 2:
                // добавлить показания
                name = NEW_READING;
                break;
            case 3:
                // удалить показания
                name = REMOVE_READING;
                break;
            case 4:
                // изменить показания
                name = CHANGE_READING;
                break;
            default:
                System.out.println("Ошибка! Сделайте свой выбор!");
                choiceOperation(number);
        }
    }

    private Request getRemoveAccountRequest() {
        // получаем пользователя по выбранному индексу списка
        User user = userList.get(numberIndex);
        // создаём запрос на удаление аккаунта
        Request request = new Request(REMOVE_ACCOUNT, false);
        request.getBody()[0][0] = ImappingConstants.ACCOUNT;
        request.getBody()[0][1] = "";// имя пользователя
        request.addToBody(user.getAcc());
        return request;
    }

    private Request getRemoveReadingsRequest() {
        // создаём запрос на добавление показаний
        Request request = new Request(REMOVE_READING, false);
        WaterReading wr = (WaterReading) readingList.get(numberIndex);
        request.getBody()[0][0] = ImappingConstants.ACCOUNT;
        request.getBody()[0][1] = accountNumber;
        request.addToBody(wr);
        return request;
    }

    private Request getAddNewReadingRequest() {
        // создаём запрос на добавление показаний
        Request request = new Request(mapping, false);
        request.getBody()[0][1] = accountNumber;
        request.getBody()[1][0] = ImappingConstants.ROLE;
        request.getBody()[1][1] = IRoleConstants.ADMIN;
        return request;
    }

    private Request getChangeReadingRequest() {
        Request request = new Request(mapping, false);
        request.getBody()[0][1] = accountNumber;
        return request;
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
