package db;

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
     * @return true - если добавление удачно, иначе возвращает false
     */
    boolean addNewUser(String userName, String login, String password);
    
    /**
     * Удаляет аккаунт пользователя
     * @param account номер удаляемого аккаунта
     * @return true в случае успеха, иначе возвращает false
     */
    boolean removeAccount(String account);
    
    /**
     * Возвращает массив всех зарегистрированных пользоватвелей
     * @return массив пользователей
     */
    ArrayList<User> getAllUsers();
    
    /**
     * Добавляет новые показания по заданному аккаунту
     * @param accountNumber номер аккаунта для добавления
     * @param waterReading показания для добавления
     * @return true в случае успеха, иначе возвращает false
     */
    boolean addNewReading(String accountNumber, WaterReading waterReading);
}
