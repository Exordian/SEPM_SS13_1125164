package sepm.ui.horse;

import net.miginfocom.swing.MigLayout;
import org.apache.log4j.Logger;
import sepm.entities.Gender;
import sepm.entities.Horse;
import sepm.entities.Therapy;
import sepm.exceptions.ServiceException;
import sepm.exceptions.ValidationException;
import sepm.services.HorseService;
import sun.awt.image.ToolkitImage;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;

public class HorseView extends JPanel {
    private static Logger logger = Logger.getLogger(HorseView.class);
    private JLabel idText;
    private JTextField nameText;
    private JFormattedTextField priceText;
    private JComboBox therapyComboBox;
    private JComboBox genderComboBox;
    private JLabel imageLabel;
    private Horse horse;
    private HorseService horseService;
    private DecimalFormat format;

    public HorseView() {
        this(new Horse());
    }

    public HorseView(Horse h) {
        super(new MigLayout("ins 10", "[][]30[]"));
        horseService = new HorseService();
        this.horse = h;

        // ID
        JLabel idLabel = new JLabel("ID: ");
        idText = new JLabel(String.valueOf(h.getId()));

        // Name
        JLabel nameLabel = new JLabel("Name: ");
        nameText = new JTextField(h.getName());

        // Price
        JLabel priceLabel = new JLabel("Preis: ");
        format = new DecimalFormat();
        format.setMaximumFractionDigits(2);
        format.setMinimumFractionDigits(2);
        priceText = new JFormattedTextField(format);
        priceText.setValue(h.getPrice());

        // Therapy
        JLabel therapyLabel = new JLabel("Therapie: ");
        therapyComboBox = new JComboBox();

        therapyComboBox.addItem("Hippotherapie");
        therapyComboBox.addItem("Heilpädagogisches Voltigieren (HPV)");
        therapyComboBox.addItem("Heilpädagogisches Reiten (HPR)");

        // Gender
        JLabel genderLabel = new JLabel("Geschlecht: ");
        genderComboBox = new JComboBox();

        genderComboBox.addItem("Männlich");
        genderComboBox.addItem("Weiblich");

        imageLabel = new JLabel();
        imageLabel.setMaximumSize(new Dimension(120,120));
        if(horse.getPicture() != null)
            imageLabel.setIcon(new ImageIcon(horse.getPicture()));
        JButton imageButton = new JButton("Durchsuchen");
        imageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        return f.isDirectory() ||
                                f.getName().toLowerCase().endsWith(".jpg") ||
                                f.getName().toLowerCase().endsWith(".jpeg") ||
                                f.getName().toLowerCase().endsWith(".png") ||
                                f.getName().toLowerCase().endsWith(".gif");
                    }

                    @Override
                    public String getDescription() {
                        return "Bild-Dateien";
                    }

                });
                if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    try {
                        BufferedImage img = ImageIO.read(fileChooser.getSelectedFile());
                        BufferedImage newimg = new BufferedImage(120, 120, BufferedImage.TYPE_INT_RGB);
                        newimg.getGraphics().drawImage(img.getScaledInstance(120, 120, Image.SCALE_SMOOTH), 0, 0, null);
                        horse.setPicture(newimg);
                        imageLabel.setIcon(new ImageIcon(horse.getPicture()));
                    } catch (IOException e) {
                        logger.error("error on image selection");
                    }
                }
            }
        });

        JButton save = new JButton("Speichern");
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    if(horse == null)
                        horse = new Horse();

                    horse.setName(nameText.getText());
                    horse.setPrice(format.parse(priceText.getText()).doubleValue());
                    horse.setTherapy(Therapy.values()[therapyComboBox.getSelectedIndex()]);
                    horse.setGender(Gender.values()[genderComboBox.getSelectedIndex()]);
                    horseService.updateHorse(horse);
                    Window w = SwingUtilities.getWindowAncestor(HorseView.this);
                    w.setVisible(false);
                    w.dispose();
                } catch (ServiceException e) {
                    JOptionPane.showMessageDialog(HorseView.this, e.getMessage());
                } catch (ValidationException e) {
                    JOptionPane.showMessageDialog(HorseView.this, e.getMessage());
                } catch (ParseException e) {
                    JOptionPane.showMessageDialog(HorseView.this, "no valid price");
                }

            }
        });

        JButton abort = new JButton("Abbrechen");
        abort.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Window w = SwingUtilities.getWindowAncestor(HorseView.this);
                w.setVisible(false);
                w.dispose();
            }
        });

        JButton delete = new JButton("Löschen");
        delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(JOptionPane.showConfirmDialog(HorseView.this, "Pferde wirklich löschen?") != JOptionPane.YES_OPTION)
                    return;
                try {
                    horseService.deleteHorse(horse);
                    JOptionPane.showMessageDialog(HorseView.this, "Pferd erfolgreich gelöscht.");
                    Window w = SwingUtilities.getWindowAncestor(HorseView.this);
                    w.setVisible(false);
                    w.dispose();
                } catch (ServiceException e) {
                    JOptionPane.showMessageDialog(HorseView.this, e.getMessage());
                } catch (ValidationException e) {
                    JOptionPane.showMessageDialog(HorseView.this, e.getMessage());
                }
            }
        });


        // TODO: show deleted flag? savebutton => rename: save to new, dont show delete button

        if(horse.isDeleted()) {
            nameText.setEnabled(false);
            priceText.setEnabled(false);
            therapyComboBox.setEnabled(false);
            genderComboBox.setEnabled(false);
            save.setEnabled(false);
            imageButton.setEnabled(false);
        }

        this.add(idLabel);
        this.add(idText);
        this.add(imageLabel, "wrap, span 1 5");

        this.add(nameLabel);
        this.add(nameText, "growx, wrap");

        this.add(priceLabel);
        this.add(priceText, "growx, wrap");

        this.add(therapyLabel);
        this.add(therapyComboBox, "growx, wrap");

        this.add(genderLabel);
        this.add(genderComboBox, "growx, wrap");

        this.add(save, "growx");
        this.add(abort, "growx");
        this.add(imageButton, "wrap");

        if(horse != null && horse.getId() >= 0)
            this.add(delete, "growx");
    }

}
