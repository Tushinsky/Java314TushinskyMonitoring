package entities;

import java.time.LocalDate;

public abstract class Reading {
    private final LocalDate date;
    private final int measuring;

    protected Reading(LocalDate date, int measuring) {
        this.date = date;
        this.measuring = measuring;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getMeasuring() {
        return measuring;
    }

    @Override
    public String toString() {
        return "date=" + date +
                ", measuring=" + measuring;
    }
}
