
package frame;

import api.Response;
import in.Request;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.EtchedBorder;

public class PagePanel extends JPanel {
    private final JPanel mainPanel;// главная панель для расположения компонентов
    private JLabel lblCaption;// метка для вывода названия страницы
    private final JButton okButton = new JButton("Ввод");// кнопка подтверждения ввода
    private final JButton exitButton = new JButton("Выход");// кнопка выхода со страницы
    private final JButton removeButton = new JButton("Удалить");// кнопка удаления
    private String okAction = "ok";
    private String exitAction = "exit";
    private String removeAction = "";
    private Box box;
    private int componentCount;
    private final JPanel centralPanel;// панель для размещения дополнительных элементов
    private final JPanel buttonPanel;// панель для размещения кнопок
    /**
     * Создаёт шаблон панели для размещения элементов пользовательского интерфейса
     * с рамочным менеджером компоновки. В верхней части панели располагается метка
     * с названием панели. В нижней части располагаются кнопки ввода, выхода и
     * удаления. Новые элементы пользовательского интерфейса будут добавляться
     * в центр панели.
     */
    public PagePanel() {
        mainPanel = new JPanel();// создаём главную панель
        centralPanel = new JPanel();
        buttonPanel = new JPanel();
        super.add(mainPanel);
        initComponents();
    }

    /**
     * Creates a new <code>JPanel</code> with a double buffer
     * and a flow layout.
     * @param okAction действие для кнопки Ввод
     * @param exitAction действие для кнопки Выход
     * @param removeAction действие для кнопки Удалить
     */
    public PagePanel(String okAction, String exitAction, String removeAction) {
        this.okAction = okAction;
        this.exitAction = exitAction;
        this.removeAction = removeAction;
        centralPanel = new JPanel();
        buttonPanel = new JPanel();
        mainPanel = new JPanel();// создаём главную панель
//        mainPanel.setBorder(new EtchedBorder(2, Color.BLUE, Color.GREEN));
//        mainPanel.setBackground(Color.ORANGE);
        initComponents();
        super.add(mainPanel);
        
    }

    /**
     * Действия для кнопки ввода (задаёт свойство Name панели)
     * @param okAction задаёт свойство Name панели
     */
    public void setOkAction(String okAction) {
        this.okAction = okAction;
    }

    /**
     * Действия для кнопки выхода (задаёт свойство Name панели)
     * @param exitAction задаёт свойство Name панели
     */
    public void setExitAction(String exitAction) {
        this.exitAction = exitAction;
    }

    /**
     * Действия для кнопки удаления (задаёт свойство Name панели)
     * @param removeAction задаёт свойство Name панели
     */
    public void setRemoveAction(String removeAction) {
        this.removeAction = removeAction;
        if (!removeAction.equals("")) {
            removeButton.setVisible(true);
        }
    }

