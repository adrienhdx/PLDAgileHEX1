package source.controller;

import source.model.*;
import source.model.XmlExtractor;
import source.view.Interface;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class Controller implements ActionListener,ListSelectionListener {
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
            if (this.loadDeliveries()) {
                view.setSettingsDelivery(true);
                view.showMessage("Deliveries loaded successfully");
            } else {
                view.setSettingsDelivery(false);
                view.showMessage("Deliveries load failed: file is not an XML file or is not well formatted");
            };
        }
        if (e.getActionCommand().equals("ApproveSelection") && e.getSource() == view.getFileChooserMap()) {
            if (this.loadMap()) {
                view.showMessage("Map loaded successfully");
            } else {
                view.showMessage("Map load failed: file is not an XML file or is not well formatted");
            };
        }
        if (e.getSource() == view.getAddCourierButton()) {
            this.createCourier();
        }
        if (e.getSource() == view.getAssignCourierButton()) {
            this.assignDeliveriesCourier();
        }
        if (e.getSource() == view.getRemoveCourierButton()) {
            this.deleteCourier();
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e){
        if (!e.getValueIsAdjusting()) {
            System.out.println(e);
            if (e.getSource() == view.getCourierList()){
                System.out.println(view.getCourierList().getSelectedValue());
            }

        }
    }

    public boolean loadMap(){
        String filePath = view.getFileChooserMap().getSelectedFile().getAbsolutePath();
        ArrayList<Object> map = XmlExtractor.extractMap(filePath);
        if (map == null){
            return false;
        }
        ArrayList<Vertex> vertexList = (ArrayList<Vertex>) map.getFirst();
        ArrayList<Segment> segmentList = (ArrayList<Segment>) map.getLast();
        model.setSegmentArrayList(segmentList);
        model.setVertexArrayList(vertexList);
        return true;
    }

    public boolean loadDeliveries(){
        String filePath = view.getFileChooserDelivery().getSelectedFile().getAbsolutePath();
        if (model.getVertexArrayList() != null) {
            ArrayList<Object> deliveryArrayList = XmlExtractor.extractDeliveryDemand(filePath,model.getVertexArrayList());
            if (deliveryArrayList == null) {
                return false;
            }
            model.setPendingDeliveryArrayList((ArrayList<Delivery>) deliveryArrayList.get(1));
            model.setEntrepot((Entrepot) deliveryArrayList.getFirst());
            ArrayList<Segment> tour = model.ObtenirArrayListeSegmentsTSP(model.getPendingDeliveryArrayList());
            for (Segment s : tour) {
                System.out.println(s);
            }
            return true;
        }
        return false;
    }


    public void createCourier(){
        String newCourierFirstName = view.getCourierFieldFirstName().getText().trim();
        String newCourierLastName = view.getCourierFieldLastName().getText().trim();
        String newCourierPhoneNumber = view.getCourierFieldPhoneNumber().getText().trim();
        if(!newCourierFirstName.equals("") & !newCourierLastName.equals("") & !newCourierPhoneNumber.equals("")){
            model.addCourier(newCourierFirstName, newCourierLastName, newCourierPhoneNumber);
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
        if (courierInfo != null) {
            String[] splitInfo = courierInfo.split(" ");
            String firstName = splitInfo[0];
            String lastName = splitInfo[1];
            model.deleteCourier(firstName, lastName);
        }
    }

    public void withdrawDelivery(){}
}
