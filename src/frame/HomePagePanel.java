package frame;

import in.Request;
import api.Response;
import entities.Reading;
import entities.User;
import entities.WaterReading;
import in.FormattedTextFieldFerifier;
import java.awt.Color;
import java.awt.Font;
import java.text.NumberFormat;
import java.text.ParseException;
import mapping.ImappingConstants;

import javax.swing.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.MaskFormatter;

import static mapping.ImappingConstants.*;

public class HomePagePanel extends PagePanel {
    private final JList<String> readingList = new JList<>();// список показаний
    private JFormattedTextField txtReading;// поле для ввода показаний
    private String accountNumber;// номер аккаунта
    private String userName;// имя пользователя
    private JCheckBox chkHotBox;// отмечает показания по горячей или холодной воде
    private JFormattedTextField txtReadingDate;// поле для ввода даты показаний
    private final User user;
    /**
     * Creates a new <code>JPanel</code> with a double buffer
     * and a flow layout.
     * @param response результат запроса к базе данных, содержащий входные данные
     */
    public HomePagePanel(Response response) {
        super(NEW_READING, LOG_OUT, "");
        // получаем пользователя из тела ответа
        user = response.getFromBody(0);
        initComponents();// инициализация компонентов

    }

    @Override
    public Request getRequest() {
        return addNewReading();
        
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
        try {
            initTextField();
        } catch (ParseException ex) {
            Logger.getLogger(HomePagePanel.class.getName()).log(Level.SEVERE, null, ex);
        }
//        this.response = response;
        chkHotBox = new JCheckBox("горячая");
        userName = user.getUsername();// получаем имя пользователя
        accountNumber = user.getAcc().getAccountNumber();// получаем номер аккаунта
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
        super.addComponent(getUserBox());
        // задаём название для кнопки ввода
        super.setOkCaption("Добавить показания");

        // добавляем слушатель на флажок
        chkHotBox.addActionListener((e -> {
                Color color = chkHotBox.isSelected() ? Color.PINK : 
                        new Color(150, 150, 255, 20);
                chkHotBox.setBackground(color);
                
                updateResponseData();// список показаний
        }));
        chkHotBox.setSelected(true);// вывод показаний по холодной воде
        chkHotBox.doClick();// список показаний
    }
    
    /**
     * Инициализирует поля ввода и задаёт их свойства
     * @throws ParseException 
     */
    private void initTextField() throws ParseException {
        // поле для ввода показаний
        txtReading = new JFormattedTextField(NumberFormat.getIntegerInstance());
        txtReading.setValue(0);
        // задаём верификатор для проверки корректности ввода показаний
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

    /**
     * Создаёт и возвращает модель списка по умолчанию
     * @param data данные для заполнения модели
     * @return модель списка, заполненную полученными данными
     */
    private DefaultListModel<String> readingListModel(ArrayList<Reading> data, boolean isHot) {
        DefaultListModel<String> model = new DefaultListModel<>();
        System.out.println("data= " + data);
        if(data != null) {
            // фильтруем данные по флагу
            data.stream().filter((r) -> {
                WaterReading wr = (WaterReading) r;// приводим к нужному типу
                return wr.isHot() == isHot;// возвращаем, если флаг соответствует
            }).forEach((Reading r) -> model.addElement(r.getDate() + 
                    " | " + r.getMeasuring()));// в модель ложим дату и показания
            
        }
        return model;// результат фильтра
    }

    /**
     * Создаёт и заполняет тело запроса на добавление новых показаний пользователя
     * @return request - тело запроса, содержащее данные
     */
    private Request addNewReading() {
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
        // создаём объект показаний с нулевым кодом
        WaterReading wr = new WaterReading(0, ld, 
                Integer.parseInt(txtReading.getValue().toString()), 
                chkHotBox.isSelected());
        request.addToBody(wr);// ложим его в тело запроса
        return request;
    }

    /**
     * Информирует пользователя об операции добавления новых показаний
     * @param response ответ базы данных о результатах операции добавления
     */
    private void addReading(int id) {
        // создаём объект показаний
        WaterReading reading = new WaterReading(id, LocalDate
                .parse(txtReadingDate.getValue().toString()), 
                Integer.parseInt(txtReading.getValue().toString()), 
                chkHotBox.isSelected());
        // получаем список показаний
        ArrayList<Reading> readings = user.getAcc()
                .getReadings();
        readings.add(reading);// добавляем показание в список
        updateResponseData();

    }
    
    /**
     * Обновление данных, полученных из ответа к базе данных
     */
    private void updateResponseData() {
        // получаем данные по показаниям
        ArrayList<Reading> readings = user.getAcc()
                .getReadings();

        // заполняем список
        readingList.setModel(readingListModel(readings, chkHotBox.isSelected()));
    }
    

//            
}
