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
    private final API api = new API();// класс для связи с базой данных
    private StartPanel startPanel;// начальная панель
    
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

    /**
     * Инициализация компонентов пользовательского интерфейса
     */
    private void initComponents() {
        cardLayout = new CardLayout();// создаём менеджер компоновки
        
        // создаём главную панель и задаём её свойства
        mainPanel = new JPanel(true);
        mainPanel.setBorder(new BevelBorder(2));// граница
        mainPanel.setBackground(Color.CYAN);// фон
        mainPanel.setOpaque(true);// прозрачность
        mainPanel.setAutoscrolls(true);// автоматическая прокрутка содержимого
        // панель прокрутки
        JScrollPane pane = new JScrollPane(mainPanel, 
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        createStartPanel();// создаём стартовую панель

        super.setSize(650, 450);// размеры формы
        super.getContentPane().add(pane);// на панель содержимого добавляем панель
        mainPanel.setLayout(cardLayout);// задаём менеджер компоновки
        
        startPanel.setSize(mainPanel.getSize());// размер стартовой панели
        
        mainPanel.add(startPanel,LOG_OUT);
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
            if (evt.getNewValue() == null) return;
            if (evt.getNewValue().equals(LOG_IN)) {
                // показываем страницу ввода пароля и логина
                createLoginPanel();
                showPanel(LOG_IN);
                startPanel.setOkEnabled(true);// разблокируем кнопку входа
            } else if(evt.getNewValue().equals(REGISTER)) {
                // показываем страницу регистрации
                createRegisterPanel();
                showPanel(REGISTER);
            } else if (evt.getNewValue().equals(EXIT)){
                System.exit(0);
            }
            startPanel.setName(null);// сбрасываем свойство в null
        });
    }

    /**
     * Создаёт панель ввода пароля
     */
    private void createLoginPanel() {
        LoginPanel loginPanel = new LoginPanel();
        // добавляем к панели пароля слушатель изменения свойства Name
        loginPanel.addPropertyChangeListener(evt -> {
            Object value = evt.getNewValue();
            if (value == null) return;
            switch (value.toString()) {
                case LOG_IN:
                    // получаем тело запроса
                    Request request = loginPanel.getRequest();
                    // проверяем запрос
                    if(request != null) {
                        if(signIn(request)) {
                            // удаляем панель из менеджера
                            cardLayout.removeLayoutComponent(loginPanel);
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
                        // удаляем панель из менеджера
                        cardLayout.removeLayoutComponent(loginPanel);
                        showPanel(LOG_OUT);// показываем начальную страницу
                        startPanel.setOkEnabled(false);// блокируем кнопку ввода
                    }
                    break;
                case LOG_OUT:
                    showPanel(LOG_OUT);
            }
            loginPanel.setName(null);// сбрасываем свойство в null
        });
        mainPanel.add(loginPanel, LOG_IN);
    }
    
    /**
     * Создаёт панель регистрации нового пользователя
     */
    private void createRegisterPanel() {
        RegisterPanel registerPanel = new RegisterPanel();
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
            registerPanel.setName(null);// сбрасываем свойство в null
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
//                homePagePanel.setResponse(response);
            } else {
                // создаём панель домашней страницы администратора
                createAdminPagePanel(response);
//                adminPagePanel.setResponse(response);
            }
            
            showPanel(HOME_PAGE);
        }
        return retValue;
    }

    /**
     * Создаёт панель Домашняя страница
     */
    private void createHomePagePanel(Response responseInit) {
        HomePagePanel homePagePanel = new HomePagePanel(responseInit);
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
                    Request request = homePagePanel.getRequest();// получаем тело запроса
                    if(request != null) {
                        Response response = api.response(request);
                        homePagePanel.setResponse(response);
                    }
            }
            homePagePanel.setName(null);// сбрасываем имя в null
        });
        mainPanel.add(homePagePanel, HOME_PAGE);
    }
    
    /**
     * Создаёт панель страницы для администратора
     */
    private void createAdminPagePanel(Response responseInit) {
        AdminPagePanel adminPagePanel = new AdminPagePanel(responseInit);
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
                case CHANGE_READING:
                case REMOVE_ACCOUNT:
                case REMOVE_READING:
                    Response response = api.response(adminPagePanel.getRequest());
                    adminPagePanel.setResponse(response);
            }
            adminPagePanel.setName(null);// сбрасываем имя в null
        });
        mainPanel.add(adminPagePanel, HOME_PAGE);
    }

}
