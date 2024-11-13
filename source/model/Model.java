package source.model;


import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;

public class Model {
    private ArrayList<Delivery> pendingDeliveryArrayList;
    private final ArrayList<Delivery> waitingDeliveryArrayList;
    private final ArrayList<Courier> courierArrayList;
    private final PropertyChangeSupport propertyChangeSupport;
    private SolveurTSP solveur;

    private final double VITESSE_COURIER_KMH = 15.0;
    private final double DUREE_JOURNEE_MIN = 420.0;

    public Model(){
        propertyChangeSupport = new PropertyChangeSupport(this);
        this.courierArrayList = new ArrayList<>();
        this.pendingDeliveryArrayList = new ArrayList<>();
        this.waitingDeliveryArrayList = new ArrayList<>();
   }

    /**
     * @param ArrayListener A class that implements the Interface PropertyChangeListener
     */
    public void addPropertyChangeListener(PropertyChangeListener ArrayListener){
        propertyChangeSupport.addPropertyChangeListener(ArrayListener);
    }

    /**
     * Create a new instance of the Courier class
     * @param firstName The first name of the courier
     * @param lastName The last name of the courier
     * @param phoneNumber The phone number of the courier
     * @return The created courier
     */
    public Courier createCourier (String firstName, String lastName, String phoneNumber){
        if(!firstName.isEmpty() & !lastName.isEmpty() & !phoneNumber.isEmpty()) {
            for (Courier courier : courierArrayList) {
                if (courier.getFirstName().equals(firstName) && courier.getLastName().equals(lastName)) {
                    propertyChangeSupport.firePropertyChange("errorMessage", null, "This courier already exists");
                    return null;
                }
            }
            return new Courier(firstName, lastName, phoneNumber);
        } else {
            propertyChangeSupport.firePropertyChange("errorMessage", null, "Please fill in all fields");
            return null;
        }
    }

    /**
     * Add the courier to the couriers' list
     * @param courier A courier
     */
    public void addCourier (Courier courier){
        courierArrayList.add(courier);
        propertyChangeSupport.firePropertyChange("courierArrayList", null, courierArrayList);
    }

    /**
     * Get the courier from its first name and last name
     * @param firstName The first name of the courier
     * @param lastName The last name of the courier
     * @return The matching instance of the Courier class
     */
    public Courier getCourier(String firstName, String lastName) {
        for (Courier courier : courierArrayList){
            if (courier.getFirstName().equals(firstName) && courier.getLastName().equals(lastName)) {
                return courier;
            }
        }
        return null;
    }

    /**
     * Get the delivery from its pick-up and delivery points
     * @param pickUpPtStr The id of the pick-up vertex
     * @param deliveryPtStr The id of the delivery vertex
     * @return The matching instance of the Delivery class
     */
    public Delivery getPendingDelivery(Long pickUpPtStr, Long deliveryPtStr) {
        for (Delivery delivery : pendingDeliveryArrayList){
            if (delivery.getPickUpPt().getId().equals(pickUpPtStr) && delivery.getDeliveryPt().getId().equals(deliveryPtStr)) {
                return delivery;
            }
        }
        return null;
    }

    /**
     * Delete the courier
     * @param courier The courier to be deleted
     */
    public void deleteCourier(Courier courier){
        if (courier != null) {
            courierArrayList.remove(courier);
            String courierID = courier.getFirstName().concat(" ").concat(courier.getLastName());
            propertyChangeSupport.firePropertyChange("courierArrayList", null, courierArrayList);
            propertyChangeSupport.firePropertyChange("courierDeleted", null, courierID);
        } else {
            propertyChangeSupport.firePropertyChange("errorMessage", null, "No courier selected");
        }
    }

