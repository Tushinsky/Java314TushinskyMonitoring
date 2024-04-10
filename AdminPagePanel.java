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
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Optional;
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
    private String accountNumber;
    private String userName;
    private final JCheckBox chkHotBox = new JCheckBox("горячая");// отмечает показания по горячей или холодной воде
    private final JList<String> userList = new JList<>();// элемент-список зарегистрированных пользователей
    private JFormattedTextField txtReadingDate;// поле для ввода даты
    private final ArrayList<User> userArray;// массив пользователей
    private final JButton readingButton = new JButton("Добавить показания");
    
    /**
     * Создаёт панель администратора
     * @param response результат запроса к базе данных, содержащий входные данные
     */
    public AdminPagePanel(Response response) {
        /*
        кнопке ОК назначаем действие на удаление аккаунта, а
        кнопке Удалить назначаем действие на удаление показаний,
        действие кнопки Выход оставляем без изменения
        */
        super(REMOVE_ACCOUNT, LOG_OUT, REMOVE_READING);
        userArray = new ArrayList<>();
        initComponents(response);// инициализация компонентов пользовательского интерфейса
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
                return getChangeReadingRequest();
        }
        return null;
    }

    @Override
    public void setResponse(Response response) {
        // передаются другие данные (новые показания, на удаление аккаунта)
        switch (response.getBody()[0][0]) {
            case REMOVE_ACCOUNT:
                removeAccount(response);
                break;
            case REMOVE_READING:
                removeReading(response);
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
     * @param response результат запроса к базе данных, содержащий входные данные
     */
    private void initComponents(Response response) {
        try {
            initTextField();
        } catch (ParseException ex) {
            Logger.getLogger(AdminPagePanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        chkHotBox.setSelected(false);// вывод показаний по холодной воде
        userName = response.getBody()[0][1];
        // создаём элементы пользовательского интерфейса
        super.setCaption("<table border=\"0\" cellspacing=\"0\" cellpadding=\"3\" " + 
                "align=\"center\" cols=\"1\" width=\"100%\" " +
                "style=\"font-size:medium;\" bgcolor=\"#AAFF80\">" +
                "<tr><td align=\"justify\">" +
                "Вы вошли на страницу с правами администратора. " + 
                "Здесь можно удалить аккаунт выбранного пользователя, добавить, " + 
                "удалить или изменить показания выбранного пользователя.</td></tr>" +
                "<tr><td align=\"left\">Администратор <b><u>" +
                userName + "</u></b>. Сегодня <b><u>" + LocalDate.now() + 
                "</u></b></td></tr></table>");
        fillUserArrayList(response);// заполняем список пользователей
        super.addComponent(getAdminBox());
        super.setOkCaption("Удалить аккаунт");
        super.setRemoveCaption("Удалить показания");
        // добавляем слушатель на флажок
        chkHotBox.addActionListener((e -> {
//            Color color = chkHotBox.isSelected() ? Color.PINK : 
//                    new Color(150, 150, 255, 20);
//            chkHotBox.setBackground(color);
            updateResponseData();// список показаний
        }));
        userList.setSelectedIndex(0);
    }
    
    /**
     * Создание и установка свойств текстовых полей
     * @throws ParseException 
     */
    private void initTextField() throws ParseException {
        txtReading = new JFormattedTextField(NumberFormat.getInstance());// поле для ввода показаний
        txtReading.setValue(0);
        txtReading.setInputVerifier(new FormattedTextFieldFerifier());
        // поле для ввода даты - зададим маску ввода
        MaskFormatter dateFormatter = new MaskFormatter("####-##-##");
//        dateFormatter.setPlaceholderCharacter('1');// символ-заполнитель маски
        dateFormatter.setValidCharacters("0123456789");// разрешённые символы для ввода
        txtReadingDate = new JFormattedTextField(dateFormatter);
        txtReadingDate.setValue(LocalDate.now());// задаём значение - текущая дата
        /*
        ------------------Установим шрифт для полей----------------------
        */
        // получаем текущий шрифт поля ввода показаний и увеличиваем его размер
        Font font = txtReading.getFont();
        font = new Font(font.getFontName(), Font.BOLD, 14);
        // и передаём его полям ввода
        txtReading.setFont(font);
        txtReadingDate.setFont(font);
    }

    /**
     * Заполняет список пользователей, полученный из ответа базы данных
     * @param response результат запроса к базе данных, содержащий входные данные
     */
    private void fillUserArrayList(Response response) {
        int index = 0;
        User user;
        while((user = response.getFromBody(index))!= null) {
            userArray.add(user);
            index++;
        }
    }

    /**
     * Создаёт элементы пользовательского интерфайса для администратора.
     * Заполняет их данными
     */
    private Box getAdminBox() {
        
        Box adminBox = Box.createVerticalBox();// контейнер для размещения
        JLabel lblUsers = new JLabel("Пользователи");
        readingButton.addActionListener(al -> {
            super.setName(NEW_READING);
        });
        // создаём контейнер для размещения списка пользователей
        Box box1 = getUserBox();
        // создаём контейнер для списка показаний
        Box box2 = getReadingBox();

        /*
        Создаём контейнер для размещения поля ввода новых показаний, флажка
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
        box3.add(readingButton);
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
    private Box getReadingBox() {
        /*
        к списку показаний добавляем обработку двойного щелчка по выбранному
        элементу списка
        */
        readingList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                super.mouseClicked(me);
                // при двойном щелчке по элементу выводим показания и дату в полях ввода
                if(me.getClickCount() == 2) {
                    WaterReading wr = getWaterReading();// получаем показание
                    // задаём значения
                    txtReading.setValue(wr.getMeasuring());
                    txtReadingDate.setValue(wr.getDate().toString());
                }
            }
            
        });
        // разрешаем выбор только одного элемента списка
        readingList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        readingList.setAutoscrolls(true);
        readingList.setFixedCellHeight(15);
        readingList.setFixedCellWidth(150);
        
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
//        System.out.println("data= " + data);
        if(data != null) {
            data.stream().filter((r) -> {
                WaterReading wr = (WaterReading) r;
                return wr.isHot() == isHot;
            }).forEach((Reading r) -> model.addElement(r.toString()));
            
        }
        return model;
    }

    /**
     * Создаёт контейнер, содержащий заполненный данными список пользователей
     * @return BOX - созданный контейнер
     */
    private Box getUserBox() {
        userList.setModel(getListModel());
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);// выделение одной строки
        userList.addListSelectionListener((ListSelectionEvent e) -> {
            try {
            // получаем пользователя по индексу выделенного элемента списка
            int index = userList.getSelectedIndex();
            User u = userArray.get(userList.getSelectedIndex());
            accountNumber = u.getAcc().getAccountNumber();
            // заполняем список показаний
            readingList.setModel(readingListModel(u.getAcc().getReadings(), 
                    chkHotBox.isSelected()));// список показаний
            } catch (ArrayIndexOutOfBoundsException ex) {
                System.out.println("error: " + ex.getMessage());
            }
        });
        userList.setAutoscrolls(true);
        userList.setFixedCellHeight(15);
        userList.setFixedCellWidth(150);
        JLabel lblUsers = new JLabel("Пользователи");
        // создаём контейнер для размещения списка пользователей
        Box box = Box.createVerticalBox();
        box.add(lblUsers);
        box.add(Box.createVerticalStrut(5));
        // список ложим в панель прокрутки и размещаем в контейнере
        box.add(new JScrollPane(userList));
        box.add(Box.createVerticalStrut(10));
        return box;
    }
    
    /**
     * Создаёт и возвращает модель списка, принятую по умолчанию
     * @return созданную модель списка
     */
    private DefaultListModel<String> getListModel() {
        DefaultListModel<String> model = new DefaultListModel<>();
        userArray.forEach(user -> {
            model.addElement(user.getUsername());
        });
        return model;
    }
    
    /**
     * Создаёт контейнер для размещения поля ввода новых показаний, флажка
     * для задания признака холодной или горячей воды, кнопки для добавления
     * новых или изменения выбранных показаний
     */
    private Box getNewReadingBox() {
        Box box = Box.createHorizontalBox();
        box.add(Box.createHorizontalStrut(10));
        box.add(new JLabel("Новые показания"));
        box.add(Box.createHorizontalStrut(10));
        box.add(txtReading);
        box.add(Box.createHorizontalStrut(10));
        box.add(new JLabel("Дата"));
        box.add(Box.createHorizontalStrut(10));
        box.add(txtReadingDate);
        box.add(Box.createHorizontalStrut(10));
        
        box.add(chkHotBox);
        box.add(Box.createHorizontalStrut(20));
        box.add(readingButton);
        box.add(Box.createHorizontalGlue());
        return box;
    }

    /**
     * Возвращает запрос на удаление пользовательского аккаунта
     * @return request - запрос на удаление аккаунта
     */
    private Request getRemoveAccountRequest() {
        // запрос на подтверждение
        if(JOptionPane.showConfirmDialog(this, "Удалить выбранный аккаунт?", 
                "Внимание", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.CANCEL_OPTION) {
            return null;// если отмена
        }
        // получаем пользователя по выбранному индексу списка
        User user = userArray.get(userList.getSelectedIndex());
        // создаём запрос на удаление аккаунта
        Request request = new Request(REMOVE_ACCOUNT, false);
        request.getBody()[0][0] = ImappingConstants.ACCOUNT;
        request.getBody()[0][1] = "";// имя пользователя
        request.addToBody(user.getAcc());
        return request;
    }

    /**
     * Возвращает запрос на удаление показаний из аккаунта выбранного пользователя
     * @return request - запрос на удаление показаний
     */
    private Request getRemoveReadingsRequest() {
        // запрос на подтверждение
        if(JOptionPane.showConfirmDialog(this, "Удалить показания?", 
                "Внимание", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.CANCEL_OPTION) {
            return null;// если отмена
        }
        // создаём запрос на добавление показаний
        Request request = new Request(REMOVE_READING, false);
        WaterReading wr = getWaterReading();
        request.getBody()[0][0] = ImappingConstants.ACCOUNT;
        request.getBody()[0][1] = accountNumber;
        request.addToBody(wr);
        return request;
    }

    /**
     * Возвращает запрос на получение показаний выделенного пользователя
     * @return request - запрос на получение показаний
     */
    private Request getAddNewReadingRequest() {
        // запрос на подтверждение
        if(JOptionPane.showConfirmDialog(this, "Добавить новые показания?", 
                "Внимание", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.CANCEL_OPTION) {
            return null;// если отмена
        }
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
        WaterReading wr = new WaterReading(0, ld, 
                Integer.parseInt(txtReading.getValue().toString()), 
                chkHotBox.isSelected());
        request.addToBody(wr);
        return request;
    }

    /**
     * Обновление данных из полученного ответа, заполнение списка
     */
    private void updateResponseData() {
        int index = userList.getSelectedIndex();
        ArrayList<Reading> responseData = userArray.get(index).getAcc().getReadings();
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
            userArray.remove(userList.getSelectedIndex());// удаляем пользователя из списка
            userList.setModel(getListModel());
            userList.setSelectedIndex(0);
        }
    }
    
    /**
     * Информирует пользователя об операции удаления показаний
     * @param response ответ базы данных о результатах операции удаления
     */
    private void removeReading(Response response) {
        if(response.isAuth()) {
            // получаем показания, которые нужно удалить
            WaterReading reading = getWaterReading();
            // получаем список показаний
            int index = userList.getSelectedIndex();
            ArrayList<Reading> readings = userArray.get(index).getAcc()
                    .getReadings();
            readings.remove(reading);// добавляем показание в список
            updateResponseData();
            JOptionPane.showMessageDialog(null,
                    "Удаление показаний успешно!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        }
        
        
    }
    
    /**
     * Информирует пользователя об операции добавления новых показаний
     * @param response ответ базы данных о результатах операции добавления
     */
    private void addReading(Response response) {
        // если ответ положительный, и новые показания приняты, добавляем их в список показаний
        if (response.isAuth()) {
            // создаём объект показаний
            int id = Integer.parseInt(response.getBody()[0][1]);
            WaterReading reading = new WaterReading(id, LocalDate
                    .parse(txtReadingDate.getValue().toString()), 
                    Integer.parseInt(txtReading.getValue().toString()), 
                    chkHotBox.isSelected());
            // получаем список показаний
            int index = userList.getSelectedIndex();
            ArrayList<Reading> readings = userArray.get(index).getAcc()
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

    private Request getChangeReadingRequest() {
        // запрос на подтверждение
        if(JOptionPane.showConfirmDialog(this, "Изменить показания?", 
                "Внимание", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.CANCEL_OPTION) {
            return null;// если отмена
        }
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private WaterReading getWaterReading () {
        ArrayList<Reading> responseData = userArray.
                get(userList.getSelectedIndex()).getAcc().getReadings();
        String string = readingList.getSelectedValue();
        Optional<Reading> waterReading = responseData.stream()
                .filter((r) -> {
                    WaterReading wr = (WaterReading) r;
                    return ((wr.isHot() == chkHotBox.isSelected()));
                }).filter(wr -> wr.toString().equals(string)).findFirst();
        return (WaterReading) waterReading.get();
    }
}
