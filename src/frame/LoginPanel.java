package frame;

import mapping.ImappingConstants;
import in.Request;

import javax.swing.*;

import static mapping.ImappingConstants.LOG_OUT;
import java.beans.PropertyChangeListener;

/**
 * Панель для ввода логина и пароля
 */
public class LoginPanel extends PagePanel {

    private JTextField txtLogin;
    private JPasswordField passwordField;
    private Request request;
    private JTextField txtUserName;
    private final JLabel lblUserName = new JLabel("Имя пользователя");
    private String okAction;
    private int count = 0;// счётчик попыток ввода
    
    @Override
    public Request getRequest() {
        request.getBody()[0][0] = ImappingConstants.LOG_IN;// ключ
        request.getBody()[0][1] = txtLogin.getText();// значение - логин пользователя
        request.getBody()[1][0] = ImappingConstants.PASSWORD;// ключ
        request.getBody()[1][1] = String.valueOf(passwordField.getPassword());// значение - пароль пользователя
        count++;// увеличиваем счётчик попыток
        if(count > 3) {
            // если количество попыток больше 3
           count = 0;// сбрасываем счётчик
            
            // очищаем поля ввода
            txtLogin.setText("");
            passwordField.setText("");
            
            return null;// возвращаем null в запросе
        }
        System.out.println("request: " + request);
        return request;
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        super.removePropertyChangeListener(listener); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        super.addPropertyChangeListener(listener); //To change body of generated methods, choose Tools | Templates.
    }

    public String getPassword() {
        return String.valueOf(passwordField.getPassword());
    }

    public String getLogin() {
        return txtLogin.getText();
    }

    public String getUserName() {
        return txtUserName.getText();
    }
    /**
     * Creates a new <code>JPanel</code> with a double buffer
     * and a flow layout.
     */
    public LoginPanel() {
        super();
        initComponents();

    }

    /**
     * Инициализация компонентов пользовательского интерфейса
     */
    private void initComponents() {
        txtLogin = new JTextField(20);// поле для ввода логина
        passwordField = new JPasswordField(20);// поле для ввода пароля
        txtUserName = new JTextField(20);
        super.setOkAction(ImappingConstants.LOG_IN);
        super.setCaption("<table border=\"0\" cellspacing=\"0\" cellpadding=\"3\" " + 
                    "align=\"center\" cols=\"1\" width=\"100%\" height=\"100%\" bgcolor=\"#00FF00\">" +
                    "<tr><td align=\"justify\">" +
                    "Вы находитесь на странице авторизации. " +
                    "Введите Ваш <b><u>логин</u></b> и <b><u>пароль</u></b>" +
                    " для доступа в личный кабинет." +
                    "</td></tr></table>");
        super.addComponent(getLoginBox());
        super.setExitAction(LOG_OUT);
//        super.addComponent(box);
        super.setOkCaption("Войти");
        request = new Request(ImappingConstants.LOG_IN, false);
    }

    private Box getLoginBox() {
        
        JLabel lblLogin = new JLabel("Логин пользователя");
        JLabel lblPassword = new JLabel("Пароль");
        Box box = Box.createVerticalBox();
        box.add(Box.createVerticalStrut(10));

        Box box2 = Box.createHorizontalBox();
        box2.add(Box.createHorizontalStrut(10));
        box2.add(lblLogin);
        box2.add(Box.createHorizontalStrut(10));
        box2.add(txtLogin);
        box2.add(Box.createHorizontalStrut(10));
        box.add(box2);
        box.add(Box.createVerticalStrut(10));

        Box box3 = Box.createHorizontalBox();
        box3.add(Box.createHorizontalStrut(10));
        box3.add(lblPassword);
        box3.add(Box.createHorizontalStrut((int) (lblLogin.getPreferredSize().getWidth() - 
                lblPassword.getPreferredSize().getWidth()) + 10));
        box3.add(passwordField);
        box3.add(Box.createHorizontalStrut(10));
        
        box.add(box3);
        box.add(Box.createVerticalStrut(10));
        return box;
    }
    
    private Box getRegisterBox() {
        
        JLabel lblLogin = new JLabel("Логин пользователя");
        JLabel lblPassword = new JLabel("Пароль");
        Box box = Box.createVerticalBox();
        box.add(Box.createVerticalStrut(10));

        Box box1 = Box.createHorizontalBox();
        box1.add(Box.createHorizontalStrut(10));
        box1.add(lblUserName);
        box1.add(Box.createHorizontalStrut((int) (lblLogin.getPreferredSize().getWidth() - 
                lblUserName.getPreferredSize().getWidth()) + 10));
        box1.add(txtUserName);
        box1.add(Box.createHorizontalStrut(10));
        
        box.add(box1);
        box.add(Box.createVerticalStrut(10));

        Box box2 = Box.createHorizontalBox();
        box2.add(Box.createHorizontalStrut(10));
        box2.add(lblLogin);
        box2.add(Box.createHorizontalStrut(10));
        box2.add(txtLogin);
        box2.add(Box.createHorizontalStrut(10));
        box.add(box2);
        box.add(Box.createVerticalStrut(10));

        Box box3 = Box.createHorizontalBox();
        box3.add(Box.createHorizontalStrut(10));
        box3.add(lblPassword);
        box3.add(Box.createHorizontalStrut((int) (lblLogin.getPreferredSize().getWidth() - 
                lblPassword.getPreferredSize().getWidth()) + 10));
        box3.add(passwordField);
        box3.add(Box.createHorizontalStrut(10));
        
        box.add(box3);
        box.add(Box.createVerticalStrut(40));
        return box;
    }

    
}