    /**
     * Assign a delivery to a courier
     * @param courier The courier you want to assign the delivery to
     * @param delivery The delivery you want to assign
     */
    public void assignDelivery(Courier courier, Delivery delivery){
        if (courier != null) {
            if (delivery != null) {
                courier.getRoute().getDeliveries().add(delivery);
                ArrayList<Segment> routeComputed = solveur.ObtenirArrayListeSegmentsTSP(courier.getRoute().getDeliveries());
                double distanceRoute = solveur.getLongueurSolutionCourante();
                double tempsRoute = 0;
                for (int i = 0 ; i < courier.getRoute().getDeliveries().size() ; i++) {
                    // il y a 5 minutes de temps de pickup/delivery
                    tempsRoute += 10;
                }
                tempsRoute += ((distanceRoute / 1000) / VITESSE_COURIER_KMH) * 60;
                if (routeComputed != null && tempsRoute <= DUREE_JOURNEE_MIN) {
                    courier.getRoute().setSegments(routeComputed);
                    pendingDeliveryArrayList.remove(delivery);
                    delivery.setState(DeliveryState.ASSIGNED);
                    propertyChangeSupport.firePropertyChange("courierRouteDeliveries", null, courier.getRoute().getDeliveries());
                    propertyChangeSupport.firePropertyChange("pendingDeliveryRemoved", null, delivery);
                    propertyChangeSupport.firePropertyChange("routeTime", null, String.valueOf((int)tempsRoute));
                } else {
                    courier.getRoute().getDeliveries().remove(delivery);
                    if (routeComputed == null) propertyChangeSupport.firePropertyChange("errorMessage", null, "No route found : the delivery can't be assigned to this courier");
                    else if (tempsRoute > DUREE_JOURNEE_MIN) propertyChangeSupport.firePropertyChange("errorMessage", null, "The delivery can't be assigned to this courier : the route is too long");
                }
            } else {
                propertyChangeSupport.firePropertyChange("errorMessage", null, "No delivery selected");
            }
        } else {
            propertyChangeSupport.firePropertyChange("errorMessage", null, "No courier selected");
        }
    }

    /**
     * Update the map
     * @param vertexList The list of vertices
     * @param segmentList The list of segments
     */
    public void updateMap(ArrayList<Vertex> vertexList, ArrayList<Segment> segmentList){
        if (segmentList != null && vertexList != null) {
            if (!vertexList.isEmpty()) {
                solveur = new SolveurTSP();
                solveur.setSegmentList(segmentList);
                solveur.setVertexList(vertexList);
                propertyChangeSupport.firePropertyChange("segmentArrayList", null, solveur.getSegmentList());
                propertyChangeSupport.firePropertyChange("map", null, solveur.getVertexList().getFirst());
            } else {
                propertyChangeSupport.firePropertyChange("errorMessage", null, "The file doesn't contain any vertex, please select an other file");
            }
        } else {
            propertyChangeSupport.firePropertyChange("errorMessage", null, "The selected file is not an XML file");
        }
    }

    /**
     * Create a new map where the route is displayed
     * @param vertexList The list of vertices
     * @param segmentList The list of segments
     */
    public void createRouteMap(ArrayList<Vertex> vertexList, ArrayList<Segment> segmentList){
        if (segmentList != null && vertexList != null) {
            if (!vertexList.isEmpty()) {
                propertyChangeSupport.firePropertyChange("createNewMap", null, vertexList);
                propertyChangeSupport.firePropertyChange("displaySegmentsRouteMap", null, segmentList);
            } else {
                propertyChangeSupport.firePropertyChange("errorMessage", null, "The file doesn't contain any vertex, please select an other file");
            }
        } else {
            propertyChangeSupport.firePropertyChange("errorMessage", null, "The selected file is not an XML file");
        }
    }

