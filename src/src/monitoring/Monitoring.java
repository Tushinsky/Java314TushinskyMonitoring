/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitoring;

import console.StartAppConsole;
import frame.StartAppWindow;
import java.awt.Window;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
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
        try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | 
                    IllegalAccessException | UnsupportedLookAndFeelException ex) {
                Logger.getLogger(Monitoring.class.getName()).log(Level.SEVERE, null, ex);
            }
        System.out.println("Выберите: 1 - графическое приложение; Any - консоль");
        int choice = new Scanner(System.in).nextInt();
        if(choice == 1) {
            // TODO code application logic here
            StartAppWindow startAppWindow = new StartAppWindow();
            StartAppWindow.setDefaultLookAndFeelDecorated(true);
    //        startAppWindow.setUndecorated(true);
    //        startAppWindow.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
            startAppWindow.setType(Window.Type.NORMAL);
            startAppWindow.setLocationRelativeTo(null);
    //        SwingUtilities.updateComponentTreeUI(startAppWindow);
            startAppWindow.setVisible(true);
        } else {
            StartAppConsole console = new StartAppConsole();
            console.display();
        }
    }
    
}
