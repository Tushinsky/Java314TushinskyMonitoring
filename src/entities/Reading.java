package entities;

import java.time.LocalDate;
import java.util.Objects;

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
        return date.toString() + " | " + measuring;
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