    /**
     * Update the list of deliveries
     * @param deliveryList The list of deliveries
     * @param entrepot The warehouse
     */
    public void updateDeliveryList(ArrayList<Delivery> deliveryList, Entrepot entrepot){
        if (deliveryList != null) {
            if (deliveryList.isEmpty() && entrepot == null) {
                propertyChangeSupport.firePropertyChange("errorMessage", null, "The selected file is not an XML file");
            } else if (entrepot != null){
                this.resetRoutes();
                pendingDeliveryArrayList = deliveryList;
                propertyChangeSupport.firePropertyChange("pendingDeliveryArrayList", null, pendingDeliveryArrayList);
                solveur.setEntrepot(entrepot);
            } else {
                propertyChangeSupport.firePropertyChange("errorMessage", null, "Entrepot location missing");
            }
        } else {
            propertyChangeSupport.firePropertyChange("errorMessage", null, "The delivery demand doesn't match the uploaded map");
        }
    }

    /**
     * Reset the routes of each courier
     */
    private void resetRoutes(){
        for (Courier courier :  courierArrayList) {
            courier.getRoute().setDeliveries(new ArrayList<>());
            courier.getRoute().setSegments(new ArrayList<>());
            propertyChangeSupport.firePropertyChange("courierRouteDeliveries", null, courier.getRoute().getDeliveries());
            propertyChangeSupport.firePropertyChange("resetClear", null, "reset");
        }
    }

    /**
     * Determine if the vertex corresponds to a delivery point or a pick-up point of the courier, or none of them
     * @param courier The courier you want to check the list of deliveries of
     * @param vertex The vertex you want to check whether of not it's a delivery or pick-up point
     * @return The nature of the vertex
     */
    private String isDeliveryPoint(Courier courier, Vertex vertex) {
        if (courier != null && vertex != null) {
            for (Delivery delivery : courier.getRoute().getDeliveries()) {
                if (delivery.getPickUpPt() == vertex) {
                    return "PICK_UP";
                } else if (delivery.getDeliveryPt() == vertex) {
                    return "DELIVERY";
                }
            }
            return null;
        }
        return null;
    }

    /**
     * Reset the map
     */
    public void resetMap(){
        propertyChangeSupport.firePropertyChange("resetMap", null, null);
    }

    /**
     * Put a delivery in the waiting list
     * @param delivery The delivery to be put in the waiting list
     */
    public void updateWaitingList(Delivery delivery){
        if (delivery != null) {
            delivery.setState(DeliveryState.POSTPONED);
            waitingDeliveryArrayList.add(delivery);
            propertyChangeSupport.firePropertyChange("updateWaitingList", null,waitingDeliveryArrayList);
        }
    }

    /**
     * Get the deliveries assigned to a courier to display them in the delivery tab
     * @param courier The courier
     */
    public void getCourierDeliveriesDeliveryTab(Courier courier){
        if (courier != null) {
            propertyChangeSupport.firePropertyChange("deliveryListDeliveryTab", null, courier.getRoute().getDeliveries());
        }
    }

    /**
     * Get the deliveries assigned to a courier to display them in the courier management tab
     * @param courier The courier
     */
    public void getCourierDeliveriesCourierTab(Courier courier){
        if (courier != null) {
            propertyChangeSupport.firePropertyChange("deliveryListCourierTab", null, courier.getRoute().getDeliveries());
        }
    }

    /**
     * Display the pick-up and delivery points of the delivery
     * @param delivery The delivery
     */
    public void displayDelivery(Delivery delivery){
        if (delivery != null) {
            ArrayList<Vertex> deliveryVertices = new ArrayList<>();
            deliveryVertices.add(delivery.getPickUpPt());
            deliveryVertices.add(delivery.getDeliveryPt());
            propertyChangeSupport.firePropertyChange("displayDelivery", null, deliveryVertices);
        }
    }

