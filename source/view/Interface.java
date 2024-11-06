package source.view;

import source.controller.Controller;
import source.model.Courier;
import source.model.Delivery;
import source.model.Segment;
import source.model.Vertex;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
    private JPanel mapPanel, deliveryPanel, controlMapPanel, mainPanelMap ;
    private JScrollPane scrollPanelMap;
    private JButton mapButton, deliveryButton, assignCourierButton, addCourierButton, removeCourierButton, exportRoutes, importRoutes;
    private JComboBox<String> unassignedDeliveryDropdown, courierDeliveryDropdown, courierMapDropdown;
    private DefaultComboBoxModel<String> unassignedModel, courierModel, courierMapModel, courierDeliveryModel;
    private Vector<String> couriers;
    private JList<String> courierList, selectedCourierListCourierTab, selectedCourierListDeliveryTab;
    private MapDisplay map;
    private JFileChooser fileChooserDelivery;
    private JFileChooser fileChooserMap;
    private JTextField courierFieldFirstName, courierFieldLastName, courierFieldPhoneNumber;
    private JLabel firstNameOfSelectedCourier, lastNameOfSelectedCourier, phoneNumberOfSelectedCourier;

    public void addController(Controller controller) {
        fileChooserDelivery.addActionListener(controller);
        fileChooserMap.addActionListener(controller);
        addCourierButton.addActionListener(controller);
        assignCourierButton.addActionListener(controller);
        removeCourierButton.addActionListener(controller);
        courierList.addListSelectionListener(controller);
        courierMapDropdown.addActionListener(controller);
    }

    public Interface() {
        couriers = new Vector<>();
        fileChooserDelivery = new JFileChooser();
        fileChooserDelivery.setCurrentDirectory(new File("."));
        fileChooserMap = new JFileChooser();
        fileChooserMap.setCurrentDirectory(new File("."));
        unassignedModel = new DefaultComboBoxModel<>();
        courierModel = new DefaultComboBoxModel<>(couriers);
        courierMapModel = new DefaultComboBoxModel<>(couriers);
        courierDeliveryModel = new DefaultComboBoxModel<>(couriers);
        unassignedDeliveryDropdown = new JComboBox<>(unassignedModel);
        courierDeliveryDropdown = new JComboBox<>(courierDeliveryModel);
        assignCourierButton = new JButton("Assign the delivery to this courier");
        courierMapDropdown = new JComboBox<>(courierMapModel);
        map = new MapDisplay();
        scrollPanelMap = new JScrollPane(map.getMapViewer());
        courierList = new JList<>(courierModel);
        courierList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

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
            fileChooserMap.showOpenDialog(null);
        });
    }

    private void setupDeliveryPanel() {
        deliveryPanel = new JPanel();
        deliveryButton = new JButton("Load Delivery");
        deliveryPanel.add(deliveryButton);
        tabPan.addTab("Delivery", deliveryPanel);

        deliveryButton.addActionListener(e -> {
            fileChooserDelivery.showOpenDialog(null);
        });
    }

    private void showSettingsDelivery(File file){
        // Supprimer le bouton de chargement
        deliveryPanel.remove(deliveryButton);
        deliveryPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Livraisons non attribuées
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        deliveryPanel.add(new JLabel("Pending Deliveries:"), gbc);

        gbc.gridx = 1;
        deliveryPanel.add(unassignedDeliveryDropdown, gbc);

        // Choisir un livreur
        gbc.gridx = 0;
        gbc.gridy = 3;
        deliveryPanel.add(new JLabel("Choose Courier:"), gbc);

        gbc.gridx = 1;
        deliveryPanel.add(courierDeliveryDropdown, gbc);

        // Bouton pour affecter le livreur
        gbc.gridx = 0;
        gbc.gridy = 4;
        deliveryPanel.add(assignCourierButton, gbc);

        //tabPan.addTab("Delivery", deliveryPanel);
        deliveryPanel.revalidate();  // Met à jour le layout
        deliveryPanel.repaint();  // Redessine le panel
    }

    private void setupCourierManagementPanel() {
        JPanel mainManagementPanel = new JPanel(new BorderLayout());

        // Configuration de infoPanel
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("Selected Courier Informations"));

        // Labels et ComboBox
        firstNameOfSelectedCourier = new JLabel("First Name: ");
        lastNameOfSelectedCourier = new JLabel("Last Name: ");
        phoneNumberOfSelectedCourier = new JLabel("Phone Number: ");
        JLabel listOfDeliveries = new JLabel("List of Deliveries: ");

        JComboBox<String> deliveryOfCourier = new JComboBox<>(new String[]{"Livraison 1", "Livraison 2", "Livraison 3"});
        deliveryOfCourier.setPreferredSize(new Dimension(150, 25));

        // Configuration des contraintes de GridBagLayout pour un alignement vertical centré
        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.anchor = GridBagConstraints.CENTER;  // Centrer horizontalement
        gbc1.insets = new Insets(10, 0, 10, 0);   // Espacement entre les éléments
        gbc1.gridx = 0;
        gbc1.fill = GridBagConstraints.HORIZONTAL;

        // Ajouter les composants avec un espacement vertical uniforme
        gbc1.gridy = 0;
        infoPanel.add(firstNameOfSelectedCourier, gbc1);

        gbc1.gridy = 1;
        infoPanel.add(lastNameOfSelectedCourier, gbc1);

        gbc1.gridy = 2;
        infoPanel.add(phoneNumberOfSelectedCourier, gbc1);

        gbc1.gridy = 3;
        infoPanel.add(listOfDeliveries, gbc1);

        gbc1.gridy = 4;
        infoPanel.add(deliveryOfCourier, gbc1);

        // Configuration de managementPanel
        JPanel managementPanel = new JPanel(new GridBagLayout());
        managementPanel.setBorder(BorderFactory.createTitledBorder("Management Panel"));

        // Organisation de la managementPanel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);  // Réduction de l'espacement entre les composants pour rapprocher les zones de texte
        gbc.anchor = GridBagConstraints.WEST;  // Alignement des composants à gauche

        // Utilisation d'un modèle dynamique pour faciliter les modifications
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel listActualCourier = new JLabel("Actual Courier List :");
        listActualCourier.setFont(new Font("Georgia", Font.BOLD, 16));  // Mettre le texte en gras et plus grand
        managementPanel.add(listActualCourier, gbc);

        // JScrollPane pour afficher la liste des livreurs
        JScrollPane scrollPane = new JScrollPane(courierList);
        scrollPane.setPreferredSize(new Dimension(200, 100));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1; // Le JScrollPane occupe une colonne
        managementPanel.add(scrollPane, gbc);

        // Bouton "Remove Courier" à droite de la liste
        removeCourierButton = new JButton("Remove courier");
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;  // Le bouton occupe une seule colonne
        gbc.fill = GridBagConstraints.HORIZONTAL; // Le bouton remplit la hauteur disponible
        managementPanel.add(removeCourierButton, gbc);

        // Ajouter le "New Courier Info" avant les champs de saisie
        JLabel newCourierInfoLabel = new JLabel("New Courier Info :");
        newCourierInfoLabel.setFont(new Font("Georgia", Font.BOLD, 16));  // Mettre le texte en gras et plus grand
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;  // Il occupe toute la largeur disponible
        gbc.anchor = GridBagConstraints.CENTER;
        managementPanel.add(newCourierInfoLabel, gbc);

        // Champ de texte pour "First Name"
        courierFieldFirstName = new JTextField(15);
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        managementPanel.add(new JLabel("First Name :"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        managementPanel.add(courierFieldFirstName, gbc);

        // Champ de texte pour "Last Name"
        courierFieldLastName = new JTextField(15);
        gbc.gridy = 5;
        gbc.gridx = 0;
        managementPanel.add(new JLabel("Last Name :"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        managementPanel.add(courierFieldLastName, gbc);

        // Champ de texte pour "Phone number"
        courierFieldPhoneNumber = new JTextField(15);
        gbc.gridy = 6;
        gbc.gridx = 0;
        managementPanel.add(new JLabel("Phone number :"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        managementPanel.add(courierFieldPhoneNumber, gbc);

        // Section des boutons
        // Bouton pour ajouter un nouveau livreur
        addCourierButton = new JButton("Add Courier");
        gbc.gridx = 0;
        gbc.gridy = 7;
        //gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        managementPanel.add(addCourierButton, gbc);


        /*GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);  // Espacement entre les composants
        gbc.anchor = GridBagConstraints.WEST;  // Alignement des composants à gauche

        // Utilisation d'un modèle dynamique pour faciliter les modifications
        // Section des couriers
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        managementPanel.add(new JLabel("Couriers :"), gbc);

        JScrollPane scrollPane = new JScrollPane(courierList);
        scrollPane.setPreferredSize(new Dimension(200, 100));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        managementPanel.add(scrollPane, gbc);

        // Ajouter le "New Courier Info" avant les champs de saisie
        JLabel newCourierInfoLabel = new JLabel("New Courier Info");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;  // Il occupe toute la largeur disponible
        gbc.anchor = GridBagConstraints.CENTER;
        managementPanel.add(newCourierInfoLabel, gbc);

        // Champ de texte pour "First Name"
        courierFieldFirstName = new JTextField(15);
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        managementPanel.add(new JLabel("First Name :"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        managementPanel.add(courierFieldFirstName, gbc);

        // Champ de texte pour "Last Name"
        courierFieldLastName = new JTextField(15);
        gbc.gridy = 4;
        gbc.gridx = 0;
        managementPanel.add(new JLabel("Last Name :"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        managementPanel.add(courierFieldLastName, gbc);

        // Champ de texte pour "Phone number"
        courierFieldPhoneNumber = new JTextField(15);
        gbc.gridy = 5;
        gbc.gridx = 0;
        managementPanel.add(new JLabel("Phone number :"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        managementPanel.add(courierFieldPhoneNumber, gbc);

        // Section des boutons
        // Bouton pour ajouter un nouveau livreur
        addCourierButton = new JButton("Add Courier");
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        managementPanel.add(addCourierButton, gbc);

        // Bouton pour supprimer le livreur sélectionné
        removeCourierButton = new JButton("Remove courier");
        gbc.gridx = 1;
        gbc.gridy = 6;
        managementPanel.add(removeCourierButton, gbc);*/

        // Division de mainManagementPanel en deux avec un JSplitPane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, managementPanel, infoPanel);
        splitPane.setResizeWeight(0.5); // Division égale
        splitPane.setDividerSize(5); // Largeur du séparateur

        // Ajout de splitPane dans mainManagementPanel
        mainManagementPanel.add(splitPane, BorderLayout.CENTER);

        tabPan.addTab("Courier Management", mainManagementPanel);

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("errorMessage")) {
            JOptionPane.showMessageDialog(this, evt.getNewValue());
        }
        if (evt.getPropertyName().equals("courierRouteDeliveries")) {
            //maj de la liste des livraisons du livreur
        }
        if (evt.getPropertyName().equals("map")) {
            mapPanel.removeAll();
            map.setCentre((Vertex) evt.getNewValue());
            // Initialisation du panneau principal
            mainPanelMap = new JPanel(new BorderLayout());

            // Panneau de contrôle amélioré
            controlMapPanel = new JPanel();
            controlMapPanel.setLayout(new BoxLayout(controlMapPanel, BoxLayout.Y_AXIS));
            controlMapPanel.setBorder(new EmptyBorder(15, 15, 15, 15)); // Ajouter des marges internes

            // Espacement entre les composants
            int componentSpacing = 15;

            // Création des composants
            JLabel select = new JLabel("Select a Courier:");
            select.setFont(new Font("Arial", Font.BOLD, 14)); // Font plus visible
            select.setAlignmentX(Component.CENTER_ALIGNMENT);

            courierMapDropdown.setMaximumSize(new Dimension(Integer.MAX_VALUE, courierMapDropdown.getPreferredSize().height));
            courierMapDropdown.setAlignmentX(Component.CENTER_ALIGNMENT);

            exportRoutes = new JButton("Export Routes");
            exportRoutes.setAlignmentX(Component.CENTER_ALIGNMENT);

            importRoutes = new JButton("Import Routes");
            importRoutes.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Ajout des composants avec des espaces entre eux
            controlMapPanel.add(select);
            controlMapPanel.add(Box.createVerticalStrut(componentSpacing)); // Espacement
            controlMapPanel.add(courierMapDropdown);
            controlMapPanel.add(Box.createVerticalStrut(componentSpacing)); // Espacement
            controlMapPanel.add(exportRoutes);
            controlMapPanel.add(Box.createVerticalStrut(componentSpacing)); // Espacement
            controlMapPanel.add(importRoutes);

            // Ajouter une bordure pour encadrer visuellement le panneau
            controlMapPanel.setBorder(BorderFactory.createTitledBorder("Control Panel"));

            // Ajout de controlMapPanel et scrollPanelMap dans mainPanelMap
            mainPanelMap.add(controlMapPanel, BorderLayout.EAST);
            mainPanelMap.add(scrollPanelMap, BorderLayout.CENTER);
            tabPan.setComponentAt(0,mainPanelMap);
        }
        if (evt.getPropertyName().equals("displayVertices")) {
            ArrayList<Vertex> vertexArrayList = (ArrayList<Vertex>) evt.getNewValue();
            for(Vertex vertex : vertexArrayList){
                map.displayVertex(vertex);
            }
        }
        if (evt.getPropertyName().equals("displaySegments")) {
            ArrayList<Segment> segmentArrayList = (ArrayList<Segment>) evt.getNewValue();
            if (!segmentArrayList.isEmpty()){
                for(Segment segment : segmentArrayList){
                    map.displaySegment(segment);
                }
            }
        }
        if (evt.getPropertyName().equals("courierArrayList")) {
            ArrayList<Courier> courierList  = (ArrayList<Courier>) evt.getNewValue();
            updateCourierList(courierList);
            JOptionPane.showMessageDialog(this, "Courier list updated");
            courierFieldFirstName.setText("");
            courierFieldLastName.setText("");
            courierFieldPhoneNumber.setText("");
        }
        if (evt.getPropertyName().equals("pendingDeliveryArrayList")) {
            File selectedFile = fileChooserDelivery.getSelectedFile();
            deliveryPanel.remove(deliveryButton);
            showSettingsDelivery(selectedFile);
            unassignedModel.removeAllElements();
            ArrayList<Delivery> deliveryArrayList = (ArrayList<Delivery>) evt.getNewValue();
            for (Delivery delivery : deliveryArrayList) {
                String deliveryString = delivery.getPickUpPt().getId() + "-" + delivery.getDeliveryPt().getId();
                unassignedModel.addElement(deliveryString);
            }
        }
        if (evt.getPropertyName().equals("pendingDeliveryRemoved")) {
            Delivery delivery = (Delivery) evt.getNewValue();
            String deliveryString = delivery.getPickUpPt().getId() + "-" + delivery.getDeliveryPt().getId();
            unassignedModel.removeElement(deliveryString);
        }

        if(evt.getPropertyName().equals("courierInfo")){
            Courier courier = (Courier) evt.getNewValue();
            System.out.println("Ca fonctionne");
            firstNameOfSelectedCourier.setText("First Name : " + courier.getFirstName());
            lastNameOfSelectedCourier.setText("Last Name : " + courier.getLastName());
            phoneNumberOfSelectedCourier.setText("Phone Number : " + courier.getPhoneNum());
        }
    }

    private void updateCourierList(ArrayList<Courier> newCourierList) {
        couriers.clear();
        courierMapDropdown.removeAllItems();
        courierDeliveryDropdown.removeAllItems();
        for (Courier courier : newCourierList) {
            couriers.add(courier.getFirstName()+ " " + courier.getLastName());
        }
        courierList.setListData(couriers);
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

    public JButton getAssignCourierButton(){
        return assignCourierButton;
    }

    public JComboBox<String> getCourierDeliveryComboBox() {
        return courierDeliveryDropdown;
    }

    public JComboBox<String> getPendingDeliveryComboBox() {
        return unassignedDeliveryDropdown;
    }

    public JList<String> getCourierList() {
        return courierList;    }

    public JComboBox<String> getCourierMapComboBox(){
        return courierMapDropdown;
    }
}

