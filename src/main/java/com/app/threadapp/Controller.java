package com.app.threadapp;

import com.opencsv.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
public class Controller implements Initializable{

    @FXML
    private AnchorPane anchorPane;
    private CustomTable uploadTable;

    private CustomTable resultTable;

    private String[] headers;
    private final ObservableList<Person> uploadedData = FXCollections.observableArrayList();

    private final ObservableList<Person> resultData = FXCollections.observableArrayList();

    private final String dirPath = "SavedData";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        upload();
        setUpUploadTable();
        setUpResultTable();

    }

    void setUpUploadTable(){
        uploadTable = new CustomTable(headers);
        anchorPane.getChildren().add(uploadTable);

        AnchorPane.setBottomAnchor(uploadTable, 5.0);
        AnchorPane.setLeftAnchor(uploadTable, 5.0);

        uploadTable.setItems(uploadedData);

    }

    void setUpResultTable(){
        resultTable = new CustomTable(headers);
        anchorPane.getChildren().add(resultTable);

        AnchorPane.setBottomAnchor(resultTable, 5.0);
        AnchorPane.setRightAnchor(resultTable, 5.0);

        resultTable.setItems(resultData);
    }

    void upload(){
        CSVReader reader;
        Person person;
        try {
            CSVParser parser = new CSVParserBuilder().withSeparator(';').build();
            reader = new CSVReaderBuilder(new FileReader("MOCK_DATA.csv")).withCSVParser(parser).build();

            String[] nextLine;
            if((nextLine = reader.readNext()) != null){
                headers = nextLine;
            }

            while ((nextLine = reader.readNext()) != null) {
                person = new Person();
                person.setData(nextLine);
                uploadedData.add(person);
            }
            reader.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void saveToFiles() {
        int threadCount = 10;
        Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> saveAndUpdate());
            threads[i].start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    void saveAndUpdate(){
        while (true) {
            Person person = null;
            synchronized (uploadedData) {
                if (!uploadedData.isEmpty()) {
                    person = uploadedData.remove(0);
                }
            }
            if (person != null) {
                saveToFile(person);
                resultData.add(person);
            } else {
                break;
            }

        }
    }
    void saveToFile(Person person){
        String fileName = "";
        fileName += person.getFirstName().length() >= 3 ? person.getFirstName().substring(0, 3) : person.getFirstName();
        fileName += person.getLastName().length() >= 3 ? person.getLastName().substring(0, 3) : person.getFirstName();

        String tempIP = person.getIpAddress();
        int dotIndex = tempIP.lastIndexOf('.');
        fileName += tempIP.substring(dotIndex + 1);

        fileName += ".csv";
        person.setFileName(fileName);

        File directory = new File(dirPath);
        if(!directory.exists()){
            directory.mkdir();
        }

        try{
            FileWriter fileWriter = new FileWriter(dirPath + File.separator + fileName);
            CSVWriterBuilder builder = new CSVWriterBuilder(fileWriter);
            builder.withSeparator(';');
            builder.withQuoteChar(CSVWriter.NO_QUOTE_CHARACTER);
            ICSVWriter csvWriter = builder.build();

            csvWriter.writeNext(headers);

            String[] info = {person.getFirstName(), person.getLastName(), person.getEmail(), person.getImageLink(), person.getIpAddress()};
            csvWriter.writeNext(info);

            csvWriter.close();
            fileWriter.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    void deleteAll() {
        ArrayList<Person> people = new ArrayList<>();
        for(Person person : resultData){
            deleteFile(person);
            people.add(person);
        }

        resultData.removeAll(people);
        uploadedData.addAll(people);
    }

    @FXML
    void deleteSelected() {
        int selectionIndex = resultTable.getSelectionModel().getSelectedIndex();
        if(selectionIndex != -1){
            Person person = resultTable.getItems().get(selectionIndex);
            deleteFile(person);

            resultData.remove(person);
            uploadedData.add(person);
        }
    }

    void deleteFile(Person person){
        File file = new File(dirPath + File.separator + person.getFileName());
        if (file.exists()) {
            file.delete();
        }
    }

}