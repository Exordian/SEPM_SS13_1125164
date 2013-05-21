package sepm;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.*;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import sepm.ui.MainPanel;

public class Main implements Runnable {

    private static Logger logger = Logger.getLogger(Main.class);
    
    public static void main(String[] args) {
        // Log4J
        BasicConfigurator.configure();
        
        // Start UI
        EventQueue.invokeLater(new Main());
    }
    
    @Override
    public void run() {
        JFrame f = new JFrame("Simpel Effizente Pferdetherapie Manager");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //f.setExtendedState(f.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        f.add(new MainPanel(), BorderLayout.CENTER);
        f.setResizable(false);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
}
