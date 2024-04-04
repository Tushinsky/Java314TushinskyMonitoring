package frame;

import in.Request;
import api.Response;
import entities.Reading;
import entities.User;
import entities.WaterReading;
import mapping.ImappingConstants;

import javax.swing.*;
import java.time.LocalDate;
import java.util.ArrayList;
import javax.swing.event.ListSelectionEvent;

import static mapping.ImappingConstants.*;

public class HomePagePanel extends PagePanel {
    private final JList<String> readingList = new JList<>();// список показаний
    private JTextField txtReading;// поле для ввода показаний
    private Response response;
    private String accountNumber;
    private String userName;
    private String userRole;
    private final JCheckBox chkHotBox = new JCheckBox("горячая");// отмечает показания по горячей или холодной воде
    private ArrayList<Reading> responseData;
    
    /**
     * Creates a new <code>JPanel</code> with a double buffer
     * and a flow layout.
     */
    public HomePagePanel() {
        super();
        initComponents();

    }

    @Override
    public Request getRequest() {
        switch (this.getName()) {
            case NEW_READING:
                return addNewReading();
            case GET_READING:
                return getReading();
            default:
                break;
        }
        return null;
    }

    @Override
    public void setResponse(Response response) {
        this.response = response;
        if (response.getBody()[0][0].equals(ImappingConstants.USER_NAME)) {
            // если передаётся имя пользователя, вызывается инициализация компонентов
            userName = response.getFromBody(0).getUsername();
            accountNumber = response.getFromBody(0).getAcc().getAccountNumber();
            super.setCaption("<table border=\"0\" cellspacing=\"0\" cellpadding=\"3\" " + 
                    "width=\"100%\" style=\"font-size:medium;\" bgcolor=\"#AAFF00\">" +
                    "<tr><td align=\"justify\">" + 
                    "Добро пожаловать на страницу персонального аккаунта." +
                    " Пользователь: <b>" + 
                    userName + "</b>  Лицевой счёт: <b>" + accountNumber +
                    "</b></td></tr>" +
                    "<tr><td align=\"left\">Сегодня: <b><u>" + LocalDate.now() + "</u></b>" +
                    "</td></tr></table>");
            userRole = response.getBody()[2][1];
            responseData = response.getFromBody(0).getAcc().getReadings();
            readingList.setModel(readingListModel(responseData, chkHotBox.isSelected()));// список показаний

        } else {
            // если ответ положительный, и новые показания приняты, добавляем их в список показаний
            if (response.isAuth()) {
                // создаём класс показаний
                updateResponseData();// обновляем данные в списке
            } else {
                JOptionPane.showMessageDialog(null,
                        "Внесение показаний допускается только одни раз в текущем месяце",
                        "Warning", JOptionPane.WARNING_MESSAGE);
            }

//                }

        }
    }

    private void initComponents() {
        // вошёл простой пользователь
        txtReading = new JTextField(10);// поле для ввода показаний
        super.setCaption("<table border=\"0\" cellspacing=\"0\" cellpadding=\"3\" " + 
                "align=\"center\" width=\"100%\" bgcolor=\"#AAFF80\">" +
                "<tr><td align=\"center\">" + 
                "Добро пожаловать на страницу персонального аккаунта." +
                "</td></tr>" +
                "<tr><td align=\"left\">Пользователь: <b>Пользователь</b>  Лицевой счёт: <b>" +
                "Номер лицевого счёта</b></td></tr>" +
                "<tr><td align=\"left\">Сегодня: <b><u>" + LocalDate.now() + "</u></b>" +
                "</td></tr></table>");
        super.setRemoveAction("");// скрываем кнопку удаления аккаунта
        super.addComponent(getUserBox());
        
        super.setOkAction(NEW_READING);
        super.setRemoveAction(REMOVE_ACCOUNT);
        super.setExitAction(LOG_OUT);
        super.setOkCaption("Добавить показания");

        // добавляем слушатель на флажок
        chkHotBox.addActionListener((e -> {
            try {
                readingList.setModel(readingListModel(responseData, 
                        chkHotBox.isSelected()));// список показаний
            } catch(Exception ex) {
                System.out.println("error:" + ex.getMessage());
            }
        }));
        chkHotBox.setSelected(false);// вывод показаний по холодной воде
        
    }

    /**
     * Возвращает данные текущего пользователя
     */
    private Box getUserBox() {
        Box userBox = createReadingBox();

        JLabel lblReading = new JLabel("Введите показание");
        Box readingBox = Box.createHorizontalBox();
        readingBox.add(lblReading);
        readingBox.add(Box.createHorizontalStrut(5));
        readingBox.add(txtReading);
        readingBox.add(Box.createHorizontalStrut(5));
        readingBox.add(chkHotBox);
        readingBox.add(Box.createHorizontalStrut(5));

        userBox.add(readingBox);
        userBox.add(Box.createVerticalStrut(10));
        return userBox;
    }

