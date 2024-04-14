package db;

import csv.CSVOperate;
import entities.Account;
import entities.IRoleConstants;
import entities.User;
import entities.WaterReading;
import java.io.FileNotFoundException;
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
    private final CSVOperate csvOperate;
    private final String accountFileName = "readingsDB/Account.csv";
    private final String readingFileName = "readingsDB/Readings.csv";
    private final String userFileName = "readingsDB/Users.csv";
    private User currentUser;//текущий пользователь, подключившейся к базе данных
    
    public DataBase() {
        csvOperate = new CSVOperate();
        csvOperate.setCharSet("UTF-8");// задаём кодировку
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
        int idNumber = 1;// начальный номер в очереди
        for(Object[] data : database) {
            Users.add(new User(idNumber, Integer.parseInt(data[0].toString()), 
                    Integer.parseInt(data[1].toString()), (String)data[2], 
                    (String)data[3], (String)data[4]));
            idNumber++;// увеличиваем номер
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
        int idNumber = 1;// счётчик сущностей
        // перебираем в цикле
        for(Object[] db : database) {
            if(Objects.equals(db[1], String.valueOf(acc.getId()))) {
                // заполняем аккаунт пользователя данными
                int id = Integer.parseInt(db[0].toString());
                LocalDate date = LocalDate.parse(db[4].toString());
                int count = Integer.parseInt(db[2].toString());
                boolean hot = !db[3].toString().equals("0");
                WaterReading reading = new WaterReading(idNumber, id, date, count, hot);
        //                    System.out.println("reading:" + reading);
                acc.addReading(reading);
                idNumber++;// увеличиваем счётчик
            }
        }
            
        
    }

    @Override
    public String toString() {
        return "DataBase{" +
                "users=" + getAllUsers() +
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
        currentUser = new User(1, Integer.parseInt(data[0].toString()), 
                Integer.parseInt(data[1].toString()), data[2].toString(), 
                data[3].toString(), data[4].toString());
        // получаем информацию по аккаунту
        Account acc = accountInit(currentUser.getId());
        readingsInit(acc);
        currentUser.setAcc(acc);
        return true;
    }

    @Override
    public int addNewUser(String username, String login, String password) {
        // проверяем существование пользователя с таким же логином
        ArrayList<User> Users = getDBInit();
        if(!Users.stream().noneMatch(u -> u.getLogin().equals(login))) {
            // если такой пользователь существует в списке, возвращаемся
            return 0;
        }
        /*
        если пользователей с такими данными не найдено в списке, тогда
        добавляем нового пользователя в файл пользователей и в список
        */
        Users = getAllUsers();// список всех простых пользователей
        
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
            User u = new User(1, id, 2, username, login, password);
            u.setAcc(accountInit(u.getId()));
            Users.add(u);// добавляем его в список
            currentUser = u;
            return id;
        }
        return 0;
        
    }

    @Override
    public boolean removeAccount(Account account) {
        Object[] data;// массив данных
        data = getIDRecord(accountFileName, 2, String.valueOf(account.getId()));
        /*
        Получаем код аккаунта и код пользователя для удаления данных из таблиц
        Users, Account, Readings
        */
        Object idAccount = account.getId();
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
    public int addNewReading(String role, String accountNumber, WaterReading waterReading) {
        // ищем код по номеру аккаунта
        Object[] data = getIDRecord(accountFileName, 2, accountNumber);
        int idAccount = Integer.parseInt(data[0].toString());
        
        Object[][] database;
        // читаем таблицу показаний
        database = getDataTable(readingFileName);// получаем массив
        /*
        проверяем права доступа и новые показания на соответствие заданным условиям:
        если операцию проводит обычный пользователь, проводим проверку,
        если оперецию проводит администратор, проверку опускаем
        */
        if(role.equals(IRoleConstants.USER)) {
            if(testNewReading(database, idAccount, waterReading) == false) {
                return 0;// если не соответствуют, возвращаем код 0
            }
        }
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
        if(writeDataToFile(readingFileName, str)) {
            return idReading;
        }
        return 0;
    }

    @Override
    public ArrayList<User> getAllUsers() {
        ArrayList<User> returnList = new ArrayList<>();
        // считываем таблицу зарегистрированных пользователей и заполняем массив
        Object[][] dataTable = getDataTable(userFileName);// получаем массив
        // перебираем, получаем пользователей
        int idNumber = 1;
        for(Object[] data : dataTable) {
            if(Integer.parseInt(data[1].toString()) == 2) {
                User user = new User(idNumber, Integer.parseInt(data[0].toString()), 
                    Integer.parseInt(data[1].toString()), (String)data[2], 
                    (String)data[3], (String)data[4]);// создаём пользователя
                Account acc = accountInit(user.getId());// создаём аккаунт
                readingsInit(acc);// заполняем данными
                user.setAcc(acc);// передаём пользователю
                System.out.println("user:" + user.getUsername() + "; readings:" + 
                        user.getAcc().getReadings());
                returnList.add(user);
                idNumber++;// увеличиваем счётчик
            }
        }
        return returnList;
    }
    
    /**
     * Считывает и возвращает данные из выбранного файла
     * @param filename имя файла для чтения
     * @return двухмерный массив объектов типа Object
     */
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
    
    /**
     * Возвращает результат записи данных в указанный файл
     * @param filename имя файла для записи
     * @param string строка, содержащая данные для записи
     * @return true в случае успеха, иначе false
     */
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
    
    /**
     * Возвращает массив данных, содержащий код записи из указанного файла
     * @param filename имя файла для поиска
     * @param col номер столбца для поиска
     * @param template шаблон поиска
     * @return одномерный массив типа Object, если найден, иначе null
     */
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
            if(Objects.equals(db[col].toString(), template.toString())) {
                data.add(db);// добавляем в список удаления
                System.out.println("db: " + Arrays.toString(db));
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
            if(reWriteFile(filename, writeData)) {
                return database;
            }
        }
        return null;
        
    }
    
    /**
     * Перезаписывает файл с указанными данными
     * @param fileName имя файла для перезаписи
     * @param writeData массив данных для записи
     */
    private boolean reWriteFile(String fileName, ArrayList<Object[]> writeData) {
        if(!writeData.isEmpty()) {
            
            // формируем данные для записи в файл
            Object[][] database = new Object[writeData.size()][];
            for(int i = 0; i < writeData.size(); i++) {
                database[i] = writeData.get(i);
            }
            csvOperate.setFileName(fileName);
            csvOperate.setData(database);
            try {
                csvOperate.writeData();
                return true;
            } catch (FileNotFoundException ex) {
                Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;

    }
    
    /**
     * Проверяет переданные показания на соответствие заданным условиям:
     * период и дата ввода новых показаний должны быть больше уже имеющихся в 
     * базе данных
     * @param data двухмерный массив данных, содержащий показания
     * @param idAccount код аккаунта, по которому выбирабтся показания
     * @param wr показания, которые вносятся
     * @return true если условия выполнены, иначе false
     */
    private boolean testNewReading(Object[][] data, int idAccount, 
            WaterReading wr) {
        // массив, куда складываем показания по заданному коду аккаунта
        ArrayList<Object[]> listReading = new ArrayList<>();
        for(Object[] dat : data) {
            if(Objects.deepEquals(Integer.parseInt(String.valueOf(dat[1])), idAccount)) {
                listReading.add(dat);
            }
        }
        System.out.println("list: " + listReading);
        if(listReading.isEmpty()) {
            /*
            если список пустой, показаний по данному аккаунту нет, значит
            новые показания можно добавлвять
            */
            return true;
        }
        // обрабатыааем полученный список
        int hot = wr.isHot() ? 1 : 0;
        // фильтруем наш список в массив показаний по нужному признаку
        Object[] array = listReading.stream()
                .filter(dat -> Objects.deepEquals(Integer.
                        parseInt(String.valueOf(dat[3])), hot)).toArray();
        // берём последний элемент и сравниваем дату внесения данных
        Object[] dat = (Object[]) array[array.length - 1];
        // дата внесения показаний содержится в последнем элементе
        LocalDate date = LocalDate.parse(dat[dat.length - 1].toString());
        // сравниваем месяцы из этой даты и из даты новых показаний
        if(date.getMonthValue() == LocalDate.now().getMonthValue()) {
            // если месяцы совпадают (в этом месяце показания уже вносились)
            return false;
        } else {
            // проверим ещё возможное совпадение с датой предыдущих показаний
            int m = wr.getDate().getMonthValue();
            for(Object a : array) {
                // преобразуем к массиву
                dat = (Object[]) a;
                // вытаскиваем из даты месяц
                int month = LocalDate.parse(dat[dat.length - 2]
                        .toString()).getMonthValue();
                // сравниваем
                if(month == m) {
                    return false;
                }
            }
        }
        return true;
        
        
    }

    @Override
    public boolean removeReading(WaterReading waterReading) {
        // в качестве шаблона для поиска удаляемых записей используем код показаний
        Object template = waterReading.getId();
        System.out.println("reading: " + waterReading.toString());
        // получаем данные, возвращаемые в результате удаления
        Object[][] data;
        data = removeDataFromFile(readingFileName, 0, template);
        
        return (data != null);
    }

    @Override
    public boolean changeReading(WaterReading waterReading) {
        Object[][] database = getDataTable(readingFileName);// получаем массив данных из файла
        ArrayList<Object[]> writeData = new ArrayList<>();// список данных для записи
        Object template = waterReading.getId();// шаблон, по которому ищем запись
        Object isHot = waterReading.isHot() == true ? 1 : 0;
        for(Object[] db : database) {
            // сравниваем данные
            if(Objects.equals(db[0].toString(), template.toString())) {
                Object[] data = {waterReading.getId(), db[1], 
                    waterReading.getMeasuring(), isHot, waterReading.getDate(), 
                    db[db.length - 1]};
                writeData.add(data);// добавляем в список удаления
                System.out.println("db: " + Arrays.toString(db));
            } else {
                writeData.add(db);// добавляем в список для перезаписи файла
            }
        }
        
        return reWriteFile(readingFileName, writeData);
    }
}
