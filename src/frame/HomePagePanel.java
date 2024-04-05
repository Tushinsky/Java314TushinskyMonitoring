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
    private JTextField txtReadingDate;// поле для ввода даты показаний
    /**
     * Creates a new <code>JPanel</code> with a double buffer
     * and a flow layout.
     * @param response результат запроса к базе данных, содержащий входные данные
     */
    public HomePagePanel(Response response) {
        super();
        this.response = response;
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
        // если ответ положительный, и новые показания приняты, добавляем их в список показаний
        if (response.isAuth()) {
            updateResponseData();// обновляем данные в списке
        } else {
            JOptionPane.showMessageDialog(null,
                    "Внесение показаний допускается только одни раз в текущем месяце",
                    "Warning", JOptionPane.WARNING_MESSAGE);
        }

    }

    private void initComponents() {
        // вошёл простой пользователь
        txtReading = new JTextField(10);// поле для ввода показаний
        txtReadingDate = new JTextField(10);// поле для ввода даты
        User user = response.getFromBody(0);
        userName = user.getUsername();
        accountNumber = user.getAcc().getAccountNumber();
        super.setCaption("<table border=\"0\" cellspacing=\"0\" cellpadding=\"3\" " + 
                "width=\"100%\" style=\"font-size:small;\" bgcolor=\"#AAFF00\">" +
                "<tr><td align=\"justify\">" + 
                "Добро пожаловать на страницу персонального аккаунта. " +
                "Здесь вы можете передать новые показания, посмотреть историю " +
                "показаний. Предупреждение: внесение показаний допускается " +
                "только один раз в текущем месяце для каждой категории. Пользователь: <b>" + 
                userName + "</b>  Лицевой счёт: <b>" + accountNumber +
                "</b></td></tr>" +
                "<tr><td align=\"left\">Сегодня: <b><u>" + LocalDate.now() + "</u></b>" +
                "</td></tr></table>");
        userRole = response.getBody()[2][1];
        responseData = user.getAcc().getReadings();
        readingList.setModel(readingListModel(responseData, chkHotBox.isSelected()));// список показаний

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
     * Создаёт и возвращает контейнер, содержащий данные текущего пользователя
     */
    private Box getUserBox() {
        Box userBox = createReadingBox();

        JLabel lblReading = new JLabel("Показание");
        JLabel lblDate = new JLabel("Дата");
        Box readingBox = Box.createHorizontalBox();
        readingBox.add(lblReading);
        readingBox.add(Box.createHorizontalStrut(5));
        readingBox.add(txtReading);
        readingBox.add(Box.createHorizontalStrut(10));
        readingBox.add(lblDate);
        readingBox.add(Box.createHorizontalStrut(5));
        readingBox.add(txtReadingDate);
        readingBox.add(Box.createHorizontalStrut(10));
        
        readingBox.add(chkHotBox);
        readingBox.add(Box.createHorizontalStrut(5));

        userBox.add(readingBox);
        userBox.add(Box.createVerticalStrut(10));
        return userBox;
    }

    /**
     * Создаёт и возвращает контейнер, содержащий список с показаниями
     * текущего пользователя
     * @return BOX - контейнер, содержащий список с показаниями
     */
    private Box createReadingBox() {
        Box box = Box.createVerticalBox();
        // контейнер для размещения мктки
        Box horBox = Box.createHorizontalBox();
        horBox.add(Box.createHorizontalGlue());
        horBox.add(new JLabel("Переданные показания"));
        horBox.add(Box.createHorizontalGlue());
        box.add(horBox);
        box.add(Box.createVerticalStrut(5));
        // контейнер для размещения списка показаний
        Box horListBox = Box.createHorizontalBox();
        horListBox.add(Box.createHorizontalStrut(150));
        horListBox.add(new JScrollPane(readingList));
        horListBox.add(Box.createHorizontalStrut(150));
        box.add(horListBox);
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
