/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frame;

import api.Response;
import entities.IRoleConstants;
import entities.Reading;
import entities.User;
import entities.WaterReading;
import mapping.ImappingConstants;
import in.Request;
import java.time.LocalDate;
import java.util.ArrayList;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import static mapping.ImappingConstants.GET_READING;
import static mapping.ImappingConstants.LOG_OUT;
import static mapping.ImappingConstants.NEW_READING;
import static mapping.ImappingConstants.REMOVE_ACCOUNT;
import static mapping.ImappingConstants.REMOVE_READING;

/**
 *
 * @author Sergey
 */
public class AdminPagePanel extends PagePanel{
    private final JList<String> readingList = new JList<>();// список показаний
    private JTextField txtReading;// поле для ввода показаний
    private Response response;
    private String accountNumber;
    private String userName;
    private String userRole;
    private final JCheckBox chkHotBox = new JCheckBox("горячая");// отмечает показания по горячей или холодной воде
    private ArrayList<Reading> responseData;
    private final JList<String> userList = new JList<>();// список зарегистрированных пользователей
        
    /**
     *
     */
    public AdminPagePanel() {
        super();
    }
    
    @Override
    public Request getRequest() {
        switch (this.getName()) {
            case REMOVE_ACCOUNT:
                return removeAccount();
            case REMOVE_READING:
                return removeReadings();
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
            initComponents();// инициализация компонентов пользовательского интерфейса
        } else {
            // если передаются другие данные (новые показания, на удаление аккаунта)
            switch (response.getBody()[0][0]) {
                case REMOVE_ACCOUNT:
                    if (!response.isAuth()) {
                        // удаление аккаунта пользователя
                        JOptionPane.showMessageDialog(null,
                                "При удалении аккаунта произошли ошибки. Обратитесь к разработчику",
                                "Warning", JOptionPane.WARNING_MESSAGE);
                    } else {
                        // проверяем кто дал запрос на удаление аккаунта: администратор или рядовой пользователь
                        if (response.getBody()[2][1].equals(IRoleConstants.USER)) {
                            // запрос сделал пользователь, выходим со страницы
                            this.setName(LOG_OUT);
                        } else {
                            // запрос сделал администратор, обновляем список пользователей
                            System.out.println("Удаление аккаунта успешно");
                        }
                    }   break;
                case GET_READING:
                    updateResponseData();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Инициализация компонентов пользовательского интерфейса
     */
    private void initComponents() {
        txtReading = new JTextField(10);// поле для ввода показаний
        chkHotBox.setSelected(false);// вывод показаний по холодной воде
        userName = response.getBody()[0][1];
        userRole = response.getBody()[2][1];
        // создаём элементы пользовательского интерфейса
        super.setCaption("<table border=\"0\" cellspacing=\"0\" cellpadding=\"3\" " + 
                "align=\"center\" cols=\"1\" width=\"100%\" " +
                "style=\"font-size:medium;\" bgcolor=\"#AAFF80\">" +
                "<tr><td align=\"center\">" +
                "Вы вошли на страницу с правами администратора." + 
                " Администратор <b><u>" +
                userName + "</u></b></td></tr>" +
                "<tr><td align=\"left\">Сегодня <b><u>" + LocalDate.now() + "</u></b></td></tr></table>");
        super.addComponent(getAdminBox());
        super.setRemoveCaption("Удалить аккаунт");
        super.setOkAction(NEW_READING);
        super.setRemoveAction(REMOVE_ACCOUNT);
        super.setExitAction(LOG_OUT);
        super.setOkCaption("Добавить показания");

        // добавляем слушатель на флажок
        chkHotBox.addActionListener((e -> {
            updateResponseData();// список показаний
        }));
        userList.setSelectedIndex(0);
    }

    /**
     * Создаёт элементы пользовательского интерфайса для администратора.
     * Заполняет их данными
     */
    private Box getAdminBox() {
        createUserList();// создаём и заполняем список пользователей
        Box adminBox = Box.createVerticalBox();// контейнер для размещения
        JLabel lblUsers = new JLabel("Пользователи");
        JButton removeReadingButton = new JButton("Удалить показания");

        // создаём контейнер для размещения списка пользователей
        Box box1 = Box.createVerticalBox();
        box1.add(lblUsers);
        box1.add(Box.createVerticalStrut(5));
        // список ложим в панель прокрутки и размещаем в контейнере
        box1.add(new JScrollPane(userList));
        box1.add(Box.createVerticalStrut(10));

        Box box2 = createReadingBox();// создаём контейнер для списка показаний

        /*
        Создаём контейнер для размещения поля ввода новы показаний, флажка
        для задания признака холодной или горячей воды, кнопки для удаления
        выбранных показаний
        */
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

        // размещаем все созданные элементы
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

    /**
     * Создаёт и возвращает контейнер для размещения списка показаний выбранного
     * пользователя.
     * @return box - контейнер для размещения списка показаний
     */
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

    /**
     * Возвращает модель для заполнения списка данными
     * @param data список данных
     * @param isHot флаг для фильтра данных
     * @return полученную модель для заполнения списка
     */
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

    /**
     * Создание и заполнение списка пользователей данными
     */
    private void createUserList() {
        DefaultListModel<String> model = new DefaultListModel<>();
        int index = 0;
        User user;
        // заполняем модель списка
        while((user = response.getFromBody(index)) != null) {
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
    }

    /**
     * Возвращает запрос на удаление пользовательского аккаунта
     * @return request - запрос на удаление аккаунта
     */
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

    /**
     * Возвращает запрос на удаление показаний из аккаунта выбранного пользователя
     * @return request - запрос на удаление показаний
     */
    private Request removeReadings() {
        return null;
    }

    /**
     * Возвращает запрос на получение показаний выделенного пользователя
     * @return request - запрос на получение показаний
     */
    private Request getReading() {
        Request request = new Request(GET_READING, false);// создаём запрос на добавление показаний
        request.getBody()[0][0] = ImappingConstants.ACCOUNT;
        request.getBody()[0][1] = accountNumber;
        return request;
    }

    /**
     * Обновление данных из полученного ответа, заполнение списка
     */
    private void updateResponseData() {
        int index = userList.getSelectedIndex();
        responseData = response.getFromBody(index).getAcc().getReadings();
        readingList.setModel(readingListModel(responseData, chkHotBox.isSelected()));

    }
}
