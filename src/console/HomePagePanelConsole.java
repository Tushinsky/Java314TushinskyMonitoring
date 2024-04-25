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
    private final Scanner scanner = new Scanner(System.in, "Windows-1251");
    private String name;
    private final NewChangeReadingPanelConsole newChangeReadingPanelConsole;
    private final Account account;
    
    public HomePagePanelConsole(Response response) {
        // получаем пользователя из тела ответа
        user = (User) response.getFromBody(0);
        account = user.getAcc();
        newChangeReadingPanelConsole = new NewChangeReadingPanelConsole();
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
            name = NEW_READING;
            
        }
    }
    
    private void initComponents() {
        String userName = user.getUsername();// получаем имя пользователя
        String accountNumber = account.getAccountNumber();// получаем номер аккаунта
        String title = 
                "|---------------------------------------------------------------------|\n" +
                "|Добро пожаловать на страницу персонального аккаунта. Здесь вы можете:|\n" +
                "|передать новые показания, посмотреть историю показаний.              |\n" +
                "|Предупреждение: внесение показаний допускается только один раз в те- |\n" +
                "|кущем месяце для каждой категории.                                   |\n" +
                "|Пользователь: " + userName + "                                       |\n" +
                "|Лицевой счёт: " + accountNumber + "                                  |\n" +
                "|Сегодня: " + LocalDate.now() + "                                     |\n" +
                "|---------------------------------------------------------------------|";
        System.out.println(title);
        // получаем данные по показаниям
        ArrayList<Reading> readings = account.getReadings();
        // выводим показания
        printReadingList(readings);
        name = null;
    }

    private void printReadingList(ArrayList<Reading> readings) {
        if(readings != null) {
            System.out.println("|-------История показаний--------|");
            System.out.println("|№ п/п|--Дата--|Показание|Горячая|");
            readings.stream().map((r) -> (WaterReading) r).forEachOrdered((wr) -> {
                System.out.println("|-" + wr.getIdNumber() +
                        " | " + wr.getDate() + " | " + wr.getMeasuring() +
                        " | " + wr.isHot() + " |");
            });
        }
    }
    
    private Request addNewReadingRequest() {
        // создаём запрос на добавление показаний
        Request request;
        request = newChangeReadingPanelConsole.getNewReadingRequest();
        if(request != null) {
            request.getBody()[0][1] = account.getAccountNumber();
            request.getBody()[1][0] = ImappingConstants.ROLE;
            request.getBody()[1][1] = user.getRole();
        }
        return request;
    }

    private void addReading(int id) {
        // создаём объект показаний
        WaterReading reading = newChangeReadingPanelConsole.getWaterReading();
        ArrayList<Reading> readings = account.getReadings();
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
