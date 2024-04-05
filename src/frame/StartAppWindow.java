package frame;

import in.Request;
import api.API;
import api.Response;
import entities.IRoleConstants;

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
    private RegisterPanel registerPanel;
    private AdminPagePanel adminPagePanel;
    
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
//        createLoginPanel();// создаём панель для ввода логина и пароля пользователя
////            LoginPanel registerPanel = registerPanel();// создаём панель для регистрации пользователя
//        createHomePagePanel();// создаём домашнюю страницу пользователя
        
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
//        mainPanel.add(loginPanel, LOG_IN);
//        mainPanel.add(homePagePanel, HOME_PAGE);
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
     * Создаёт стартовую панель приложения
     */
    private void createStartPanel() {
        startPanel = new StartPanel();// создаём стартовую панель
        // добавляем к стартовой панели слушатель изменения свойства Name
        startPanel.addPropertyChangeListener(evt -> {
//                System.out.println("start_name=" + evt.getPropertyName());
            if (evt.getNewValue() == null) return;
            if (evt.getNewValue().equals(LOG_IN)) {
                // показываем страницу ввода пароля и логина
                createLoginPanel();
                showPanel(LOG_IN);
                startPanel.setOkEnabled(true);// разблокируем кнопку входа
            } else if(evt.getNewValue().equals(REGISTER)) {
                // показываем страницу регистрацию
                createRegisterPanel();
                showPanel(REGISTER);
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
        // добавляем к панели пароля слушатель изменения свойства Name
        loginPanel.addPropertyChangeListener(evt -> {
            Object value = evt.getNewValue();
//                System.out.println("login_name=" + evt.getNewValue().toString());
            if (value == null) return;
            switch (value.toString()) {
                case LOG_IN:
                    // получаем тело запроса
                    Request request = loginPanel.getRequest();
                    // проверяем запрос
                    if(request != null) {
                        if(signIn(request)) {
                            
                            cardLayout.removeLayoutComponent(loginPanel);// удаляем панель из менеджера
                        } else {
                            JOptionPane.showMessageDialog(this, 
                                "Проверьте правильность ввода логина или пароля!", "Аутентификация", 
                                        JOptionPane.WARNING_MESSAGE);
                        }
                    } else {
                        // извещаем пользователя
                        JOptionPane.showMessageDialog(this, 
                                "Превышен лимит попыток для входа. Доступ закрыт.\n" +
                                        "Обратитесь к администратору!", "Аутентификация", 
                                        JOptionPane.WARNING_MESSAGE);
                        cardLayout.removeLayoutComponent(loginPanel);// удаляем панель из менеджера
                        showPanel(LOG_OUT);// показываем начальную страницу
                        startPanel.setOkEnabled(false);// блокируем кнопку ввода
                    }
                    break;
                case LOG_OUT:
                    showPanel(LOG_OUT);
                    break;
                default:
                    break;
            }
            loginPanel.setName(null);
//            loginPanel = null;

        });
        mainPanel.add(loginPanel, LOG_IN);
    }
    
    /**
     * Создаёт панель регистрации нового пользователя
     */
    private void createRegisterPanel() {
        registerPanel = new RegisterPanel();
        // добавляем к панели регистрации слушатель изменения свойства Name
        registerPanel.addPropertyChangeListener(evt -> {
            Object value = evt.getNewValue();
//                System.out.println("login_name=" + evt.getNewValue().toString());
            if (value == null) return;
            switch (value.toString()) {
                case REGISTER:
                    signIn(registerPanel.getRequest());
                    break;
                case LOG_OUT:
                    
                    showPanel(LOG_OUT);
                    break;
                default:
                    break;
            }
            cardLayout.removeLayoutComponent(registerPanel);// удаляем панель из менеджера
            registerPanel.setName(null);
        });
        mainPanel.add(registerPanel, REGISTER);
    }

    /**
     * Выполняет запрос на вход в домашнюю страницу
     * @param request запрос с параметрами на вход
     */
    private boolean signIn(Request request) {
        // отправляем запрос на вход
        Response response = api.response(request);
        boolean retValue = response.isAuth();// получаем результат ответа
        if (retValue) {
            // если запрос на вход подтверждён, переходим на домашнюю страницу
            if(response.getBody()[2][1].equals(IRoleConstants.USER)) {
                createHomePagePanel(response);// создаём панель домашней страницы пользователя
                homePagePanel.setResponse(response);
            } else {
                // создаём панель домашней страницы администратора
                createAdminPagePanel();
                adminPagePanel.setResponse(response);
            }
            
            showPanel(HOME_PAGE);
        }
        return retValue;
    }

    /**
     * Создаёт панель Домашняя страница
     */
    private void createHomePagePanel(Response responseInit) {
        homePagePanel = new HomePagePanel(responseInit);
        // добавляем слушатель изменений свойства NAME
        homePagePanel.addPropertyChangeListener("name", evt -> {
            if (evt.getNewValue() == null) return;
            switch(evt.getNewValue().toString()) {
                case LOG_OUT:
                    // выход на стартовую панель
                    cardLayout.removeLayoutComponent(homePagePanel);
                    showPanel(LOG_OUT);
                    break;
                case NEW_READING:
                case GET_READING:
                    Response response = api.response(homePagePanel.getRequest());
                    homePagePanel.setResponse(response);
                    break;
                default:
                    return;
            }
            homePagePanel.setName(null);
        });
        mainPanel.add(homePagePanel, HOME_PAGE);
    }
    
    /**
     * Создаёт панель страницы для администратора
     */
    private void createAdminPagePanel() {
        adminPagePanel = new AdminPagePanel();
        // добавляем слушатель изменений свойства NAME
        adminPagePanel.addPropertyChangeListener("name", evt -> {
            if (evt.getNewValue() == null) return;
            switch(evt.getNewValue().toString()) {
                case LOG_OUT:
                    // выход на стартовую панель
                    cardLayout.removeLayoutComponent(adminPagePanel);
                    showPanel(LOG_OUT);
                    break;
                case NEW_READING:
                case GET_READING:
                    Response response = api.response(adminPagePanel.getRequest());
                    adminPagePanel.setResponse(response);
                    break;
                default:
                    return;
            }
            adminPagePanel.setName(null);
        });
        mainPanel.add(adminPagePanel, HOME_PAGE);
    }

}
