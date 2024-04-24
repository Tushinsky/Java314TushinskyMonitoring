/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package console;

import entities.WaterReading;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import mapping.ImappingConstants;
import static mapping.ImappingConstants.NEW_READING;
import query.Request;

/**
 *
 * @author Sergii.Tushinskyi
 */
public class NewChangeReadingPanelConsole {
    private WaterReading reading;
    private int measuring;
    private String readingDate;
    private boolean isHot;
    private final Scanner scanner;
    
    public NewChangeReadingPanelConsole() {
        scanner = new Scanner(System.in, "UTF-8");
//        initComponents();
    }

    /**
     * Возвращает тело запроса для добавления новых показаний
     * @return тело запроса на добавление
     */
    public Request getNewReadingRequest() {
        System.out.println("Введите дату, показание, признак горячей воды," + 
                " разделяя данные точкой с запятой:");
        String strReading = scanner.nextLine();// получили данные
        // преобразуем в массив, размер должен быть равен 3
        String[] strData = strReading.split(";");
        // проверяем
        if(strData.length != 3 || !isValidData(strData[1])) {
            System.out.println("Ошибка! Неверный ввод данных!");
            return null;
        }
        
        readingDate = strData[0];
        
        isHot = !strData[2].equals("0");
        LocalDate ld = getValidDate();// получаем дату
        
        // создаём объект новых показаний
        WaterReading wr = new WaterReading(ld, measuring, isHot);
        // создаём запрос на добавление показаний
        Request request = new Request(NEW_READING, false);
        request.getBody()[0][0] = ImappingConstants.ACCOUNT;
        request.addToBody(wr);
        return request;
    }
    
    /**
     * Возвращает тело запроса для операции изменения показаний
     * @return request - запрос для операции именения показаний
     */
    public Request getChangeReadingRequest() {
        System.out.println("Внесите изменения: ");
        System.out.println(reading.getDate() + ";" + reading.getMeasuring() +
                ";" + (reading.isHot() == true ? 1 : 0));
        String strReading = scanner.nextLine();
        // преобразуем в массив, размер должен быть равен 3
        String[] strData = strReading.split(";");
        // проверяем
        if(strData.length != 3 || !isValidData(strData[1])) {
            System.out.println("Ошибка! Неверный ввод данных!");
            return null;
        }
        
        readingDate = strData[0];
        isHot = !strData[2].equals(0);
        
        LocalDate ld = getValidDate();
        
        WaterReading wr = new WaterReading(reading.getIdNumber(), 
                reading.getId(), ld, measuring, isHot);
        // создаём запрос на добавление показаний
        Request request = new Request(ImappingConstants.CHANGE_READING, false);
        request.getBody()[0][0] = ImappingConstants.CHANGE_READING;
        request.addToBody(wr);
        return request;
    }

    /**
     * Задаёт экземпляр показаний для операции изменения
     * @param reading экземпляр WaterReading для операции изменения
     */
    public void setWaterReading(WaterReading reading) {
        // задаём значения
        this.reading = reading;
        measuring = reading.getMeasuring();
        readingDate = reading.getDate().toString();
        isHot = reading.isHot();
    }

    /**
     * Возвращает новые показания или после редактирования
     * @return объект показаний после добавления или редактирования
     */
    public WaterReading getWaterReading() {
        
        return new WaterReading(LocalDate.parse(readingDate), measuring, isHot);
    }

    private void initComponents() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    /**
     * Проверяет корректность и возвращает дату как экземпляр класса LocalDate
     * @return дата в формате "гггг-мм-дд"
     */
    private LocalDate getValidDate() {
        LocalDate ld;
        // проверяем корректность ввода даты
        try {
            ld = LocalDate.parse(readingDate);
        } catch (DateTimeParseException ex) {
            System.out.println("Неверный ввод даты! Проверьте правильность ввода.");
            ld = LocalDate.now();
        }
        return ld;
    }
    
    /**
     * Проверка корректности введённых данных
     * @return true в случае успеха, иначе возвращается false
     */
    private boolean isValidData(String data) {
        // проверяем корректность ввода показаний
        try {
            measuring = Integer.parseInt(data);
        } catch(NumberFormatException ex) {
            return false;
        }
        return true;
    }
}