    public void setCaption(String caption) {
        String text = "<html><body>" + caption + "</body></html>";
        System.out.println("main:" + mainPanel.getSize());
        lblCaption.setPreferredSize(new Dimension(
                (int) box.getWidth(), 100));
//        lblCaption.setSize(lblCaption.getPreferredSize());
        lblCaption.setText(text);
//        lblCaption.setMaximumSize(lblCaption.getPreferredSize());
    }
    /**
     * Инициализация компонентов пользовательского интерфейса
     */
    private void initComponents() {
        lblCaption = new JLabel("Caption");
        lblCaption.setBorder(new EtchedBorder(Color.yellow, Color.black));
        box = Box.createHorizontalBox();// контейнер для кнопок
        box.add(Box.createHorizontalGlue());
        box.add(okButton);
        box.add(Box.createHorizontalStrut(100));
        box.add(removeButton);
        box.add(Box.createHorizontalStrut(100));
        box.add(exitButton);
        box.add(Box.createHorizontalGlue());
        
        /*
        ------Менеджеры размещения-------
        */
//        // для центральной панели
//        GroupLayout centalGroupLayout = new GroupLayout(centralPanel);
//        centralPanel.setLayout(centalGroupLayout);
//        centalGroupLayout.setHorizontalGroup(centalGroupLayout
//                .createParallelGroup(GroupLayout.Alignment.LEADING).
//                addGap(0, 0, Short.MAX_VALUE));
//        centalGroupLayout.setVerticalGroup(centalGroupLayout.
//                createParallelGroup(GroupLayout.Alignment.LEADING).
//                addGap(0, 250, Short.MAX_VALUE));
//        
        // для панели кнопок
        GroupLayout buttonGroupLayout = new GroupLayout(buttonPanel);
        buttonPanel.setLayout(buttonGroupLayout);
        buttonGroupLayout.setHorizontalGroup(buttonGroupLayout
                .createParallelGroup(GroupLayout.Alignment.LEADING).
                addGroup(buttonGroupLayout.createSequentialGroup()
                        .addContainerGap().addGroup(buttonGroupLayout.
                        createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(box, GroupLayout.DEFAULT_SIZE, 
                                        500, Short.MAX_VALUE))
                        .addContainerGap()));
        buttonGroupLayout.setVerticalGroup(buttonGroupLayout
                .createParallelGroup(GroupLayout.Alignment.LEADING).
                addGroup(buttonGroupLayout.createSequentialGroup()
                        .addContainerGap().addGroup(buttonGroupLayout.
                        createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(box, GroupLayout.DEFAULT_SIZE, 
                                        80, Short.MAX_VALUE))
                        .addContainerGap()));
        
        // для основной панели
        GroupLayout mainGroupLayout = new GroupLayout(mainPanel);
        mainPanel.setLayout(mainGroupLayout);
        mainGroupLayout.setHorizontalGroup(mainGroupLayout
                .createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(mainGroupLayout.createSequentialGroup()
                        .addContainerGap().addGroup(mainGroupLayout
                                .createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(lblCaption, 
                                        GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
                        .addComponent(centralPanel, GroupLayout.DEFAULT_SIZE, 
                                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(buttonPanel, GroupLayout.DEFAULT_SIZE, 
                                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap()));
        mainGroupLayout.setVerticalGroup(mainGroupLayout.
                createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(mainGroupLayout.createSequentialGroup()
                .addContainerGap().addComponent(lblCaption, 
                        GroupLayout.PREFERRED_SIZE, 100, 
                        GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(centralPanel, GroupLayout.PREFERRED_SIZE, 
                        GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(buttonPanel, GroupLayout.PREFERRED_SIZE, 
                        GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
        //------------------
        
        // добавляем обработчики
        okButton.addActionListener((e) -> this.setName(okAction));
        removeButton.addActionListener((e) -> this.setName(removeAction));
        exitButton.addActionListener((e) -> this.setName(exitAction));
        
        if (removeAction.equals("")) {

            removeButton.setVisible(false);
        }
        
        componentCount = mainPanel.getComponentCount();
    }

    /**
     * Реализует добавление компонента в центр панели
     * @param component компонент для добавления
     */
    public void addComponent(Component component) {
        if(mainPanel.getComponentCount() > componentCount) {
            mainPanel.remove(mainPanel.getComponentCount() - 1);
        }
        mainPanel.add(component, BorderLayout.CENTER);
//        centralPanel.removeAll();
//        centralPanel.add(component);
    }

    /**
     * Задаёт наименование для кнопки подтверждения взамен заданного по умолчанию
     * @param caption новое наименование
     */
    public void setOkCaption(String caption) {
        okButton.setText(caption);
    }

    /**
     * Задаёт наименование для кнопки выхода взамен заданного по умолчанию
     * @param caption новое наименование
     */
    public void setExitCaption(String caption) {
        exitButton.setText(caption);
    }

    /**
     * Задаёт наименование для кнопки удаления взамен заданного по умолчанию
     * @param caption новое наименование
     */
    public void setRemoveCaption(String caption) {
        removeButton.setText(caption);
    }
    
    public void setOkEnabled(boolean enabled) {
        okButton.setEnabled(enabled);
    }
    
    public void setRemoveEnabled(boolean enabled) {
        removeButton.setEnabled(enabled);
    }
    
    /**
     * Возвращает тело запроса к базе данных
     * @return запрос на выборку данных
     */
    public Request getRequest() {
        return new Request(okAction, true);
    }
    
    /**
     * Задаёт данные, полученные в результате запроса к базе
     * @param response ответ из базы данных
     */
    public void setResponse(Response response) {
        
    }
}
