/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uicomponent;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;

/**
 * Верификатор, проверяющий корректность содержимого форматированных
 * текстовых полей. В случае некорректного ввода данных при попытке
 * перейти на другое поле фокус останется в текущем поле ввода.
 * @author Sergey
 */
public class FormattedTextFieldFerifier extends InputVerifier {

    @Override
    public boolean verify(JComponent input) {
        JFormattedTextField field = (JFormattedTextField) input;
        return field.isEditValid();
    }
    
}
