package source;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import source.model.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import java.io.FileInputStream;

public class XmlExtractor {
    public static List<String> extractEntrepot(String file) {
        List<String> entrepotList = new ArrayList<>();
        try{
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new FileInputStream(file));

            Element entrepot = (Element) document.getElementsByTagName("entrepot").item(0);
            String adresse = entrepot.getAttribute("adresse");
            String heureDepart = entrepot.getAttribute("heureDepart");
            entrepotList.add(adresse);
            entrepotList.add(heureDepart);
            return entrepotList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Delivery> extractDemande(String file, Map<String, Vertex> verticesMap) {
        try {

            List<Delivery> deliveries = new ArrayList<>();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new FileInputStream(file));

            NodeList livraisonList = document.getElementsByTagName("livraison");
            for (int i = 0; i < livraisonList.getLength(); i++) {
                Element livraison = (Element) livraisonList.item(i);

                String idEnlevement = livraison.getAttribute("adresseEnlevement");
                Vertex adresseEnlevement = verticesMap.get(idEnlevement);

                String idLivraison = livraison.getAttribute("adresseLivraison");
                Vertex adresseLivraison = verticesMap.get(idLivraison);

                int dureeEnlevement = Integer.parseInt(livraison.getAttribute("dureeEnlevement"));
                int dureeLivraison = Integer.parseInt(livraison.getAttribute("dureeLivraison"));

                Delivery delivery = new Delivery(adresseEnlevement, adresseLivraison, dureeEnlevement, dureeLivraison, DeliveryState.PENDING);
                deliveries.add(delivery);
            }

            return deliveries;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Vertex> extractDeliveryVertices(List<Delivery> deliveries) {
        try {
            List<Vertex> vertices = new ArrayList<>();
            for (Delivery delivery : deliveries) {
                if (!vertices.contains(delivery.getPickUpPt()))
                    vertices.add(delivery.getPickUpPt());
                if (!vertices.contains(delivery.getDeliveryPt()))
                    vertices.add(delivery.getDeliveryPt());
            }
            return vertices;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map<String, Vertex> extractPlan(String file) {
        try {

            // file
            DocumentBuilderFactory factoryMap = DocumentBuilderFactory.newInstance();
            DocumentBuilder builderMap = factoryMap.newDocumentBuilder();
            Document documentMap = builderMap.parse(new FileInputStream(file));

            NodeList nodesMap = documentMap.getElementsByTagName("noeud");

            Map<String, Vertex> noeudMap = new HashMap<>();

            for (int i = 0; i < nodesMap.getLength(); i++) {
                Node node = nodesMap.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    // On recupere les attributs pour creer chaque objet noeud
                    String id = element.getAttribute("id");
                    double latitude = Double.parseDouble(element.getAttribute("latitude"));
                    double longitude = Double.parseDouble(element.getAttribute("longitude"));

                    Vertex noeud = new Vertex(id, latitude, longitude);
                    noeudMap.put(id, noeud);
                }
            }
            return noeudMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Vertex> extractVertices(Map<String, Vertex> verticesMap, List<Vertex> vertexDelivery) {
        try {
            List<Vertex> vertices = new ArrayList<>();
            for (Map.Entry<String, Vertex> entry : verticesMap.entrySet()) {
                Vertex vert = entry.getValue();

                if (vertexDelivery.contains(vert))
                    vertices.add(vert);
            }
            return vertices;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Segment> extractTroncon(String mapPlan, Map<String, Vertex> verticesMap, List<Vertex> vertexDelivery) {
        try {

            // mapPlan
            DocumentBuilderFactory factoryMap = DocumentBuilderFactory.newInstance();
            DocumentBuilder builderMap = factoryMap.newDocumentBuilder();
            Document documentMap = builderMap.parse(new FileInputStream(mapPlan));

            // On récupère tous les tronçons "troncon" correspondant aux segments de la carte
            NodeList segmentList = documentMap.getElementsByTagName("troncon");
            List<Segment> segments = new ArrayList<>();

            for (int i = 0; i < segmentList.getLength(); i++) {
                Node node = segmentList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    // On recupere les attributs pour creer chaque objet segment: destination (noeud), longueur, nomRue et origine (noeud)
                    String destination = element.getAttribute("destination");
                    double longueur = Double.parseDouble(element.getAttribute("longueur"));
                    String nomRue = element.getAttribute("nomRue");
                    String origine = element.getAttribute("origine");

                    Vertex noeudOrigine = verticesMap.get(origine);
                    Vertex noeudDestination = verticesMap.get(destination);

                    Segment segment = new Segment(nomRue, noeudOrigine, noeudDestination, longueur);
                    if (vertexDelivery.contains(segment.getOrigine()) && vertexDelivery.contains(segment.getDestination()))
                        segments.add(segment);
                }
            }

            return segments;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}