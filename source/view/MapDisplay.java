package source.view;

import org.jxmapviewer.viewer.*;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.OSMTileFactoryInfo;


import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MapDisplay {

    private static JXMapViewer mapViewer;

    public MapDisplay(){
        mapViewer = new JXMapViewer();
        // Initialisation de la carte via OpenStreetMap
        OSMTileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mapViewer.setTileFactory(tileFactory);

        // Initialisation des paramètres de la carte
        mapViewer.setZoom(3);
        mapViewer.setAddressLocation(new GeoPosition(45.75555, 4.86922));
    }

    public static void afficherNoeuds(List<double[]> coordinates){
        try {
            // Création de la collection d'objets "Waypoints" à partir de la liste de Noeud obtenue avec le xml parser
            Set<Waypoint> waypoints = new HashSet<>();
            for (double[] coord : coordinates) {
                waypoints.add(new DefaultWaypoint(new GeoPosition(coord[0], coord[1])));
            }

            // Création d'un "waypoint painter" pour afficher les waypoints
            WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<>();
            waypointPainter.setWaypoints(waypoints);

            // Combine the painters (you can add more painters if needed)
            List<Painter<JXMapViewer>> painters = new ArrayList<>();
            painters.add(waypointPainter);

            CompoundPainter<JXMapViewer> painter = new CompoundPainter<>(painters);
            mapViewer.setOverlayPainter(painter);
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public static JXMapViewer getMapViewer() {
        return mapViewer;
    }
}

