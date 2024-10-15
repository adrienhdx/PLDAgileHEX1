package source.vue;

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

    public static void afficherNoeuds(List<double[]> coordinates){
        try {
            // Initialisation du map viewer
            JXMapViewer mapViewer = new JXMapViewer();

            // Initialisation de la carte via OpenStreetMap
            OSMTileFactoryInfo info = new OSMTileFactoryInfo();
            DefaultTileFactory tileFactory = new DefaultTileFactory(info);
            mapViewer.setTileFactory(tileFactory);

            // Initialisation des paramètres de la carte
            mapViewer.setZoom(3);
            mapViewer.setAddressLocation(new GeoPosition(45.75555, 4.86922));
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
            Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
            int screenHeight = dimension.height;
            int screenWidth = dimension.width;
            JFrame frame = new JFrame("Visualisation des noeuds");
            frame.getContentPane().add(new JScrollPane(mapViewer));
            frame.setSize(screenWidth, screenHeight);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        } catch (Exception e) {
            System.out.println(e);
        }

    }

}

