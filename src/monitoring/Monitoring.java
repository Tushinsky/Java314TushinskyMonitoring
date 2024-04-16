/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitoring;

import frame.StartAppWindow;
import java.awt.Window;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Точка входа в приложение - запускает на выполнение
 * @author Sergii.Tushinskyi
 */
public class Monitoring {

    /**
     * @param args the command line arguments
     * @throws javax.swing.UnsupportedLookAndFeelException исключительная
     * ситуация, которая может произойти при выполнении метода
     */
    public static void main(String[] args) throws UnsupportedLookAndFeelException {
//        try {
//                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//            } catch (ClassNotFoundException | InstantiationException | 
//                    IllegalAccessException | UnsupportedLookAndFeelException ex) {
//                Logger.getLogger(Monitoring.class.getName()).log(Level.SEVERE, null, ex);
//            }
            
        // TODO code application logic here
        StartAppWindow startAppWindow = new StartAppWindow();
//        StartAppWindow.setDefaultLookAndFeelDecorated(true);
//        startAppWindow.setUndecorated(true);
//        startAppWindow.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
        startAppWindow.setType(Window.Type.NORMAL);
        startAppWindow.setLocationRelativeTo(null);
//        startAppWindow.setTitle("Сервис передачи показаний счётчиков воды");
//        SwingUtilities.updateComponentTreeUI(startAppWindow);
        startAppWindow.setVisible(true);

    }
    
}
