package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        parseCSV(fileName);
        List<Employee> list = parseCSV(columnMapping, fileName);
        list.forEach(System.out::println);
        String data = listToJson(list);
        boolean isWriteToJson = writeString(data);
        if (isWriteToJson) {
            System.out.println("JSON-file saved.");
        } else {
            System.out.println("JSON-file not saved.");
        }
    }

// метод для чтения CSV в объекты

    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> staff = null;
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();

            staff = csv.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return staff;
    }

    //метод для чтения CSV в текст с выводом в консоль

    public static void parseCSV(String fileName) {
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                System.out.println(Arrays.toString(nextLine));
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
    }

// метод для получения строки с данными в формате JSON из объектов

    public static String listToJson(List<Employee> employees) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder
                .setPrettyPrinting()
                .create();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[\n");
        for (Employee employee : employees) {
            stringBuilder.append(gson.toJson(employee));
            stringBuilder.append(",\n");
        }
        stringBuilder.replace(stringBuilder.length() - 2, stringBuilder.length() - 1, "");
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

// метод для записи строки в JSON-файл

    public static boolean writeString(String data) {
        try (FileWriter writer = new FileWriter("data.json", false)) {
            writer.write(data);
            writer.flush();
            return true;
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return false;
    }
}