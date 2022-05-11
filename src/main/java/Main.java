import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvBadConverterException;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;

public class Main {

    public static List<Employee> parseCSV(String[] columnMapping, String fileNameCsv) {
        List<Employee> list = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(new FileReader(fileNameCsv))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            list = csv.parse();
        } catch (IllegalStateException | CsvBadConverterException | IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static String listToJson(List<Employee> list) {
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String json = gson.toJson(list, listType);
        return json;
    }

    public static void writeString(String json, String fileNameJson) {
        try (FileWriter file = new FileWriter(fileNameJson)) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Employee> parseXML(String fileNameXml) {
        List<Employee> list = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(fileNameXml));
            Node root = document.getDocumentElement();
            NodeList nodeList = root.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (Node.ELEMENT_NODE == node.getNodeType()) {
                    NodeList elementList = node.getChildNodes();
                    Element element = (Element) elementList;
                    long id = parseLong(element.getElementsByTagName("id").item(0).getTextContent());
                    String firstName = element.getElementsByTagName("firstName").item(0).getTextContent();
                    String lastName = element.getElementsByTagName("lastName").item(0).getTextContent();
                    String country = element.getElementsByTagName("country").item(0).getTextContent();
                    int age = parseInt(element.getElementsByTagName("age").item(0).getTextContent());
                    Employee employee = new Employee(id, firstName, lastName, country, age);
                    list.add(employee);
                }
            }
        } catch (NumberFormatException | DOMException | ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static String readString(String fileNameJson) {
        String json = "";
        try (BufferedReader br = new BufferedReader(new FileReader(fileNameJson))) {
            json = br.readLine();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return json;
    }

    public static List<Employee> jsonToList(String json) {
        List<Employee> list = new ArrayList<>();
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(json);
            JSONArray jsonObject = (JSONArray) obj;
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            for (Object jsOb : jsonObject) {
                Employee employee = gson.fromJson(gson.toJson(jsOb), Employee.class);
                list.add(employee);
            }
        } catch (JsonSyntaxException | ParseException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void hw1() {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileNameCsv = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileNameCsv);
        String json = listToJson(list);
        String fileNameJson = "data.json";
        writeString(json, fileNameJson);
    }

    public static void hw2() {
        String fileNameXml = "data.xml";
        List<Employee> list = parseXML(fileNameXml);
        String json = listToJson(list);
        String fileNameJson = "data2.json";
        writeString(json, fileNameJson);

    }

    public static void hw3() {
        String fileNameJson = "data.json";
        String json = readString(fileNameJson);
        List<Employee> list = jsonToList(json);
        list.forEach(System.out::println);

    }

    public static void main(String[] args) {
        hw1();
        hw2();
        hw3();
    }

}