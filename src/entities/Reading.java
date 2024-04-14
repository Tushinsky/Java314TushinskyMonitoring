package entities;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Класс, реализующий объект показаний
 * @author Sergey
 */
public abstract class Reading {
    private final LocalDate date;// дата на которую передаются показания
    private final int measuring;// показание
    private final int id;// идентификатор записи в базе данных
    private final int idNumber;// порядковый номер сущности в списке (очереди)
    
    /**
     * Создаёт объект уже существующих показаний в базе
     * @param idNumber порядковый номер сущности в списке
     * @param id идентификатор записи в базе
     * @param date дата показаний
     * @param measuring числовое представление показаний
     */
    protected Reading(int idNumber, int id, LocalDate date, int measuring) {
        this.idNumber = idNumber;
        this.id = id;
        this.date = date;
        this.measuring = measuring;
    }

    /**
     * Создаёт объект новых показаний
     * @param date дата показаний
     * @param measuring числовое предствление показаний
     */
    protected Reading(LocalDate date, int measuring) {
        id = 0;
        idNumber = 0;
        this.date = date;
        this.measuring = measuring;
    }

    /**
     * Возвращает дату показаний
     * @return дата показаний в формате "гггг-мм-дд"
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Возвращает числовое представление показаний
     * @return целое число показаний
     */
    public int getMeasuring() {
        return measuring;
    }

    /**
     * Возвращает идентификатор показаний из базы данных
     * @return целое число - идентификатор показаний
     */
    public int getId() {
        return id;
    }

    /**
     * Возвращает порядковый номер сущности в списке
     * @return целое - порядковый номер
     */
    public int getIdNumber() {
        return idNumber;
    }

    @Override
    public String toString() {
        String string = " | " + date.toString() + " | " + measuring;
        String returnString;
        if(idNumber < 10) {
            returnString = "  ".concat(String.valueOf(idNumber)).concat(string);
        } else if(idNumber >= 10 && idNumber < 100) {
            returnString = " ".concat(String.valueOf(idNumber)).concat(string);
        } else {
            returnString = String.valueOf(idNumber).concat(string);
        }
        return returnString;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + Objects.hashCode(this.date);
        hash = 61 * hash + this.measuring;
        hash = 61 * hash + this.id;
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
        final Reading other = (Reading) obj;
        if (this.measuring != other.measuring) {
            return false;
        }
        if (this.id != other.id) {
            return false;
        }
        return Objects.equals(this.date, other.date);
    }
    
}
