package entities;

import java.util.Objects;

public class User {
    private final int id;
    private final String login;
    private final String username;
    private final String password;
    private final String role;
    private Account acc;
    private final int idNumber;

    /**
     * Создаёт новый объект пользователя
     * @param idNumber номер пользователя в очереди
     * @param id код пользователя
     * @param idrole код доступа (роль)
     * @param username имя пользователя
     * @param login логин для входа (идентификации)
     * @param password пароль для входа
     */
    public User(int idNumber, int id, int idrole, String username, String login, String password) {
        this.idNumber = idNumber;
        this.id = id;
        this.username = username;
        this.login = login;
        this.password = password;
        this.role = idrole == 1 ? IRoleConstants.ADMIN : IRoleConstants.USER;
    }

    /**
     * Возвращает имя пользователя
     * @return имя пользователя
     */
    public String getUsername() {
        return username;
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
     * Возвращает индентификатор пользователя в базе данных
     * @return идентификатор пользователя
     */
    public int getId() {
        return id;
    }

    /**
     * Возвращает логин пользователя для доступа в базу данных
     * @return логин пользователя
     */
    public String getLogin() {
        return login;
    }

    /**
     * Возвращает номер пользователя в очереди
     * @return номер пользователя
     */
    public int getIdNumber() {
        return idNumber;
    }

    @Override
    public String toString() {
        return "User{number=" + idNumber + 
                ", id=" + id + 
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
        if (this.id != other.id) {
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
        hash = 53 * hash + this.id;
        hash = 53 * hash + Objects.hashCode(this.login);
        hash = 53 * hash + Objects.hashCode(this.username);
        hash = 53 * hash + Objects.hashCode(this.password);
        hash = 53 * hash + Objects.hashCode(this.acc);
        return hash;
    }

    
}
