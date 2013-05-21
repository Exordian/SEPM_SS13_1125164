package sepm.ui.horse;

import net.miginfocom.swing.MigLayout;
import org.apache.log4j.Logger;
import sepm.entities.Gender;
import sepm.entities.Horse;
import sepm.entities.Therapy;
import sepm.exceptions.ServiceException;
import sepm.services.HorseService;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class HorseSearch extends JPanel {
    private static Logger logger = Logger.getLogger(HorseSearch.class);
    private HorseService horseService;
    private JComboBox searchType;
    private JTextField searchName;
    private JComboBox therapyType;
    private JComboBox genderType;
    private JList horseList;
    private JTextField searchPriceFrom;
    private JTextField searchPriceTo;
    private JLabel priceFrom;
    private JLabel priceTo;

    public HorseSearch() {
        super(new MigLayout("","[grow 10][grow 5][grow 40][grow 5][grow 40]","grow"));
        horseService = new HorseService();

        searchType = new JComboBox();
        searchType.addItem("Name");
        searchType.addItem("Preis");
        searchType.addItem("Therapie");
        searchType.addItem("Geschlecht");
        therapyType = new JComboBox();
        therapyType.addItem("Hippotherapie");
        therapyType.addItem("Heilpädagogisches Voltigieren (HPV)");
        therapyType.addItem("Heilpädagogisches Reiten (HPR)");
        genderType = new JComboBox();
        genderType.addItem("Männlich");
        genderType.addItem("Weiblich");

        searchName = new JTextField();
        searchPriceFrom = new JTextField();
        searchPriceTo = new JTextField();
        priceFrom = new JLabel("von:");
        priceTo = new JLabel("bis:");


        this.add(searchType, "growx");
        this.add(searchName, "span 4, growx");

        searchType.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                HorseSearch.this.remove(searchPriceFrom);
                HorseSearch.this.remove(searchPriceTo);
                HorseSearch.this.remove(searchName);
                HorseSearch.this.remove(therapyType);
                HorseSearch.this.remove(priceFrom);
                HorseSearch.this.remove(priceTo);
                HorseSearch.this.remove(genderType);
                switch(searchType.getSelectedIndex()) {
                    case 0:
                        HorseSearch.this.add(searchName, "span 5, growx");
                        break;
                    case 1:
                        HorseSearch.this.add(priceFrom, "growx");
                        HorseSearch.this.add(searchPriceFrom, "growx");
                        HorseSearch.this.add(priceTo, "growx");
                        HorseSearch.this.add(searchPriceTo, "growx");
                        break;
                    case 2:
                        HorseSearch.this.add(therapyType, "span 5, growx");
                        break;
                    case 3:
                        HorseSearch.this.add(genderType, "span 5, growx");
                    default:
                        logger.fatal("selected not exsistent item");
                        break;
                }
                HorseSearch.this.revalidate();
                refreshHorseList();
            }
        });

        searchName.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                refreshHorseList();
            }
        });

        searchPriceFrom.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                refreshHorseList();
            }
        });

        searchPriceTo.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                refreshHorseList();
            }
        });

        therapyType.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                refreshHorseList();
            }
        });

        genderType.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                refreshHorseList();
            }
        });

        horseList = new JList();
        refreshHorseList();
        this.add(new JScrollPane(horseList), "span 5,dock south");

    }

    public void refreshHorseList() {
        DefaultListModel listModel = new DefaultListModel();
        try {
            ArrayList<Horse> horses = new ArrayList<Horse>();
            switch(searchType.getSelectedIndex()) {
                case 0:
                    if(searchName.getText().isEmpty())
                        horses = horseService.findAllHorses();
                    else
                        horses = horseService.findHorsesByName("%"+searchName.getText()+"%");
                    break;
                case 1:
                    double from = 0.00;
                    double to = Double.MAX_VALUE;
                    try {
                        from = Double.parseDouble(searchPriceFrom.getText());
                        to = Double.parseDouble(searchPriceTo.getText());
                    } catch (NumberFormatException e) {}

                    horses = horseService.findHorsesByPrice(from, to);
                    break;
                case 2:
                    horses = horseService.findHorsesByTherpy(Therapy.values()[therapyType.getSelectedIndex()]);
                    break;
                case 3:
                    horses = horseService.findHorsesByGender(Gender.values()[genderType.getSelectedIndex()]);
                default:
                    break;
            }
            for(Horse h : horses) {
                listModel.addElement(h);
            }
        } catch (ServiceException e) {
            logger.error("couldnt get horses");
        }
        horseList.setModel(listModel);
    }

    public JList getHorseList() {
        return horseList;
    }
}
