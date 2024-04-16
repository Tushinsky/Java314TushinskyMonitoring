package entities;


import java.util.ArrayList;
import java.util.Objects;

/**
 * Аккаунт пользователя
 * @author Sergey
 */
public class Account {
    private final int id;// идентификационный код
    private final String accountNumber; //лицевой счет
    private final ArrayList<Reading> readings = new ArrayList<>();

    public Account(int id, String accountNumber) {
        this.id = id;
        this.accountNumber = accountNumber;
    }

    /**
     * Возвращает список показаний по данному аккаунту
     * @return список показаний
     */
    public ArrayList getReadings() {
        return readings;
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountNumber='" + accountNumber + '\'' +
                ", readings=" + readings +
                '}';
    }

    /**
     * Возвращает номер аккаунта
     * @return строка, содержащая номер аккаунта
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Возвращает идентификатор аккаунта из базы данных
     * @return целое число - идентификатор аккаунта
     */
    public int getId() {
        return id;
    }
    
    /**
     * Добавляет новые показания в список
     * @param reading новые показания
     */
    public void addReading(Reading reading) {
        readings.add(reading);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + this.id;
        hash = 37 * hash + Objects.hashCode(this.accountNumber);
        hash = 37 * hash + Objects.hashCode(this.readings);
        return hash;
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
        final Account other = (Account) obj;
        if (this.id != other.id) {
            return false;
        }
        if (!Objects.equals(this.accountNumber, other.accountNumber)) {
            return false;
        }
        return Objects.equals(this.readings, other.readings);
    }
}
