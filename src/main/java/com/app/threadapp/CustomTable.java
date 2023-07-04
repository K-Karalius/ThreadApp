package com.app.threadapp;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class CustomTable extends TableView<Person> {

    private String[] columnTitles;
    public CustomTable(String[] columnTitles){
        this.columnTitles = columnTitles;
        setPrefSize(350, 500);
        setEditable(false);
        setUpTableColumns();
    }

    public void setUpTableColumns(){
        addColumn(columnTitles[0], "firstName");
        addColumn(columnTitles[1], "lastName");
        addColumn(columnTitles[2], "email");
        addColumn(columnTitles[3], "imageLink");
        addColumn(columnTitles[4], "ipAddress");
    }
    public void addColumn(String title, String property){
        TableColumn<Person, String> column = new TableColumn<>(title);
        column.setEditable(false);
        column.setSortable(false);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        getColumns().add(column);
    }
}
