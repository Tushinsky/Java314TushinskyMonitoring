/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frame;

import api.Response;
import entities.Reading;
import entities.User;
import entities.WaterReading;
import in.FormattedTextFieldFerifier;
import mapping.ImappingConstants;
import in.Request;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.text.MaskFormatter;
import static mapping.ImappingConstants.LOG_OUT;
import static mapping.ImappingConstants.REMOVE_ACCOUNT;
import static mapping.ImappingConstants.REMOVE_READING;
import static mapping.ImappingConstants.CHANGE_READING;
import static mapping.ImappingConstants.NEW_READING;

/**
 *
 * @author Sergey
 */
public class AdminPagePanel extends PagePanel{
    private final JList<String> readingList = new JList<>();// список показаний
    private JFormattedTextField txtReading;// поле для ввода показаний
    private Response response;
    private String accountNumber;
    private String userName;
    private final JCheckBox chkHotBox = new JCheckBox("горячая");// отмечает показания по горячей или холодной воде
    private final JList<String> userList = new JList<>();// список зарегистрированных пользователей
    private JFormattedTextField txtReadingDate;// поле для ввода даты
    private ArrayList<User> userArray;// 
    /**
     *
     * @param response результат запроса к базе данных, содержащий входные данные
     */
    public AdminPagePanel(Response response) {
        super();
        this.response = response;
        try {
            initComponents();// инициализация компонентов пользовательского интерфейса
        } catch (ParseException ex) {
            Logger.getLogger(AdminPagePanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public Request getRequest() {
        switch (this.getName()) {
            case REMOVE_ACCOUNT:
                return getRemoveAccountRequest();
            case REMOVE_READING:
                return getRemoveReadingsRequest();
            case NEW_READING:
                return getAddNewReadingRequest();
            case CHANGE_READING:
                break;
            default:
                break;
        }
        return null;
    }

    @Override
    public void setResponse(Response response) {
        this.response = response;
        // передаются другие данные (новые показания, на удаление аккаунта)
        switch (response.getBody()[0][0]) {
            case REMOVE_ACCOUNT:
                removeAccount(response);
                break;
            case REMOVE_READING:
                updateResponseData();
                break;
            case NEW_READING:
                addReading(response);
                break;
            case CHANGE_READING:
                changeReading(response);
                break;
        }
        
    }

    /**
     * Инициализация компонентов пользовательского интерфейса
     */
    private void initComponents() throws ParseException {
        txtReading = new JFormattedTextField(NumberFormat.getInstance());// поле для ввода показаний
        txtReading.setValue(0);
        txtReading.setInputVerifier(new FormattedTextFieldFerifier());
        // поле для ввода даты - зададим маску ввода
        MaskFormatter dateFormatter = new MaskFormatter("####-##-##");
//        dateFormatter.setPlaceholderCharacter('1');// символ-заполнитель маски
        dateFormatter.setValidCharacters("0123456789");// разрешённые символы для ввода
        txtReadingDate = new JFormattedTextField(dateFormatter);
        txtReadingDate.setValue(LocalDate.now());// задаём значение - текущая дата
        
        chkHotBox.setSelected(false);// вывод показаний по холодной воде
        userName = response.getBody()[0][1];
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
        box3.add(Box.createHorizontalStrut(10));
        box3.add(new JLabel("Дата"));
        box3.add(Box.createHorizontalStrut(10));
        box3.add(txtReadingDate);
        box3.add(Box.createHorizontalStrut(10));
        
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
        userList.setModel(getListModel());
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);// выделение одной строки
        userList.addListSelectionListener((ListSelectionEvent e) -> {
            try {
            // получаем пользователя по индексу выделенного элемента списка
            int index = userList.getSelectedIndex();
            User u = response.getFromBody(userList.getSelectedIndex());
            accountNumber = u.getAcc().getAccountNumber();
            // заполняем список показаний
            readingList.setModel(readingListModel(u.getAcc().getReadings(), 
                    chkHotBox.isSelected()));// список показаний
            } catch (ArrayIndexOutOfBoundsException ex) {
                System.out.println("error: " + ex.getMessage());
            }
        });
    }
    
    private DefaultListModel<String> getListModel() {
        DefaultListModel<String> model = new DefaultListModel<>();
        int index = 0;
        User user;
        // заполняем модель списка
        while((user = response.getFromBody(index)) != null) {
            model.addElement(user.getUsername());// имя пользователя
            index++;// увеличиваем счётчик цикла
        }
        return model;
    }

    /**
     * Возвращает запрос на удаление пользовательского аккаунта
     * @return request - запрос на удаление аккаунта
     */
    private Request getRemoveAccountRequest() {
        // получаем пользователя по выбранному индексу списка
        User user = response.getFromBody(userList.getSelectedIndex());
        // создаём запрос на удаление аккаунта
        Request request = new Request(REMOVE_ACCOUNT, false);
        request.getBody()[0][0] = ImappingConstants.USER_NAME;
        request.getBody()[0][1] = user.getUsername();// имя пользователя
        request.getBody()[1][0] = ImappingConstants.ACCOUNT;
        request.getBody()[1][1] = user.getAcc().getAccountNumber();// номер аккаунта

        return request;
    }

    /**
     * Возвращает запрос на удаление показаний из аккаунта выбранного пользователя
     * @return request - запрос на удаление показаний
     */
    private Request getRemoveReadingsRequest() {
        return null;
    }

    /**
     * Возвращает запрос на получение показаний выделенного пользователя
     * @return request - запрос на получение показаний
     */
    private Request getAddNewReadingRequest() {
        LocalDate ld;
        // проверяем корректность ввода даты
        try {
            ld = LocalDate.parse(txtReadingDate.getValue().toString());
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, 
                    "Неверный ввод даты! Проверьте правильность ввода.", 
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
            ld = LocalDate.now();
        }
        // проверяем корректность ввода показаний
        if(!txtReading.isEditValid()) {
            // если пользователь ввёл некорректные данные, уведомляем его
            JOptionPane.showMessageDialog(this, 
                    "Проверьте правильность ввода показаний!", 
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
            return null;// возвращаем null
        }
        // создаём запрос на добавление показаний
        Request request = new Request(NEW_READING, false);
        request.getBody()[0][0] = ImappingConstants.ACCOUNT;
        request.getBody()[0][1] = accountNumber;
        request.getBody()[1][0] = ImappingConstants.MEASURING;
        request.getBody()[1][1] = txtReading.getValue().toString();
        request.getBody()[2][0] = ImappingConstants.LOCAL_DATE;
        request.getBody()[2][1] = ld.toString();
        request.getBody()[3][0] = ImappingConstants.IS_HOT;
        request.getBody()[3][1] = chkHotBox.isSelected() ? "1" : "0";
        return request;
    }

    /**
     * Обновление данных из полученного ответа, заполнение списка
     */
    private void updateResponseData() {
        int index = userList.getSelectedIndex();
        ArrayList<Reading> responseData = response.getFromBody(index).getAcc().getReadings();
        readingList.setModel(readingListModel(responseData, chkHotBox.isSelected()));

    }
    
    /**
     * Информирует пользователя об операции удаления аккаунта
     * @param response ответ базы данных о результатах операции удаления
     */
    private void removeAccount(Response response) {
        if (!response.isAuth()) {
            // удаление аккаунта пользователя
            JOptionPane.showMessageDialog(this.getParent(),
                    "При удалении аккаунта произошли ошибки. Обратитесь к разработчику",
                    "Warning", JOptionPane.WARNING_MESSAGE);
        } else {
            // извещаем пользователя, обновляем список
            JOptionPane.showMessageDialog(this.getParent(),
                    "Удаление аккаунта успешно!", "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            userList.setModel(getListModel());
            userList.setSelectedIndex(0);
        }
    }
    
    /**
     * Информирует пользователя об операции удаления показаний
     * @param response ответ базы данных о результатах операции удаления
     */
    private void removeReading(Response response) {
        
    }
    
    /**
     * Информирует пользователя об операции добавления новых показаний
     * @param response ответ базы данных о результатах операции добавления
     */
    private void addReading(Response response) {
        // если ответ положительный, и новые показания приняты, добавляем их в список показаний
        if (response.isAuth()) {
            // создаём объект показаний
            WaterReading reading = new WaterReading(LocalDate
                    .parse(txtReadingDate.getValue().toString()), 
                    Integer.parseInt(txtReading.getValue().toString()), 
                    chkHotBox.isSelected());
            // получаем список показаний
            int index = userList.getSelectedIndex();
            ArrayList<Reading> readings = response.getFromBody(index).getAcc()
                    .getReadings();
            readings.add(reading);// добавляем показание в список
            updateResponseData();
        } else {
            JOptionPane.showMessageDialog(null,
                    "Внесение показаний допускается только одни раз в текущем месяце",
                    "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    /**
     * Информирует пользователя об операции изменения показаний
     * @param response ответ базы данных о результатах операции изменения
     */
    private void changeReading(Response response) {
        
    }
}
