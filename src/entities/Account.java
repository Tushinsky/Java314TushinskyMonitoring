package entities;


import java.util.ArrayList;

public class Account {
    private final int id;// идентификационный код
    private final String accountNumber; //лицевой счет
    private final ArrayList<Reading> readings = new ArrayList<>();

    public Account(int id, String accountNumber) {
        this.id = id;
        this.accountNumber = accountNumber;
    }

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

    public String getAccountNumber() {
        return accountNumber;
    }

    public int getId() {
        return id;
    }
    
    public void addReading(Reading reading) {
        readings.add(reading);
    }
}
