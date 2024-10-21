package source;

import java.util.*;

import source.model.Delivery;
import source.model.Vertex;


public class main {
    public static void main(String[] args) {
        Map<String, Vertex> map = XmlExtractor.extractPlan("./resources/moyenPlan.xml");
        List<Delivery> demandes = XmlExtractor.extractDemande("./resources/demandeMoyen5.xml", map);
        for (Delivery demande : demandes) {
            System.out.println(demande);
        }
        List<Vertex> demandesVertex = XmlExtractor.extractDeliveryVertices(demandes);
        for (Vertex vertex : demandesVertex) {
            System.out.println(vertex);
        }
    }
}
