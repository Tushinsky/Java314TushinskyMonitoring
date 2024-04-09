package entities;

import java.time.LocalDate;

public abstract class Reading {
    private final LocalDate date;
    private final int measuring;
    private final int id;

    protected Reading(int id, LocalDate date, int measuring) {
        this.id = id;
        this.date = date;
        this.measuring = measuring;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getMeasuring() {
        return measuring;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "date=" + date +
                ", measuring=" + measuring;
    }
}
