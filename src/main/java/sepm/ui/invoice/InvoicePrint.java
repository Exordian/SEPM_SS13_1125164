package sepm.ui.invoice;

import net.miginfocom.swing.MigLayout;
import org.apache.log4j.Logger;
import sepm.entities.Invoice;
import sepm.exceptions.ServiceException;
import sepm.services.InvoiceService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InvoicePrint extends JPanel{
    private InvoiceService invoiceService;
    private static Logger logger = Logger.getLogger(InvoicePrint.class);

    public InvoicePrint(Invoice i) {
        super(new MigLayout());
        invoiceService = new InvoiceService();

        String text = "";
        try {
            text = invoiceService.printInvoice(i);
        } catch (ServiceException e) {
            logger.error("couldnt get printable invoice");
            text = "ungültige Rechnung";
        }
        JTextArea ta = new JTextArea(text);
        ta.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JButton exit = new JButton("Schließen");
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Window w = SwingUtilities.getWindowAncestor(InvoicePrint.this);
                w.setVisible(false);
                w.dispose();
            }
        });

        this.add(new JScrollPane(ta), "grow");
        this.add(exit, "south");

    }
}
