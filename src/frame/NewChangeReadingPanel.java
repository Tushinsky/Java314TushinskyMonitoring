/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frame;

import entities.WaterReading;
import in.FormattedTextFieldFerifier;
import in.Request;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.text.MaskFormatter;
import mapping.ImappingConstants;
import static mapping.ImappingConstants.NEW_READING;

/**
 *
 * @author Sergii.Tushinskyi
 */
public class NewChangeReadingPanel extends JPanel{
    private final JLabel lblReading = new JLabel("Показание");
    private final JLabel lblReadingDate = new JLabel("Дата");
    private JFormattedTextField txtReading = new JFormattedTextField();
    private JFormattedTextField txtReadingDate = new JFormattedTextField();
    private final JButton okButton = new JButton("Добавить/Изменить");
    private final JCheckBox chkHotBox = new JCheckBox("горячая");
    private Container parentContainer;
    
    /**
     * Создаёт панель с компонентами пользовательского интерфейса для добавления
     * или изменения показаний
     */
    public NewChangeReadingPanel() {
        super();
        initComponents();
        super.setBorder(BorderFactory.createTitledBorder(BorderFactory.
                createLineBorder(Color.DARK_GRAY, 1), "Новые показания"));
    }

    public Request getNewReadingRequest(String accountNumber) {
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
        
        WaterReading wr = new WaterReading(0, ld, Integer
                .parseInt(txtReading.getValue().toString()), 
                chkHotBox.isSelected());
        // создаём запрос на добавление показаний
        Request request = new Request(NEW_READING, false);
        request.getBody()[0][0] = ImappingConstants.ACCOUNT;
        request.getBody()[0][1] = accountNumber;
        request.addToBody(wr);
        return request;
    }

    public void setWaterReading(WaterReading reading) {
        // задаём значения
        txtReading.setValue(reading.getMeasuring());
        txtReadingDate.setValue(reading.getDate().toString());
    }
    /**
     * Задаёт действие для кнопки
     * @param okAction действие для кнопки (одна из констант ImappingConstants)
     */
    public void setOkAction(String okAction) {
        try {
            okButton.removeActionListener(okButton.getActionListeners()[0]);
        } catch(ArrayIndexOutOfBoundsException ex) {
            System.out.println("error:" + ex.getLocalizedMessage());
        } finally {
            okButton.addActionListener(al -> {
                System.out.println("name=" + okAction);
                System.out.println("parent:" + parentContainer.getClass().toString());
                System.out.println("parent:" + parentContainer.getName());
                parentContainer.setName(okAction);
            });
        }
    }
    
    /**
     * Задаёт название для кнопки. Если передан null или пусто, кнопка
     * становится невидимой.
     * @param caption название для кнопки
     */
    public void setOkCaption(String caption) {
        if(caption == null || caption.equals("")) {
            okButton.setVisible(false);
        } else {
            okButton.setText(caption);
        }
        
    }
    
    /**
     * Инициализация и установка свойств компонентов интерфейса
     */
    private void initComponents() {
        try {
            // задаём свойсва полей ввода
            initTextField();
        } catch (ParseException ex) {
            Logger.getLogger(NewChangeReadingPanel.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
        
        GroupLayout layout = new GroupLayout(this);// менеджер компоновки групп
        this.setLayout(layout);
        // размещаем элементы
        // по горизонтали
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.
                        GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblReadingDate)
                        .addGap(53, 53, 53)
                        .addComponent(txtReadingDate, 
                                javax.swing.GroupLayout.PREFERRED_SIZE, 
                                javax.swing.GroupLayout.DEFAULT_SIZE, 
                                javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblReading)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing
                                .GroupLayout.Alignment.LEADING, false)
                            .addComponent(chkHotBox, 
                                    javax.swing.GroupLayout.DEFAULT_SIZE, 
                                    javax.swing.GroupLayout.DEFAULT_SIZE, 
                                    Short.MAX_VALUE)
                            .addComponent(txtReading)))
                    .addComponent(okButton, javax.swing.GroupLayout.DEFAULT_SIZE, 
                            javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        // по вертикали
        layout.setVerticalGroup(layout.createParallelGroup(javax.swing
                .GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(18, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing
                        .GroupLayout.Alignment.BASELINE)
                    .addComponent(lblReadingDate)
                    .addComponent(txtReadingDate, javax.swing
                            .GroupLayout.PREFERRED_SIZE, 
                            javax.swing.GroupLayout.DEFAULT_SIZE, 
                            javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing
                        .GroupLayout.Alignment.BASELINE)
                    .addComponent(lblReading)
                    .addComponent(txtReading, javax.swing
                            .GroupLayout.PREFERRED_SIZE, javax.swing
                                    .GroupLayout.DEFAULT_SIZE, 
                            javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addComponent(chkHotBox)
                .addGap(10, 10, 10)
                .addComponent(okButton)
                .addContainerGap(18, Short.MAX_VALUE))
        );
    }
    
    private void initTextField() throws ParseException {
        // поле для ввода показаний
        txtReading = new JFormattedTextField(NumberFormat.getIntegerInstance());
        txtReading.setValue(0);
        // задаём верификатор для проверки корректности ввода показаний
        txtReading.setInputVerifier(new FormattedTextFieldFerifier());
        txtReading.setColumns(10);
        // поле для ввода даты - зададим маску ввода
        MaskFormatter dateFormatter = new MaskFormatter("####-##-##");
//        dateFormatter.setPlaceholderCharacter('1');// символ-заполнитель маски
        dateFormatter.setValidCharacters("0123456789");// разрешённые символы для ввода
        txtReadingDate = new JFormattedTextField(dateFormatter);
        txtReadingDate.setValue(LocalDate.now());// задаём значение - текущая дата
        txtReadingDate.setColumns(10);
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

    
    public WaterReading getWaterReading(int id) {
        LocalDate ld = LocalDate.parse(txtReadingDate.getValue().toString());
        return new WaterReading(id, ld, Integer
                .parseInt(txtReading.getValue().toString()), 
                chkHotBox.isSelected());
    }

    
    public void setParentContainer(Container parentContainer) {
        this.parentContainer = parentContainer;
    }
    
    
}
