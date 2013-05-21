package sepm.ui.invoice;


import net.miginfocom.swing.MigLayout;
import org.apache.log4j.Logger;
import sepm.entities.Horse;
import sepm.entities.Invoice;
import sepm.ui.horse.HorseSearch;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InvoiceAddHorses extends JPanel{

    private static Logger logger = Logger.getLogger(InvoiceAddHorses.class);
    private Invoice invoice;
    private HorseSearch horseSearch;


    public InvoiceAddHorses(Invoice i) {
        super(new MigLayout());
        this.invoice = i;
        horseSearch = new HorseSearch();
        this.add(horseSearch, "north");

        JButton add = new JButton("Hinzufügen");
        add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                for(Object o : horseSearch.getHorseList().getSelectedValues()) {
                    Horse h = (Horse) o;
                    if(invoice.getHorses().containsKey(o))
                        continue;
                    invoice.getHorses().put(h, 1);
                }
                Window w = SwingUtilities.getWindowAncestor(InvoiceAddHorses.this);
                w.setVisible(false);
                w.dispose();
            }
        });

        this.add(add);
    }
}
