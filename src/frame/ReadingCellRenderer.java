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

    @Override
    public Component getListCellRendererComponent(JList<? extends Reading> jlist, 
            Reading e, int i, boolean bln, boolean bln1) {
        JLabel label = new JLabel();
        WaterReading wr = (WaterReading) e;// приведение типов
        if(wr.isHot()) {
            // если в ячейке находится объект горячей воды
            label.setBackground(new Color(250, 150, 150));
        } else {
            // если в ячейке находится объект холодной воды
            label.setBackground(new Color(150, 150, 250));
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
