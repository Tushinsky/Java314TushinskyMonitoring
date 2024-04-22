package frame;

import java.awt.Color;
import mapping.ImappingConstants;

import javax.swing.*;
import javax.swing.border.EtchedBorder;

/**
 * Стартовая панель страницы входа
 */
public class StartPanel extends PagePanel {

    /**
     * Creates a new <code>JPanel</code> with a double buffer
     * and a flow layout.
     */
    public StartPanel() {
        super(ImappingConstants.LOG_IN, ImappingConstants.EXIT, ImappingConstants.REGISTER);
        initComponents();// инициализируем компоненты интерфейса
        super.setBorder(new EtchedBorder(4, Color.yellow, Color.black));
    }

    private void initComponents() {

        super.setCaption("<table border=\"0\" cellspacing=\"0\" cellpadding=\"3\" " + 
                "align=\"center\" cols=\"1\" width=\"100%\" " +
                "style=\"font-size:10px;\" bgcolor=\"#00FF00\">" +
                "<tr><td align=\"justify\">" +
                "Вы находитесь на домашней странице сервиса передачи " + 
                "показаний счетчиков ЖКХ. Здесь вы " +
                "можете <b><u>войти</u></b> в личный кабинет для передачи показаний " +
                "за текущий месяц. Или " +
                "<b><u>зарегистрироваться</u></b> для получения доступа к личному кабинету." +
                "</td></tr>" +
                "<tr><td align=\"center\">Выберите:</td></tr>" +
                "</table>");
        super.addComponent(Box.createVerticalBox().add(Box.createVerticalStrut(100)));
        super.setOkCaption("Вход в л/к");
        super.setRemoveCaption("Регистрация");
        super.setExitCaption("Выйти");
        

    }

}
