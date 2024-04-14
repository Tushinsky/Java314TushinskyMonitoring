/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frame;

import entities.Reading;
import entities.WaterReading;
import java.awt.Color;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * Класс - рисовальщик ячеек списка покааний
 * @author Sergii.Tushinskyi
 */
public class ReadingCellRenderer implements ListCellRenderer<Reading>{
    private final Color lightRedColor = new Color(242, 125, 140, 255);// цвет ячеек для горячей воды
    private final Color lightBlueColor = new Color(113, 205, 238, 255);// цвет ячеек для холодной воды

    @Override
    public Component getListCellRendererComponent(JList<? extends Reading> jlist, 
            Reading e, int i, boolean bln, boolean bln1) {
        JLabel label = new JLabel();
        WaterReading wr = (WaterReading) e;// приведение типов
        // выделение ячейки фоном 
        if(wr.isHot()) {
            // если в ячейке находится объект горячей воды
            label.setBackground(lightRedColor);
        } else {
            // если в ячейке находится объект холодной воды
            label.setBackground(lightBlueColor);
        }
        if(bln) {
            // если элемент выделен, меняем его фон на принятый по умолчанию
            label.setBackground(jlist.getSelectionBackground());
            label.setForeground(jlist.getSelectionForeground());
        }
        label.setText(wr.toString());
        label.setOpaque(true);
        label.setBorder(BorderFactory.createEtchedBorder(1));
        return label;
    }
    
}
