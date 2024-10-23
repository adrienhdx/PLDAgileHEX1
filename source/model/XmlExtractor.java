package source.model;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

import java.util.*;

import java.io.FileInputStream;

public class XmlExtractor {
    public static Vertex extractEntrepot(String file, ArrayList<Vertex> vertexArrayList) {

        try{
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new FileInputStream(file));

            Element entrepot = (Element) document.getElementsByTagName("entrepot").item(0);
            HashMap<Integer, Vertex> vertexIdMap = new HashMap<>();

            for (Vertex vertex : vertexArrayList) {
                vertexIdMap.put(vertex.getId(), vertex);
            }
            return vertexIdMap.get(entrepot.getAttribute("adresse"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Delivery> extractDemande(String file, ArrayList<Vertex> vertexArrayList) {
        try {
            List<Delivery> deliveries = new ArrayList<>();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new FileInputStream(file));

            HashMap<Integer, Vertex> vertexIdMap = new HashMap<>();

            for (Vertex vertex : vertexArrayList) {
                vertexIdMap.put(vertex.getId(), vertex);
            }

            NodeList livraisonList = document.getElementsByTagName("livraison");
            for (int i = 0; i < livraisonList.getLength(); i++) {
                Element livraison = (Element) livraisonList.item(i);

                String idEnlevement = livraison.getAttribute("adresseEnlevement");
                Vertex adresseEnlevement = vertexIdMap.get(idEnlevement);

                String idLivraison = livraison.getAttribute("adresseLivraison");
                Vertex adresseLivraison = vertexIdMap.get(idLivraison);

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

    public static ArrayList<Vertex> extractPlan(String file) {
        try {
            // file
            DocumentBuilderFactory factoryMap = DocumentBuilderFactory.newInstance();
            DocumentBuilder builderMap = factoryMap.newDocumentBuilder();
            Document documentMap = builderMap.parse(new FileInputStream(file));

            NodeList nodesMap = documentMap.getElementsByTagName("noeud");

            ArrayList<Vertex> vertexArrayList = new ArrayList<>();

            for (int i = 0; i < nodesMap.getLength(); i++) {
                Node node = nodesMap.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    // On recupere les attributs pour creer chaque objet noeud
                    String id = element.getAttribute("id");
                    int id_int = Integer.valueOf(id);
                    double latitude = Double.parseDouble(element.getAttribute("latitude"));
                    double longitude = Double.parseDouble(element.getAttribute("longitude"));

                    Vertex noeud = new Vertex(id_int, latitude, longitude);
                    vertexArrayList.add(noeud);
                }
            }
            return vertexArrayList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Vertex> extractPlanAsList(Map<String, Vertex> verticesMap) {
        try {
            List<Vertex> vertices = new ArrayList<>();

            // Changement de Map à Liste
            for (Map.Entry<String, Vertex> entry : verticesMap.entrySet()) {
                Vertex vert = entry.getValue();
                vertices.add(vert);
            }
            return vertices;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Segment> extractTroncon(String file, Map<String, Vertex> verticesMap, List<Vertex> vertexDelivery) {
        try {

            // file
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document documentMap = builder.parse(new FileInputStream(file));

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
                    segments.add(segment);

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