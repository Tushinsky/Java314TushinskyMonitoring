package in;

import frame.StartAppWindow;
import javax.swing.SwingUtilities;


/**
 * Класс, который реализует функционал интернет-сайта с личным кабинетом.
 * Цепочка вызова методов копирует навигацию по импровизированному сайту
 * В поле класса кладем сущность API, которая будет отвечать на наши запросы
 * Храним, в качестве "cookie" флаг аутентификации
 * */

public class Front {
    /**
     * Отрисовка домашней страницы
     * */
    public void start() {
        StartAppWindow startAppWindow = new StartAppWindow();
        startAppWindow.setLocationRelativeTo(null);
        SwingUtilities.updateComponentTreeUI(startAppWindow);
        startAppWindow.setVisible(true);
    }

}
