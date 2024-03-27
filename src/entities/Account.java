package entities;


import java.util.Arrays;

public class Account {
    private final int id;// идентификационный код
    private final int id_user;// код пользователя аккаунта
    private final String accountNumber; //лицевой счет
    private final Reading[] readings = new Reading[500];

    public Account(int id, int id_user, String accountNumber) {
        this.id = id;
        this.id_user = id_user;
        this.accountNumber = accountNumber;
    }

    public Reading[] getReadings() {
        return readings;
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountNumber='" + accountNumber + '\'' +
                ", readings=" + Arrays.toString(readings) +
                '}';
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public int getId() {
        return id;
    }
    
}
