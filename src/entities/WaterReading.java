package entities;

import java.time.LocalDate;
import java.util.Objects;

public class WaterReading extends Reading{
    private final String type = "water";
    private final boolean isHot;

    public WaterReading(int id, LocalDate date, int measuring, boolean isHot) {
        super(id, date, measuring);
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
