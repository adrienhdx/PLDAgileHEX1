package source.vue;

import org.jxmapviewer.viewer.*;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.OSMTileFactoryInfo;
import source.model.Vertice;


import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.*;
import java.util.List;

public class MapDisplay {

    public static void afficherNoeuds(List mapPlan){
        try {
            // Initialisation du map viewer
            JXMapViewer mapViewer = new JXMapViewer();

            // Initialisation de la carte via OpenStreetMap
            OSMTileFactoryInfo info = new OSMTileFactoryInfo();
            DefaultTileFactory tileFactory = new DefaultTileFactory(info);
            mapViewer.setTileFactory(tileFactory);

            List<Vertice> vertices = (List<Vertice>) mapPlan.get(0);

            // Initialisation des paramètres de la carte
            mapViewer.setZoom(3);
            //mapViewer.setAddressLocation(new GeoPosition(45.75555, 4.86922));
            mapViewer.setAddressLocation(new GeoPosition(vertices.get(0).getLatitude(), vertices.get(0).getLongitude()));
            // Création de la collection d'objets "Waypoints" à partir de la liste de Noeud obtenue avec le xml parser
            Set<Waypoint> waypoints = new HashSet<>();
            for (Vertice node : vertices) {
                waypoints.add(new DefaultWaypoint(new GeoPosition(node.getLatitude(), node.getLongitude())));
            }

            // Création d'un "waypoint painter" pour afficher les waypoints
            WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<>();
            waypointPainter.setWaypoints(waypoints);

            //Ajout du zoom de la carte
            mapViewer.addMouseWheelListener(new MouseWheelListener() {
                @Override
                public void mouseWheelMoved(MouseWheelEvent e) {
                    if (e.getWheelRotation() < 0) {
                        // Zoom avant
                        int currentZoom = mapViewer.getZoom();
                        mapViewer.setZoom(Math.max(currentZoom - 1, info.getMinimumZoomLevel()));
                    } else {
                        // Zoom arrière
                        int currentZoom = mapViewer.getZoom();
                        mapViewer.setZoom(Math.min(currentZoom + 1, info.getMaximumZoomLevel()));
                    }
                }
            });

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

