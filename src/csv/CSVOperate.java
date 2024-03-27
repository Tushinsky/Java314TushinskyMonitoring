package csv;

import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CSVOperate {
    private String fileName;// имя файла csv
    private String separator;// разделитель полей
    private Object[][] data;// массив данных
    private Object[] columnName;// наименование столбцов
    private boolean header;// флаг наличия заголовков в первой строке
    private BufferedReader reader;

    /**
     * Конструктор класса по умолчанию. Задаёт в качестве разделителя полей ";"
     */
    public CSVOperate(){
        separator = ";";// принимаем разделитель по умолчанию ";"
    }

    /**
     * Конструктор класса. Принимает имя файла для чтения/записи и значение разделителя полей данных в указанном файле
     * @param filename имя файла для записи/чтения
     * @param separator значение разделителя полей
     */
    public CSVOperate(String filename, String separator){
        this.fileName = filename;
        this.separator = separator;
    }

    /**
     * Задаёт имя файла, содержащего данные
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Задаёт символ разделитель полей данных в файле
     * @param separator the separator to set
     */
    public void setSeparator(String separator) {
        this.separator = separator;
    }

    /**
     * Возвращает массив данных
     * @return the data
     */
    public Object[][] getData() {
        return data;
    }

    /**
     * Задаёт массив данных
     * @param data the data to set
     */
    public void setData(Object[][] data) {
        this.data = data;
    }

    /**
     * Чтение данных из файла
     */
    public void readData(){
        try {
            readFile();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CSVOperate.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Запись данных в файл
     */
    public void writeData(){
        try {
            writeFile();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CSVOperate.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Возвращает флаг наличия заголовков в первой строке данных
     * @return the header
     */
    public boolean isHeader() {
        return header;
    }

    /**
     * Задаёт флаг наличия заголовков в первой строке данных
     * @param header the header to set
     */
    public void setHeader(boolean header) {
        this.header = header;
    }

    /**
     * Чтение данных из указанного файла
     */
    private void readFile() throws FileNotFoundException{
        try {
            reader = new BufferedReader(new FileReader(fileName));

            // проверяем наличие в первой строке заголовков столбцов
            if(!header){
                // заголовков нет
                getCellData();// читаем данные
                // задаём заголовки
                columnName = new Object[data[1].length];
                for(int i = 0; i <columnName.length; i++){
                    int j = i + 1;
                    columnName[i] = (Object) ("A" + j);
                }
            } else{
                // заголовки есть
                String line;
                line = reader.readLine();// считываем первую строку данных
                columnName = (String[]) line.split(separator);// получаем заголовки, разбивая строку
                // читаем данные
                getCellData();
            }
        } catch (IOException ex) {
            Logger.getLogger(CSVOperate.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Запись данных в указанный файл
     */
    private void writeFile() throws FileNotFoundException{
        // создаём объект для записи
        try(PrintWriter writer = new PrintWriter(fileName)){
            if (header) {
                StringBuilder value = new StringBuilder();// начальное значение строки данных
                for (Object columnName1 : columnName) {
                    // выводим заголовки
                    value.append(columnName1).append(separator);
                }
                // обрезаем строку на один символ
                value = new StringBuilder(value.substring(0, value.length() - 1));
                writer.println(value);
            }
            // формируем данные для вывода
            for (Object[] data1 : data) {
                StringBuilder value = new StringBuilder();// начальное значение
                for (Object data11 : data1) {
                    value.append(data11).append(separator);
                }
                // обрезаем строку на один символ
                value = new StringBuilder(value.substring(0, value.length() - 1));
                writer.println(value);
            }
        }
    }

    /**
     * Возвращает наименование столбцов
     * @return the columnName
     */
    public Object[] getColumnName() {
        return columnName;
    }

    /**
     * Задает наименование столбцов
     * @param columnName the columnName to set
     */
    public void setColumnName(Object[] columnName) {
        this.columnName = columnName;
    }

    /**
     * Получение данных из файла
     */
    private void getCellData() throws IOException{
        ArrayList<Object> rowList = new ArrayList<>();
        String line;
        while((line = reader.readLine()) != null){
            // читаем файл пока не достигнут его конец
            String[] values = line.split(separator);// получаем массив значений
            ArrayList<Object> colList = new ArrayList<>();
            for (String value : values) {
                colList.add((Object) value);
            }
            Object[] cells = colList.toArray();
            rowList.add(cells);
        }
        data = new Object[rowList.size()][];
        for (int i = 0; i < data.length; i++) {
            data[i] = (Object[]) (Object) rowList.get(i);
        }
    }

}
