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
            HashMap<Long, Vertex> vertexIdMap = vertexListToMap(vertexArrayList);

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

            HashMap<Long, Vertex> vertexIdMap = vertexListToMap(vertexArrayList);

            NodeList livraisonList = document.getElementsByTagName("livraison");
            for (int i = 0; i < livraisonList.getLength(); i++) {
                Element livraison = (Element) livraisonList.item(i);

                Long idEnlevement = Long.valueOf(livraison.getAttribute("adresseEnlevement"));
                Vertex adresseEnlevement = vertexIdMap.get(idEnlevement);

                Long idLivraison = Long.valueOf(livraison.getAttribute("adresseLivraison"));
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

    // Pour appeller la fonction:
    // List plan = XmlExtractor.extractPlan("./resources/grandPlan.xml");
    //        List<Vertex> vertex = (List<Vertex>) plan.get(0);
    //        List<Segment> segment = (List<Segment>) plan.get(1);
    public static List extractPlan(String file) {
        try {
            // file
            DocumentBuilderFactory factoryMap = DocumentBuilderFactory.newInstance();
            DocumentBuilder builderMap = factoryMap.newDocumentBuilder();
            Document documentMap = builderMap.parse(new FileInputStream(file));

            NodeList nodesMap = documentMap.getElementsByTagName("noeud");

            HashMap<Long, Vertex> verticesMap = new HashMap<Long, Vertex>();
            ArrayList<Vertex> vertexArrayList = new ArrayList<>();
            ArrayList plan = new ArrayList<>();

            for (int i = 0; i < nodesMap.getLength(); i++) {
                Node node = nodesMap.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    // On recupere les attributs pour creer chaque objet noeud
                    Long id = Long.valueOf(element.getAttribute("id"));
                    double latitude = Double.parseDouble(element.getAttribute("latitude"));
                    double longitude = Double.parseDouble(element.getAttribute("longitude"));

                    Vertex noeud = new Vertex(id, latitude, longitude);
                    vertexArrayList.add(noeud);
                    verticesMap.put(id, noeud);
                }
            }

            plan.add(vertexArrayList);

            // On récupère tous les tronçons "troncon" correspondant aux segments de la carte
            NodeList segmentList = documentMap.getElementsByTagName("troncon");
            ArrayList<Segment> segments = new ArrayList<>();


            for (int i = 0; i < segmentList.getLength(); i++) {
                Node node = segmentList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    // On recupere les attributs pour creer chaque objet segment: destination (noeud), longueur, nomRue et origine (noeud)
                    Long destination = Long.valueOf(element.getAttribute("destination"));
                    double longueur = Double.parseDouble(element.getAttribute("longueur"));
                    String nomRue = element.getAttribute("nomRue");
                    Long origine = Long.valueOf(element.getAttribute("origine"));

                    Vertex noeudOrigine = verticesMap.get(origine);
                    Vertex noeudDestination = verticesMap.get(destination);

                    Segment segment = new Segment(nomRue, noeudOrigine, noeudDestination, longueur);
                    segments.add(segment);
                }
            }
            plan.add(segments);
            return plan;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static HashMap<Long, Vertex> vertexListToMap(ArrayList<Vertex> vertexArrayList) {
        HashMap<Long, Vertex> vertexIdMap = new HashMap<>();
        for (Vertex vertex : vertexArrayList) {
            vertexIdMap.put(vertex.getId(), vertex);
        }
        return vertexIdMap;
    }
}