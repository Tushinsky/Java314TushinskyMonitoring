/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package console;

import java.util.Scanner;
import mapping.ImappingConstants;

/**
 *
 * @author Sergey
 */
public class StartPanelConsole {

    private String mapping;
    private String[] listMapping;
    private final Scanner scanner = new Scanner(System.in, "Windows-1251");
        
    public StartPanelConsole() {
        initComponents();
    }

    public String getMapping() {
        return mapping;
    }
    
    private void initComponents() {
        String title = 
                "|----------------------------------------------------------------|" +
                "\n" +
                "|Вы находитесь на домашней странице сервиса передачи показаний   |" + 
                "\n" + 
                "|счетчиков ЖКХ. Здесь вы можете:                                 |" + 
                "\n" + 
                "|1 - войти в личный кабинет для передачи показаний.              |" + 
                "\n" +
                "|2 - зарегистрироваться для получения доступа к личному кабинету.|" +
                "\n" +
                "|Для завершения работы нажмите 0.                                |" +
                "\n" +
                "|----------------------------------------------------------------|";
        System.out.println(title);
        listMapping = new String[] {ImappingConstants.LOG_OUT, 
            ImappingConstants.LOG_IN, ImappingConstants.REGISTER};
    }
    
    public void waitForChoice() {
        System.out.println("Сделайте свой выбор:\n" +
                "0 - Завершить работу.\n" +
                "1 - Войти в личный кабинет.\n" +
                "2 - Зарегистрироваться.");
        int choice = scanner.nextInt();
        if(choice >= 0 && choice <= 3) {
            mapping = listMapping[choice];
        } else {
            waitForChoice();
        }
        
    }
}
