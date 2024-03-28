package frame;

import in.Request;
import api.Response;
import entities.IRoleConstants;
import in.IRequestResponseConstants;

import javax.swing.*;
import java.time.LocalDate;

import static mapping.ImappingConstants.*;

public class HomePagePanel extends PagePanel {
    private final JList<String> readingList = new JList<>();// список показаний
    private JTextField txtReading;// поле для ввода показаний
    private Response response;
    private String accountNumber;
    private String userName;
    private String userRole;
    private final JCheckBox chkHotBox = new JCheckBox("горячая");// отмечает показания по горячей или холодной воде
    private String responseData;
    private String mapping = "";
    /**
     * Creates a new <code>JPanel</code> with a double buffer
     * and a flow layout.
     */
    public HomePagePanel() {
        super();


    }

    public Request getRequest() {
        switch (this.getName()) {
            case NEW_READING:
                return addNewReading();
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

    /**
     * Задаёт данные, полученные в результате запроса к базе
     * @param response ответ из базы данных
     */
    public void setResponse(Response response) {
        this.response = response;
        if (response.getBody()[0][0].equals(IRequestResponseConstants.USER_NAME)) {
            // если передаётся имя пользователя, вызывается инициализация компонентов
            initComponents();// инициализация компонентов пользовательского интерфейса
        } else {
            // если передаются другие данные (новые показания, на удаление аккаунта)
            if (response.getBody()[0][0].equals(NEW_READING)) {
                // добавление новых показаний
                if (response.isAuth()) {
                    // если ответ положительный, и новые показания приняты, добавляем их в список показаний
                    if (response.isAuth()) {
                        updateResponseData();
                    } else {
                        JOptionPane.showMessageDialog(null,
                                "Внесение показаний допускается только одни раз в текущем месяце",
                                "Warning", JOptionPane.WARNING_MESSAGE);
                    }

                }
            } else if (response.getBody()[0][0].equals(REMOVE_ACCOUNT)) {
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
                }
            } else if (response.getBody()[0][0].equals(GET_READING)) {
//                System.out.println("we are here");
                updateResponseData();
            }
        }
    }

    private void initComponents() {
        txtReading = new JTextField(10);// поле для ввода показаний
        chkHotBox.setSelected(false);// вывод показаний по холодной воде
        userName = response.getBody()[0][1];
        userRole = response.getBody()[2][1];
        // в зависимости от прав пользователя будут создаваться соответствующие элементы
        if (response.getBody()[2][1].equals(IRoleConstants.USER)) {
            // вошёл простой пользователь
            super.setCaption("<table border=\"0\" cellspacing=\"0\" cellpadding=\"3\" " + 
                    "align=\"center\" cols=\"1\" width=\"100%\" bgcolor=\"#008080\">" +
                    "<tr><td align=\"justify\">" + "Добро пожаловать на страницу персонального аккаунта." +
                    "</td></tr>" +
                    "<tr><td align=\"left\"><b>" + userName + "</b>. Лицевой счёт <u>" + 
                    response.getBody()[3][1] + "</u>" +
                    "</td></tr>" +
                    "<tr><td align=\"right\">" +
                    "Сегодня <b><u>" + LocalDate.now() +
                    "</u></b>" +
                    "</td></tr></table>");
            super.setRemoveAction("");// скрываем кнопку удаления аккаунта
            super.addComponent(getUserBox());
            responseData = response.getBody()[4][1];
            readingList.setModel(readingListModel(responseData, chkHotBox.isSelected()));// список показаний
            accountNumber = response.getBody()[3][1];
        } else {
            // вошёл администратор
            super.setCaption("<table border=\"0\" cellspacing=\"0\" cellpadding=\"3\" " + 
                    "align=\"center\" cols=\"1\" width=\"100%\" bgcolor=\"#008080\">" +
                    "<tr><td align=\"center\">" +
                    "Вы вошли на страницу с правами администратора." + 
                    "</td></tr>" +
                    "<tr><td align=\"left\">Администратор <b><u>" +
                    userName + "</u></b></td></tr>" +
                    "<tr><td align=\"right\">Сегодня <b><u>" + LocalDate.now() + "</u></b></td></tr></table>");
            super.addComponent(getAdminBox());
            super.setRemoveCaption("Удалить аккаунт");
        }
        super.setOkAction(NEW_READING);
        super.setRemoveAction(REMOVE_ACCOUNT);
        super.setExitAction(LOG_OUT);
        super.setOkCaption("Добавить показания");

        // добавляем слушатель на флажок
        chkHotBox.addActionListener((e -> {
            readingList.setModel(readingListModel(responseData, chkHotBox.isSelected()));// список показаний
        }));

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
        final JList<String> userList = userList();// список зарегистрированных пользователей
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

    private DefaultListModel<String> readingListModel(String data, boolean isHot) {
        DefaultListModel<String> model = new DefaultListModel<>();
        System.out.println("data= " + data);
        if(data != null) {
            String[] readings = data.split(";");// получаем показания по данному л/с
            for (String reading : readings) {
                System.out.println(reading);
                if (reading.endsWith(String.valueOf(isHot))) {
                    int pos = reading.lastIndexOf(" | ");
                    // если хотим видеть показания по горячей воде или холодной
                    model.addElement(reading.substring(0, pos));
                }
            }
        }
        return model;
    }

    private JList<String> userList() {
        JList<String> userList = new JList<>();// список зарегистрированных пользователей
        String[] users = response.getBody()[3][1].split(";");// получаем массив пользователей
        DefaultListModel<String> model = new DefaultListModel<>();
        for (String user : users) {
            model.addElement(user);
        }
        userList.setModel(model);
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);// выделение одной строки
        userList.addListSelectionListener(e -> {
            String value = userList.getSelectedValue();
            int pos = value.indexOf(" | ");
//            userName = value.substring(0, pos);
            accountNumber = value.substring(pos + 3);
//            System.out.println("username - " + userName + "; account=" + accountNumber);
            this.setName(GET_READING);
        });
        return userList;
    }

    private Request addNewReading() {
        Request request = new Request(NEW_READING, false);// создаём запрос на добавление показаний
        request.getBody()[0][0] = IRequestResponseConstants.ACCOUNT;
        request.getBody()[0][1] = accountNumber;
        request.getBody()[1][0] = IRequestResponseConstants.MEASURING;
        request.getBody()[1][1] = txtReading.getText();
        request.getBody()[2][0] = IRequestResponseConstants.LOCAL_DATE;
        request.getBody()[2][1] = LocalDate.now().toString();
        request.getBody()[3][0] = IRequestResponseConstants.IS_HOT;
        request.getBody()[3][1] = chkHotBox.isSelected() ? "1" : "0";
        return request;
    }

    private Request removeAccount() {
        Request request = new Request(REMOVE_ACCOUNT, false);// создаём запрос на добавление показаний
        request.getBody()[0][0] = IRequestResponseConstants.USER_NAME;
        request.getBody()[0][1] = userName;
        request.getBody()[1][0] = IRequestResponseConstants.ROLE;
        request.getBody()[1][1] = userRole;
        request.getBody()[2][0] = IRequestResponseConstants.ACCOUNT;
        request.getBody()[2][1] = accountNumber;

        return request;
    }

    private Request removeReadings() {
        return null;
    }

    private Request getReading() {
        Request request = new Request(GET_READING, false);// создаём запрос на добавление показаний
        request.getBody()[0][0] = IRequestResponseConstants.ACCOUNT;
        request.getBody()[0][1] = accountNumber;
        return request;
    }

    private void updateResponseData() {
        responseData = response.getBody()[0][1];
        readingList.setModel(readingListModel(responseData, chkHotBox.isSelected()));

    }

    public String getMapping() {
        return mapping;
    }

}
