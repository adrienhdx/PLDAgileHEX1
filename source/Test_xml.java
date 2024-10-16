package source;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import source.modeles.Noeud;
import source.modeles.Segment;

public class Test_xml {
    public static void main(String[] args) throws ParserConfigurationException, SAXException {
        try {
            File file = new File("resources/grandPlan.xml");
            // Chargement du fichier XML
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(file);
            document.getDocumentElement().normalize();
            // On récupère tous les noeuds "noeud" correspondant aux points de la carte
            NodeList nodeList = document.getElementsByTagName("noeud");

            Map<String, Noeud> noeudMap = new HashMap<>();

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    // On recupere les attributs pour creer chaque objet noeud
                    String id = element.getAttribute("id");
                    double latitude = Double.parseDouble(element.getAttribute("latitude"));
                    double longitude = Double.parseDouble(element.getAttribute("longitude"));

                    Noeud noeud = new Noeud(id, latitude, longitude);
                    noeudMap.put(id, noeud);
                    System.out.println(noeud);
                }
            }

            // On récupère tous les tronçons "troncon" correspondant aux segments de la carte
            NodeList segmentList = document.getElementsByTagName("troncon");

            for (int i = 0; i < segmentList.getLength(); i++) {
                Node node = segmentList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    // On recupere les attributs pour creer chaque objet segment: destination (noeud), longueur, nomRue et origine (noeud)
                    String destination = element.getAttribute("destination");
                    double longueur = Double.parseDouble(element.getAttribute("longueur"));
                    String nomRue = element.getAttribute("nomRue");
                    String origine = element.getAttribute("origine");

                    Noeud noeudOrigine = noeudMap.get(origine);
                    Noeud noeudDestination = noeudMap.get(destination);

                    Segment segment = new Segment(nomRue, noeudOrigine, noeudDestination, longueur);
                    System.out.println(segment);
                }
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
