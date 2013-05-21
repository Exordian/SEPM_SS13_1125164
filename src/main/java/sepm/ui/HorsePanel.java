package sepm.ui;

import net.miginfocom.swing.MigLayout;
import org.apache.log4j.Logger;
import sepm.dao.hsqldb.DBHorseDAO;
import sepm.entities.Horse;
import sepm.exceptions.PersistenceException;
import sepm.exceptions.ServiceException;
import sepm.exceptions.ValidationException;
import sepm.services.HorseService;
import sepm.ui.horse.HorseSearch;
import sepm.ui.horse.HorseView;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.*;
import java.util.logging.ErrorManager;

public class HorsePanel extends JPanel {
    private static Logger logger = Logger.getLogger(HorsePanel.class);
    private HorseService horseService;

    private HorseSearch horseSearch;
    private JButton changeHorse;
    private JButton deleteHorse;

    public HorsePanel() {
        super(new MigLayout("", "[80%][20%]"));
        horseService = new HorseService();

        // List
        horseSearch = new HorseSearch();
        horseSearch.refreshHorseList();
        horseSearch.getHorseList().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                ListSelectionModel lsm = (ListSelectionModel) listSelectionEvent.getSource();
                if (!lsm.isSelectionEmpty()) {
                    changeHorse.setEnabled(true);
                    deleteHorse.setEnabled(true);
                } else {
                    changeHorse.setEnabled(false);
                    deleteHorse.setEnabled(false);
                }
            }
        });
        this.add(horseSearch, "grow, spany, wmin 500");

        JButton addHorse = new JButton("Pferd anlegen");

        addHorse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFrame newframe = new JFrame("Neues Pferd anlegen");
                newframe.add(new HorseView());
                newframe.pack();
                newframe.setResizable(false);
                newframe.setVisible(true);
                newframe.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        super.windowClosed(e);
                        horseSearch.refreshHorseList();
                    }
                });
            }
        });
        this.add(addHorse, "growx, wrap");

        changeHorse = new JButton("Pferd ändern");

        changeHorse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                for(Object o : horseSearch.getHorseList().getSelectedValues()) {
                    Horse h = (Horse) o;
                    // TODO: one frame, tabs
                    JFrame newframe = new JFrame("Pferd ändern");
                    newframe.add(new HorseView(h));
                    newframe.setResizable(false);
                    newframe.pack();
                    newframe.setVisible(true);
                    newframe.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosed(WindowEvent e) {
                            super.windowClosed(e);
                            horseSearch.refreshHorseList();
                        }
                    });
                }
            }
        });
        changeHorse.setEnabled(false);
        this.add(changeHorse, "growx, wrap");

        deleteHorse = new JButton("Pferd löschen");

        deleteHorse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String horses = "";
                for(Object o : horseSearch.getHorseList().getSelectedValues()) {
                    Horse h = (Horse) o;
                    horses += h + "\n";
                }
                if(JOptionPane.showConfirmDialog(HorsePanel.this, "Pferde wirklich löschen?\n"+horses) != JOptionPane.YES_OPTION)
                    return;
                horses = "";
                for(Object o : horseSearch.getHorseList().getSelectedValues()) {
                    Horse h = (Horse) o;
                    if(h == null)
                        JOptionPane.showMessageDialog(HorsePanel.this, "Pferd auswählen");
                    try {
                        horseService.deleteHorse(h);
                        horses += h + "\n";
                        horseSearch.refreshHorseList();
                    } catch (ServiceException e) {
                        logger.error("couldnt delete horse");
                        JOptionPane.showMessageDialog(HorsePanel.this, "Pferd konnte nicht gelöscht werden");
                    } catch (ValidationException e) {
                        logger.fatal("tried to delete an invalid horse - hack attempt?");
                    }
                }
                JOptionPane.showMessageDialog(HorsePanel.this, "Folgende Pferde wurden gelöscht: \n"+horses);
            }
        });
        deleteHorse.setEnabled(false);
        this.add(deleteHorse, "growx, wrap");
	}
}
