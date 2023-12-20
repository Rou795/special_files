package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class Main {
    public static void main(String[] args) {
        List<Employee> list = parseXML("data.xml");
        list.forEach(System.out::println);
        String data = listToJson(list);
        boolean isWriteToJson = writeString(data);
        if (isWriteToJson) {
            System.out.println("JSON-file saved.");
        } else {
            System.out.println("JSON-file not saved.");
        }
        String json = readString("data.json");
        System.out.println("*******************************\n");
        jsonToList(json).forEach(System.out::println);
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

// метод для чтения XML и занесения информаици в объект. Запись делаю в цикле через конструктор.

    private static List<Employee> parseXML(String fileName) {
        List<Employee> employees = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(fileName));

            Node root = doc.getDocumentElement();

            NodeList nodeList = root.getChildNodes();

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (Node.ELEMENT_NODE == node.getNodeType()) {
                    Element personData = (Element) node;
                    Employee employee = new Employee(
                            Long.parseLong(personData
                                    .getElementsByTagName("id")
                                    .item(0)
                                    .getTextContent()),
                            personData
                                    .getElementsByTagName("firstName")
                                    .item(0)
                                    .getTextContent(),
                            personData
                                    .getElementsByTagName("lastName")
                                    .item(0)
                                    .getTextContent(),
                            personData
                                    .getElementsByTagName("country")
                                    .item(0)
                                    .getTextContent(),
                            Integer.parseInt(personData
                                    .getElementsByTagName("age")
                                    .item(0)
                                    .getTextContent()));
                    employees.add(employee);
                }
            }
        } catch (IOException | ParserConfigurationException | SAXException ex) {
            System.out.println(ex.getMessage());
        }
        return employees;
    }

    public static String listToJson(List<Employee> employees) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder
                .setPrettyPrinting()
                .create();
        String row = gson.toJson(employees);
        return row;
    }

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