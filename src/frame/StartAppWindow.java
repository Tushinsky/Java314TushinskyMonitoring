package frame;

import in.Request;
import api.API;
import api.Response;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.border.BevelBorder;

import static mapping.ImappingConstants.*;

public class StartAppWindow extends JFrame {
    private JPanel mainPanel;// главная панель, на которой будут располагаться все компоненты
    private CardLayout cardLayout;// менеджер карточной компоновки для главной панели
    private LoginPanel loginPanel;
    private final API api = new API();
    private HomePagePanel homePagePanel;
    private StartPanel startPanel;
    
    /**
     * Constructs a new frame that is initially invisible.
     * <p>
     * This constructor sets the component's locale property to the value
     * returned by <code>JComponent.getDefaultLocale</code>.
     *
     * @throws HeadlessException if GraphicsEnvironment.isHeadless()
     *                           returns true.
     * @see GraphicsEnvironment#isHeadless
     * @see Component#setSize
     * @see Component#setVisible
     * @see JComponent#getDefaultLocale
     */
    public StartAppWindow() throws HeadlessException {
        super.setTitle("Сервис передачи показаний счётчиков воды");
        initComponents();// инициализируем компоненты интерфейса
        super.addWindowListener(new WindowAdapter() {
            /**
             * Invoked when a window is in the process of being closed.
             * The close operation can be overridden at this point.
             *
             * @param e событие, возникающее при закрытии окна
             */
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                System.exit(0);
            }
            
        });
        showPanel(LOG_OUT);// начальная панель
    }

    private void initComponents() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel();
        mainPanel.setBorder(new BevelBorder(2));
        mainPanel.setBackground(Color.CYAN);
        mainPanel.setOpaque(true);
        createStartPanel();// создаём стартовую панель
        createLoginPanel();// создаём панель для ввода логина и пароля пользователя
//            LoginPanel registerPanel = registerPanel();// создаём панель для регистрации пользователя
        createHomePagePanel();// создаём домашнюю страницу пользователя
        
//            mainPanel.add(registerPanel, REGISTER);

        super.setSize(650, 450);
        super.getContentPane().add(mainPanel);
        mainPanel.setLayout(cardLayout);
//        startPanel.setPreferredSize(mainPanel.getPreferredSize());
//        loginPanel.setPreferredSize(new Dimension(600, 400));
//        homePagePanel.setPreferredSize(new Dimension(600, 400));
        
        startPanel.setSize(mainPanel.getSize());
//        loginPanel.setSize(loginPanel.getPreferredSize());
//        homePagePanel.setSize(homePagePanel.getPreferredSize());
        
        mainPanel.add(startPanel,LOG_OUT);
        mainPanel.add(loginPanel, LOG_IN);
        mainPanel.add(homePagePanel, HOME_PAGE);
        super.setType(Type.NORMAL);
        
    }

    /**
     * Отображает панель с заданным именем
     * @param namePanel имя панели для отображения
     */
    private void showPanel(String namePanel) {
        cardLayout.show(mainPanel, namePanel);
    }

    /**
     * Создаёт и возвращает стартовую панель приложения
     * @return 
     */
    private void createStartPanel() {
        startPanel = new StartPanel();// создаём стартовую панель
        // добавляем к стартовой панели слушатель изменения свойства Name
        startPanel.addPropertyChangeListener(evt -> {
//                System.out.println("start_name=" + evt.getPropertyName());
            if (evt.getNewValue() == null) return;
            if (evt.getNewValue().equals(LOG_IN) || evt.getNewValue().equals(REGISTER)) {
                loginPanel.setOkAction(evt.getNewValue().toString());
                showPanel(LOG_IN);
                startPanel.setOkEnabled(true);// разблокируем кнопку входа
            } else if (evt.getNewValue().equals(EXIT)){
                System.exit(0);
            }
            startPanel.setName(null);
        });
    }

    /**
     * Создаёт панель ввода пароля
     */
    private void createLoginPanel() {
        loginPanel = new LoginPanel();
        loginPanel.setOkAction(LOG_IN);
        // добавляем к панели пароля слушатель изменения свойства Name
        loginPanel.addPropertyChangeListener(evt -> {
            Object value = evt.getNewValue();
//                System.out.println("login_name=" + evt.getNewValue().toString());
            if (value == null) return;
            switch (value.toString()) {
                case LOG_IN:
                case REGISTER:
                    signIn(loginPanel.getRequest());
                    break;
                case LOG_OUT:
                    showPanel(value.toString());
                    break;
                default:
                    break;
            }
            loginPanel.setName(null);

        });
    }

    /**
     * Выполняет запрос на вход в домашнюю страницу
     * @param request запрос с параметрами на вход
     */
    private void signIn(Request request) {
        if(request == null) {
            JOptionPane.showMessageDialog(this, 
                    "Превышел лимит попыток для входа. Доступ закрыт.\n" +
                            "Обратитесь к администратору!", "Аутентификация", 
                            JOptionPane.WARNING_MESSAGE);
            showPanel(LOG_OUT);// показываем начальную страницу
            startPanel.setOkEnabled(false);// блокируем кнопку ввода
        } else {
            // отправляем запрос на вход
            Response response = api.response(request);
            if (response.isAuth()) {
                // если запрос на вход подтверждён, переходим на домашнюю страницу
                homePagePanel.setResponse(response);
                showPanel(HOME_PAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Проверьте правильность ввода логина или пароля!", "Аутентификация", 
                            JOptionPane.WARNING_MESSAGE);
                
            }
        }
    }

    /**
     * Создаёт панель Домашняя страница
     */
    private void createHomePagePanel() {
        homePagePanel = new HomePagePanel();
        // добавляем слушатель изменений свойства NAME
        homePagePanel.addPropertyChangeListener("name", evt -> {
            if (evt.getNewValue() == null) return;
            switch(evt.getNewValue().toString()) {
                case LOG_OUT:
                    // выход на стартовую панель
                    showPanel(evt.getNewValue().toString());
                    break;
                case NEW_READING:
                case REMOVE_ACCOUNT:
                case REMOVE_READING:
                case GET_READING:
                    setResponseToHomePanel(homePagePanel.getRequest());
                    break;
                default:
                    return;
            }
            homePagePanel.setName(null);
        });
    }

    /**
     * Передаёт данные на домашнюю страницу
     * @param request запрос на получение данных
     */
    private void setResponseToHomePanel(Request request) {
        // отправляем запрос на добавление
        Response response = api.response(request);
        homePagePanel.setResponse(response);

    }

}
