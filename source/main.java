package source;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class main {
    public static void main(String[] args) {
        List<Map<String,?>> Points = XmlExtractor.extractPlan("resources/grandPlan.xml");
        System.out.println("Hello world!");
        System.out.println(Points.get(0));
        System.out.println(Points.get(1));
    }
}
