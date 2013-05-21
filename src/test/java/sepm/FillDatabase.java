package sepm;

import sepm.entities.*;
import sepm.dao.*;
import sepm.dao.hsqldb.*;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;

public class FillDatabase {

    public static void main(String[] args) throws Exception {
        HorseDAO horseDAO = new DBHorseDAO();
        InvoiceDAO invoiceDAO = new DBInvoiceDAO();
        Connection dbc = DatabaseConnection.getConnection();

        insertFileToDB(dbc, "drop.sql");
        insertFileToDB(dbc, "create.sql");

        for(int i = 0; i <= 20; i++)
            horseDAO.saveHorse(generateRandomHorse());

        for(int i = 0; i <= 20; i++)
            invoiceDAO.saveInvoice(generateRandomInvoice(horseDAO.findAllNonDeletedHorses()));
    }

    public static Horse generateRandomHorse() {
        String[] names = {"Wendy", "Baiky", "Abajo", "Fabina", "Haifa", "Capone", "Capella"};
        Horse h = new Horse();
        h.setName(names[((int) (Math.random() * names.length))]);
        h.setPrice(Math.random()*100);
        h.setTherapy(Therapy.values()[((int) (Math.random() * Therapy.values().length))]);
        h.setGender(Gender.values()[((int) (Math.random() * Gender.values().length))]);
        h.setPicture(new BufferedImage(120, 120, BufferedImage.TYPE_INT_ARGB));
        return h;
    }

    public static Invoice generateRandomInvoice(ArrayList<Horse> horses) {
        String[] receiver = {"Hannes", "Doerte", "Dieter", "Fritz", "Richard", "Franz"};
        Invoice i = new Invoice();
        i.setReceiver(receiver[((int) (Math.random() * receiver.length))]);
        i.setState(InvoiceState.values()[((int) (Math.random() * InvoiceState.values().length))]);
        i.setInsurancerate((int) ((Math.random()*9)+5));
        i.setDate(new Date((long) (1000000*Math.random())));
        HashMap<Horse, Integer> hM = new HashMap<Horse, Integer>();
        for(int j = 0; j <= (Math.random()*horses.size()); j++)
            if(horses != null)
                hM.put(horses.get((int) (horses.size() * Math.random())), ((int) (Math.random() * 100)));
            else
                hM.put(generateRandomHorse(), ((int) (Math.random() * 100)));

        i.setHorses(hM);
        return i;
    }

    public static void insertFileToDB(Connection db, String file) throws Exception {
        BufferedReader in = new BufferedReader(new FileReader(FillDatabase.class.getClassLoader().getResource(file).getFile()));
        String sql = "";
        String line;
        while ((line = in.readLine()) != null) {
            sql += line + "\n";
            if (line.endsWith(";")) {
                db.prepareStatement(sql).execute();
                sql = "";
            }
        }
        db.commit();
    }
}
