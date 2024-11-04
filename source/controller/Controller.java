package source.controller;

import source.model.*;
import source.model.XmlExtractor;
import source.view.Interface;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class Controller implements ActionListener {
    private final Model model;
    private final Interface view;

    public Controller(Model model, Interface view) {
        this.model = model;
        this.view = view;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println(e);
        if (e.getActionCommand().equals("ApproveSelection") && e.getSource() == view.getFileChooserDelivery()) {
            this.loadDeliveries();
        }
        if(e.getActionCommand().equals("ApproveSelection") && e.getSource() == view.getFileChooserMap()){
            this.loadMap();
        }
        if (e.getSource() == view.getAddCourierButton()) {
            this.createCourier();
        }
        if (e.getSource() == view.getAssignCourierButton()) {
            this.assignDeliveriesCourier();
        }
        if(e.getSource() == view.getRemoveCourierButton()){
            this.deleteCourier();
        }
    }

    public void loadMap(){
        String filePath = view.getFileChooserMap().getSelectedFile().getAbsolutePath();
        ArrayList<Vertex> vertexList = (ArrayList<Vertex>) XmlExtractor.extractMap(filePath).getFirst();
        ArrayList<Segment> segmentList = (ArrayList<Segment>) XmlExtractor.extractMap(filePath).getLast();
        model.setSegmentArrayList(segmentList);
        model.setVertexArrayList(vertexList);
    }

    public void loadDeliveries(){
        String filePath = view.getFileChooserDelivery().getSelectedFile().getAbsolutePath();
        Map<String, Vertex> vertexIdMap = new HashMap<>();
        if (model.getVertexArrayList() != null) {
            ArrayList<Object> demandArrayList = XmlExtractor.extractDeliveryDemand(filePath,model.getVertexArrayList());
            model.setPendingDeliveryArrayList((ArrayList<Delivery>) demandArrayList.get(1));
            model.setEntrepot((Entrepot) demandArrayList.getFirst());
//            List<Segment> tour = model.computeTour(deliveryArrayList);
//            for (Segment s : tour) {
//                System.out.println(s);
//            }
        }
    }

    public void createCourier(){
        String newCourierFirstName = view.getCourierFieldFirstName().getText().trim();
        String newCourierLastName = view.getCourierFieldLastName().getText().trim();
        String newCourierPhoneNumber = view.getCourierFieldPhoneNumber().getText().trim();
        Courier courier = model.createCourier(newCourierFirstName, newCourierLastName, newCourierPhoneNumber);
        if (courier != null) {
            model.addCourier(courier);
        }
    }

    public void assignDeliveriesCourier(){
        String courierStr = view.getCourierDeliveryComboBox().getSelectedItem().toString();
        int index = courierStr.indexOf(' '); //trouve l'index de l'espace
        String lastName = courierStr.substring(index+1);
        String firstName = courierStr.substring(0,index);
        Courier selectedCourier = null;
        for (Courier courier : model.getCourierArrayList()) {
            if (courier.getFirstName().equals(firstName) && courier.getLastName().equals(lastName)) {
                selectedCourier = courier;
            }
        }
        if (selectedCourier != null){
            Vector<String> attributedDeliveriesList = view.getAttributedDeliveries();
            ArrayList<Delivery> deliveryArrayList = new ArrayList<>();
            for (String attributedDelivery : attributedDeliveriesList) {
                index = attributedDelivery.indexOf('-');
                Long deliveryPtStr = Long.parseLong(attributedDelivery.substring(index + 1));
                Long pickUpPtStr = Long.parseLong(attributedDelivery.substring(0, index));
                for (Delivery delivery : model.getPendingDeliveryArrayList()) {
                    if (delivery.getPickUpPt().getId().equals(pickUpPtStr) && delivery.getDeliveryPt().getId().equals(deliveryPtStr)) {
                        deliveryArrayList.add(delivery);
                    }
                }
            }
            for (Delivery deliveryAtt : deliveryArrayList) {
                assignDelivery(selectedCourier, deliveryAtt);
                model.getPendingDeliveryArrayList().remove(deliveryAtt);
                model.getAssignedDeliveryArrayList().add(deliveryAtt);
                deliveryAtt.setState(DeliveryState.ASSIGNED);
            }
        }
        System.out.println(selectedCourier);
    }
    public void assignDelivery(Courier courier, Delivery delivery){
        courier.getRoute().getDeliveries().add(delivery);
    }

    public void deleteCourier(){
        String courierInfo = (String) view.getCourierManagementComboBox().getSelectedItem();
        String firstName = "";
        String lastName = "";
        if (courierInfo != null) {
            String[] splitInfo = courierInfo.split(" ");
            firstName = splitInfo[0];
            lastName = splitInfo[1];
        }
        Courier courier = model.getCourier(firstName, lastName);
        model.deleteCourier(courier);
    }

    public void withdrawDelivery(){}
}
