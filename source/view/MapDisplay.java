package source.view;

import org.jxmapviewer.viewer.*;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.OSMTileFactoryInfo;
import source.model.Segment;
import source.model.Vertex;


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

    public void afficherNoeuds_Segments(List<Vertex> vertices, List<Segment> segments){
        try {
            //Création de la liste de GeoPosition à partir des coordonnées du Xml
            List<GeoPosition> geoCoordinates = new ArrayList<GeoPosition>(vertices.size());
            for (Vertex node : vertices) {
                geoCoordinates.add(new GeoPosition(node.getLatitude(), node.getLongitude()));
            }

            // Création de la collection d'objets "Waypoints" à partir de la liste de GeoPosition
            Set<Waypoint> waypoints = new HashSet<>();
            for (GeoPosition geoCoord : geoCoordinates) {
                waypoints.add(new DefaultWaypoint(geoCoord));
            }

            // Création des segments et de leurs painters
            List<RoutePainter> routePainters = new ArrayList<>();
            for(Segment segment : segments){
                GeoPosition origine = new GeoPosition(segment.getOrigine().getLatitude(), segment.getOrigine().getLongitude());
                GeoPosition destination = new GeoPosition(segment.getDestination().getLatitude(), segment.getDestination().getLongitude());
                List<GeoPosition> track = Arrays.asList(origine,destination);
                RoutePainter routePainter = new RoutePainter(track);
                routePainters.add(routePainter);
            }


            // Création d'un "waypoint painter" pour afficher les waypoints
            WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<>();
            waypointPainter.setWaypoints(waypoints);


            //Création d'un Compound Painter pour utiliser plusieurs painters
            List<Painter<JXMapViewer>> painters = new ArrayList<>();
            painters.add(waypointPainter);
            painters.addAll(routePainters);

            CompoundPainter<JXMapViewer> MainPainter = new CompoundPainter<>(painters);
            mapViewer.setOverlayPainter(MainPainter);

            //mapViewer.setAddressLocation(new GeoPosition(45.75555, 4.86922));
            mapViewer.setAddressLocation(new GeoPosition(vertices.get(0).getLatitude(), vertices.get(0).getLongitude()));


            //Ajout du zoom de la carte avec écouteur sur la souris
            mapViewer.addMouseWheelListener(new MouseWheelListener() {
                @Override
                public void mouseWheelMoved(MouseWheelEvent e) {
                    Point mousePoint = e.getPoint();
                    GeoPosition geoBeforeZoom = mapViewer.convertPointToGeoPosition(mousePoint);
                    if (e.getWheelRotation() < 0) {
                        // Zoom avant
                        int currentZoom = mapViewer.getZoom();
                        mapViewer.setZoom(Math.max(currentZoom - 1, mapViewer.getTileFactory().getInfo().getMinimumZoomLevel()));
                    } else {
                        // Zoom arrière
                        int currentZoom = mapViewer.getZoom();
                        mapViewer.setZoom(Math.min(currentZoom + 1, mapViewer.getTileFactory().getInfo().getMaximumZoomLevel()));
                    }
                    mapViewer.setAddressLocation(geoBeforeZoom);
                }
            });
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public JXMapViewer getMapViewer() {
        return mapViewer;
    }
}

