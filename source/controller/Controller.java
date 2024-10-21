package source.controller;

import source.model.Model;
import source.view.Interface;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Controller implements ActionListener {
    private Model model;
    private Interface view;

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    public Controller(Model model, Interface view) {
        this.model = model;
        this.view = view;
    }

    public void setModel(Model model) {}
    public void setView(Interface view) {}
}
