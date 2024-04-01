
package frame;

import api.Response;
import in.Request;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
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
    /**
     * Создаёт шаблон панели для размещения элементов пользовательского интерфейса
     * с рамочным менеджером компоновки. В верхней части панели располагается метка
     * с названием панели. В нижней части располагаются кнопки ввода, выхода и
     * удаления. Новые элементы пользовательского интерфейса будут добавляться
     * в центр панели.
     */
    public PagePanel() {
        mainPanel = new JPanel(new BorderLayout());// создаём главную панель
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
        mainPanel = new JPanel(new BorderLayout());// создаём главную панель
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
        super.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent ce) {
                super.componentResized(ce); //To change body of generated methods, choose Tools | Templates.
//                mainPanel.setSize(mainPanel.getParent().getSize());
//                mainPanel.updateUI();
//                System.out.println("page:" + mainPanel.getParent().getSize());
//                System.out.println("main:" + mainPanel.getSize());
//                lblCaption.setPreferredSize(new Dimension((int)mainPanel
//                        .getSize().getWidth(), (int)lblCaption.getPreferredSize().getHeight()));
//                box.setSize(mainPanel.getSize());
//                
//                System.out.println("box:" + box.getParent().getSize());
//                System.out.println("box:" + box.getSize());
                
            }
            
        });
        lblCaption = new JLabel("Caption");
//        Box horBox = Box.createHorizontalBox();
//        horBox.add(Box.createHorizontalStrut(10));
//        horBox.add(lblCaption);
//        horBox.add(Box.createHorizontalStrut(10));
        lblCaption.setBorder(new EtchedBorder(Color.yellow, Color.black));
        // добавляем обработчики
        okButton.addActionListener((e) -> this.setName(okAction));
        removeButton.addActionListener((e) -> this.setName(removeAction));
        exitButton.addActionListener((e) -> this.setName(exitAction));
        // метка располагается в верхней части панели
        mainPanel.add(lblCaption, BorderLayout.NORTH);
        
        box = Box.createHorizontalBox();// контейнер для кнопок
        box.add(Box.createHorizontalGlue());
        box.add(okButton);
        box.add(Box.createHorizontalStrut(100));
        box.add(removeButton);
        box.add(Box.createHorizontalStrut(100));
        box.add(exitButton);
        box.add(Box.createHorizontalGlue());
        if (removeAction.equals("")) {

            removeButton.setVisible(false);
        }
//        JPanel westPanel = new JPanel();
//        westPanel.setPreferredSize(new Dimension(20, mainPanel.getHeight()));
//        JPanel eastPanel = new JPanel();
//        eastPanel.setPreferredSize(new Dimension(20, mainPanel.getHeight()));
//        mainPanel.add(westPanel, BorderLayout.WEST);
//        mainPanel.add(eastPanel, BorderLayout.EAST);
        // контейнер располагаем в нижней части панели
        mainPanel.add(box, BorderLayout.SOUTH);
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
