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
    private final Scanner scanner = new Scanner(System.in, "Windows-1251");
    private String mapping;
    private String name;
    private final ArrayList<User> userList;
    private ArrayList<Reading> readingList;
    private String accountNumber;// номер аккаунта выбранного пользователя
    private int readingNumber;
    private final NewChangeReadingPanelConsole newChangeReadingPanelConsole;
    private int userNumber;
    
    public AdminPagePanelConsole(Response response) {
        // инициализация компонентов пользовательского интерфейса
        userList = new ArrayList<>();
        readingList = new ArrayList<>();
        newChangeReadingPanelConsole = new NewChangeReadingPanelConsole();
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
        if(number == 0) {
            name = ImappingConstants.LOG_OUT;
        } else {
            if(number < 0 || number > userList.size()) {
                System.out.println("Ошибка! Сделайте свой выбор");
                waitForChoice();
            }
            userNumber = number - 1;
            printReadingList();
            choiceOperation();// выбор операции
        }
    }
    
    private void initComponents(Response response) {
        String userName = response.getBody()[0][1];
        String title = 
                "|--------------------------------------------------------------|\n" + 
                "|        Вы вошли на страницу с правами администратора.        |\n" + 
                "|Здесь можно удалить аккаунт выбранного пользователя, добавить,|\n" + 
                "|   удалить или изменить показания выбранного пользователя.    |\n" +
                "|--------------------------------------------------------------|\n" + 
                "Администратор " + userName + ". Сегодня " + LocalDate.now();
        System.out.println(title);
        
        fillUserList(response);// заполняем список пользователей
        printUserList();
    }
    
    private void fillUserList(Response response) {
        int index = 0;
        User user;
        while((user = (User) response.getFromBody(index))!= null) {
            userList.add(user);
            index++;
            
        }
        
        
    }
    
    private void printUserList() {
        // самое длинное имя
        int lenghtName = userList.stream()
                .mapToInt(user -> user.getUsername().length()).max().getAsInt();
        // самый длинный номер
        int lenghtNumber = userList.stream()
                .mapToInt(user -> 
                        String.valueOf(user.getIdNumber()).length()).max().getAsInt();
        System.out.println("lenght: " + lenghtName + "; " + lenghtNumber);
        if(lenghtNumber <= 3) {
            lenghtNumber = 3;
        } else {
            if((lenghtNumber % 2) == 0) {
                // если количество цифр четное, то увеличим его на 1 для выравнивания
                lenghtNumber++;
            }
        }
        if(lenghtName <= 16) {
            lenghtName = 16;// длина "Имя пользователя"
        } else {
            if((lenghtName % 2) != 0) {
                // если количество цифр нечетное, то увеличим его на 1 для выравнивания
                lenghtName++;
            }
        }
        char[] nameChar = new char[lenghtName];
        char[] numberChar = new char[lenghtNumber];
        for(int i = 0; i < lenghtName; i++) {
            nameChar[i] = '-';
        }
        for(int i = 0; i < lenghtNumber; i++) {
            numberChar[i] = '-';
        }
        
        String title = 
                "|" + String.copyValueOf(nameChar) + "-" + String.copyValueOf(numberChar) + "|";
        System.out.println(title);
        System.out.println("|" + getNumSpace((lenghtNumber - 1) / 2) + 
                "N" + getNumSpace((lenghtNumber - 1) / 2) + "|" + 
                         getNumSpace((lenghtName - 16) / 2) + "Имя пользователя" +
                                 getNumSpace((lenghtName - 16) / 2) + "|");
        System.out.println(title);
        for(User user : userList) {
            int len1 = String.valueOf(user.getIdNumber()).length();
            int len2 = String.valueOf(user.getUsername()).length();
            System.out.println("|" + getNumSpace(lenghtNumber - len1) +
                    user.getIdNumber() + "|" + user.getUsername() +
                    getNumSpace(lenghtName - len2) + "|");
        }
        System.out.println(title);
    }
    
    private String getNumSpace(int count) {
        if(count == 0) {
            return "";
        }
        char[] space = new char[count];
        for(int i = 0; i < count; i++) {
            space[i] = ' ';
        }
        return String.copyValueOf(space);
    }
    
    private void printReadingList() {
        User u = userList.get(userNumber);
        Account acc = u.getAcc();
        accountNumber = acc.getAccountNumber();
        System.out.println("Пользователь " + u.getUsername() + ", account " + 
                accountNumber + "\n" + "| # |   Дата   |Показания|Гор/хол|");
        
        readingList = acc.getReadings();
        readingList.stream().map((r) -> (WaterReading) r).forEachOrdered((wr) -> {
                System.out.println("| " + wr.getIdNumber() +
                        " |" + wr.getDate() + "| " + wr.getMeasuring() +
                        " |" + wr.isHot() + "|");
            });
    }
    
    private void choiceOperation() {
        System.out.println("Введите:\n1 - удалить аккаунт;\n2 - добавить показания;" +
                "\n3 - удалить показания;\n4 - изменить показания." +
                "\n0 - для возврата на стартовую страницу.\nВведите:");
        int number = scanner.nextInt();
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
                choiceOperation();
        }
    }

    private Request getRemoveAccountRequest() {
        // получаем пользователя по выбранному индексу списка
        User user = userList.get(userNumber);
        System.out.println("account: " + user.getAcc().toString());
        // создаём запрос на удаление аккаунта
        Request request = new Request(REMOVE_ACCOUNT, false);
        request.getBody()[0][0] = ImappingConstants.ACCOUNT;
        request.getBody()[0][1] = "";// имя пользователя
        request.addToBody(user.getAcc());
        return request;
    }

    private Request getRemoveReadingsRequest() {
        // выбор показаний
        System.out.println("Введите номер показания для удаления:");
        int number = scanner.nextInt();
        // проверяем диапазон
        if(number < 1 || number > readingList.size()) {
            System.out.println("Ошибка!");
            getRemoveReadingsRequest();
        }
        readingNumber = number - 1;
        // создаём запрос на добавление показаний
        Request request = new Request(REMOVE_READING, false);
        WaterReading wr = (WaterReading) readingList.get(readingNumber);
        request.getBody()[0][0] = ImappingConstants.ACCOUNT;
        request.getBody()[0][1] = accountNumber;
        request.addToBody(wr);
        return request;
    }

    private Request getAddNewReadingRequest() {
        // создаём запрос на добавление показаний
        Request request = newChangeReadingPanelConsole.getNewReadingRequest();
        if(request != null) {
            request.getBody()[0][1] = accountNumber;
            request.getBody()[1][0] = ImappingConstants.ROLE;
            request.getBody()[1][1] = IRoleConstants.ADMIN;
        }
        return request;
    }

    private Request getChangeReadingRequest() {
        System.out.println("Введите номер показаний для изменения:");
        int number = scanner.nextInt();
        // проверка корректности ввода
        if(number <=0 || number > readingList.size()) {
            System.out.println("Ошибка! Сделайте свой выбор.");
            getChangeReadingRequest();
        }
        readingNumber = number - 1;
        // если всё прошло нормально
        newChangeReadingPanelConsole.setWaterReading((WaterReading) 
                readingList.get(readingNumber));
        Request request = newChangeReadingPanelConsole.getChangeReadingRequest();
        request.getBody()[0][1] = accountNumber;
        return request;
    }

    private void removeAccount(Response response) {
        if(!response.isAuth()) {
            System.out.println("Ошибка! Что-то не сложилось!");
        } else {
            userList.remove(userNumber);
            // после удаления перенумеруем список пользователей
            for(int i = 0; i < userList.size(); i++) {
                userList.get(i).setIdNumber(i + 1);
            }
        }
        printUserList();
    }

    private void removeReading(Response response) {
        if(!response.isAuth()) {
            System.out.println("Ошибка! Что-то не сложилось!");
        }
        // запрос на удаление подтверждён
        readingList.remove(readingNumber);
        printReadingList();
    }

    private void addNewReading(Response response) {
        if(!response.isAuth()) {
            System.out.println("Ошибка! Что-то не сложилось!");
        }
        // получаем список показаний выбранного пользователя
        ArrayList<Reading> readings = userList.get(userNumber).getAcc()
                .getReadings();
        // создаём объект показаний
        int id = Integer.parseInt(response.getBody()[0][1]);// идентификатор записи
        WaterReading wr = newChangeReadingPanelConsole.getWaterReading();
        int idNumber = readings.size();// количество элементов в списке
        idNumber++;
        // создаём объект новых показаний
        WaterReading reading = new WaterReading(idNumber, id, wr.getDate(), 
                wr.getMeasuring(), wr.isHot());
        readings.add(reading);// добавляем показание в список
        printReadingList();

    }

    private void changeReading(Response response) {
        if(response.isAuth()) {
            // создаём объект показаний после изменения
            WaterReading wr = newChangeReadingPanelConsole.getWaterReading();
            readingList.set(readingNumber, wr);// изменяем
            // обновляем список показаний
            printReadingList();
            choiceOperation();
        }
    }
    
    
}
