package source.view;

import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.viewer.*;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.OSMTileFactoryInfo;

import source.model.Segment;
import source.model.Vertex;
import source.view.mapTools.*;

import java.awt.*;
import java.util.*;
import java.util.List;

public class MapDisplay {

    private final JXMapViewer mapViewer;
    private final List<Painter<JXMapViewer>> painters;
    private final CompoundPainter<JXMapViewer> mainPainter;

    public MapDisplay(){
        mapViewer = new JXMapViewer();

        // Initialisation de la carte via OpenStreetMap
        OSMTileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mapViewer.setTileFactory(tileFactory);

        // Initialisation des paramètres de la carte
        mapViewer.setZoom(3);

        painters = new ArrayList<>();
        mainPainter = new CompoundPainter<>(painters);
        mapViewer.setOverlayPainter(mainPainter);
        //Initialisation des écouteurs
        this.initListenersMap();
    }

    public void setCentre(Vertex centre){
        mapViewer.setAddressLocation(new GeoPosition(centre.getLatitude(), centre.getLongitude()));
    }

    private void initListenersMap(){
        try {
            //Ajout du zoom et du pan de la carte avec écouteurs sur la souris
            PanMouseInputListener dragListener = new PanMouseInputListener(mapViewer);
            ZoomMouseWheelListenerCursor zoomWheelListener = new ZoomMouseWheelListenerCursor(mapViewer);
            mapViewer.addMouseWheelListener(zoomWheelListener);
            mapViewer.addMouseMotionListener(dragListener);
            mapViewer.addMouseListener(dragListener);
            mapViewer.setFocusable(true);
            mapViewer.requestFocusInWindow();
        }catch (Exception e) {
            System.out.println(e);
        }
    }

    public void displayVertex (Vertex vertex, String label, Color color){
        try{
            GeoPosition geoCoord = new GeoPosition(vertex.getLatitude(), vertex.getLongitude());
            CustomWaypoint waypoint = new CustomWaypoint(label,color,geoCoord);
            WaypointPainter<CustomWaypoint> waypointPainter = new WaypointPainter<>();
            waypointPainter.setWaypoints(Collections.singleton(waypoint));
            waypointPainter.setRenderer(new FancyWaypointRenderer());
            painters.add(waypointPainter);
            mainPainter.addPainter(waypointPainter);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    public void displayVertex (Vertex vertex){
        try{
            GeoPosition geoCoord = new GeoPosition(vertex.getLatitude(), vertex.getLongitude());
            DefaultWaypoint waypoint = new DefaultWaypoint(geoCoord);
            WaypointPainter<DefaultWaypoint> waypointPainter = new WaypointPainter<>();
            waypointPainter.setWaypoints(Collections.singleton(waypoint));
            painters.add(waypointPainter);
            mainPainter.addPainter(waypointPainter);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void displaySegment (Segment segment) {
        try {
            if (segment != null) {
                GeoPosition origin = new GeoPosition(segment.getOrigine().getLatitude(), segment.getOrigine().getLongitude());
                GeoPosition destination = new GeoPosition(segment.getDestination().getLatitude(), segment.getDestination().getLongitude());
                List<GeoPosition> track = Arrays.asList(origin, destination);
                RoutePainter routePainter = new RoutePainter(track);
                painters.add(routePainter);
                mainPainter.addPainter(routePainter);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void hideAll(){
        try{
            for(Painter<JXMapViewer> p : mainPainter.getPainters()){
                mainPainter.removePainter(p);
            }
            mapViewer.revalidate();
        }catch (Exception e) {
            System.out.println(e);
        }
    }

    public JXMapViewer getMapViewer() {
        return mapViewer;
    }
}

