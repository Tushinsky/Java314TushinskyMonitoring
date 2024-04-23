/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uicomponent;

import entities.User;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * Класс - рисовальщик для отображения информации в списке пользователей
 * @author Sergey
 */
public class UserCellRenderer implements ListCellRenderer<User>{

    @Override
    public Component getListCellRendererComponent(JList<? extends User> list, 
            User value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = new JLabel();
        label.setText(String.valueOf(value.getIdNumber()) + ". " + value.getUsername());
        label.setOpaque(true);
        label.setBorder(BorderFactory.createEtchedBorder(1));
        if(isSelected) {
            // если элемент выделен, меняем его фон на принятый по умолчанию
            label.setBackground(list.getSelectionBackground());
            label.setForeground(list.getSelectionForeground());
        } else {
            label.setBackground(list.getBackground());
            label.setForeground(list.getForeground());
        }
        return label;
    }
    
}
