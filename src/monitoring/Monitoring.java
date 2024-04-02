/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitoring;

import frame.StartAppWindow;
import javax.swing.SwingUtilities;

/**
 *
 * @author Sergii.Tushinskyi
 */
public class Monitoring {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        StartAppWindow startAppWindow = new StartAppWindow();
        startAppWindow.setLocationRelativeTo(null);
        SwingUtilities.updateComponentTreeUI(startAppWindow);
        startAppWindow.setVisible(true);

    }
    
}
