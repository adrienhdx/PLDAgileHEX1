package source;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Test_xml {
    public static void main(String[] args) throws ParserConfigurationException, SAXException {
        try {
            File file = new File("petitPlan.xml");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(file);
            document.getDocumentElement().normalize();
            System.out.println("Racine :" + document.getDocumentElement().getNodeName());
            NodeList liste_noeud = document.getElementsByTagName("noeud");
            System.out.println("noeuds :" + liste_noeud);
//            System.out.println("----------------------------");
//            for (int temp = 0; temp < nList.getLength(); temp++) {
//                Node nNode = nList.item(temp);
//                System.out.println("\nCurrent Element :" + nNode.getNodeName());
//                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
//                    Element eElement = (Element) nNode;
//                    System.out.println("Employee id : " + eElement.getAttribute("id"));
//                    System.out.println("First Name : "
//                            + eElement.getElementsByTagName("firstname").item(0).getTextContent());
//                    System.out.println(
//                            "Last Name : " + eElement.getElementsByTagName("lastname").item(0).getTextContent());
//                    System.out.println(
//                            "Salary : " + eElement.getElementsByTagName("salary").item(0).getTextContent());
//                }
//            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
