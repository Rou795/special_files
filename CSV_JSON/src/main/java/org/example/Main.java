package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvValidationException;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        File test = new File(fileName);
        System.out.println(test.getAbsolutePath());
        parseCSV(fileName);
        List<Employee> list = parseCSV(columnMapping, fileName);
        list.forEach(System.out::println);
        String data = listToJson(list);
        System.out.println(data);
        boolean isWriteToJson = writeString(data);
        if (isWriteToJson) {
            System.out.println("JSON-file saved.");
        } else {
            System.out.println("JSON-file not saved.");
        }
        System.out.println("*******************************\n");
        String jsonRow = readString("data.json");
        jsonToList(jsonRow).forEach(System.out::println);
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
                System.out.println(nextLine.length);
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
        String row = gson.toJson(employees);
        return row;
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

    // метод для чтения json и выдачи данных в виде строки

    private static String readString(String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))) {
            String row;
            while ((row = bufferedReader.readLine()) != null) {
                stringBuilder.append(row);
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return stringBuilder.toString();
    }

// метод для получения списка объектов из json-строки

    private static List<Employee> jsonToList(String data) {
        List<Employee> list = new ArrayList<>();
        JSONParser parser = new JSONParser();
        try {
            Object object = parser.parse(data);
            JSONArray jsonArray = (JSONArray) object;
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            for (Object element : jsonArray) {
                list.add(gson.fromJson(element.toString(), Employee.class));
            }
        } catch (ParseException ex) {
            ex.getMessage();
        }
        return list;
    }
}