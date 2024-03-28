package db;

import csv.CSVOperate;
import entities.Account;
import entities.IRoleConstants;
import entities.User;
import entities.Reading;
import entities.WaterReading;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Наша импровизированная база данных
 * В последствии, доступ к ней реализуем не через методы базы,
 * а через объекты, дающие доступ к конкретным ее сущностям
 * */
public class DataBase implements IDao {
    private final ArrayList<User> users = new ArrayList<>();
//    private boolean aBoolean = false;// флаг добавления нового пользователя
    private final CSVOperate csvOperate;
    private final String accountFileName = "readingsDB/Account.csv";
    private final String readingFileName = "readingsDB/Readings.csv";
    private final String userFileName = "readingsDB/Users.csv";
    public DataBase() {
        csvOperate = new CSVOperate();
        dbInit();
    }

    /**
     * Инициализация списка пользователей
     */
    private void dbInit() {
        // считываем таблицу зарегистрированных пользователей и заполняем массив
        Object[][] database = getDataTable(userFileName);// получаем массив
        // перебираем, получаем пользователей
        for(Object[] data : database) {
            users.add(new User(Integer.parseInt(data[0].toString()), 
                    Integer.parseInt(data[1].toString()), (String)data[2], 
                    (String)data[3], (String)data[4]));
        }
        users.forEach(u -> u.setAcc(accountInit(u.getId())));
    }

    /**
     * Инициализация данных по пользоветельским аккаунтам
     * @param iduser код пользователя для получения данных по аккаунтв
     * @return аккаунт пользователя с данными
     */
    private Account accountInit(int iduser) {
        Object[][] database;// получаем массив
        database = getDataTable(accountFileName);
        Account acc = null;
        for(Object[] data : database) {
            // получаем код пользователя из второго столбца
            int id = Integer.parseInt(data[1].toString());
            
            if(id == iduser) {
                // создаём аккаунт по коду пользователя
                acc = new Account(Integer.parseInt(data[0].toString()), 
                        Integer.parseInt(data[1].toString()), data[2].toString());
                break;
            }
        }
        if(acc != null) {
            // получаем данные по аккаунту
            database = getDataTable(readingFileName);// получаем массив
            int index = 0;
            for(Object[] data : database) {
                // получаем код аккаунта пользователя из второго столбца
                int idAcc = Integer.parseInt(data[1].toString());
                if(idAcc == acc.getId()) {
                    // заполняем аккаунт пользователя данными
                    LocalDate date = LocalDate.parse(data[4].toString());
                    int count = Integer.parseInt(data[2].toString());
                    boolean hot = !data[3].toString().equals("0");
                    WaterReading reading = new WaterReading(date, count, hot);
//                    System.out.println("reading:" + reading);
                    acc.getReadings()[index] = reading;
                    index++;
                }
            }
        }
        return acc;
    }

    @Override
    public String toString() {
        return "DataBase{" +
                "users=" + users +
                '}';
    }

    @Override
    public boolean authorize(String login, String password) {
        for (User user : users) {
            if (user == null) {
                return false;
            }
            if (user.getLogin().equals(login) && user.getPassword().equals(password)) {
                
                return true;
            }
        }
        return false;
    }

    @Override
    public User findUserByAccountNumber(String accountNumber) {
        for (User user : users) {
            if (user.getRole().equals(IRoleConstants.USER)) {
                if (user.getAcc().getAccountNumber().equals(accountNumber)) {
                    System.out.println("user name = " + user.getUsername());
                    return user;
                }
            }
        }
        return null;
    }

    @Override
    public User findUserByUsername(String login) {
        for (User user : users) {
            if (user.getLogin().equals(login)) {
//                System.out.println(Arrays.toString(user.getAcc().getReadings()));
                return user;
            }
        }
        return null;
    }

