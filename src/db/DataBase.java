package db;

import csv.CSVOperate;
import entities.Account;
import entities.User;
import entities.WaterReading;
import java.io.FileWriter;
import java.io.IOException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
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
    private User currentUser;//текущий пользователь, подключившейся к базе данных
    
    public DataBase() {
        csvOperate = new CSVOperate();
//        dbInit();
    }

    /**
     * Инициализация списка пользователей
     */
    private ArrayList<User> getDBInit() {
        ArrayList<User> Users = new ArrayList<>();
        // считываем таблицу зарегистрированных пользователей и заполняем массив
        Object[][] database = getDataTable(userFileName);// получаем массив
        // перебираем, получаем пользователей
        for(Object[] data : database) {
            Users.add(new User(Integer.parseInt(data[0].toString()), 
                    Integer.parseInt(data[1].toString()), (String)data[2], 
                    (String)data[3], (String)data[4]));
        }
        Users.forEach(u -> u.setAcc(accountInit(u.getId())));
        return Users;
    }

    /**
     * Инициализация данных по пользовательским аккаунтам
     * @param idUser код пользователя для получения данных по аккаунту
     * @return аккаунт пользователя с данными
     */
    private Account accountInit(int idUser) {
        Object[] data;// получаем массив
        data = getIDRecord(accountFileName, 1, String.valueOf(idUser));
        Account acc = null;
        if(data != null) {
            // создаём аккаунт по коду пользователя
            acc = new Account(Integer.parseInt(data[0].toString()), data[2].toString());
        }
        return acc;
    }
    
    /**
     * Инициализирует пользовательский аккаунт данными
     * @param acc аккаунт пользователя для заполнения данными
     */
    private void readingsInit(Account acc) {
        if(acc == null) {
            return;
        }
        // получаем данные по аккаунту
        Object[][] database = getDataTable(readingFileName);
        // перебираем в цикле
        for(Object[] db : database) {
            if(Objects.equals(db[1], String.valueOf(acc.getId()))) {
                // заполняем аккаунт пользователя данными
                LocalDate date = LocalDate.parse(db[4].toString());
                int count = Integer.parseInt(db[2].toString());
                boolean hot = !db[3].toString().equals("0");
                WaterReading reading = new WaterReading(date, count, hot);
        //                    System.out.println("reading:" + reading);
                acc.addReading(reading);
            }
        }
            
        
    }

    @Override
    public String toString() {
        return "DataBase{" +
                "users=" + users +
                '}';
    }

    @Override
    public boolean authorize(String login, String password) {
        // считываем таблицу зарегистрированных пользователей и заполняем массив
        Object[] data = getIDRecord(userFileName, 3, login);// получаем массив
        // проверяем
        if(data == null) {
            return false;
        }
        // если логин найден в базе данных, создаём пользователя
        currentUser = new User(Integer.parseInt(data[0].toString()), 
                Integer.parseInt(data[1].toString()), data[2].toString(), 
                data[3].toString(), data[4].toString());
        // получаем информацию по аккаунту
        Account acc = accountInit(currentUser.getId());
        readingsInit(acc);
        currentUser.setAcc(acc);
        return true;
    }

    @Override
    public User findUserByAccountNumber(String accountNumber) {
        Object[] data;
        data = getIDRecord(accountFileName, 2, accountNumber);
        int idAccount = Integer.parseInt(data[0].toString());
        if(idAccount == 0) {
            // если пользователь не найден
            return null;
        }
        // читаем таблицу пользователей
        User user;
        data = getIDRecord(userFileName, 0, data[1].toString());// получаем массив
        int idUser = Integer.parseInt(data[0].toString());
        // перебираем, получаем данные по показаниям
        Account acc = accountInit(idUser);
        readingsInit(acc);// заполняем аккаунт данными
        user = new User(idUser, Integer.parseInt(data[1].toString()), 
                data[2].toString(), data[3].toString(), data[4].toString());
        user.setAcc(acc);
        System.out.println("user:" + user);
        return user;// возвращаем пользователя

    }

    @Override
    public User findUserByUsername(String login) {
        // получаем данные из базы по логину
        Object[] data = getIDRecord(userFileName, 3, login);
        if(data == null) return null;
        // создаём пользователя
        User user = new User(Integer.parseInt(data[0].toString()), 
                Integer.parseInt(data[1].toString()), data[2].toString(), 
                login, data[4].toString());
        return user;// возвращаем его
        
    }

    @Override
    public boolean addNewUser(String username, String login, String password) {
        // проверяем существование пользователя с таким же логином
        ArrayList<User> Users = getDBInit();
        if(!Users.stream().noneMatch(u -> u.getLogin().equals(login))) {
            // если такой пользователь существует в списке, возвращаемся
            return false;
        }
        /*
        если пользователей с такими данными не найдено в списке, тогда
        добавляем нового пользователя в файл пользователей и в список
        */
        // получаем идентификатор последнего пользователя в списке и увеличиваем на 1
        User user = Users.get(Users.size() - 1);// получаем последнего пользователя
        int id = user.getId() + 1;// код нового пользователя
        int idAccount = user.getAcc().getId() + 1;// код нового аккаунта
        int number = Integer.parseInt(user.getAcc().getAccountNumber()) + 1;// номер аккаунта увеличиваем на 1
        String accountNumber = String.valueOf(number);

        // строка для добавления в файл
        String separator = System.getProperty("line.separator");
        String userString = id + ";2;" + username + ";" + login + ";" + 
                password + separator;
        String accountString = idAccount + ";" + id + ";" + accountNumber + separator;
        System.out.println("string: " + userString);
        if(writeDataToFile(userFileName, userString) && 
                writeDataToFile(accountFileName, accountString)) {
            // создаём нового пользователя
            User u = new User(id, 2, username, login, password);
            u.setAcc(accountInit(u.getId()));
            Users.add(u);// добавляем его в список
            currentUser = u;
            return true;
        }
        return false;
        
    }

    @Override
    public boolean removeAccount(String accountNumber) {
        Object[] data;// массив данных
        data = getIDRecord(accountFileName, 2, accountNumber);
        /*
        Получаем код аккаунта и код пользователя для удаления данных из таблиц
        Users, Account, Readings
        */
        Object idAccount = data[0];
        Object idUser = data[1];
        /*
        удаление всех данных этого пользователя: показания, номер аккаунта,
        запись из таблицы пользователей
        */
        Object[][] removeData;
        if((removeData = removeDataFromFile(userFileName, 0, idUser)) != null) {
            /*
            сначала удаляем данные из таблицы показаний и проверяем:
            если всё нормально удаляем дальше
            */
            System.out.println("removeUser: " + Arrays.toString(removeData));
            if((removeData = removeDataFromFile(accountFileName, 0, idAccount)) != null) {
                System.out.println("removeAccount: " + Arrays.toString(removeData));
                if((removeData = removeDataFromFile(readingFileName, 1, idAccount)) != null) {
                    System.out.println("removeReading: " + Arrays.toString(removeData));
                    return true;
                }
            } else {
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean addNewReading(String accountNumber, WaterReading waterReading) {
        // ищем код по номеру аккаунта
        Object[] data = getIDRecord(accountFileName, 2, accountNumber);
        int idAccount = Integer.parseInt(data[0].toString());
        if(idAccount == 0) {
            return false;
        }
        Object[][] database;
        // читаем таблицу показаний
        database = getDataTable(readingFileName);// получаем массив
        
        // из последнего элемента массива получаем код последней записи в таблице
        int idReading = Integer.parseInt(database[database.length - 1][0].toString());
        idReading++;// увеличиваем код записи
        
        // формируем строку для добавления в таблицу
        String separator = System.getProperty("line.separator");
        String isHot = waterReading.isHot() == true ? "1" : "0";
        String str = String.valueOf(idReading) + ";" +
                String.valueOf(idAccount) + ";" +
                String.valueOf(waterReading.getMeasuring()) + ";" + isHot + ";" +
                waterReading.getDate().toString() + ";" +
                LocalDate.now().toString() + separator;
        System.out.println("str=" + str);
        return writeDataToFile(readingFileName, str);
    }

    /**
     * Возвращает массив всех зарегистрированных пользователей, кроме администраторов
     * @return массив всех зарегистрированных пользователей
     */
    @Override
    public ArrayList<User> getAllUsers() {
        ArrayList<User> returnList = new ArrayList<>();
        // считываем таблицу зарегистрированных пользователей и заполняем массив
        Object[][] dataTable = getDataTable(userFileName);// получаем массив
        // перебираем, получаем пользователей
        for(Object[] data : dataTable) {
            if(Integer.parseInt(data[1].toString()) == 2) {
                User user = new User(Integer.parseInt(data[0].toString()), 
                    Integer.parseInt(data[1].toString()), (String)data[2], 
                    (String)data[3], (String)data[4]);// создаём пользователя
                Account acc = accountInit(user.getId());// создаём аккаунт
                readingsInit(acc);// заполняем данными
                user.setAcc(acc);// передаём пользователю
                System.out.println("user:" + user.getUsername() + "; readings:" + 
                        user.getAcc().getReadings());
                returnList.add(user);
            }
        }
        return returnList;
    }
    
    private Object[][] getDataTable(String filename) {
        csvOperate.setFileName(filename);
        csvOperate.setHeader(true);// в первой строке находятся заголовки столбцов
        csvOperate.readData();// читаем данные
        return csvOperate.getData();// получаем массив
        
    }

    @Override
    public User getCurrentUser() {
        return currentUser;
    }
    
    private boolean writeDataToFile(String filename, String string) {
        try (
                FileWriter writer = new FileWriter(filename, true);// объект для записи в файл
        ) {
            try {
                writer.write(string);
                writer.close();
                return true;
                // закрываем файл
            } catch (IOException ex) {
                Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    private Object[] getIDRecord(String filename, int col, String template) {
        Object[][] database;
        database = getDataTable(filename);// получаем массив
        
        // перебираем, получаем код аккаунта
        for(Object[] data : database) {
            // проверяем соответствие значения в указанном столбце шаблону
            if(!data[col].toString().equals(template)) {
                continue;
            }
            // если шаблон в строке найден, возвращаем данные
            return data;
            
        }
        return null;
    }
    
    /**
     * Удаляет данные из указанного файла.
     * @param filename имя файла для удаления
     * @param col номер столбца для поиска строки-шаблона
     * @param template строка-шаблон, содержащаяся в удаляемой строке данных
     * @return двухмерный массив данных, которые были удалены, или null, если строка-шаблон
     * не была найдена
     */
    private Object[][] removeDataFromFile(String filename, int col, Object template) {
        Object[][] database = getDataTable(filename);// получаем массив данных из файла
        ArrayList<Object[]> writeData = new ArrayList<>();// список данных для записи
        ArrayList<Object[]> data = new ArrayList<>();// список данных для возврата
        for(Object[] db : database) {
            // сравниваем данные
            if(Objects.equals(db[col], template)) {
                data.add(db);// добавляем в список удаления
            } else {
                writeData.add(db);// добавляем в список для перезаписи файла
            }
        }
        // проверяем данные
        if(!data.isEmpty()) {
            // формируем данные для возврата
            database = new Object[data.size()][];
            for(int i = 0; i < data.size(); i++) {
                database[i] = data.get(i);
            }
            // перезаписываем файл
            reWriteFile(filename, writeData);
            return database;
        }
        return null;
        
    }
    
    /**
     * Перезаписывает файл с указанными данными
     * @param fileName имя файла для перезаписи
     * @param writeData массив данных для записи
     */
    private void reWriteFile(String fileName, ArrayList<Object[]> writeData) {
        if(!writeData.isEmpty()) {
            
            // формируем данные для записи в файл
            Object[][] database = new Object[writeData.size()][];
            for(int i = 0; i < writeData.size(); i++) {
                database[i] = writeData.get(i);
            }
            csvOperate.setFileName(fileName);
            csvOperate.setData(database);
            csvOperate.writeData();
        }

    }
}
