package entities;

import java.util.Objects;

public class User {
    private final int id;
    private final String login;
    private final String username;
    private final String password;
    private final String role;
    private Account acc;

    /**
     * Создаёт новый объект пользователя
     * @param id код пользователя
     * @param idrole код доступа (роль)
     * @param username имя пользователя
     * @param login логин для входа (идентификации)
     * @param password пароль для входа
     */
    public User(int id, int idrole, String username, String login, String password) {
        this.id = id;
        this.username = username;
        this.login = login;
        this.password = password;
        this.role = idrole == 1 ? IRoleConstants.ADMIN : IRoleConstants.USER;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public Account getAcc() {
        return acc;
    }

    public void setAcc(Account acc) {
        this.acc = acc;
    }

    public int getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    @Override
    public String toString() {
        return "User{" + "id=" + id + 
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
