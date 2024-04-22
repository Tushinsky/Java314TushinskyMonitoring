package entities;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Класс, расширяющий объект показаний Reading
 * @author Sergey
 */
public class WaterReading extends Reading{
    private final String type = "water";
    private final boolean isHot;// флаг горячей или холодной воды
    
    /**
     * Создаёт объект уже существующих показаний в базе
     * @param idNumber порядковый номер сущности в списке
     * @param id идентификатор записи в базе
     * @param date дата показаний
     * @param measuring числовое представление показаний
     * @param isHot флаг горячей или холодной воды
     */
    public WaterReading(int idNumber, int id, LocalDate date, int measuring, boolean isHot) {
        super(idNumber, id, date, measuring);
        this.isHot = isHot;
    }
    
    /**
     * Создаёт объект новых показаний
     * @param date дата показаний
     * @param measuring числовое предствление показаний
     * @param isHot флаг горячей или холодной воды
     */
    public WaterReading(LocalDate date, int measuring, boolean isHot) {
        super(date, measuring);
        this.isHot = isHot;
    }
    

    public boolean isHot() {
        return isHot;
    }
//
//    @Override
//    public String toString() {
//        return "WaterReading{" +
//                super.toString() +
//                ", type='" + type + '\'' +
//                ", isHot=" + isHot +
//                '}';
//    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.type);
        hash = 97 * hash + (this.isHot ? 1 : 0);
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
        final WaterReading other = (WaterReading) obj;
        if (this.isHot != other.isHot) {
            return false;
        }
        return Objects.equals(this.type, other.type);
    }
    
}
