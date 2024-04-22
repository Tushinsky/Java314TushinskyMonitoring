/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frame;

import uicomponent.ReadingListComponent;
import uicomponent.UserCellRenderer;
import query.Response;
import mapping.IRoleConstants;
import entities.Reading;
import entities.User;
import entities.WaterReading;
import mapping.ImappingConstants;
import query.Request;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import static mapping.ImappingConstants.LOG_OUT;
import static mapping.ImappingConstants.REMOVE_ACCOUNT;
import static mapping.ImappingConstants.REMOVE_READING;
import static mapping.ImappingConstants.CHANGE_READING;
import static mapping.ImappingConstants.NEW_READING;

/**
 * Класс, предоствляющий компоненты интерфейса для редактирования данных
 * зарегистрированных пользователей
 * @author Sergey
 */
public class AdminPagePanel extends PagePanel{
    private final ReadingListComponent readingList = new ReadingListComponent();// список показаний
    private String accountNumber;// номер аккаунта выбранного пользователя
    
    // элемент-список зарегистрированных пользователей
    private final JList<User> userList = new JList<>();
    
    // панель для добавления / изменения показаний выбранного пользователя
    private final NewChangeReadingPanel pnlNewChangeReadingPanel = new NewChangeReadingPanel();
    
    /**
     * Создаёт панель администратора
     * @param response результат запроса к базе данных, содержащий входные данные
     */
    public AdminPagePanel(Response response) {
        super(REMOVE_READING, LOG_OUT, REMOVE_ACCOUNT);
        // инициализация компонентов пользовательского интерфейса
        initComponents(response);
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
                addNewReading(response);
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
        
        String userName = response.getBody()[0][1];
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
        fillUserList(response);// заполняем список пользователей
        super.addComponent(getAdminBox());// добавляем компоненты администратора
        super.setRemoveCaption("Удалить аккаунт");
        super.setOkCaption("Удалить показания");

        userList.setSelectedIndex(0);
    }

    /**
     * Заполняет список пользователей, полученный из ответа базы данных
     * @param response ответ базе данных, содержащий входные данные
     */
    private void fillUserList(Response response) {
        // модель списка содержит данные пользователей
        DefaultListModel<User> model = new DefaultListModel<>();
        int index = 0;
        User user;
        while((user = (User) response.getFromBody(index))!= null) {
            // заполняем модель списка данными
            model.addElement(user);
            index++;
        }
        userList.setModel(model);// задаём модель списку
    }

