package source.controler;

import source.model.Model;
import source.view.Interface;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Controler implements ActionListener {
    private Model model;
    private Interface view;

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    public Controler(){}

    public void setModel(Model model) {}
    public void setView(Interface view) {}
}