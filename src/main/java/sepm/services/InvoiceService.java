package sepm.services;

import org.apache.log4j.Logger;
import sepm.dao.InvoiceDAO;
import sepm.dao.hsqldb.DBInvoiceDAO;
import sepm.entities.Horse;
import sepm.entities.Invoice;
import sepm.entities.InvoiceState;
import sepm.exceptions.PersistenceException;
import sepm.exceptions.ServiceException;
import sepm.exceptions.ValidationException;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class InvoiceService {
    private InvoiceDAO invoiceDao;
    private static Logger logger = Logger.getLogger(InvoiceService.class);


    public InvoiceService() {
        this.invoiceDao = new DBInvoiceDAO();
        logger.info("initialized");
    }


    public void createInvoice(Invoice i) throws ServiceException, ValidationException {
        updateInvoice(i);
    }

    public void updateInvoice(Invoice i) throws ServiceException, ValidationException {
        validateInvoice(i);
        try {
            Invoice oldinvoce = invoiceDao.findInvoice(i.getId());
            if(oldinvoce != null && (oldinvoce.getState() == InvoiceState.CLOSED || oldinvoce.getState() == InvoiceState.PAID))
                throw new ValidationException("Geschlossene oder bezahlte Rechnungen können nicht bearbeitet");

            invoiceDao.saveInvoice(i);
        } catch (PersistenceException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public ArrayList<Invoice> findAllInvoices() throws ServiceException {
        try {
            return invoiceDao.findAllInvoices();
        } catch (PersistenceException e) {
            throw new ServiceException("cannot get invoice");
        }
    }

    public void deleteInvoice(Invoice i) throws ServiceException, ValidationException {
        try {
            validateInvoice(i);
            Invoice oldinvoce = invoiceDao.findInvoice(i.getId());
            if(oldinvoce != null && (oldinvoce.getState() == InvoiceState.CLOSED || oldinvoce.getState() == InvoiceState.PAID))
                throw new ValidationException("Geschlossene oder bezahlte Rechnungen können nicht gelöscht");

            invoiceDao.deleteInvoice(i);
        } catch (PersistenceException e) {
            throw new ServiceException("cannot set invoice to paid");
        }
    }

    public void paidInvoice(Invoice i) throws ServiceException, ValidationException {
        try {
            validateInvoice(i);
            if(i.getState() != InvoiceState.CLOSED)
                throw new ValidationException("Rechnung muss geschlossen sein");

            i.setState(InvoiceState.PAID);
            invoiceDao.saveInvoice(i);
        } catch (PersistenceException e) {
            throw new ServiceException("cannot set invoice to paid");
        }
    }

    public String printInvoice(Invoice i) throws ServiceException {
        if(i == null)
            throw new ServiceException("keine Rechnugn angegeben");
        String text = "";
        DecimalFormat df =   new DecimalFormat( ",##0.00" );
        double totalSum = 0.0;
        text += "Rechnung - Pferdetherapie\n\n\n";
        text += "Ausgestellt an: " + i.getReceiver() + "\n\n";
        text += "Ausgestellt am: " + i.getDate() + "\n\n";
        text += "Folgende Therapien wurden verrechnet: \n\n";
        text += String.format("%1$-35s | %2$-40s | %3$-7s | %4$10s\n", "Pferd", "Therapie", "Anzahl", "Preis");
        text += String.format("%102s\n", "").replace(' ', '-');
        for(Horse h : i.getHorses().keySet()) {
            String therapy = "";
            switch(h.getTherapy().ordinal()) {
                case 0:
                    therapy = "Hippotherapie";
                    break;
                case 1:
                    therapy = "Heilpädagogisches Voltigieren (HPV)";
                    break;
                case 2:
                    therapy = "Heilpädagogisches Reiten (HPR)";
                    break;
                default:
                    logger.fatal("invalid horse in printInvoice");
                    break;
            }
            double price =  h.getPrice()*i.getHorses().get(h);
            totalSum += price;
            text += String.format("%1$-35s | %2$-40s | %3$7d | %4$10s\n", h.getName(), therapy, i.getHorses().get(h), df.format(price));
        }
        text += String.format("%102s\n", "").replace(' ', '-');
        if(i.getInsurancerate() != 0) {
            double insurance = totalSum * i.getInsurancerate() / 100;
            text += String.format("%1$88s | %2$10s\n", "Von der Versicherung bezahlt: ", df.format(insurance));
            totalSum -= insurance;
        }
        text += String.format("%1$88s | %2$10s\n", "Gesammt: ", df.format(totalSum));
        return text;
    }

    private void validateInvoice(Invoice i) throws ValidationException {
        if(i.getId() != -1 && i.getId() < 0)
            throw new ValidationException("ungültige id");

        if(i.getDate() == null)
            throw new ValidationException("kein datum");

        if(i.getReceiver() == null || i.getReceiver().isEmpty())
            throw new ValidationException("kein empfänger angegeben");

        if(i.getInsurancerate() != 0 && (i.getInsurancerate() > 15 || i.getInsurancerate() < 5))
            throw new ValidationException("die versicherungsrate muss zwischen 5 und 15 liegen");

        if(i.getState() == null)
            throw new ValidationException("ungültiger rechnung status");

        if(i.getHorses() == null)
            throw new ValidationException("keine pferde liste übergeben");
    }

}
