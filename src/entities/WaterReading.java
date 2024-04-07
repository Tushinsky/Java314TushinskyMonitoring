package entities;

import java.time.LocalDate;

public class WaterReading extends Reading{
    private final String type = "water";
    private final boolean isHot;

    public WaterReading(LocalDate date, int measuring, boolean isHot) {
        super(date, measuring);
        this.isHot = isHot;
    }

    public boolean isHot() {
        return isHot;
    }

    @Override
    public String toString() {
        return "WaterReading{" +
                super.toString() +
                ", type='" + type + '\'' +
                ", isHot=" + isHot +
                '}';
    }
}
