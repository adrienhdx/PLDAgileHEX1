package source.model;


import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;

public class Model {
    private ArrayList<Delivery> assignedDeliveryArrayList;
    private ArrayList<Delivery> pendingDeliveryArrayList;
    private ArrayList<Delivery> waitingDeliveryArrayList;
    private ArrayList<Courier> courierArrayList;
    private PropertyChangeSupport propertyChangeSupport;
    private SolveurTSP solveur;

    private final double VITESSE_COURIER_KMH = 15.0;
    private final double DUREE_JOURNEE_MIN = 420.0;

    public Model(){
        propertyChangeSupport = new PropertyChangeSupport(this);
        this.courierArrayList = new ArrayList<>();
        this.pendingDeliveryArrayList = new ArrayList<>();
        this.assignedDeliveryArrayList = new ArrayList<>();
        this.waitingDeliveryArrayList = new ArrayList<>();
   }

    public void addPropertyChangeListener(PropertyChangeListener ArrayListener){
        propertyChangeSupport.addPropertyChangeListener(ArrayListener);
    }

    public ArrayList<Vertex> getVertexArrayList() { return solveur.getVertexList(); }

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

    public void addCourier (Courier courier){
        courierArrayList.add(courier);
        propertyChangeSupport.firePropertyChange("courierArrayList", null, courierArrayList);
    }

    public Courier getCourier(String firstName, String lastName) {
        for (Courier courier : courierArrayList){
            if (courier.getFirstName().equals(firstName) && courier.getLastName().equals(lastName)) {
                return courier;
            }
        }
        return null;
    }

    public Delivery getPendingDelivery(Long pickUpPtStr, Long deliveryPtStr) {
        for (Delivery delivery : pendingDeliveryArrayList){
            if (delivery.getPickUpPt().getId().equals(pickUpPtStr) && delivery.getDeliveryPt().getId().equals(deliveryPtStr)) {
                return delivery;
            }
        }
        return null;
    }

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