    /**
     * Создаёт элементы пользовательского интерфейса для администратора.
     * Заполняет их данными
     */
    private Box getAdminBox() {
        
        Box adminBox = Box.createHorizontalBox();// контейнер для размещения
        
        // создаём контейнер для размещения списка пользователей
        Box userBox = getUserBox();
        // создаём контейнер для списка показаний
        Box readingBox = getReadingBox();

        // задаём некоторые свойсва панели добавления/изменения показаний
        pnlNewChangeReadingPanel.setParentContainer(this);
        pnlNewChangeReadingPanel.setOkAction(NEW_READING);

        // размещаем все созданные элементы
        adminBox.add(Box.createHorizontalStrut(5));
        adminBox.add(userBox);
        adminBox.add(Box.createHorizontalStrut(10));
        adminBox.add(readingBox);
        adminBox.add(Box.createHorizontalStrut(15));
        adminBox.add(pnlNewChangeReadingPanel);
        adminBox.add(Box.createHorizontalGlue());

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
        элементу списка и обработку нажатия клавиши ввода на выбранном элементе
        */
        readingList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                super.mouseClicked(me);
                // при двойном щелчке по элементу входим в режим редактирования
                if(me.getClickCount() == 2) {
                    editReading();
                }
            }
            
        });
        readingList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent ke) {
                super.keyPressed(ke);
                // при нажатии клавишы ввода входим в режим редактирования
                if(ke.getKeyCode() == KeyEvent.VK_ENTER) {
                    editReading();
                }
            }
            
        });
        readingList.setToolTipText("двойной клик / нажать ENTER"
                + " для входа в режиим редактирования");
        Box box = Box.createVerticalBox();
        Box horBox = Box.createHorizontalBox();
        horBox.add(Box.createHorizontalGlue());
        horBox.add(new JLabel("Показания"));
        horBox.add(Box.createHorizontalGlue());
        box.add(horBox);
        box.add(Box.createVerticalStrut(5));
        box.add(new JScrollPane(readingList));// список размещаем в панели прокрутки
        box.add(Box.createVerticalStrut(10));
        return box;
    }

    /**
     * Возвращает модель для заполнения списка данными
     * @param data список данных
     * @param isHot флаг для фильтра данных
     * @return полученную модель для заполнения списка
     */
    private DefaultListModel<Reading> readingListModel(ArrayList<Reading> data) {
        DefaultListModel<Reading> model = new DefaultListModel<>();
//        System.out.println("data= " + data);
        if(data != null) {
            data.forEach((Reading r) -> model.addElement(r));
            
        }
        return model;
    }

    /**
     * Создаёт контейнер, содержащий заполненный данными список пользователей
     * @return BOX - созданный контейнер
     */
    private Box getUserBox() {
        userList.setCellRenderer(new UserCellRenderer());// отрисовщик элементов
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);// выделение одной строки
        // добавляем слушатель для выделенного элемента списка
        userList.addListSelectionListener((ListSelectionEvent e) -> {
            try {
            // получаем пользователя по индексу выделенного элемента списка
            User u = userList.getSelectedValue();
            accountNumber = u.getAcc().getAccountNumber();
            // заполняем список показаний
            readingList.setModel(readingListModel(u.getAcc().getReadings()));// список показаний
            } catch (ArrayIndexOutOfBoundsException ex) {
                System.out.println("error: " + ex.getMessage());
            }
        });
        // задаём ширину и высоту ячеек списка
        userList.setFixedCellHeight(20);
        userList.setFixedCellWidth(130);
        
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
        User user = userList.getSelectedValue();
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
        WaterReading wr = (WaterReading) readingList.getSelectedValue();
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
        // создаём запрос на добавление показаний
        Request request = pnlNewChangeReadingPanel.getNewReadingRequest();
        request.getBody()[0][1] = accountNumber;
        request.getBody()[1][0] = ImappingConstants.ROLE;
        request.getBody()[1][1] = IRoleConstants.ADMIN;
        return request;
        
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
            int index = userList.getSelectedIndex();
            DefaultListModel model = (DefaultListModel) userList.getModel();
            model.removeElementAt(index);// удаляем пользователя из списка
            userList.setSelectedIndex(0);
        }
    }
    
    /**
     * Информирует пользователя об операции удаления показаний
     * @param response ответ базы данных о результатах операции удаления
     */
    private void removeReading(Response response) {
        if(response.isAuth()) {
            // получаем список показаний выбранного пользователя
            User user = userList.getSelectedValue();
            ArrayList<Reading> readings = user.getAcc()
                    .getReadings();
            // удаляем показание из модели списка
            readings.remove(readingList.getSelectedIndex());
            DefaultListModel model = (DefaultListModel) readingList.getModel();
            model.removeElementAt(readingList.getSelectedIndex());
            JOptionPane.showMessageDialog(null,
                    "Удаление показаний успешно!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        }
        
        
    }
    
    /**
     * Информирует пользователя об операции добавления новых показаний
     * @param response ответ базы данных о результатах операции добавления
     */
    private void addNewReading(Response response) {
        // если ответ положительный, и новые показания приняты, добавляем их в список показаний
        if (response.isAuth()) {
            // получаем список показаний выбранного пользователя
            User user = userList.getModel().getElementAt(userList.getSelectedIndex());
            ArrayList<Reading> readings = user.getAcc()
                    .getReadings();
            // создаём объект показаний
            int id = Integer.parseInt(response.getBody()[0][1]);// идентификатор записи
            WaterReading wr = pnlNewChangeReadingPanel.getWaterReading();
            int idNumber = readings.size();// количество элементов в списке
            idNumber++;
            // создаём объект новых показаний
            WaterReading reading = new WaterReading(idNumber, id, wr.getDate(), 
                    wr.getMeasuring(), wr.isHot());
            readings.add(reading);// добавляем показание в список
            DefaultListModel model = (DefaultListModel) readingList.getModel();
            model.addElement(reading);// добавляем в модель списка
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
        // проверяем результат изменения показаний в базе данных
        if(response.isAuth()) {
            // получаем список показаний выбранного пользователя
            User user = userList.getModel().getElementAt(userList.getSelectedIndex());
            ArrayList<Reading> readings = user.getAcc()
                    .getReadings();
            // создаём объект показаний после изменения
            WaterReading wr = pnlNewChangeReadingPanel.getWaterReading();
            int index = readingList.getSelectedIndex();
            readings.set(index, wr);// изменяем
            // обновляем список показаний
            readingList.setModel(readingListModel(readings));
            pnlNewChangeReadingPanel.resetData();// сброс данных
        } else {
            JOptionPane.showMessageDialog(null,
                    "Что-то не сложилось, при записи данных произошла ошибка!",
                    "Внимание", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Возвращает запрос на изменение показаний
     * @return тело запроса на изменение показаний
     */
    private Request getChangeReadingRequest() {
        // запрос на подтверждение
        if(JOptionPane.showConfirmDialog(this, "Изменить показания?", 
                "Внимание", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.CANCEL_OPTION) {
            return null;// если отмена
        }
        // заполняем тело запроса на изменение показаний пользователя
        Request request = pnlNewChangeReadingPanel.getChangeReadingRequest();
        request.getBody()[0][1] = accountNumber;
        return request;
    }
    
    /**
     * Вводит выбранные показания в режим редактирования
     */
    private void editReading() {
        WaterReading wr = 
                (WaterReading) readingList.getSelectedValue();// получаем показание
        // задаём значения
        pnlNewChangeReadingPanel.setWaterReading(wr);
        pnlNewChangeReadingPanel.setOkAction(CHANGE_READING);
    }
}
