package source;

import java.util.*;

import source.model.Delivery;
import source.model.Vertex;


public class main {
    public static void main(String[] args) {
        Map<String, Vertex> map = XmlExtractor.extractPlan("./resources/grandPlan.xml");
        List<Delivery> demandes = XmlExtractor.extractDemande("./resources/demandeMoyen5.xml", map);
        System.out.println("Hello world!");
        for (Delivery demande : demandes) {
            System.out.println(demande);
        }
        List<Vertex> demandesVertex = XmlExtractor.extractDeliveryVertices(demandes);
        for (Vertex vertex : demandesVertex) {
            System.out.println(vertex);
        }

        List<Vertex> vertices = XmlExtractor.extractVertices(map, demandesVertex);
        for (Vertex vertex : vertices) {
            System.out.println(vertex);
        }
    }
}
