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

public class XmlExtractor {

    public static ArrayList<Object> extractDeliveryDemand(String file, ArrayList<Vertex> vertexArrayList) {
        if (!isXMLFile(file)) {
            return null;
        }

        try {
            ArrayList<Object> deliveryDemand = new ArrayList<>();
            ArrayList<Delivery> deliveryArrayList = new ArrayList<>();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new FileInputStream(file));

            HashMap<Long, Vertex> vertexIdMap = vertexListToMap(vertexArrayList);

            NodeList deliveryList = document.getElementsByTagName("livraison");
            for (int i = 0; i < deliveryList.getLength(); i++) {
                Element delivery = (Element) deliveryList.item(i);

                Long idPickUp = Long.valueOf(delivery.getAttribute("adresseEnlevement"));
                Vertex pickUpAddress = vertexIdMap.get(idPickUp);

                Long idDelivery = Long.valueOf(delivery.getAttribute("adresseLivraison"));
                Vertex deliveryAddress = vertexIdMap.get(idDelivery);

                int pickUpTime = Integer.parseInt(delivery.getAttribute("dureeEnlevement"));
                int deliveryTime = Integer.parseInt(delivery.getAttribute("dureeLivraison"));

                deliveryArrayList.add(new Delivery(pickUpAddress, deliveryAddress, pickUpTime, deliveryTime, DeliveryState.PENDING));
            }

            Element entrepot = (Element) document.getElementsByTagName("entrepot").item(0);

            if (entrepot == null) {
                return null;
            }

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
            deliveryDemand.add(deliveryArrayList);

            return deliveryDemand;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

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

            if (vertexNodeList.getLength() == 0) {
                return null;
            }

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

            if (segmentNodeList.getLength() == 0) {
                return null;
            }

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
        }
        return null;
    }

    private static HashMap<Long, Vertex> vertexListToMap(ArrayList<Vertex> vertexArrayList) {
        HashMap<Long, Vertex> vertexIdMap = new HashMap<>();
        for (Vertex vertex : vertexArrayList) {
            vertexIdMap.put(vertex.getId(), vertex);
        }
        return vertexIdMap;
    }

    private static boolean isXMLFile(String file) {
        return file.endsWith(".xml");
    }

}