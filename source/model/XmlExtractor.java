package source.model;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import java.io.FileInputStream;

/**
 * The XmlExtractor class is used to extract data from .xml files and to export data into new .xml files
 */
public class XmlExtractor {

    /**
     * Extract the deliveries and the warehouse from the .xml file
     * @param file The xml filepath
     * @param vertexArrayList The list of vertices
     * @return The list of deliveries and the warehouse
     */
    public static ArrayList<Object> extractDeliveryDemand(String file, ArrayList<Vertex> vertexArrayList) {
        ArrayList<Object> deliveryDemand = new ArrayList<>();
        ArrayList<Delivery> deliveryArrayList = new ArrayList<>();
        if (!isXMLFile(file)) {
            System.out.println("The file " + file + " is not an XML file");
            return deliveryDemand;
        }
        try {

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new FileInputStream(file));

            HashMap<Long, Vertex> vertexIdMap = vertexListToMap(vertexArrayList);

            NodeList deliveryList = document.getElementsByTagName("livraison");
            for (int i = 0; i < deliveryList.getLength(); i++) {
                Element delivery = (Element) deliveryList.item(i);

                Long idPickUp = Long.valueOf(delivery.getAttribute("adresseEnlevement"));
                Vertex pickUpAddress = vertexIdMap.get(idPickUp);
                if (pickUpAddress == null) return null;

                Long idDelivery = Long.valueOf(delivery.getAttribute("adresseLivraison"));
                Vertex deliveryAddress = vertexIdMap.get(idDelivery);
                if (deliveryAddress == null) return null;

                int pickUpTime = Integer.parseInt(delivery.getAttribute("dureeEnlevement"));
                int deliveryTime = Integer.parseInt(delivery.getAttribute("dureeLivraison"));

                deliveryArrayList.add(new Delivery(pickUpAddress, deliveryAddress, pickUpTime, deliveryTime, DeliveryState.PENDING));
            }

            Element entrepot = (Element) document.getElementsByTagName("entrepot").item(0);

            if (entrepot != null) {
                Long idAddress = Long.valueOf(entrepot.getAttribute("adresse"));
                Vertex address = vertexIdMap.get(idAddress);

                String departureHourStr = entrepot.getAttribute("heureDepart");

                String[] departureHourStrParts = departureHourStr.split(":");
                String hours = String.format("%02d", Integer.parseInt(departureHourStrParts[0]));
                String minutes = String.format("%02d", Integer.parseInt(departureHourStrParts[1]));
                String seconds = String.format("%02d", Integer.parseInt(departureHourStrParts[2]));

                String normalizedDepartureHourStr = hours + ":" + minutes + ":" + seconds;

                LocalTime departureHour = LocalTime.parse(normalizedDepartureHourStr, DateTimeFormatter.ofPattern("HH:mm:ss"));

                deliveryDemand.add(new Entrepot(address, departureHour));
            } else {
                deliveryDemand.add(null);
            }
            deliveryDemand.add(deliveryArrayList);
            return deliveryDemand;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
            return new ArrayList<Object>();
        }
    }

    /**
     * Extract the segments and the vertices of the map from the.xml file
     * @param file The xml filepath
     * @return Both lists of segments and vertices from the file
     */
    public static ArrayList<Object> extractMap(String file) {
        if (!isXMLFile(file)) {
            return null;
        }
        try {
            // file
            DocumentBuilderFactory factoryMap = DocumentBuilderFactory.newInstance();
            DocumentBuilder builderMap = factoryMap.newDocumentBuilder();
            Document documentMap = builderMap.parse(new FileInputStream(file));

            HashMap<Long, Vertex> verticesMap = new HashMap<>();
            ArrayList<Vertex> vertexArrayList = new ArrayList<>();
            ArrayList<Object> map = new ArrayList<>();

            NodeList vertexNodeList = documentMap.getElementsByTagName("noeud");

            for (int i = 0; i < vertexNodeList.getLength(); i++) {
                Node vertexNode = vertexNodeList.item(i);
                if (vertexNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) vertexNode;
                    // On recupere les attributs pour creer chaque objet noeud
                    Long id = Long.valueOf(element.getAttribute("id"));
                    double latitude = Double.parseDouble(element.getAttribute("latitude"));
                    double longitude = Double.parseDouble(element.getAttribute("longitude"));

                    Vertex vertex = new Vertex(id, latitude, longitude);
                    vertexArrayList.add(vertex);
                    verticesMap.put(id,vertex);
                }
            }

            map.add(vertexArrayList);

            // On récupère tous les tronçons "troncon" correspondant aux segments de la carte
            NodeList segmentNodeList = documentMap.getElementsByTagName("troncon");
            ArrayList<Segment> segmentArrayList = new ArrayList<>();

            for (int i = 0; i < segmentNodeList.getLength(); i++) {
                Node node = segmentNodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    // On recupere les attributs pour creer chaque objet segment: destination (noeud), longueur, nomRue et origine (noeud)
                    Long destination = Long.valueOf(element.getAttribute("destination"));
                    double length = Double.parseDouble(element.getAttribute("longueur"));
                    String nomRue = element.getAttribute("nomRue");
                    Long origin = Long.valueOf(element.getAttribute("origine"));

                    Vertex originNode = verticesMap.get(origin);
                    Vertex destinationNode = verticesMap.get(destination);

                    Segment segment = new Segment(nomRue, originNode, destinationNode, length);
                    segmentArrayList.add(segment);
                }
            }
            map.add(segmentArrayList);
            return map;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Extract the segments and the vertices of the route from the.xml file
     * @param file The xml filepath
     * @param vertices The list of the vertices of the map
     * @return Both lists of segments and vertices from the file
     */
    public static ArrayList<Object> extractRoute(String file, ArrayList<Vertex> vertices) {
        if (!isXMLFile(file)) {
            return null;
        }
        try {
            // file
            DocumentBuilderFactory factoryMap = DocumentBuilderFactory.newInstance();
            DocumentBuilder builderMap = factoryMap.newDocumentBuilder();
            Document documentMap = builderMap.parse(new FileInputStream(file));

            HashMap<Long, Vertex> verticesMap = vertexListToMap(vertices);

            ArrayList<Vertex> vertexArrayList = new ArrayList<>();
            ArrayList<Object> map = new ArrayList<>();

            NodeList vertexNodeList = documentMap.getElementsByTagName("noeud");

            for (int i = 0; i < vertexNodeList.getLength(); i++) {
                Node vertexNode = vertexNodeList.item(i);
                if (vertexNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) vertexNode;
                    // On recupere les attributs pour creer chaque objet noeud
                    Long id = Long.valueOf(element.getAttribute("id"));
                    double latitude = Double.parseDouble(element.getAttribute("latitude"));
                    double longitude = Double.parseDouble(element.getAttribute("longitude"));

                    Vertex vertex = new Vertex(id, latitude, longitude);
                    vertexArrayList.add(vertex);
                    verticesMap.put(id,vertex);
                }
            }

            map.add(vertexArrayList);

            // On récupère tous les tronçons "troncon" correspondant aux segments de la carte
            NodeList segmentNodeList = documentMap.getElementsByTagName("troncon");
            ArrayList<Segment> segmentArrayList = new ArrayList<>();

            for (int i = 0; i < segmentNodeList.getLength(); i++) {
                Node node = segmentNodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    // On recupere les attributs pour creer chaque objet segment: destination (noeud), longueur, nomRue et origine (noeud)
                    Long destination = Long.valueOf(element.getAttribute("destination"));
                    double length = Double.parseDouble(element.getAttribute("longueur"));
                    String nomRue = element.getAttribute("nomRue");
                    Long origin = Long.valueOf(element.getAttribute("origine"));

                    Vertex originNode = verticesMap.get(origin);
                    Vertex destinationNode = verticesMap.get(destination);

                    Segment segment = new Segment(nomRue, originNode, destinationNode, length);
                    segmentArrayList.add(segment);
                }
            }
            map.add(segmentArrayList);
            return map;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Create a hash map [vertexID] -> Vertex
     * @param vertexArrayList The list of vertices
     * @return The hash map linking each vertex to its ID
     */
    private static HashMap<Long, Vertex> vertexListToMap(ArrayList<Vertex> vertexArrayList) {
        HashMap<Long, Vertex> vertexIdMap = new HashMap<>();
        for (Vertex vertex : vertexArrayList) {
            vertexIdMap.put(vertex.getId(), vertex);
        }
        return vertexIdMap;
    }

    /**
     * Test is the file is a .xml file
     * @param file The filepath
     * @return True is the file is a .xml file
     */
    private static boolean isXMLFile(String file) {
        return file.endsWith(".xml");
    }

    /**
     * Create a .xml file with the list of deliveries
     * @param pendingDeliveries The list of deliveries
     * @param entrepot The warehouse
     * @return The content of the .xml file
     */
    public static String exportWaitingList(ArrayList<Delivery> pendingDeliveries, Entrepot entrepot){
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n";
        xml += "<demandeDeLivraisons>\n";
        LocalTime departureHour = entrepot.getDepartureHour();
        departureHour = departureHour.withSecond(0);
        xml += "<entrepot adresse=\"" + entrepot.getAddress().getId() + "\" heureDepart=\"" + departureHour.format(DateTimeFormatter.ofPattern("H:m:s")) +"\"/>\n";
        for (Delivery delivery : pendingDeliveries) {
            xml += "<livraison adresseEnlevement=\"" + delivery.getPickUpPt().getId() + "\" adresseLivraison=\"" + delivery.getDeliveryPt().getId() + "\" dureeEnlevement=\"" + delivery.getPickUpTime() + "\" dureeLivraison=\"" + delivery.getDeliveryTime() + "\"/>\n";
        }
        xml += "</demandeDeLivraisons>";
        return xml;
    }

    /**
     * Create a .xml file with the list of segments and vertices of a route
     * @param vertices The list of vertices
     * @param segments The list of segments
     * @return The content of the .xml file
     */
    public static String exportRoutes(ArrayList<Vertex> vertices, ArrayList<Segment> segments) {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n";
        xml += "<reseau>\n";
        for (Vertex vertex : vertices) {
            xml += "<noeud id=\"" + vertex.getId() + "\" latitude=\"" + vertex.getLatitude() + "\" longitude=\"" + vertex.getLongitude() + "\"/>\n";
        }
        for (Segment segment : segments) {
            xml += "<troncon destination=\"" + segment.getDestination().getId() + "\" longueur=\"" + segment.getLength() + "\" nomRue=\"" + segment.getStreetName() + "\" origine=\"" + segment.getOrigin().getId() + "\"/>\n";
        }
        xml += "</reseau>";
        return xml;
    }

}