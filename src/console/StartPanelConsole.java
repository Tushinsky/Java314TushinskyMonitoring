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
    private final Scanner scanner = new Scanner(System.in);
        
    public StartPanelConsole() {
        initComponents();
    }

    public String getMapping() {
        return mapping;
    }
    
    private void initComponents() {
        String title = "Вы находитесь на домашней странице сервиса передачи\n" + 
                "показаний счетчиков ЖКХ. Здесь вы можете: \n1 - войти в личный" +
                " кабинет для передачи показаний за текущий месяц.\n" +
                "2 - зарегистрироваться для получения доступа к личному кабинету.\n" +
                "0 - завершить работу";
        System.out.println(title);
        listMapping = new String[] {ImappingConstants.LOG_OUT, 
            ImappingConstants.LOG_IN, ImappingConstants.REGISTER};
    }
    
    public void waitForChoice() {
        System.out.println("1 - Войдите или 2 - Зарегистрируйтесь. " +
                "0 - Завершить работу.\nСделайте свой выбор:");
        int choice = scanner.nextInt();
        if(choice >= 0 && choice <= 3) {
            mapping = listMapping[choice];
        } else {
            waitForChoice();
        }
        
    }
}
