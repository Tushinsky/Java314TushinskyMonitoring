/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uicomponent;

import entities.Reading;
import javax.swing.JList;

/**
 * Создаёт список - элемент пользовательского интерфейса, содержищий объекты,
 * представляющие собой экземпляры класса Reading
 * @author Sergii.Tushinskyi
 */
public class ReadingListComponent extends JList<Reading> {
    
    public ReadingListComponent() {
        super();
        super.setCellRenderer(new ReadingCellRenderer());// класс рисовальщик ячеек
        super.setFixedCellHeight(20);// высота ячейки списка
        super.setFixedCellWidth(130);// ширина ячейки списка
        super.setAutoscrolls(true);// автоматическое отображение полос прокрутки
    }
    
}