    @Override
    public boolean addNewUser(String username, String login, String password) {
        // проверяем существование пользователя с таким же логином
        if(!users.stream().noneMatch(u -> u.getLogin().equals(login))) {
            // если такой пользователь существует в списке, возвращаемся
            return false;
        }
        FileWriter writer = null;// объект для записи в файл
        try {
            /*
            если пользователей с такими данными не найдено в списке, тогда
            добавляем нового пользователя в файл пользователей и в список
            */
            // получаем инедтификатор последнего пользователя в списке и увеличиваем на 1
            User user = users.get(users.size() - 1);// получаем последнего пользователя
            int id = user.getId() + 1;// код нового пользователя
            int idAccount = user.getAcc().getId() + 1;// код нового аккаунта
            int number = Integer.parseInt(user.getAcc().getAccountNumber()) + 1;// номер аккааунта уввеличиваем на 1
            String accountNumber = String.valueOf(number);
            
            // открываем файл с данными пользователей для добавления новой записи
            writer = new FileWriter(userFileName, true);
            // строка для добавления в файл
            String separator = System.getProperty("line.separator");
            String string = id + ";2;" + username + ";" + login + ";" + 
                    password + separator;
            System.out.println("string: " + string);
            writer.write(string);
            writer.close();// закрываем файл

            // открываем файл аккаунтов для добавления
            writer = new FileWriter(accountFileName, true);
            // записываем данные в файл
            writer.write(idAccount + ";" + id + ";" + accountNumber + separator);
            writer.close();// закрываем файл
            
            // создаём нового пользователя
            User u = new User(id, 2, username, login, password);
            u.setAcc(accountInit(u.getId()));
            users.add(u);// добавляем его в список
            
            return true;
        } catch (IOException ex) {
            try {
                writer.close();
            } catch (IOException ex1) {
                Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex1);
            }
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        

        
    }

    @Override
    public boolean removeAccount(String account) {
        boolean remove = false;
        User user = findUserByAccountNumber(account);// нашли пользователя
        try {
            /*
            удаление всех данных этого пользователя: показания, номер аккаунта,
            запись из таблицы пользователей
            */
            // создаём объект для произвольгого доступа к файлу показаний
            RandomAccessFile raf = new RandomAccessFile(readingFileName, "rwd");
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
        remove = users.remove(user);// удаляем найденного пользователя
        
        return remove;
    }

    private String generateAccount() {
        StringBuilder returnValue = new StringBuilder();
        for(int i = 0; i < 6; i++) {
            int number = (int) (Math.random() * 10);
            returnValue.append(number);
        }
        return returnValue.toString();
    }

    @Override
    public boolean addNewReading(String accountNumber, WaterReading waterReading) {
        // ищем пользователя по номеру аккаунта
        User user = findUserByAccountNumber(accountNumber);
        // получаем массив показаний пользователя, в цикле перебираем их и находим элементы где данных нет
        LocalDate localDate = null;// дата занесения предыдущих показаний
        boolean isHot = waterReading.isHot();// тип показаний
        for (int i = 0; i < user.getAcc().getReadings().length; i++) {
            Reading reading = user.getAcc().getReadings()[i];
            if (reading != null) {
                // если в элементе массива есть данные
                WaterReading wr = (WaterReading) reading;// приведение показаний
                if (wr.isHot() == isHot) {
                    // если тип показаний соответствует добавляемым
                    localDate = wr.getDate();// запоминаем дату внесения показаний
                }

            } else {
                // если данных уже нет, сравниваем дату внесения показаний с предыдущей
//                System.out.println("localDate equals = " + i + "-" + localDate.equals(waterReading.getDate()));
                if (!Objects.equals(localDate, waterReading.getDate())) {
                    // если даты отличаются, то добавляем данные в массив
                    user.getAcc().getReadings()[i] = waterReading;
                    return true;
                }

            }
        }
        return false;
    }

    /**
     * Возвращает массив всех зарегистрированных пользователей, кроме администраторов
     * @return массив всех зарегистрированных пользователей
     */
    @Override
    public ArrayList<User> getAllUsers() {
        return users;
    }
    
    private Object[][] getDataTable(String filename) {
        csvOperate.setFileName(filename);
        csvOperate.setHeader(true);// в первой строке находятся заголовки столбцов
        csvOperate.readData();// читаем данные
        return csvOperate.getData();// получаем массив
        
    }
}
