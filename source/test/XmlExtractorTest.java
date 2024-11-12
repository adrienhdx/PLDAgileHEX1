package source.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import source.model.*;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class XmlExtractorTest {

    @Test
    void extractDeliveryDemand() {
        ArrayList<Object> result = XmlExtractor.extractMap("resources/petitPlan.xml");
        ArrayList<Vertex> vertices = (ArrayList<Vertex>) result.get(0);
        Vertex vertex1 = new Vertex(208769039L, 45.76069, 4.8749375);
        Vertex vertex2 = new Vertex(25173820L, 45.749996, 4.858258);
        vertices.add(vertex1);
        vertices.add(vertex2);
        Delivery deliveryExpected = new Delivery(vertex1, vertex2, 180, 240, DeliveryState.PENDING);
        LocalTime departureHour = LocalTime.parse("08:00:00", DateTimeFormatter.ofPattern("HH:mm:ss"));
        Entrepot entrepotExpected = new Entrepot(new Vertex(342873658L, 45.76038, 4.8775625), departureHour);
        ArrayList<Delivery> deliveryListExpected = new ArrayList<>();
        deliveryListExpected.add(deliveryExpected);
        ArrayList<Object> listExpected = new ArrayList<>();
        listExpected.add(entrepotExpected);
        listExpected.add(deliveryListExpected);
        assertAll(
                () -> assertEquals(new ArrayList<>(), XmlExtractor.extractDeliveryDemand("resources/demandePetit1.pdf", vertices)),
                () -> assertNull(XmlExtractor.extractDeliveryDemand("resources/demandePetit1.xml", new ArrayList<Vertex>())),
                () -> assertEquals(listExpected.toString(), XmlExtractor.extractDeliveryDemand("resources/demandePetit1.xml", vertices).toString())
        );
    }

    @Test
    void extractMap() {
        ArrayList<Object> result = XmlExtractor.extractMap("resources/petitPlan.xml");
        ArrayList<Vertex> vertices = (ArrayList<Vertex>) result.getFirst();
        ArrayList<Segment> segments = (ArrayList<Segment>) result.getLast();
        assertAll(
                () -> assertNull(XmlExtractor.extractMap("test.pdf")),
                () -> assertEquals(616, segments.size()),
                () -> assertEquals(308, vertices.size())
                );
    }

    @Test
    void extractRoute() {
        ArrayList<Object> map = XmlExtractor.extractMap("resources/petitPlan.xml");
        ArrayList<Vertex> vertices = (ArrayList<Vertex>) map.get(0);
        ArrayList<Object> route = XmlExtractor.extractRoute("resources/exportedRoute-20241112.xml", vertices);
        ArrayList<Vertex> verticesActual = (ArrayList<Vertex>) route.getFirst();
        ArrayList<Segment> segmentsActual = (ArrayList<Segment>) route.getLast();
        assertAll(
                () -> assertNull(XmlExtractor.extractRoute("test.pdf", vertices)),
                () -> assertEquals(5, verticesActual.size()),
                () -> assertEquals(124, segmentsActual.size())
        );
    }

    @Test
    void exportWaitingList() {
        ArrayList<Delivery> deliveries = new ArrayList<>();
        Delivery delivery1 = new Delivery(new Vertex(1679901320L, 45.762653, 4.875565), new Vertex(208769457L, 45.760174, 4.877455), 420, 600, DeliveryState.PENDING);
        Delivery delivery2 = new Delivery(new Vertex(208769120L, 45.759434, 4.869736), new Vertex(25336179L, 45.754128, 4.863194), 420, 480, DeliveryState.PENDING);
        deliveries.add(delivery1);
        deliveries.add(delivery2);
        String waitingListExpected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<demandeDeLivraisons>\n" +
                "<livraison adresseEnlevement=\"1679901320\" adresseLivraison=\"208769457\" dureeEnlevement=\"420\" dureeLivraison=\"600\"/>\n" +
                "<livraison adresseEnlevement=\"208769120\" adresseLivraison=\"25336179\" dureeEnlevement=\"420\" dureeLivraison=\"480\"/>\n" +
                "</demandeDeLivraisons>";
        String waitingList = XmlExtractor.exportWaitingList(deliveries);
        assertEquals(waitingListExpected, waitingList);
    }

    @Test
    void exportRoutes() {
        ArrayList<Vertex> vertices = new ArrayList<>();
        ArrayList<Segment> segments = new ArrayList<>();
        Vertex vertex1 = new Vertex(25175791L, 45.75406, 4.857418);
        Vertex vertex2 = new Vertex(2129259178L, 45.750404, 4.8744674);
        Segment segment1 = new Segment("Rue Danton", vertex1, new Vertex(25175778L, 45.75343, 4.8574653), 69.979805);
        Segment segment2 = new Segment("Rue de l'Abondance", vertex1, new Vertex(2117622723L, 45.75425, 4.8591485), 136.00636);
        vertices.add(vertex1);
        vertices.add(vertex2);
        segments.add(segment1);
        segments.add(segment2);
        String exportExpected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<reseau>\n" +
                "<noeud id=\"25175791\" latitude=\"45.75406\" longitude=\"4.857418\"/>\n" +
                "<noeud id=\"2129259178\" latitude=\"45.750404\" longitude=\"4.8744674\"/>\n" +
                "<troncon destination=\"25175778\" longueur=\"69.979805\" nomRue=\"Rue Danton\" origine=\"25175791\"/>\n" +
                "<troncon destination=\"2117622723\" longueur=\"136.00636\" nomRue=\"Rue de l'Abondance\" origine=\"25175791\"/>\n" +
                "</reseau>";
        String exportList = XmlExtractor.exportRoutes(vertices, segments);
        assertEquals(exportExpected, exportList);
    }
}