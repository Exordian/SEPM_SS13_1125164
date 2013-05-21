package sepm.ui.invoice;

import net.miginfocom.swing.MigLayout;
import sepm.entities.Horse;
import sepm.entities.Invoice;
import sepm.entities.InvoiceState;
import sepm.exceptions.ServiceException;
import sepm.exceptions.ValidationException;
import sepm.services.InvoiceService;
import sepm.ui.horse.HorseView;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Map;

public class InvoiceView extends JPanel {
    private InvoiceService invoiceService;
    private Invoice invoice;
    private JLabel idText;
    private JTextField receiverText;
    private JTextField insuranceText;
    private JTextField dateText;
    private JComboBox stateComboBox;
    private JTable horseTable;
    private DateFormat df;
    private JCheckBox insuranceCheck;

    public InvoiceView() {
        this(new Invoice());
    }

    public InvoiceView(Invoice i) {
        super(new MigLayout("ins 10", "[][]30[][]"));
        invoiceService = new InvoiceService();
        this.invoice = i;
        df = DateFormat.getDateInstance(DateFormat.MEDIUM);

        // ID
        JLabel idLabel = new JLabel("ID: ");
        idText = new JLabel(String.valueOf(i.getId()));

        // Receiver
        JLabel receiverLabel = new JLabel("Empfänger: ");
        receiverText = new JTextField(i.getReceiver());

        // Date
        JLabel dateLabel = new JLabel("Datum: ");
        dateText = new JTextField(i.getDate() != null? df.format(i.getDate()) : df.format(Calendar.getInstance().getTime()));

        // Insurance
        JLabel insuranceLabel = new JLabel("Versicherung: ");
        insuranceCheck = new JCheckBox();
        insuranceCheck.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                insuranceText.setEnabled(insuranceCheck.isSelected());
            }
        });
        JLabel insuranceRateLabel = new JLabel("Versicherungs Rate: ");
        insuranceText = new JTextField(""+i.getInsurancerate());
        if(i.getInsurancerate() > 0) {
            insuranceCheck.setSelected(true);
        } else
            insuranceText.setEnabled(false);


        // Status
        JLabel stateLabel = new JLabel("Status: ");
        stateComboBox = new JComboBox();

        // Horse Table
        horseTable = new JTable();
        refreshHorseTable();
        horseTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if(horseTable.columnAtPoint(e.getPoint()) != 0)
                    return;
                Horse h = (Horse) horseTable.getValueAt(horseTable.rowAtPoint(e.getPoint()), 0);
                JFrame newframe = new JFrame("Pferd:" + h.getName());
                newframe.add(new HorseView(h));
                newframe.setResizable(false);
                newframe.pack();
                newframe.setVisible(true);
            }
        });

        stateComboBox.addItem("Neu");
        stateComboBox.addItem("Ausgestellt");
        stateComboBox.addItem("Bezahlt");
        if(i.getState() != null)
            stateComboBox.setSelectedIndex(i.getState().ordinal());

        JButton save = new JButton("Speichern");
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    if(invoice == null)
                        invoice = new Invoice();

                    invoice.setReceiver(receiverText.getText());
                    invoice.setState(InvoiceState.values()[stateComboBox.getSelectedIndex()]);
                    invoice.setInsurancerate(Integer.parseInt(insuranceText.getText()));
                    invoice.setDate(df.parse(dateText.getText()));
                    invoiceService.updateInvoice(invoice);
                    Window w = SwingUtilities.getWindowAncestor(InvoiceView.this);
                    w.setVisible(false);
                    w.dispose();
                } catch (ServiceException e) {
                    JOptionPane.showMessageDialog(InvoiceView.this, e.getMessage());
                } catch (ValidationException e) {
                    JOptionPane.showMessageDialog(InvoiceView.this, e.getMessage());
                } catch (ParseException e) {
                    JOptionPane.showMessageDialog(InvoiceView.this, "Falsches Datumsformat");
                }
            }
        });

        JButton addHorse = new JButton("Pferd hinzufügen");
        addHorse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFrame newframe = new JFrame("Pferde hinzufügen");
                newframe.add(new InvoiceAddHorses(invoice));
                newframe.setResizable(false);
                newframe.pack();
                newframe.setVisible(true);
                newframe.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        super.windowClosed(e);
                        refreshHorseTable();
                    }
                });
            }
        });

        JButton removeHorse = new JButton("Pferd löschen");
        removeHorse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                for(int i : horseTable.getSelectedRows()) {
                    invoice.getHorses().remove(horseTable.getValueAt(i, 0));
                }
                refreshHorseTable();
            }
        });


        JButton abort = new JButton("Abbrechen");
        abort.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Window w = SwingUtilities.getWindowAncestor(InvoiceView.this);
                w.setVisible(false);
                w.dispose();
            }
        });

        JButton delete = new JButton("Löschen");
        delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    invoiceService.deleteInvoice(invoice);
                    Window w = SwingUtilities.getWindowAncestor(InvoiceView.this);
                    w.setVisible(false);
                    w.dispose();
                } catch (ServiceException e) {
                    JOptionPane.showMessageDialog(InvoiceView.this, e.getMessage());
                } catch (ValidationException e) {
                    JOptionPane.showMessageDialog(InvoiceView.this, e.getMessage());
                }
            }
        });

        if(invoice.getState() != InvoiceState.CREATED) {
            receiverText.setEnabled(false);
            dateText.setEnabled(false);
            insuranceCheck.setEnabled(false);
            insuranceText.setEnabled(false);
            stateComboBox.setEnabled(false);
            addHorse.setEnabled(false);
            removeHorse.setEnabled(false);

            save.setEnabled(false);
            delete.setEnabled(false);
        }

        this.add(idLabel);
        this.add(idText);

        this.add(new JScrollPane(horseTable), "span 1 6");
        this.add(addHorse, "wrap");

        this.add(receiverLabel);
        this.add(receiverText, "growx");
        this.add(removeHorse, "wrap");

        this.add(dateLabel);
        this.add(dateText, "wrap");

        this.add(insuranceLabel);
        this.add(insuranceCheck, "wrap");
        this.add(insuranceRateLabel);
        this.add(insuranceText, "growx, wrap");

        this.add(stateLabel);
        this.add(stateComboBox, "wrap");

        this.add(save, "growx");
        this.add(abort, "growx");

        if(invoice != null && invoice.getId() >= 0)
            this.add(delete, "growx");
    }

    private void refreshHorseTable() {
        horseTable.setModel(new AbstractTableModel() {
            private String [] columnNames = {"Pferd", "Therapiestunden"};
            private Object [][] rowData;
            {
                rowData = new Object[invoice.getHorses().size()][2];
                int count = 0;
                for(Map.Entry<Horse,Integer> entry : invoice.getHorses().entrySet()){
                    rowData[count][0] = entry.getKey();
                    rowData[count][1] = entry.getValue();
                    count++;
                }
            }
            public String getColumnName(int col) {
                return columnNames[col].toString();
            }
            public int getRowCount() { return rowData.length; }
            public int getColumnCount() { return columnNames.length; }
            public Object getValueAt(int row, int col) {
                return rowData[row][col];
            }
            public boolean isCellEditable(int row, int col)
            { return (invoice.getState() == InvoiceState.CREATED && col == 1); }
            public void setValueAt(Object value, int row, int col) {
                rowData[row][col] = value;
                if(col == 1)
                    invoice.getHorses().put((Horse) getValueAt(row, 0), Integer.parseInt((String)value));
                fireTableCellUpdated(row, col);
            }
        });
    }


}
