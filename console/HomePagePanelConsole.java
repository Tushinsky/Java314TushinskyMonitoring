/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package console;

import entities.Reading;
import entities.User;
import entities.WaterReading;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;
import mapping.ImappingConstants;
import static mapping.ImappingConstants.NEW_READING;
import query.Request;
import query.Response;

/**
 *
 * @author Sergey
 */
public class HomePagePanelConsole {
    private final User user;
    private String mapping;
    private final Scanner scanner = new Scanner(System.in);
    private LocalDate readingDate;
    private int measuring;
    private boolean isHot;
    private String name;
    
    
    public HomePagePanelConsole(Response response) {
        // получаем пользователя из тела ответа
        user = (User) response.getFromBody(0);
        initComponents();// инициализация инфрмации
//        waitForChoice();
    }

    public Request getRequest() {
        return addNewReadingRequest();
        
    }

    public void setResponse(Response response) {
        
        // если ответ положительный, и новые показания приняты, добавляем их в список показаний
        if (response.isAuth()) {
            int id = Integer.parseInt(response.getBody()[0][1]);
            addReading(id);// добавляем показание в аккаунт пользователя
            
        } else {
            System.out.println("Внесение показаний допускается только один раз в текущем месяце!");
        }

    }

    public String getMapping() {
        return mapping;
    }

    
    /**
     * Get the value of name
     *
     * @return the value of name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the value of name
     *
     * @param name new value of name
     */
    public void setName(String name) {
        this.name = name;
    }
    
    public void waitForChoice() {
        System.out.println("Нажмите ANY для добавления новых показаний.\n" +
                "Или нажмите 0 для возврата на стартовую страницу.\n" +
                "Сделайте свой выбор:");
        int choice = scanner.nextInt();
        if(choice == 0) {
            name = ImappingConstants.LOG_OUT;
        } else {
            // вводим дату
            name = getLocalDate();
            if(name.equals(ImappingConstants.LOG_OUT)) {
                return;
            }
            // вводим показания
            System.out.println("Введите показание:");
            if((measuring = scanner.nextInt()) == 0) {
                return;
            }
            // тип показаний
            name = getHotColdWater();
            
        }
    }
    
    private String getLocalDate() {
        System.out.println("Введите дату в формате \"гггг-мм-дд\":");
        String date = scanner.nextLine();
        if(date.equals("0")) {
            return ImappingConstants.LOG_OUT;
        } else {
            // проверяем корректность ввода даты
            try {
                readingDate = LocalDate.parse(date);
                return ImappingConstants.NEW_READING;
            } catch (DateTimeParseException ex) {
                System.out.println("Неверный ввод даты!");
                readingDate = LocalDate.now();
                System.out.println("Дата установлена - " + readingDate.toString());
                return ImappingConstants.NEW_READING;
            }
        }
    }
    
    private String getHotColdWater() {
        System.out.println("Введите 1 для горячей воды или 2 для холодной:");
        int hot = scanner.nextInt();
        if(hot == 0) {
            return ImappingConstants.LOG_OUT;
        } else {
            // проверяем корректность ввода
            switch (hot) {
                case 1:
                    isHot = true;
                    return ImappingConstants.NEW_READING;
                    
                case 2:
                    isHot = false;
                    return ImappingConstants.NEW_READING;
                    
                default:
                    System.out.println("Неверный ввод данных! Установлен признак по умолчанию для холодной воды");
                    return ImappingConstants.LOG_OUT;
            }
            
        }
        
    }
    
    private void initComponents() {
        String userName = user.getUsername();// получаем имя пользователя
        String accountNumber = user.getAcc().getAccountNumber();// получаем номер аккаунта
        String title = "Добро пожаловать на страницу персонального аккаунта.\n" +
                "Здесь вы можете передать новые показания, посмотреть историю " +
                "показаний.\nПредупреждение: внесение показаний допускается " +
                "только один раз в текущем месяце для каждой категории.\nПользователь: " + 
                userName + "\nЛицевой счёт: " + accountNumber +
                "\nСегодня: " + LocalDate.now();
        System.out.println(title);
        // получаем данные по показаниям
        ArrayList<Reading> readings = user.getAcc()
                .getReadings();
        // выводим показания
        printReadingList(readings);
        name = null;
    }

    private void printReadingList(ArrayList<Reading> readings) {
        if(readings != null) {
            System.out.println("|-История показаний-|");
            System.out.println("|-№ п/п-|----Дата----|-Показание-|-Горячая-|");
            readings.stream().map((r) -> (WaterReading) r).forEachOrdered((wr) -> {
                System.out.println("|-" + wr.getIdNumber() +
                        " | " + wr.getDate() + " | " + wr.getMeasuring() +
                        " | " + wr.isHot() + " |");
            });
        }
    }
    
    private Request addNewReadingRequest() {
        // создаём запрос на добавление показаний
        WaterReading wr = new WaterReading(readingDate, measuring, isHot);
        Request request = new Request(NEW_READING, false);
        request.getBody()[0][0] = ImappingConstants.ACCOUNT;
        request.getBody()[0][1] = user.getAcc().getAccountNumber();
        request.getBody()[1][0] = ImappingConstants.ROLE;
        request.getBody()[1][1] = user.getRole();
        request.addToBody(wr);
        return request;
    }

    private void addReading(int id) {
        // создаём объект показаний
        WaterReading reading = new WaterReading(readingDate, measuring, isHot);
        ArrayList<Reading> readings = user.getAcc().getReadings();
        // получаем номер последней записи
        int idNumber = readings.size();
        idNumber++;// увеличиваем
        // создаём новую запись для добавления
        WaterReading newWaterReading = new WaterReading(idNumber, id, 
                reading.getDate(), reading.getMeasuring(), reading.isHot());
        readings.add(newWaterReading);
        printReadingList(readings);
        name = null;
//        waitForChoice();
    }
    
}
