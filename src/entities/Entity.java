/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

/**
 *
 * @author Sergii.Tushinskyi
 */
public class Entity {
    private final int id;// идентификатор записи в базе данных
    private final int idNumber;// порядковый номер сущности в списке (очереди)
    private final String name;
    
    public Entity(int id, int idNumber) {
        this.id = id;
        this.idNumber = idNumber;
        this.name = "";
    }

    
    /**
     * Возвращает идентификатор из базы данных
     * @return целое число - идентификатор
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
    
}
