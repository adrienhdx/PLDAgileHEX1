package source;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import source.modeles.Vertice;

public class Test_xml {
    public static void main(String[] args) throws ParserConfigurationException, SAXException {
        try {
            File file = new File("petitPlan.xml");
            // Chargement du fichier XML
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(file);
            document.getDocumentElement().normalize();
            // On récupère tous les noeuds "noeud" correspondant aux points de la carte
            NodeList nodeList = document.getElementsByTagName("noeud");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    // On recupere les attributs pour creer chaque objet noeud
                    String id = element.getAttribute("id");
                    double latitude = Double.parseDouble(element.getAttribute("latitude"));
                    double longitude = Double.parseDouble(element.getAttribute("longitude"));

                    Vertice noeud = new Vertice(id, latitude, longitude);
                    System.out.println(noeud);
                }
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
