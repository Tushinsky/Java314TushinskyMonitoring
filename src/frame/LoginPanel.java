package frame;

import mapping.ImappingConstants;
import in.Request;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.*;
import static mapping.ImappingConstants.LOG_IN;

import static mapping.ImappingConstants.LOG_OUT;

/**
 * Панель для ввода логина и пароля
 */
public class LoginPanel extends PagePanel {

    private JTextField txtLogin;
    private JPasswordField txtPasswordField;
    private Request request;
    private int count = 0;// счётчик попыток ввода
    
    @Override
    public Request getRequest() {
        request.getBody()[0][0] = ImappingConstants.LOG_IN;// ключ
        request.getBody()[0][1] = txtLogin.getText();// значение - логин пользователя
        request.getBody()[1][0] = ImappingConstants.PASSWORD;// ключ
        request.getBody()[1][1] = String.valueOf(txtPasswordField.getPassword());// значение - пароль пользователя
        count++;// увеличиваем счётчик попыток
        if(count > 3) {
            // если количество попыток больше 3
           count = 0;// сбрасываем счётчик
            
            // очищаем поля ввода
            txtLogin.setText("");
            txtPasswordField.setText("");
            
            return null;// возвращаем null в запросе
        }
        System.out.println("request: " + request);
        return request;
    }

    /**
     * Creates a new <code>JPanel</code> with a double buffer
     * and a flow layout.
     */
    public LoginPanel() {
        super(LOG_IN, LOG_OUT, "");
//        this.requestFocusInWindow();
        initComponents();

    }

    /**
     * Инициализация компонентов пользовательского интерфейса
     */
    private void initComponents() {
        initTextField();
        // текст для заголовка панели - приветствие
        super.setCaption("<table border=\"0\" cellspacing=\"0\" cellpadding=\"3\" " + 
                    "align=\"center\" cols=\"1\" style=\"font-size:14px;\" " +
                    "width=\"100%\" bgcolor=\"#AAEE00\">" +
                    "<tr><td align=\"center\">" +
                    "Вы находитесь на странице авторизации. " +
                    "Введите Ваш <b><u>логин</u></b> и <b><u>пароль</u></b>" +
                    " для доступа в личный кабинет." +
                    "</td></tr></table>");
        // добавляем на родителя контейнер с элементами управления для ввода данных
        super.addComponent(getLoginBox());
        // текст для кнопки входа
        super.setOkCaption("Войти");
        // создаём запрос на вход
        request = new Request(ImappingConstants.LOG_IN, false);
        txtLogin.requestFocusInWindow();// фокус на поле ввода логина
    }
    
    /**
     * Инициализирует поля ввода и задаёт их свойства
     */
    private void initTextField() {
        txtLogin = new JTextField(20);// поле для ввода логина
        txtLogin.setFocusable(true);
        txtPasswordField = new JPasswordField(20);// поле для ввода пароля
        txtPasswordField.setFocusable(true);
        Font font = txtLogin.getFont();// получили шрифт для полей ввода
        font = new Font(font.getFontName(), Font.BOLD, 14);// увеличили размер
        txtLogin.setFont(font);
        txtPasswordField.setFont(font);
        
        /*
        добавим обработку нажатия кнопок в текстовых полях ввода имени
        пользователя и пароля
        */
        KeyAdapter ka = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent ke) {
                super.keyPressed(ke);
                // если нажата клавиша Enter, то инициируем действие
                // для кнопки Вход, если Escape, то для кнопки Выход
                switch(ke.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        setName(ImappingConstants.LOG_IN);
                        break;
                    case KeyEvent.VK_ESCAPE:
                        setName(ImappingConstants.LOG_OUT);
                }
                
            }
            
        };
        txtLogin.addKeyListener(ka);
        txtPasswordField.addKeyListener(ka);
        
    }

    /**
     * Создаёт и возвращает контейнер для размещения компонентов GUI
     * @return BOX - контейнер для размещения компнентов GUI
     */
    private Box getLoginBox() {
        
        JLabel lblLogin = new JLabel("Логин пользователя");
        JLabel lblPassword = new JLabel("Пароль пользователя");
        Box box = Box.createVerticalBox();
        box.add(Box.createVerticalStrut(10));

        // контейнер для размещения метки и поля ввода логина
        Box box2 = Box.createHorizontalBox();
        box2.add(Box.createHorizontalStrut(50));
        box2.add(lblLogin);
        box2.add(Box.createHorizontalStrut(20));
        box2.add(txtLogin);
        box2.add(Box.createHorizontalStrut(50));
        box.add(box2);// добавляем контейнер на основной
        box.add(Box.createVerticalStrut(10));

        // контейнер для размещения метки и поля ввода пароля
        Box box3 = Box.createHorizontalBox();
        box3.add(Box.createHorizontalStrut(50));
        box3.add(lblPassword);
        // выравниваем поля ввода с помощью распорки
        int strut = (int) (lblLogin.getPreferredSize().getWidth() - 
                lblPassword.getPreferredSize().getWidth()) + 20;
        box3.add(Box.createHorizontalStrut(strut));
        box3.add(txtPasswordField);
        box3.add(Box.createHorizontalStrut(50));
        
        box.add(box3);// добавляем контейнер на основной
        box.add(Box.createVerticalStrut(10));
        return box;// возвращаем созданный контейнер
    }

    
}
