package source.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import source.model.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class XmlExtractorTest {

    @Test
    void extractDeliveryDemand() {
    }

    @Test
    void extractMap() {
    }

    @Test
    void extractRoute() {
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
        Vertex vertex2 = new Vertex(2129259178L, 45.75406, 4.857418);
        Segment segment1 = new Segment("Rue Danton", new Vertex(25175791L, 45.75406, 4.857418), new Vertex(25175778L, 45.75343, 4.8574653), 69.979805);
        Segment segment2 = new Segment("Rue de l'Abondance", new Vertex(2117622723L, 45.75425, 4.8591485), new Vertex(2117622723L, 45.75406, 4.857418), 69.979805);
        // Pas fini
    }
}