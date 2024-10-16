package source;

import source.controler.Controler;
import source.model.Model;
import source.view.Interface;

public class AppName {

    public AppName() {
        Model model = new Model();
        Interface view = new Interface();
        Controler controler = new Controler();

        controler.setModel(model);
        controler.setView(view);

    }

    public static void main(String[] args) {
        new AppName();
    }
}
