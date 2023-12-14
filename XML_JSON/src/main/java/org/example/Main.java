package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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