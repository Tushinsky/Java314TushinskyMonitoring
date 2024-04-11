/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frame;

import entities.WaterReading;
import in.FormattedTextFieldFerifier;
import java.awt.Font;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.text.MaskFormatter;

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
    private String okAction;
    private WaterReading waterReading;
    
    /**
     * Создаёт панель с компонентами пользовательского интерфейса для добавления
     * или изменения показаний
     */
    public NewChangeReadingPanel() {
        super();
        initComponents();
    }

    public WaterReading getWaterReading() {
        waterReading = new WaterReading(0, 
                LocalDate.parse(txtReadingDate.getValue().toString()), 
                Integer.parseInt(txtReading.getValue().toString()), chkHotBox.isSelected());
        return waterReading;
    }
    
    public String getOkAction() {
        return okAction;
    }

    public void setOkAction(String okAction) {
        okButton.addActionListener(al -> this.getParent().setName(okAction));
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
                .addContainerGap(36, Short.MAX_VALUE))
        );
        // по вертикали
        layout.setVerticalGroup(layout.createParallelGroup(javax.swing
                .GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(36, Short.MAX_VALUE)
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
                .addGap(18, 18, 18)
                .addComponent(chkHotBox)
                .addGap(18, 18, 18)
                .addComponent(okButton)
                .addContainerGap(38, Short.MAX_VALUE))
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
    
    
}