    public void assignDelivery(Courier courier, Delivery delivery){
        if (courier != null) {
            if (delivery != null) {
                courier.getRoute().getDeliveries().add(delivery);
                ArrayList<Segment> routeComputed = solveur.ObtenirArrayListeSegmentsTSP(courier.getRoute().getDeliveries());
                // CALCUL TEMPS
                double distanceRoute = solveur.getLongueurSolutionCourante();
                double tempsRoute = 0;
                for (int i=0; i<courier.getRoute().getDeliveries().size(); i++) {
                    // il y a 5 minutes de temps de pickup/delivery
                    tempsRoute += 5;
                }

                tempsRoute += ( ( distanceRoute / 1000 ) / VITESSE_COURIER_KMH )*60; // 15 km/h
                System.out.println("Distance de la solution trouvée : " + distanceRoute + " m");
                System.out.println("Temps de la solution trouvée : " + tempsRoute + " min");

                if (routeComputed != null && tempsRoute <= DUREE_JOURNEE_MIN) {
                    courier.getRoute().setSegments(routeComputed);
                    pendingDeliveryArrayList.remove(delivery);
                    assignedDeliveryArrayList.add(delivery);
                    delivery.setState(DeliveryState.ASSIGNED);
                    propertyChangeSupport.firePropertyChange("courierRouteDeliveries", null, courier.getRoute().getDeliveries());
                    propertyChangeSupport.firePropertyChange("pendingDeliveryRemoved", null, delivery);
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

    public void updateDeliveryList(ArrayList<Delivery> deliveryList, Entrepot entrepot){
        if (deliveryList != null) {
            if (deliveryList.isEmpty() && entrepot == null) {
                propertyChangeSupport.firePropertyChange("errorMessage", null, "The selected file is not an XML file");
            } else if (entrepot != null){
                this.resetModel();
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

    private void resetModel(){
        for (Courier courier :  courierArrayList) {
            deleteCourier(courier);
        }
    }

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

    public void getCourierDeliveriesDeliveryTab(Courier courier){
        if (courier != null) {
            propertyChangeSupport.firePropertyChange("deliveryListDeliveryTab", null, courier.getRoute().getDeliveries());
        }
    }

    public void getCourierDeliveriesCourierTab(Courier courier){
        if (courier != null) {
            propertyChangeSupport.firePropertyChange("deliveryListCourierTab", null, courier.getRoute().getDeliveries());
        }
    }

    public void displayDelivery(Delivery delivery){
        if (delivery != null) {
            ArrayList<Vertex> deliveryVertices = new ArrayList<>();
            deliveryVertices.add(delivery.getPickUpPt());
            deliveryVertices.add(delivery.getDeliveryPt());
            propertyChangeSupport.firePropertyChange("displayDelivery", null, deliveryVertices);
        }
    }

    public ArrayList<Vertex> getCourierVertexArrayList(Courier courier) {
        if (courier!= null) {
            if (!courier.getRoute().getSegments().isEmpty()) {
                ArrayList<Vertex> courierVertex = new ArrayList<>();
                courierVertex.add(solveur.getEntrepot().getAddress());
                for (Segment segment : courier.getRoute().getSegments()) {
                    Vertex origin = segment.getOrigine();
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

    public ArrayList<Segment> getCourierSegmentArrayList(Courier courier) {
        if (courier != null) {
            if (!courier.getRoute().getSegments().isEmpty()) {
                ArrayList<Segment> courierSegments = new ArrayList<>();
                for (Segment segment : courier.getRoute().getSegments()) {
                    for (Segment solveurSegment : solveur.getSegmentList()) {
                        if (solveurSegment.getOrigine() == segment.getOrigine() && solveurSegment.getDestination() == segment.getDestination()) {
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

    public void getCourierSegmentList(Courier courier){
        if (courier != null) {
            if (!courier.getRoute().getSegments().isEmpty()) {
                ArrayList<Vector> vertexOrderTypeToDisplay = new ArrayList<>();
                ArrayList<Vertex> vertexDisplayed = new ArrayList<>();
                for (Segment segment : courier.getRoute().getSegments()) {
                    Vertex origin = segment.getOrigine();
                    Vertex destination = segment.getDestination();
                    String originType = this.isDeliveryPoint(courier, origin);
                    String destinationType = this.isDeliveryPoint(courier, destination);
                    if (originType != null && destinationType != null) {
                        //System.out.println("test");
                    }
                    if (originType != null && !vertexDisplayed.contains(origin)) {
                        Vector vector = new Vector();
                        vector.add(origin);
                        //if (originType == "DELIVERY" && vertexDisplayed.contains(this.getPickUpFromDelivery(courier, origin))) {
                            vertexDisplayed.add(origin);
                            vector.add(originType);
                            vertexOrderTypeToDisplay.add(vector);
                        //}
                    } else if (destinationType != null && !vertexDisplayed.contains(destination)) {
                        Vector vector = new Vector();
                        vector.add(destination);
                        //if (destinationType == "DELIVERY" && vertexDisplayed.contains(this.getPickUpFromDelivery(courier, destination))) {
                            vertexDisplayed.add(destination);
                            vector.add(destinationType);
                            vertexOrderTypeToDisplay.add(vector);
                        //}
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

    public void resetMap(){
        propertyChangeSupport.firePropertyChange("resetMap", null, null);
    }

    public void getCourierInfo(Courier courier){
        propertyChangeSupport.firePropertyChange("courierInfo", null, courier);
    }

    public SolveurTSP getSolveur() {
        return solveur;
    }

    public ArrayList<Delivery> getWaitingArrayList() { return waitingDeliveryArrayList; }

    public void updateWaitingList(Delivery delivery){
        if (delivery != null) {
            delivery.setState(DeliveryState.POSTPONED);
            waitingDeliveryArrayList.add(delivery);
            propertyChangeSupport.firePropertyChange("updateWaitingList", null,waitingDeliveryArrayList);
        }
    }
}



