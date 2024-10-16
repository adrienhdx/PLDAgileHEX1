package source;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import source.modeles.Vertice;
import source.modeles.Segment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.io.FileInputStream;

public class XmlExtractor {
    /*public static void extractDemande(String file) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new FileInputStream(file));

            Element entrepot = (Element) document.getElementsByTagName("entrepot").item(0);
            String adresse = entrepot.getAttribute("adresse");
            String heureDepart = entrepot.getAttribute("heureDepart");
            System.out.println("Entrepot - Adresse: " + adresse + ", Heure de départ: " + heureDepart);

            NodeList livraisonList = document.getElementsByTagName("livraison");
            for (int i = 0; i < livraisonList.getLength(); i++) {
                Element livraison = (Element) livraisonList.item(i);

                // À remplacer par une classe Delivery
                String adresseEnlevement = livraison.getAttribute("adresseEnlevement"); // Classe Vertice
                String adresseLivraison = livraison.getAttribute("adresseLivraison"); // Classe Vertice
                int dureeEnlevement = Integer.parseInt(livraison.getAttribute("dureeEnlevement"));
                int dureeLivraison = Integer.parseInt(livraison.getAttribute("dureeLivraison"));
                System.out.println("Livraison - Adresse Enlevement: " + adresseEnlevement + ", Adresse Livraison: " + adresseLivraison + ", Durée Enlevement: " + dureeEnlevement + ", Durée Livraison: " + dureeLivraison);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
    public static List extractPlan(String file) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new FileInputStream(file));

            NodeList nodeList = document.getElementsByTagName("noeud");

            List returnList = new ArrayList<Map<String, ?>>();
            Map<String, Vertice> noeudMap = new HashMap<>();
            List<Vertice> vertices = new ArrayList<>();

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    // On recupere les attributs pour creer chaque objet noeud
                    String id = element.getAttribute("id");
                    double latitude = Double.parseDouble(element.getAttribute("latitude"));
                    double longitude = Double.parseDouble(element.getAttribute("longitude"));

                    Vertice noeud = new Vertice(id, latitude, longitude);
                    noeudMap.put(id, noeud);
                    vertices.add(noeud);
                    System.out.println(noeud);
                }
            }
            returnList.add(vertices);

            // On récupère tous les tronçons "troncon" correspondant aux segments de la carte
            NodeList segmentList = document.getElementsByTagName("troncon");
            List segments = new ArrayList();
            for (int i = 0; i < segmentList.getLength(); i++) {
                Node node = segmentList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    // On recupere les attributs pour creer chaque objet segment: destination (noeud), longueur, nomRue et origine (noeud)
                    String destination = element.getAttribute("destination");
                    double longueur = Double.parseDouble(element.getAttribute("longueur"));
                    String nomRue = element.getAttribute("nomRue");
                    String origine = element.getAttribute("origine");
                    Vertice noeudOrigine = noeudMap.get(origine);
                    Vertice noeudDestination = noeudMap.get(destination);
                    Segment segment = new Segment(nomRue, noeudOrigine, noeudDestination, longueur);
                    segments.add(segment);
                    System.out.println(segment);
                }
            }

            returnList.add(segments);
            return returnList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}