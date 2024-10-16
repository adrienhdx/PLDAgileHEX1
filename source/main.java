package source;

import java.util.*;

import source.modeles.Vertice;

import source.vue.*;

public class main {
    public static void main(String[] args) {
        List Points = XmlExtractor.extractPlan("resources/grandPlan.xml");
        System.out.println("Hello world!");
        List<Vertice> noeuds = (List<Vertice>) Points.get(0);
        for (Vertice v : noeuds) {
            System.out.println(v.getLatitude());
        }
    }
}
