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


/**
 * Map component of the App
 */
public class MapDisplay {

    private final JXMapViewer mapViewer;
    private final List<Painter<JXMapViewer>> painters;
    private final CompoundPainter<JXMapViewer> mainPainter;

    /**
     * Constructor of the MapDisplay class
     */
    public MapDisplay(){
        mapViewer = new JXMapViewer();

        //Map init
        OSMTileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mapViewer.setTileFactory(tileFactory);

        //Init of zoom parameters
        mapViewer.setZoom(6);
        painters = new ArrayList<>();
        mainPainter = new CompoundPainter<>(painters);
        mapViewer.setOverlayPainter(mainPainter);

        //Init of the listeners
        this.initListenersMap();
    }

    /**
     * Set the centre of the map
     * @param centre Vertex centre of the map
     */
    public void setCentre(Vertex centre){
        mapViewer.setAddressLocation(new GeoPosition(centre.getLatitude(), centre.getLongitude()));
    }


    /**
     * Init the map listeners
     */
    private void initListenersMap(){
        try {
            //Zoom management of the map
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


    /**
     * Display a vertex as a waypoint on the map
     * @param vertex Vertex to display
     * @param label Label associated to the vertex
     * @param color Color of the vertex
     * @param entrepot True if the vertex corresponds to the warehouse, false otherwise
     */
    public void displayVertex (Vertex vertex, String label, Color color, boolean entrepot){
        try{
            GeoPosition geoCoord = new GeoPosition(vertex.getLatitude(), vertex.getLongitude());
            CustomWaypoint waypoint = new CustomWaypoint(label,color,geoCoord);
            WaypointPainter<CustomWaypoint> waypointPainter = new WaypointPainter<>();
            waypointPainter.setWaypoints(Collections.singleton(waypoint));
            waypointPainter.setRenderer(new FancyWaypointRenderer(entrepot));
            painters.add(waypointPainter);
            mainPainter.addPainter(waypointPainter);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Display a vertex as a waypoint on the map
     * @param vertex Vertex to display
     */
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

    /**
     * Display a segment on the map
     * @param segment Segment to display
     * @param color Color of the segment
     */
    public void displaySegment (Segment segment, Color color) {
        try {
            if (segment != null) {
                GeoPosition origin = new GeoPosition(segment.getOrigin().getLatitude(), segment.getOrigin().getLongitude());
                GeoPosition destination = new GeoPosition(segment.getDestination().getLatitude(), segment.getDestination().getLongitude());
                List<GeoPosition> track = Arrays.asList(origin, destination);
                RoutePainter routePainter = new RoutePainter(track,color);
                painters.add(routePainter);
                mainPainter.addPainter(routePainter);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Hide the displayed segments and vertices of the map
     */
    public void hideAll(){
        try{
            for(Painter<JXMapViewer> p : mainPainter.getPainters()){
                mainPainter.removePainter(p);
            }
            mapViewer.repaint();
        }catch (Exception e) {
            System.out.println(e);
        }
    }

    //Getter
    public JXMapViewer getMapViewer() {
        return mapViewer;
    }
}

