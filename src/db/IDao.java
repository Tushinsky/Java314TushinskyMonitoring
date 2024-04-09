package db;

import entities.Account;
import entities.User;
import entities.WaterReading;
import java.util.ArrayList;

/**
 * Интерфейс, который реализует (для начала) база данных
 * В последствии, доступ будет реализован не через методы базы,
 * а через объекты-обертки, дающие доступ к конкретным ее сущностям со своими собственными публичными контрактами
 * */
public interface IDao {
    /**
     * Авторизация пользователя в программе
     * @param login имя пользователя (логин)
     * @param password пароль для входа
     * @return true - если такой пользователь зарегистрирован, иначе false
     */
    boolean authorize(String login, String password);
    
    /**
     * Ищет пользователя по номеру аккаунта
     * @param accountNumber номер аккаунта для поиска
     * @return user - пользователь, если найден
     */
    User findUserByAccountNumber(String accountNumber);
    
    /**
     * Ищет пользователя по имени (логину)
     * @param login имя (логин) пользователя
     * @return user - пользователь, если найден
     */
    User findUserByUsername(String login);

    /**
     * Добавляет нового пользователя
     * @param userName имя пользователя
     * @param login логин пользователя
     * @param password пароль пользователя
     * @return целое - код добавленной записи
     */
    int addNewUser(String userName, String login, String password);
    
    /**
     * Удаляет аккаунт пользователя
     * @param account номер удаляемого аккаунта
     * @return true в случае успеха, иначе возвращает false
     */
    boolean removeAccount(Account account);
    
    /**
     * Возвращает массив всех зарегистрированных пользоватвелей
     * @return массив пользователей
     */
    ArrayList<User> getAllUsers();
    
    /**
     * Добавляет новые показания по заданному аккаунту
     * @param accountNumber номер аккаунта для добавления
     * @param waterReading показания для добавления
     * @return целое - код добавленной записи
     */
    int addNewReading(String accountNumber, WaterReading waterReading);
    
    /**
     * Возвращает текущего пользователя, который подключился
     * @return currentUser - текущий пользователь
     */
    User getCurrentUser();
    
    /**
     * Удаляет показания из заданного аккаунта
     * @param waterReading показания для удаления
     * @return true в случае успеха, иначе возвращает false
     */
    boolean removeReading(WaterReading waterReading);
    
    /**
     * Изменяет показания в заданном аккаунте
     * @param waterReading показания, которые изменяются
     * @return true в случае успеха, иначе возвращает false
     */
    boolean changeReading(WaterReading waterReading);
}
