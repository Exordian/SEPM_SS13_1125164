package sepm.ui;

import net.miginfocom.swing.MigLayout;
import sepm.ui.invoice.InvoicePrint;

import java.awt.*;

import javax.swing.*;

public class MainPanel extends JPanel {

    public MainPanel() {
    	super(new GridLayout(1, 1));
        JTabbedPane tab = new JTabbedPane();
        tab.addTab("Pferde", new HorsePanel());
        tab.addTab("Rechnungen", new InvoicePanel());
        //tab.addTab("Test", new InvoicePrint(null));
        this.add(tab);
    }
}
