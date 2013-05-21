package sepm.ui;

import net.miginfocom.swing.MigLayout;
import org.apache.log4j.Logger;
import sepm.entities.Horse;
import sepm.entities.Invoice;
import sepm.entities.InvoiceState;
import sepm.exceptions.ServiceException;
import sepm.exceptions.ValidationException;
import sepm.services.InvoiceService;
import sepm.ui.invoice.InvoicePrint;
import sepm.ui.invoice.InvoiceView;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Date;
import java.util.HashMap;

public class InvoicePanel extends JPanel {

    private static Logger logger = Logger.getLogger(InvoicePanel.class);
    private InvoiceService invoiceService;

    private JList invoiceList;
    private JButton changeInvoice;
    private JButton deleteInvoice;
    private JButton paidInvoice;
    private JButton printInvoice;

    public InvoicePanel() {
        super(new MigLayout("", "[80%][20%]"));
        invoiceService = new InvoiceService();

        // List
        invoiceList = new JList();
        refreshInvoiceList();
        invoiceList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                ListSelectionModel lsm = (ListSelectionModel) listSelectionEvent.getSource();
                if (!lsm.isSelectionEmpty()) {
                    changeInvoice.setEnabled(true);
                    printInvoice.setEnabled(true);

                    boolean allClosed = true;
                    boolean allDeleteable = true;
                    for(Object o : invoiceList.getSelectedValues()) {
                        Invoice i = (Invoice) o;
                        if(i.getState() != InvoiceState.CLOSED)
                            allClosed = false;
                        if(i.getState() != InvoiceState.CREATED)
                            allDeleteable = false;
                    }

                    deleteInvoice.setEnabled(allDeleteable);
                    paidInvoice.setEnabled(allClosed);

                } else {
                    printInvoice.setEnabled(false);
                    changeInvoice.setEnabled(false);
                    deleteInvoice.setEnabled(false);
                    paidInvoice.setEnabled(false);
                }
            }
        });
        this.add(new JScrollPane(invoiceList), "grow, spany, wmin 500");

        JButton addInvoice = new JButton("Rechnung anlegen");

        addInvoice.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFrame newframe = new JFrame("Neue Rechnung anlegen");
                newframe.add(new InvoiceView());
                newframe.setResizable(false);
                newframe.pack();
                newframe.setVisible(true);
                newframe.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        super.windowClosed(e);
                        refreshInvoiceList();
                    }
                });
            }
        });
        this.add(addInvoice, "grow, wrap");

        changeInvoice = new JButton("Rechnung bearbeiten");

        changeInvoice.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                for (Object o : invoiceList.getSelectedValues()) {
                    Invoice i = (Invoice) o;
                    // TODO: one frame, tabs
                    JFrame newframe = new JFrame("Rechnung bearbeiten");
                    newframe.add(new InvoiceView(i));
                    newframe.setResizable(false);
                    newframe.pack();
                    newframe.setVisible(true);
                    newframe.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosed(WindowEvent e) {
                            super.windowClosed(e);
                            refreshInvoiceList();
                        }
                    });
                }
            }
        });
        changeInvoice.setEnabled(false);
        this.add(changeInvoice, "growx, wrap");

        deleteInvoice = new JButton("Rechnung löschen");

        deleteInvoice.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                for (Object o : invoiceList.getSelectedValues()) {
                    Invoice i = (Invoice) o;
                    if (i == null)
                        JOptionPane.showMessageDialog(InvoicePanel.this, "Rechnung auswählen");
                    try {
                        invoiceService.deleteInvoice(i);
                        refreshInvoiceList();
                    } catch (ServiceException e) {
                        logger.error("couldnt delete horse");
                        JOptionPane.showMessageDialog(InvoicePanel.this, "Rechnung konnte nicht gelöscht werden");
                    } catch (ValidationException e) {
                        JOptionPane.showMessageDialog(InvoicePanel.this, "Rechnung kann nicht gelöscht werden");
                    }
                }
            }
        });
        deleteInvoice.setEnabled(false);
        this.add(deleteInvoice, "growx, wrap");

        paidInvoice = new JButton("Rechnung bezahlt");

        paidInvoice.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                for (Object o : invoiceList.getSelectedValues()) {
                    Invoice i = (Invoice) o;
                    if (i == null)
                        JOptionPane.showMessageDialog(InvoicePanel.this, "Rechnung auswählen");
                    try {
                        invoiceService.paidInvoice(i);
                        refreshInvoiceList();
                    } catch (ServiceException e) {
                        logger.error("couldnt set status to paid");
                        JOptionPane.showMessageDialog(InvoicePanel.this, "Rechnungsstatus konnte nicht geändert werden");
                    } catch (ValidationException e) {
                        JOptionPane.showMessageDialog(InvoicePanel.this, "Rechnungsstatus muss geschlossen sein");
                        logger.error("set paid - invoice not closed");
                    }
                }
            }
        });

        paidInvoice.setEnabled(false);
        this.add(paidInvoice, "growx, wrap");

        printInvoice = new JButton("Rechnung drucken");

        printInvoice.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                for (Object o : invoiceList.getSelectedValues()) {
                    Invoice i = (Invoice) o;
                    if (i == null)
                        JOptionPane.showMessageDialog(InvoicePanel.this, "Rechnung auswählen");

                    // TODO: one frame, tabs
                    JFrame newframe = new JFrame("Druckansicht Rechunung");
                    newframe.add(new InvoicePrint(i));
                    newframe.setResizable(false);
                    newframe.pack();
                    newframe.setVisible(true);
                }
            }
        });
        printInvoice.setEnabled(false);
        this.add(printInvoice, "growx, wrap");
    }

    private void refreshInvoiceList() {
        DefaultListModel listModel = new DefaultListModel();
        try {
            for(Invoice i : invoiceService.findAllInvoices()) {
                listModel.addElement(i);
            }
        } catch (ServiceException e) {
            logger.error("couldnt get invoices");
        }
        invoiceList.setModel(listModel);
    }

}
