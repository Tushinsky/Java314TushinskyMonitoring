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

import static mapping.ImappingConstants.*;

public class HomePagePanel extends PagePanel {
    private final ReadingListComponent readingList = new ReadingListComponent();// список показаний
    private final User user;
    private final NewChangeReadingPanel pnlNewChangeReadingPanel = new NewChangeReadingPanel();
    /**
     * Creates a new <code>JPanel</code> with a double buffer
     * and a flow layout.
     * @param response результат запроса к базе данных, содержащий входные данные
     */
    public HomePagePanel(Response response) {
        super(NEW_READING, LOG_OUT, "");
        // получаем пользователя из тела ответа
        user = (User) response.getFromBody(0);
        initComponents();// инициализация компонентов

    }

    @Override
    public Request getRequest() {
        return addNewReadingRequest();
        
    }

    @Override
    public void setResponse(Response response) {
        
        // если ответ положительный, и новые показания приняты, добавляем их в список показаний
        if (response.isAuth()) {
            int id = Integer.parseInt(response.getBody()[0][1]);
            addReading(id);// добавляем показание в аккаунт пользователя
        } else {
            JOptionPane.showMessageDialog(null,
                    "Внесение показаний допускается только одни раз в текущем месяце",
                    "Warning", JOptionPane.WARNING_MESSAGE);
        }

    }

    /**
     * Инициализация компонентов пользовательского интерфейса
     */
    private void initComponents() {
        String userName = user.getUsername();// получаем имя пользователя
        String accountNumber = user.getAcc().getAccountNumber();// получаем номер аккаунта
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
        super.addComponent(getReadingBox());
        // задаём название для кнопки ввода
        super.setOkCaption("Добавить показания");
        pnlNewChangeReadingPanel.setOkCaption(null);
        // получаем данные по показаниям
        ArrayList<Reading> readings = user.getAcc()
                .getReadings();
        // заполняем список
        readingList.setModel(readingListModel(readings));
    }
    
    /**
     * Создаёт и возвращает контейнер, содержащий список с показаниями
     * текущего пользователя
     * @return BOX - контейнер, содержащий список с показаниями
     */
    private Box getReadingBox() {
        Box box = Box.createHorizontalBox();
        // контейнер для размещения списка показаний
        Box vertBox = Box.createVerticalBox();
        vertBox.add(Box.createVerticalStrut(10));
        vertBox.add(new JLabel("История показаний"));
        vertBox.add(Box.createVerticalStrut(10));
        vertBox.add(new JScrollPane(readingList));
        box.add(Box.createHorizontalGlue());
        box.add(vertBox);
        box.add(Box.createHorizontalStrut(15));
        // контейнер для размещения списка показаний
        box.add(pnlNewChangeReadingPanel);
        box.add(Box.createHorizontalGlue());
        return box;
    }

    /**
     * Создаёт и возвращает модель списка по умолчанию
     * @param data данные для заполнения модели
     * @return модель списка, заполненную полученными данными
     */
    private DefaultListModel<Reading> readingListModel(ArrayList<Reading> data) {
        DefaultListModel<Reading> model = new DefaultListModel<>();
        if(data != null) {
            data.forEach((Reading r) -> model.addElement(r));// в модель ложим дату и показания
            
        }
        return model;// результат
    }

    /**
     * Создаёт и заполняет тело запроса на добавление новых показаний пользователя
     * @return request - тело запроса, содержащее данные
     */
    private Request addNewReadingRequest() {
        // запрос на подтверждение
        if(JOptionPane.showConfirmDialog(this, "Добавить новые показания?", 
                "Внимание", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.CANCEL_OPTION) {
            return null;// если отмена
        }
        // создаём запрос на добавление показаний
        // создаём запрос на добавление показаний
        Request request = pnlNewChangeReadingPanel.getNewReadingRequest();
        request.getBody()[0][1] = user.getAcc().getAccountNumber();
        request.getBody()[1][0] = ImappingConstants.ROLE;
        request.getBody()[1][1] = user.getRole();
        return request;
    }

    /**
     * Информирует пользователя об операции добавления новых показаний
     * @param id идентификатор новой записи в базе данных
     */
    private void addReading(int id) {
        // создаём объект показаний
        WaterReading reading = pnlNewChangeReadingPanel.getWaterReading();
        // получаем модель списка
        DefaultListModel model = (DefaultListModel) readingList.getModel();
        // получаем номер последней записи
        int idNumber = model.size();
        idNumber++;// увеличиваем
        // создаём новую запись для добавления
        WaterReading newWaterReading = new WaterReading(idNumber, id, 
                reading.getDate(), reading.getMeasuring(), reading.isHot());
        model.addElement(newWaterReading);// добавляем показание в модель
        
    }
    

//            
}
