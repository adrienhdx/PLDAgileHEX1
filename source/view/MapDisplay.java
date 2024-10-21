package source.view;

import org.jxmapviewer.viewer.*;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.OSMTileFactoryInfo;
import source.model.Vertex;


import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.*;
import java.util.List;

public class MapDisplay {

    private JXMapViewer mapViewer;

    public MapDisplay(){
        mapViewer = new JXMapViewer();
        // Initialisation de la carte via OpenStreetMap
        OSMTileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mapViewer.setTileFactory(tileFactory);
        // Initialisation des paramètres de la carte
        mapViewer.setZoom(3);
    }

    public void afficherNoeuds(List<Vertex> vertices){
        try {

            //mapViewer.setAddressLocation(new GeoPosition(45.75555, 4.86922));
            mapViewer.setAddressLocation(new GeoPosition(vertices.getFirst().getLatitude(), vertices.getFirst().getLongitude()));
            // Création de la collection d'objets "Waypoints" à partir de la liste de Noeud obtenue avec le xml parser
            Set<Waypoint> waypoints = new HashSet<>();
            for (Vertex node : vertices) {
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
                        mapViewer.setZoom(Math.max(currentZoom - 1, mapViewer.getTileFactory().getInfo().getMinimumZoomLevel()));
                    } else {
                        // Zoom arrière
                        int currentZoom = mapViewer.getZoom();
                        mapViewer.setZoom(Math.min(currentZoom + 1, mapViewer.getTileFactory().getInfo().getMaximumZoomLevel()));
                    }
                }
            });

            // Combine the painters (you can add more painters if needed)
            List<Painter<JXMapViewer>> painters = new ArrayList<>();
            painters.add(waypointPainter);

            CompoundPainter<JXMapViewer> painter = new CompoundPainter<>(painters);
            mapViewer.setOverlayPainter(painter);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public JXMapViewer getMapViewer() {
        return mapViewer;
    }
}

