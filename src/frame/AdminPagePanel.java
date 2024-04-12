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
import mapping.ImappingConstants;
import in.Request;
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
 *
 * @author Sergey
 */
public class AdminPagePanel extends PagePanel{
    private final ReadingListComponent readingList = new ReadingListComponent();// список показаний
    private String accountNumber;
    private String userName;
    private final JList<String> userList = new JList<>();// элемент-список зарегистрированных пользователей
    private final ArrayList<User> userArray;// массив пользователей
    private final NewChangeReadingPanel newChangeReadingPanel = new NewChangeReadingPanel();
    /**
     * Создаёт панель администратора
     * @param response результат запроса к базе данных, содержащий входные данные
     */
    public AdminPagePanel(Response response) {
        super(REMOVE_READING, LOG_OUT, REMOVE_ACCOUNT);
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
        super.setRemoveCaption("Удалить аккаунт");
        super.setOkCaption("Удалить показания");

        userList.setSelectedIndex(0);
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
        
        Box adminBox = Box.createHorizontalBox();// контейнер для размещения
        
        // создаём контейнер для размещения списка пользователей
        Box userBox = getUserBox();
        // создаём контейнер для списка показаний
        Box readingBox = getReadingBox();

        // 
        newChangeReadingPanel.setParentContainer(this);
        newChangeReadingPanel.setOkAction(NEW_READING);

        // размещаем все созданные элементы
        adminBox.add(Box.createHorizontalStrut(5));
        adminBox.add(userBox);
        adminBox.add(Box.createHorizontalStrut(5));
        adminBox.add(readingBox);
        adminBox.add(Box.createHorizontalStrut(25));
        adminBox.add(newChangeReadingPanel);
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
        элементу списка
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
        userList.setModel(getListModel());
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);// выделение одной строки
        userList.addListSelectionListener((ListSelectionEvent e) -> {
            try {
            // получаем пользователя по индексу выделенного элемента списка
            int index = userList.getSelectedIndex();
            User u = userArray.get(userList.getSelectedIndex());
            accountNumber = u.getAcc().getAccountNumber();
            // заполняем список показаний
            readingList.setModel(readingListModel(u.getAcc().getReadings()));// список показаний
            } catch (ArrayIndexOutOfBoundsException ex) {
                System.out.println("error: " + ex.getMessage());
            }
        });
        userList.setFixedCellHeight(20);
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
        return newChangeReadingPanel.getNewReadingRequest(accountNumber);
        
    }

    /**
     * Обновление данных из полученного ответа, заполнение списка
     */
    private void updateResponseData(ArrayList<Reading> responseData) {
        readingList.setModel(readingListModel(responseData));

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
            // получаем список показаний
            int index = userList.getSelectedIndex();
            ArrayList<Reading> readings = userArray.get(index).getAcc()
                    .getReadings();
            System.out.println("readings before [" + readings +"]");
            readings.remove(readingList.getSelectedIndex());// удаляем показание из списка
            System.out.println("readings after [" + readings +"]");
            DefaultListModel model = (DefaultListModel) readingList.getModel();
            model.removeElementAt(readingList.getSelectedIndex());
//            updateResponseData(readings);
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
            WaterReading wr = newChangeReadingPanel.getWaterReading(id);
            WaterReading reading = new WaterReading(id, wr.getDate(), 
                    wr.getMeasuring(), wr.isHot());
            // получаем список показаний
            int index = userList.getSelectedIndex();
            ArrayList<Reading> readings = userArray.get(index).getAcc()
                    .getReadings();
            readings.add(reading);// добавляем показание в список
            updateResponseData(readings);
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private void editReading() {
        WaterReading wr = 
                (WaterReading) readingList.getSelectedValue();// получаем показание
        // задаём значения
        newChangeReadingPanel.setWaterReading(wr);
        newChangeReadingPanel.setOkAction(CHANGE_READING);
    }
}
