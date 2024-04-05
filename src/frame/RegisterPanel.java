/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frame;

import in.Request;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import mapping.ImappingConstants;
import static mapping.ImappingConstants.LOG_OUT;
import static mapping.ImappingConstants.REGISTER;

/**
 *
 * @author Sergey
 */
public class RegisterPanel extends PagePanel {
    private JTextField txtLogin;
    private JPasswordField passwordField;
    private Request request;
    private JTextField txtUserName;
    private final JLabel lblUserName = new JLabel("Имя пользователя");
    
    public RegisterPanel() {
        super();
        initComponents();
    }
    
    
    @Override
    public Request getRequest() {
        request.getBody()[0][0] = ImappingConstants.USER_NAME;// ключ
        request.getBody()[0][1] = txtUserName.getText();// значение - логин пользователя
        request.getBody()[1][0] = ImappingConstants.LOG_IN;// ключ
        request.getBody()[1][1] = txtLogin.getText();// значение - логин пользователя
        request.getBody()[2][0] = ImappingConstants.PASSWORD;// ключ
        request.getBody()[2][1] = String.valueOf(passwordField.getPassword());// значение - пароль пользователя
        return request;
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
     * Инициализация компонентов пользовательского интерфейса
     */
    private void initComponents() {
        super.setExitAction(LOG_OUT);
        super.setOkAction(REGISTER);
        super.setOkCaption("Зарегистрироваться");
        super.setCaption("<table border=\"0\" cellspacing=\"0\" cellpadding=\"3\" " + 
                "align=\"center\" cols=\"1\" style=\"font-size:medium;\"" +
                "width=\"100%\" height=\"100%\" bgcolor=\"#00FF00\">" +
                "<tr><td align=\"justify\">" +
                "Вы находитесь на странице регистрации. " +
                "Задайте Ваши <b><u>имя</u></b>, <b><u>логин</u></b> и " +
                "<b><u>пароль</u></b> для регистрации на сайте и " +
                "получения доступа в личный кабинет." +
                "</td></tr></table>");
        // показываем метку и поле для ввода имени пользователя
        txtLogin = new JTextField(20);// поле для ввода логина
        passwordField = new JPasswordField(20);// поле для ввода пароля
        txtUserName = new JTextField(20);
        super.addComponent(getRegisterBox());
        request = new Request(REGISTER, false);// запрос на сервер для входа
        
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
