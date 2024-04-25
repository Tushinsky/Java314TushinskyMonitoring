package entities;

import mapping.IRoleConstants;
import java.util.Objects;

/**
 * Класс, представляющий пользователя, зарегистрированного в базе данных
 * @author Sergey
 */
public class User extends Entity {
    private final String login;// логин
    private final String username;// имя

    public String getUsername() {
        return username;
    }
    private final String password;// пароль
    private final String role;// права доступа
    private Account acc;// аккаунт
    
    /**
     * Создаёт новый объект пользователя
     * @param idNumber номер пользователя в очереди
     * @param id код пользователя
     * @param idrole код доступа (роль)
     * @param username имя пользовавтевля
     * @param login логин для входа (идентификации)
     * @param password пароль для входа
     */
    public User(int idNumber, int id, int idrole, String username, String login, String password) {
        super(id, idNumber);
        this.login = login;
        this.password = password;
        this.role = idrole == 1 ? IRoleConstants.ADMIN : IRoleConstants.USER;
        this.username = username;
    }

    /**
     * Возвращает пароль пользователя
     * @return пароль пользователя
     */
    public String getPassword() {
        return password;
    }

    /**
     * Возвращает права доступа пользователя
     * @return права доступа
     */
    public String getRole() {
        return role;
    }

    /**
     * Возвращает данные по аккаунту пользователя
     * @return данные по аккаунту
     */
    public Account getAcc() {
        return acc;
    }

    /**
     * Задает данные по аккаунту пользователя
     * @param acc данные по аккаунту
     */
    public void setAcc(Account acc) {
        this.acc = acc;
    }

    /**
     * Возвращает логин пользователя для доступа в базу данных
     * @return логин пользователя
     */
    public String getLogin() {
        return login;
    }

    @Override
    public String toString() {
        return "User{number=" + super.getIdNumber() + 
                ", id=" + super.getId() + 
                ", login=" + login + 
                ", username=" + username + 
                ", password=" + password + 
                ", role=" + role + 
                ", acc=" + acc + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final User other = (User) obj;
        if (this.getId() != other.getId()) {
            return false;
        }
        if (!Objects.equals(this.login, other.login)) {
            return false;
        }
        if (!Objects.equals(this.username, other.username)) {
            return false;
        }
        return Objects.equals(this.password, other.password);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + this.getId();
        hash = 53 * hash + Objects.hashCode(this.login);
        hash = 53 * hash + Objects.hashCode(this.username);
        hash = 53 * hash + Objects.hashCode(this.password);
        hash = 53 * hash + Objects.hashCode(this.acc);
        return hash;
    }

    
}
