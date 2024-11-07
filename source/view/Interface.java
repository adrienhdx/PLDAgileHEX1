package source.view;

import source.controller.Controller;
import source.model.Courier;
import source.model.Delivery;
import source.model.Segment;
import source.model.Vertex;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Vector;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Interface extends JFrame implements PropertyChangeListener {
    private JTabbedPane tabPan = new JTabbedPane();
    private JPanel mapPanel, deliveryPanel, controlMapPanel, mainPanelMap, mainPanelDeliveries, controlDeliveriesPanel ;
    private JScrollPane scrollPanelMap, scrollPanelDeliveriesMap;
    private JButton mapButton, deliveryButton, assignCourierButton, addCourierButton, removeCourierButton, exportRoutes, importRoutes, waitingListButton, exportWaitingListButton;
    private JComboBox<String> unassignedDeliveryDropdown, courierDeliveryDropdown, courierMapDropdown, waitingListDropdown;
    private DefaultComboBoxModel<String> unassignedModel, courierModel, courierMapModel, courierDeliveryModel, waitingListModel;
    private Vector<String> couriers, selectedCourierVectorCourierTab, selectedCourierVectorDeliveryTab;
    private JList<String> courierList,courierListMapTab, selectedCourierListCourierTab, selectedCourierListDeliveryTab;
    private MapDisplay map, mapDelivery;
    private JFileChooser fileChooserDelivery;
    private JFileChooser fileChooserMap;
    private JFileChooser fileExportDelivery;
    private JTextField courierFieldFirstName, courierFieldLastName, courierFieldPhoneNumber;
    private JLabel firstNameOfSelectedCourier, lastNameOfSelectedCourier, phoneNumberOfSelectedCourier, mapLoadingBeforeDelivery;
    private Vertex mapDefault ;

    public void addController(Controller controller) {
        fileChooserDelivery.addActionListener(controller);
        fileChooserMap.addActionListener(controller);
        fileExportDelivery.addActionListener(controller);
        addCourierButton.addActionListener(controller);
        assignCourierButton.addActionListener(controller);
        waitingListButton.addActionListener(controller); //A IMPLEMENTER !!!!!!!
        exportWaitingListButton.addActionListener(controller); //A IMPLEMENTER !!!!!

        removeCourierButton.addActionListener(controller);
        courierList.addListSelectionListener(controller);
        courierListMapTab.addListSelectionListener(controller);
        courierDeliveryDropdown.addActionListener(controller);
        waitingListDropdown.addActionListener(controller);
    }

    public Interface() {
        couriers = new Vector<>();
        selectedCourierVectorDeliveryTab = new Vector<>();
        selectedCourierVectorCourierTab = new Vector<>();
        fileChooserDelivery = new JFileChooser();
        fileChooserDelivery.setCurrentDirectory(new File("."));
        fileChooserMap = new JFileChooser();
        fileChooserMap.setCurrentDirectory(new File("."));
        fileExportDelivery = new JFileChooser();
        fileExportDelivery.setCurrentDirectory(new File("."));
        unassignedModel = new DefaultComboBoxModel<>();
        courierModel = new DefaultComboBoxModel<>(couriers);
        courierMapModel = new DefaultComboBoxModel<>(couriers);
        courierDeliveryModel = new DefaultComboBoxModel<>(couriers);
        waitingListModel = new DefaultComboBoxModel<>();
        unassignedDeliveryDropdown = new JComboBox<>(unassignedModel);
        courierDeliveryDropdown = new JComboBox<>(courierDeliveryModel);
        waitingListDropdown = new JComboBox<>(waitingListModel);
        assignCourierButton = new JButton("Assign the delivery to this courier");
        waitingListButton = new JButton("Put this delivery in the waiting list");
        exportWaitingListButton = new JButton("Export deliveries in the waiting list");
        courierMapDropdown = new JComboBox<>(courierMapModel);
        map = new MapDisplay();
        mapDelivery = new MapDisplay();
        scrollPanelMap = new JScrollPane(map.getMapViewer());
        scrollPanelDeliveriesMap = new JScrollPane(mapDelivery.getMapViewer());
        courierList = new JList<>(courierModel);
        courierList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        selectedCourierListDeliveryTab = new JList<>();
        selectedCourierListDeliveryTab.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        selectedCourierListCourierTab = new JList<>();
        selectedCourierListCourierTab.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        courierListMapTab = new JList<>(couriers);
        courierListMapTab.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

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
        deliveryPanel.setLayout(new BoxLayout(deliveryPanel, BoxLayout.Y_AXIS));
        mapLoadingBeforeDelivery = new JLabel("You must load a map before loading deliveries.");
        mapLoadingBeforeDelivery.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        mapLoadingBeforeDelivery.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        deliveryButton = new JButton("Load Delivery");
        deliveryButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        deliveryButton.setEnabled(false);
        deliveryPanel.add(deliveryButton);
        deliveryPanel.add(mapLoadingBeforeDelivery);
        tabPan.addTab("Delivery", deliveryPanel);

        deliveryButton.addActionListener(e -> {
            fileChooserDelivery.showOpenDialog(null);
        });
    }

    private void showSettingsDelivery(){
        deliveryPanel.removeAll();

        mapDelivery.setCentre(mapDefault);

        // Initialisation du panneau principal
        mainPanelDeliveries = new JPanel(new BorderLayout());

        // Config panneau de controle
        controlDeliveriesPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 1, 10, 1 );

        // Livraisons non attribuées
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        controlDeliveriesPanel.add(new JLabel("Pending Deliveries:"), gbc);

        gbc.gridx = 1;
        controlDeliveriesPanel.add(unassignedDeliveryDropdown, gbc);

        // Choisir un livreur
        gbc.gridx = 0;
        gbc.gridy = 2;
        controlDeliveriesPanel.add(new JLabel("Choose Courier:"), gbc);

        gbc.gridy = 2 ;
        gbc.gridx = 1;
        controlDeliveriesPanel.add(courierDeliveryDropdown, gbc);

        //JLabel pour la liste des livraisons deja assignée au livreur choisi
        gbc.gridx = 0;
        gbc.gridy = 3;
        controlDeliveriesPanel.add(new JLabel("Delivery list of the selected courier :"), gbc);

        // Liste des livraisons du courier
        JScrollPane scrollPaneDelivery = new JScrollPane(selectedCourierListDeliveryTab);
        scrollPaneDelivery.setPreferredSize(new Dimension(200, 100));
        scrollPaneDelivery.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPaneDelivery.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        gbc.gridx = 1;
        gbc.gridy = 3;
        controlDeliveriesPanel.add(scrollPaneDelivery, gbc);

        //Bouton pour passer la livraison en attente
        gbc.gridx = 1;
        gbc.gridy = 4;
        controlDeliveriesPanel.add(waitingListButton, gbc);

        //JLabel pour la liste des livraisons deja assignée au livreur choisi
        gbc.gridx = 0;
        gbc.gridy = 5;
        controlDeliveriesPanel.add(new JLabel("Deliveries in the waiting list : "), gbc);

        //Liste des livraison en attente
        gbc.gridy = 5 ;
        gbc.gridx = 1;
        controlDeliveriesPanel.add(waitingListDropdown, gbc);

        // Bouton pour affecter le livreur
        gbc.gridx = 0;
        gbc.gridy = 4;
        controlDeliveriesPanel.add(assignCourierButton, gbc);

        //Bouton exporter les livraisons en attente
        gbc.gridx = 1;
        gbc.gridy = 6;
        controlDeliveriesPanel.add(exportWaitingListButton, gbc);

        controlDeliveriesPanel.setBorder(BorderFactory.createTitledBorder("Management Deliveries Panel"));

        // Ajout de controlMapPanel et scrollPanelDeliveries dans mainPanelMap
        mainPanelDeliveries.add(controlDeliveriesPanel, BorderLayout.EAST);
        mainPanelDeliveries.add(scrollPanelDeliveriesMap, BorderLayout.CENTER);
        tabPan.setComponentAt(1,mainPanelDeliveries);
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

        // Liste des livraisons du courier
        JScrollPane scrollPaneDelivery = new JScrollPane(selectedCourierListCourierTab);
        scrollPaneDelivery.setPreferredSize(new Dimension(200, 100));
        scrollPaneDelivery.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPaneDelivery.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

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
        infoPanel.add(scrollPaneDelivery, gbc1);

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
        if (evt.getPropertyName().equals("map")) {
            deliveryButton.setEnabled(true);
            deliveryPanel.remove(mapLoadingBeforeDelivery);
            mapPanel.removeAll();
            map.setCentre((Vertex) evt.getNewValue());
            mapDefault = (Vertex) evt.getNewValue();
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

            JScrollPane scrollPaneCouriers = new JScrollPane(courierListMapTab);
            scrollPaneCouriers.setPreferredSize(new Dimension(200, 200));
            scrollPaneCouriers.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            scrollPaneCouriers.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scrollPaneCouriers.setAlignmentX(Component.CENTER_ALIGNMENT);

            exportRoutes = new JButton("Export Routes");
            exportRoutes.setAlignmentX(Component.CENTER_ALIGNMENT);

            exportRoutes.addActionListener(e -> {
                LocalDate actualDate = LocalDate.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                fileExportDelivery.setSelectedFile(new File("pendingDelivery-" + actualDate.format(formatter) + ".xml"));
                if (fileExportDelivery.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    JOptionPane.showMessageDialog(null, "File has been saved at " + fileExportDelivery.getSelectedFile().getAbsolutePath());
                } else {
                    JOptionPane.showMessageDialog(null, "File has not been saved.");
                };

            });

            importRoutes = new JButton("Import Routes");
            importRoutes.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Ajout des composants avec des espaces entre eux
            controlMapPanel.add(select);
            controlMapPanel.add(Box.createVerticalStrut(componentSpacing)); // Espacement
            controlMapPanel.add(scrollPaneCouriers);
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
        if (evt.getPropertyName().equals("displayEntrepot")) {
            Vertex entrepotAddress = (Vertex) evt.getNewValue();
            map.displayVertex(entrepotAddress, "Warehouse", Color.red,true);
        }
        if (evt.getPropertyName().equals("resetMap")) {
            map.hideAll();
        }
        if (evt.getPropertyName().equals("displayVertices")) {
            ArrayList<Vector> vertexVectorArrayList = (ArrayList<Vector>) evt.getNewValue();
            for (Vector vector : vertexVectorArrayList) {
                if (vector.get(1).equals("PICK_UP")) {
                    map.displayVertex((Vertex) vector.getFirst(), Integer.toString(vertexVectorArrayList.indexOf(vector)+1),Color.cyan,false);
                } else {
                    map.displayVertex((Vertex) vector.getFirst(), Integer.toString(vertexVectorArrayList.indexOf(vector)+1),Color.orange,false);
                }
            }
        }
        if (evt.getPropertyName().equals("displaySegments")) {
            ArrayList<Segment> segmentArrayList = (ArrayList<Segment>) evt.getNewValue();
            if (!segmentArrayList.isEmpty()){
                for(Segment segment : segmentArrayList){
                    map.displaySegment(segment,Color.blue);
                }
            }
        }
        if (evt.getPropertyName().equals("courierArrayList")) {
            ArrayList<Courier> courierList  = (ArrayList<Courier>) evt.getNewValue();
            updateCourierList(courierList);
            courierFieldFirstName.setText("");
            courierFieldLastName.setText("");
            courierFieldPhoneNumber.setText("");
            JOptionPane.showMessageDialog(this, "Courier list updated");
        }
        if (evt.getPropertyName().equals("pendingDeliveryArrayList")) {
            deliveryPanel.remove(deliveryButton);
            showSettingsDelivery();
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
        if (evt.getPropertyName().equals("deliveryListDeliveryTab")){
            ArrayList<Delivery>  deliveryList = (ArrayList<Delivery>) evt.getNewValue();
            updateDeliveryListDeliveryTab(deliveryList);
        }
        if (evt.getPropertyName().equals("deliveryListCourierTab")){
            ArrayList<Delivery>  deliveryList = (ArrayList<Delivery>) evt.getNewValue();
            updateDeliveryListCourierTab(deliveryList);
        }
        if(evt.getPropertyName().equals("courierInfo")){
            Courier courier = (Courier) evt.getNewValue();
            firstNameOfSelectedCourier.setText("First Name : " + courier.getFirstName());
            lastNameOfSelectedCourier.setText("Last Name : " + courier.getLastName());
            phoneNumberOfSelectedCourier.setText("Phone Number : " + courier.getPhoneNum());
        }
        if(evt.getPropertyName().equals("updateWaitingList")){
            ArrayList<Delivery>  deliveryWaitingList = (ArrayList<Delivery>) evt.getNewValue();
            waitingListModel.removeAllElements();
            for (Delivery delivery : deliveryWaitingList) {
                String deliveryWaitingString = delivery.getPickUpPt().getId() + "-" + delivery.getDeliveryPt().getId();
                waitingListModel.addElement(deliveryWaitingString);
                unassignedModel.removeElement(deliveryWaitingString);
            }
        }
    }

    private void updateCourierList(ArrayList<Courier> newCourierList) {
        couriers.clear();
        courierDeliveryDropdown.removeAllItems();
        for (Courier courier : newCourierList) {
            couriers.add(courier.getFirstName()+ " " + courier.getLastName());
        }
        courierList.setListData(couriers);
        courierListMapTab.setListData(couriers);
    }

    private void updateDeliveryListDeliveryTab(ArrayList<Delivery> newDeliveryList) {
        selectedCourierVectorDeliveryTab.clear();
        for (Delivery delivery : newDeliveryList) {
            selectedCourierVectorDeliveryTab.add(delivery.getPickUpPt().getId() + "-" + delivery.getDeliveryPt().getId());
        }
        selectedCourierListDeliveryTab.setListData(selectedCourierVectorDeliveryTab);
    }

    private void updateDeliveryListCourierTab(ArrayList<Delivery> newDeliveryList) {
        selectedCourierVectorCourierTab.clear();
        for (Delivery delivery : newDeliveryList) {
            selectedCourierVectorCourierTab.add(delivery.getPickUpPt().getId() + "-" + delivery.getDeliveryPt().getId());
        }
        selectedCourierListCourierTab.setListData(selectedCourierVectorCourierTab);
    }


    // Getters
    public JFileChooser getFileChooserDelivery() {
        return fileChooserDelivery;
    }

    public JFileChooser getFileChooserMap() {
        return fileChooserMap;
    }

    public JFileChooser getFileExportDelivery() { return fileExportDelivery; }

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

    public JButton getExportRoutesButton(){
        return exportRoutes;
    }

    public JComboBox<String> getCourierDeliveryComboBox() {
        return courierDeliveryDropdown;
    }

    public JComboBox<String> getPendingDeliveryComboBox() {
        return unassignedDeliveryDropdown;
    }

    public JList<String> getCourierList() {
        return courierList;
    }

    public JList<String> getCourierMapList() {
        return courierListMapTab;
    }

    public JButton getWaitingListButton() {return waitingListButton;}

}

