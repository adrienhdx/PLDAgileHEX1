package source.controller;

import source.model.*;
import source.model.XmlExtractor;
import source.view.Interface;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Controller implements ActionListener {
    private Model model;
    private Interface view;

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println(e.getActionCommand());
        if (e.getActionCommand().equals("ApproveSelection") && e.getSource() == view.getFileChooserDelivery()) {
            this.loadDeliveries();
        }
        if(e.getActionCommand().equals("ApproveSelection") && e.getSource() == view.getFileChooserMap()){
            this.loadMap();
        }
        if (e.getSource() == view.getAddCourierButton()) {
            this.createCourier();
        }
        if(e.getSource() == view.getRemoveCourierButton()){
            this.deleteCourier();
        }
    }

    public Controller(Model model, Interface view) {
        this.model = model;
        this.view = view;
    }

    public void loadMap(){
        String filePath = view.getFileChooserMap().getSelectedFile().getAbsolutePath();
        ArrayList<Vertex> vertexList = (ArrayList<Vertex>) XmlExtractor.extractPlan(filePath);
        model.setVertexList(vertexList);
        System.out.println(vertexList.size());
    }

    public void loadDeliveries(){
        String filePath = view.getFileChooserDelivery().getSelectedFile().getAbsolutePath();
        Map<String, Vertex> vertexIdMap = new HashMap<>();
        if (model.getVertexList() != null) {
            ArrayList<Delivery> deliveryList = (ArrayList<Delivery>) XmlExtractor.extractDemande(filePath, model.getVertexList());
            model.setPendingDeliveryList(deliveryList);
            System.out.println(deliveryList.size());
        }
    }

    public void createCourier(){
        String newCourierFirstName = view.getCourierFieldFirstName().getText().trim();
        String newCourierLastName = view.getCourierFieldLastName().getText().trim();
        String newCourierPhoneNumber = view.getCourierFieldPhoneNumber().getText().trim();
        if(!newCourierFirstName.equals("") & !newCourierLastName.equals("") & !newCourierPhoneNumber.equals("")){
            model.addCourrier(newCourierFirstName, newCourierLastName, newCourierPhoneNumber);
        }
    }

    public void deleteCourier(){
        String courierInfo = (String) view.getCourierComboBox().getSelectedItem();
        if (courierInfo != null) {
            String[] splitInfo = courierInfo.split(" ");
            String firstName = splitInfo[0];
            String lastName = splitInfo[1];
            model.deleteCourier(firstName, lastName);
        }
    }

    public void assignDelivery(){}
    public void withdrawDelivery(){}
}
