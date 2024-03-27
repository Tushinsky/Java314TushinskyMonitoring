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
    
    public Request getRequest() {
        if(okAction.equals(ImappingConstants.LOG_IN)){
            request.getBody()[0][0] = "login";// ключ
            request.getBody()[0][1] = txtLogin.getText();// значение - логин пользователя
            request.getBody()[1][0] = "password";// ключ
            request.getBody()[1][1] = String.valueOf(passwordField.getPassword());// значение - пароль пользователя
        } else {
            request.getBody()[0][0] = "username";// ключ
            request.getBody()[0][1] = txtUserName.getText();// значение - логин пользователя
            request.getBody()[1][0] = "login";// ключ
            request.getBody()[1][1] = txtLogin.getText();// значение - логин пользователя
            request.getBody()[2][0] = "password";// ключ
            request.getBody()[2][1] = String.valueOf(passwordField.getPassword());// значение - пароль пользователя
        }
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

    /**
     * Действия для кнопки ввода (задаёт свойство Name панели)
     *
     * @param okAction задаёт свойство Name панели
     */
    @Override
    public void setOkAction(String okAction) {
        super.setOkAction(okAction);
        if (okAction.equals(ImappingConstants.LOG_IN)) {
            super.setCaption("<table border=\"0\" cellspacing=\"0\" cellpadding=\"2\" " + 
                    "align=\"center\" cols=\"1\" width=\"100%\">" +
                    "<tr><td align=\"justify\">" +
                    "Вы находитесь на странице авторизации. " +
                    "Введите Ваш <b><u>логин</u></b> и <b><u>пароль</u></b>" +
                    " для доступа в личный кабинет." +
                    "</td></tr></table>");
            // скрывавем метку и поле для ввода имени пользователя
            super.addComponent(getLoginBox());
        } else {
            super.setCaption("<table border=\"0\" cellspacing=\"0\" cellpadding=\"2\" " + 
                    "align=\"center\" cols=\"1\" width=\"100%\">" +
                    "<tr><td align=\"justify\">" +
                    "Вы находитесь на странице регистрации. " +
                    "Задайте Ваши <b><u>имя</u></b>, <b><u>логин</u></b> и " +
                    "<b><u>пароль</u></b> для регистрации на сайте и " +
                    "получения доступа в личный кабинет." +
                    "</td></tr></table>");
            // показываем метку и поле для ввода имени пользователя
            super.addComponent(getRegisterBox());
        }
        request = new Request(okAction, false);// запрос на сервер для входа
        this.okAction = okAction;
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
        super.setCaption("Caption...");
        txtLogin = new JTextField(20);// поле для ввода логина
        passwordField = new JPasswordField(20);// поле для ввода пароля
        txtUserName = new JTextField(20);
//        Box box = loginBox();
        super.setExitAction(LOG_OUT);
//        super.addComponent(box);
        super.setOkCaption("Войти");
//        addPropertyChangeListener("visible", evt -> {
//            System.out.println("visible " + evt.getNewValue().toString());
//            txtLogin.setText("");
//            passwordField.setText("");
//        });
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
        box.add(Box.createVerticalStrut(40));
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