    /**
     * Get the vertices list of the route of the courier in order to export them
     * @param courier The courier
     * @return The list of vertices
     */
    public ArrayList<Vertex> getCourierVertexListToExport(Courier courier) {
        if (courier != null) {
            if (!courier.getRoute().getSegments().isEmpty()) {
                ArrayList<Vertex> courierVertex = new ArrayList<>();
                courierVertex.add(solveur.getEntrepot().getAddress());
                for (Segment segment : courier.getRoute().getSegments()) {
                    Vertex origin = segment.getOrigin();
                    Vertex destination = segment.getDestination();
                    String originType = this.isDeliveryPoint(courier, origin);
                    String destinationType = this.isDeliveryPoint(courier, destination);
                    if (originType != null && !courierVertex.contains(origin)) {
                        courierVertex.add(origin);
                    }
                    if (destinationType != null && !courierVertex.contains(destination)) {
                        courierVertex.add(destination);
                    }
                }
                return courierVertex;
            }
            return null;
        }
        return null;
    }

    /**
     * Get the segments list of the route of the courier in order to export them
     * @param courier The courier
     * @return The list of segments
     */
    public ArrayList<Segment> getCourierSegmentListToExport(Courier courier) {
        if (courier != null) {
            if (!courier.getRoute().getSegments().isEmpty()) {
                ArrayList<Segment> courierSegments = new ArrayList<>();
                for (Segment segment : courier.getRoute().getSegments()) {
                    for (Segment solveurSegment : solveur.getSegmentList()) {
                        if (solveurSegment.getOrigin() == segment.getOrigin() && solveurSegment.getDestination() == segment.getDestination()) {
                            courierSegments.add(solveurSegment);
                        }
                    }
                }
                return courierSegments;
            }
            return null;
        }
        return null;
    }

    /**
     * Get the segments list of the route of the courier in order to display them
     * @param courier The courier
     */
    public void getCourierSegmentList(Courier courier){
        if (courier != null) {
            if (!courier.getRoute().getSegments().isEmpty()) {
                ArrayList<Vector> vertexOrderTypeToDisplay = new ArrayList<>();
                ArrayList<Vertex> vertexDisplayed = new ArrayList<>();
                for (Segment segment : courier.getRoute().getSegments()) {
                    Vertex origin = segment.getOrigin();
                    Vertex destination = segment.getDestination();
                    String originType = this.isDeliveryPoint(courier, origin);
                    String destinationType = this.isDeliveryPoint(courier, destination);
                    if (originType != null && !vertexDisplayed.contains(origin)) {
                        Vector vector = new Vector();
                        vector.add(origin);
                            vertexDisplayed.add(origin);
                            vector.add(originType);
                            vertexOrderTypeToDisplay.add(vector);
                    } else if (destinationType != null && !vertexDisplayed.contains(destination)) {
                        Vector vector = new Vector();
                        vector.add(destination);
                            vertexDisplayed.add(destination);
                            vector.add(destinationType);
                            vertexOrderTypeToDisplay.add(vector);
                    }
                }
                String courierID = courier.getFirstName().concat(" ").concat(courier.getLastName());
                propertyChangeSupport.firePropertyChange("displayVerticesMainMap", null, vertexOrderTypeToDisplay);
                propertyChangeSupport.firePropertyChange("displaySegmentsMainMap", courierID, courier.getRoute().getSegments());
                propertyChangeSupport.firePropertyChange("displayEntrepot", null, solveur.getEntrepot().getAddress());
            } else {
                propertyChangeSupport.firePropertyChange("errorMessage", null, "No route is associated with this courier : you must assign him at least one delivery");
            }
        } else {
            propertyChangeSupport.firePropertyChange("errorMessage", null, "No courier selected");
        }
    }

    /**
     * Get the identity infos of the courier
     * @param courier The courier
     */
    public void getCourierInfo(Courier courier){
        propertyChangeSupport.firePropertyChange("courierInfo", null, courier);
    }

    //Getters
    public ArrayList<Delivery> getWaitingArrayList() { return waitingDeliveryArrayList; }
    public SolveurTSP getSolveur() {
        return solveur;
    }
    public ArrayList<Vertex> getVertexArrayList() { return solveur.getVertexList(); }
    public ArrayList<Courier> getCourierArrayList() { return courierArrayList; }
}



