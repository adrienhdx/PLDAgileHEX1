package source.view;

import source.controller.Controller;
import source.model.Courier;
import source.model.Segment;
import source.model.Vertex;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

public class Interface extends JFrame implements PropertyChangeListener {
    private JTabbedPane tabPan = new JTabbedPane();
    private JPanel mapPanel, deliveryPanel;
    private JScrollPane scrollPanelMap;
    private JButton mapButton, deliveryButton, addDeliveryButton, removeDeliveryButton, assignCourierButton, showRoutesButton, addCourierButton, removeCourierButton;
    private JComboBox<String> unassignedList, assignedList, courierDropdown;
    private DefaultComboBoxModel<String> unassignedModel, assignedModel, courierModel;
    private String[] initialDeliveries = {};
    private Vector<String> couriers = new Vector<String>();
    private MapDisplay map;
    private JFileChooser fileChooserDelivery;
    private JFileChooser fileChooserMap;
    private JTextField courierFieldFirstName, courierFieldLastName, courierFieldPhoneNumber;

    public void addController(Controller controller) {
        fileChooserDelivery.addActionListener(controller);
        fileChooserMap.addActionListener(controller);
        addCourierButton.addActionListener(controller);
        removeCourierButton.addActionListener(controller);
    }

    public Interface() {
        fileChooserDelivery = new JFileChooser();
        fileChooserDelivery.setCurrentDirectory(new File("."));
        fileChooserMap = new JFileChooser();
        fileChooserMap.setCurrentDirectory(new File("."));

        setTitle("App Delivery Services");
        setSize(600, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Onglet "Map"
        setupMapPanel();

        // Onglet "Delivery"
        setupDeliveryPanel();


        // Onglet "Gestion des Courriers"
        setupCourierManagementPanel();

        // Organisation de la fenêtre
        getContentPane().add(tabPan, BorderLayout.CENTER);
        setVisible(true);
        pack();
    }

    private void setupMapPanel() {
        mapPanel = new JPanel();
        mapButton = new JButton("Upload Map");
        mapPanel.add(mapButton);
        tabPan.addTab("Map", mapPanel);

        mapButton.addActionListener(e -> {
            int result = fileChooserMap.showOpenDialog(null);
        });
    }


    private void setupDeliveryPanel() {
        deliveryPanel = new JPanel(new GridLayout(0, 1));  // Utiliser GridLayout pour une meilleure ergonomie
        deliveryButton = new JButton("Load Delivery");
        deliveryPanel.add(deliveryButton);  // Ajout du bouton dans le panel "Delivery"
        tabPan.addTab("Delivery", deliveryPanel);

        deliveryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = fileChooserDelivery.showOpenDialog(null);  // Ouvre le dialogue pour choisir un fichier
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooserDelivery.getSelectedFile();
                    // Supprimer le bouton de chargement
                    deliveryPanel.remove(deliveryButton);
                    // Configurer le panneau de livraison
                    showSettingsDelivery(selectedFile);
                }
            }
        });
    }

    private void showSettingsDelivery(File file){
        // Supprimer le bouton de chargement
        deliveryPanel.remove(deliveryButton);
        deliveryPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        unassignedModel = new DefaultComboBoxModel<>(initialDeliveries);
        assignedModel = new DefaultComboBoxModel<>();
        courierDropdown.setModel(courierModel);

        // Livraisons non attribuées
        gbc.gridx = 0;
        gbc.gridy = 0;
        deliveryPanel.add(new JLabel("Pending Deliveries:"), gbc);

        unassignedList = new JComboBox<>(unassignedModel);
        gbc.gridx = 1;
        deliveryPanel.add(unassignedList, gbc);

        // Livraisons attribuées
        gbc.gridx = 0;
        gbc.gridy = 1;
        deliveryPanel.add(new JLabel("Assigned Deliveries:"), gbc);

        assignedList = new JComboBox<>(assignedModel);
        gbc.gridx = 1;
        deliveryPanel.add(assignedList, gbc);

        // Bouton pour attribuer une livraison
        addDeliveryButton = new JButton("Assign Deliveries");
        gbc.gridx = 0;
        gbc.gridy = 2;
        deliveryPanel.add(addDeliveryButton, gbc);

        // Bouton pour retirer une livraison
        removeDeliveryButton = new JButton("Remove Deliveries");
        gbc.gridx = 1;
        deliveryPanel.add(removeDeliveryButton, gbc);

        // Choisir un livreur
        gbc.gridx = 0;
        gbc.gridy = 3;
        deliveryPanel.add(new JLabel("Choose Courier:"), gbc);

        courierDropdown = new JComboBox<>(courierModel);
        gbc.gridx = 1;
        deliveryPanel.add(courierDropdown, gbc);

        // Bouton pour affecter le livreur
        assignCourierButton = new JButton("Assign Courier");
        gbc.gridx = 0;
        gbc.gridy = 4;
        deliveryPanel.add(assignCourierButton, gbc);

        // Bouton pour calculer l'itinéraire
        showRoutesButton = new JButton("Compute Route");
        gbc.gridx = 1;
        gbc.gridy = 4;
        deliveryPanel.add(showRoutesButton, gbc);

        // Actions des boutons
        addDeliveryButton.addActionListener(e -> assignDelivery());
        removeDeliveryButton.addActionListener(e -> removeDelivery());
        assignCourierButton.addActionListener(e -> assignCourier());
        showRoutesButton.addActionListener(e -> calculateRoute());

        //tabPan.addTab("Delivery", deliveryPanel);
        deliveryPanel.revalidate();  // Met à jour le layout
        deliveryPanel.repaint();  // Redessine le panel
    }

    private void assignDelivery() {
        String selectedDelivery = (String) unassignedList.getSelectedItem();
        if (selectedDelivery != null) {
            assignedModel.addElement(selectedDelivery);
            unassignedModel.removeElement(selectedDelivery);
        } else {
            JOptionPane.showMessageDialog(this, "No delivery selected.");
        }
    }

    private void removeDelivery() {
        String selectedDelivery = (String) assignedList.getSelectedItem();
        if (selectedDelivery != null) {
            unassignedModel.addElement(selectedDelivery);
            assignedModel.removeElement(selectedDelivery);
        } else {
            JOptionPane.showMessageDialog(this, "No delivery selected.");
        }
    }

    private void assignCourier() {
        String selectedCourier = (String) courierDropdown.getSelectedItem();
        String assignedDelivery = (String) assignedList.getSelectedItem();
        if (selectedCourier != null && assignedDelivery != null) {
            JOptionPane.showMessageDialog(this, "Delivery " + assignedDelivery + " managed by " + selectedCourier);
        } else {
            JOptionPane.showMessageDialog(this, "No courier or delivery selected");
        }
    }

    private void calculateRoute() {
        JOptionPane.showMessageDialog(this, "Computing route...");
        // Logique pour calculer l'itinéraire
    }

    private void setupCourierManagementPanel() {
        JPanel managementPanel = new JPanel(new GridBagLayout());  // Utilisation de GridBagLayout pour une disposition flexible et esthétique
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);  // Espacement entre les composants

        // Liste déroulante des livreurs existants
        courierModel = new DefaultComboBoxModel<>(couriers);
        courierDropdown = new JComboBox<>(courierModel);  // Utilisation d'un modèle dynamique pour faciliter les modifications
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        managementPanel.add(new JLabel("Couriers :"), gbc);
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        managementPanel.add(courierDropdown, gbc);

        // Champ de texte pour ajouter un nouveau livreur
        this.courierFieldFirstName = new JTextField(15);
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        managementPanel.add(new JLabel("First Name :"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        managementPanel.add(courierFieldFirstName, gbc);

        this.courierFieldLastName = new JTextField(15);
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        managementPanel.add(new JLabel("Last Name :"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        managementPanel.add(courierFieldLastName, gbc);

        this.courierFieldPhoneNumber = new JTextField(15);
        gbc.gridy = 5;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        managementPanel.add(new JLabel("Phone number :"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        managementPanel.add(courierFieldPhoneNumber, gbc);

        // Bouton pour ajouter un nouveau livreur
        addCourierButton = new JButton("Add courier");
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        managementPanel.add(addCourierButton, gbc);

        addCourierButton.addActionListener(e ->{
            if(!courierFieldFirstName.getText().trim().equals("") && !courierFieldLastName.getText().trim().equals("") && !courierFieldPhoneNumber.getText().trim().equals("")) {
                courierFieldFirstName.setText("");
                courierFieldLastName.setText("");
                courierFieldPhoneNumber.setText("");
                JOptionPane.showMessageDialog(null, "Courier added");
            }
            else{
                JOptionPane.showMessageDialog(null, "Please fill in all fields");
            }
        });

        // Bouton pour supprimer le livreur sélectionné
        removeCourierButton = new JButton("Remove courier");
        gbc.gridy = 2;
        managementPanel.add(removeCourierButton, gbc);

        tabPan.addTab("Courier Management", managementPanel);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("vertexList")) {
            mapPanel.removeAll();  // Retire tous les composants
            ArrayList<Vertex> vertexArrayList = (ArrayList<Vertex>) evt.getNewValue();
            map = new MapDisplay(vertexArrayList.getFirst());
            map.afficherNoeuds(vertexArrayList);
            scrollPanelMap = new JScrollPane(map.getMapViewer());
            tabPan.setComponentAt(0,scrollPanelMap);
        }

        if (evt.getPropertyName().equals("addCourierList")) {
            ArrayList<Courier> courierList  = (ArrayList<Courier>) evt.getNewValue();
            updateCourierList(courierList);
        }
        if (evt.getPropertyName().equals("deleteCourierList")) {
            courierDropdown.removeAllItems();
            ArrayList<Courier> courierList  = (ArrayList<Courier>) evt.getNewValue();
            updateCourierList(courierList);
        }
    }

    private void updateCourierList(ArrayList<Courier> courierList) {
        this.couriers.clear();
        for (Courier courier : courierList) {
            this.couriers.add(courier.getFirstName()+ " " + courier.getLastName());
        }
    }

    // Getters
    public JFileChooser getFileChooserDelivery() {
        return fileChooserDelivery;
    }

    public JFileChooser getFileChooserMap() {
        return fileChooserMap;
    }

    public JTextField getCourierFieldFirstName() {
        return courierFieldFirstName;
    }

    public JTextField getCourierFieldLastName() {
        return courierFieldLastName;
    }

    public JTextField getCourierFieldPhoneNumber() {
        return courierFieldPhoneNumber;
    }

    public JButton getAddCourierButton() {
        return addCourierButton;
    }

    public JButton getRemoveCourierButton() {
        return removeCourierButton;
    }

    public JComboBox<String> getCourierComboBox() {
        return courierDropdown;
    }
}