    /**
     * Возвращает данные для администратора сайта
     */
    private Box getAdminBox() {
        Box adminBox = Box.createVerticalBox();
        JLabel lblUsers = new JLabel("Пользователи");
        final JList<String> userList = getUserList();// список зарегистрированных пользователей
        JButton removeReadingButton = new JButton("Удалить показания");

        Box box1 = Box.createVerticalBox();
        box1.add(lblUsers);
        box1.add(Box.createVerticalStrut(5));
        box1.add(new JScrollPane(userList));
        box1.add(Box.createVerticalStrut(10));

        Box box2 = createReadingBox();

        Box box3 = Box.createHorizontalBox();
        box3.add(Box.createHorizontalStrut(10));
        box3.add(new JLabel("Новые показания"));
        box3.add(Box.createHorizontalStrut(10));
        box3.add(txtReading);
        box3.add(Box.createHorizontalStrut(5));
        box3.add(chkHotBox);
        box3.add(Box.createHorizontalStrut(20));
        box3.add(removeReadingButton);
        box3.add(Box.createHorizontalGlue());

        Box box4 = Box.createHorizontalBox();
        box4.add(box1);
        box4.add(Box.createHorizontalStrut(10));
        box4.add(box2);

        adminBox.add(box4);
        adminBox.add(Box.createVerticalStrut(20));
        adminBox.add(box3);
        adminBox.add(Box.createVerticalStrut(20));

        return adminBox;
    }

    private Box createReadingBox() {
        Box box = Box.createVerticalBox();
        Box horBox = Box.createHorizontalBox();
        horBox.add(Box.createHorizontalGlue());
        horBox.add(new JLabel("Показания"));
        horBox.add(Box.createHorizontalGlue());
        box.add(horBox);
        box.add(Box.createVerticalStrut(5));
        box.add(new JScrollPane(readingList));
        box.add(Box.createVerticalStrut(10));
        return box;
    }

    private DefaultListModel<String> readingListModel(ArrayList<Reading> data, boolean isHot) {
        DefaultListModel<String> model = new DefaultListModel<>();
        System.out.println("data= " + data);
        if(data != null) {
            data.stream().filter((r) -> {
                WaterReading wr = (WaterReading) r;
                return wr.isHot() == isHot;
            }).forEach((Reading r) -> model.addElement(r.getDate() + 
                    " | " + r.getMeasuring()));
            
        }
        return model;
    }

    private JList<String> getUserList() {
        JList<String> userList = new JList<>();// список зарегистрированных пользователей
        DefaultListModel<String> model = new DefaultListModel<>();
        int index = 0;
        User user;
        // заполняем модель списка
        while((user = response.getFromBody(index)) != null) {
//            System.out.println("user:" + user.getUsername() + "; readings:" + 
//                    user.getAcc().getReadings());
            model.addElement(user.getUsername());// имя пользователя
            index++;// увеличиваем счётчик цикла
        }
        userList.setModel(model);
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);// выделение одной строки
        userList.addListSelectionListener((ListSelectionEvent e) -> {
            // получаем пользователя по индексу выделенного элемента списка
            User u = response.getFromBody(userList.getSelectedIndex());
            // заполняем список показаний
            readingList.setModel(readingListModel(u.getAcc().getReadings(), 
                    chkHotBox.isSelected()));// список показаний
        });
        return userList;
    }

    private Request addNewReading() {
        Request request = new Request(NEW_READING, false);// создаём запрос на добавление показаний
        request.getBody()[0][0] = ImappingConstants.ACCOUNT;
        request.getBody()[0][1] = accountNumber;
        request.getBody()[1][0] = ImappingConstants.MEASURING;
        request.getBody()[1][1] = txtReading.getText();
        request.getBody()[2][0] = ImappingConstants.LOCAL_DATE;
        request.getBody()[2][1] = LocalDate.now().toString();
        request.getBody()[3][0] = ImappingConstants.IS_HOT;
        request.getBody()[3][1] = chkHotBox.isSelected() ? "1" : "0";
        return request;
    }

    private Request removeAccount() {
        Request request = new Request(REMOVE_ACCOUNT, false);// создаём запрос на добавление показаний
        request.getBody()[0][0] = ImappingConstants.USER_NAME;
        request.getBody()[0][1] = userName;
        request.getBody()[1][0] = ImappingConstants.ROLE;
        request.getBody()[1][1] = userRole;
        request.getBody()[2][0] = ImappingConstants.ACCOUNT;
        request.getBody()[2][1] = accountNumber;

        return request;
    }

    private Request getReading() {
        Request request = new Request(GET_READING, false);// создаём запрос на добавление показаний
        request.getBody()[0][0] = ImappingConstants.ACCOUNT;
        request.getBody()[0][1] = accountNumber;
        return request;
    }

    private void updateResponseData() {
        responseData = response.getFromBody(0).getAcc().getReadings();
        readingList.setModel(readingListModel(responseData, chkHotBox.isSelected()));

    }

}
