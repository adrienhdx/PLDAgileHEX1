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
    private List<Painter<JXMapViewer>> painters;
    private CompoundPainter<JXMapViewer> MainPainter;
    public MapDisplay(Vertex centre){
        mapViewer = new JXMapViewer();

        // Initialisation de la carte via OpenStreetMap
        OSMTileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mapViewer.setTileFactory(tileFactory);

        // Initialisation du centre
        mapViewer.setAddressLocation(new GeoPosition(centre.getLatitude(), centre.getLongitude()));

        // Initialisation des paramètres de la carte
        mapViewer.setZoom(3);

        painters = new ArrayList<>();
        MainPainter = new CompoundPainter<>(painters);
        mapViewer.setOverlayPainter(MainPainter);
        //Initialisation des écouteurs
        this.initListenersMap();
    }

    public void initListenersMap(){
        try {
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
        }catch (Exception e) {
            System.out.println(e);
        }
    }

    public void afficherNoeud(Vertex vertex){
        try{
            GeoPosition geoCoord = new GeoPosition(vertex.getLatitude(), vertex.getLongitude());
            DefaultWaypoint waypoint = new DefaultWaypoint(geoCoord);
            WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<>();
            waypointPainter.setWaypoints(Collections.singleton(waypoint));
            painters.add(waypointPainter);
            MainPainter.addPainter(waypointPainter);
        }catch (Exception e) {
            System.out.println(e);
        }

    }

    public void afficherNoeuds(List<Vertex> vertices){
        try{
            for(Vertex vertex : vertices){
                afficherNoeud(vertex);
            }
        }catch (Exception e) {
            System.out.println(e);
        }
    }


    public void afficherSegments(List<Segment> segments){
        List<RoutePainter> routePainters = new ArrayList<>();
        if (segments != null) {
            for (Segment segment : segments) {
                GeoPosition origine = new GeoPosition(segment.getOrigine().getLatitude(), segment.getOrigine().getLongitude());
                GeoPosition destination = new GeoPosition(segment.getDestination().getLatitude(), segment.getDestination().getLongitude());
                List<GeoPosition> track = Arrays.asList(origine, destination);
                RoutePainter routePainter = new RoutePainter(track);
                routePainters.add(routePainter);
                painters.add(routePainter);
                MainPainter.addPainter(routePainter);
            }

        }
    }

    public void afficherNoeuds_Segments(List<Vertex> vertices  /*, List<Segment> segments*/){
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

            /* Création des segments et de leurs painters
            List<RoutePainter> routePainters = new ArrayList<>();
            if (segments != null) {
                for (Segment segment : segments) {
                    GeoPosition origine = new GeoPosition(segment.getOrigine().getLatitude(), segment.getOrigine().getLongitude());
                    GeoPosition destination = new GeoPosition(segment.getDestination().getLatitude(), segment.getDestination().getLongitude());
                    List<GeoPosition> track = Arrays.asList(origine, destination);
                    RoutePainter routePainter = new RoutePainter(track);
                    routePainters.add(routePainter);
                }
            }*/

            
            // Création d'un "waypoint painter" pour afficher les waypoints
            WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<>();
            waypointPainter.setWaypoints(waypoints);


            //Création d'un Compound Painter pour utiliser plusieurs painters
            List<Painter<JXMapViewer>> painters = new ArrayList<>();
            painters.add(waypointPainter);
            //painters.addAll(routePainters);

            CompoundPainter<JXMapViewer> MainPainter = new CompoundPainter<>(painters);
            mapViewer.setOverlayPainter(MainPainter);


        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public JXMapViewer getMapViewer() {
        return mapViewer;
    }
}

