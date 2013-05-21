package sepm.dao.hsqldb;

import org.apache.log4j.Logger;
import sepm.dao.InvoiceDAO;
import sepm.entities.Horse;
import sepm.entities.Invoice;
import sepm.entities.InvoiceState;
import sepm.exceptions.PersistenceException;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class DBInvoiceDAO implements InvoiceDAO {
    private static Logger logger = Logger.getLogger(DBInvoiceDAO.class);
    private DBHorseDAO horseDao = new DBHorseDAO();
    private static final String FIND_BY_ID = "SELECT id, date, receiver, insurancerate, state FROM Invoice WHERE id = ?";
    //private static final String FIND_BY_RECEIVER = "SELECT id, date, receiver, insurancerate, state FROM Invoice WHERE receiver LIKE ?";
    //private static final String FIND_BY_STATE = "SELECT id, date, receiver, insurancerate, state FROM Invoice WHERE state = ?";
    //private static final String FIND_BY_DATE = "SELECT id, date, receiver, insurancerate, state FROM Invoice WHERE";
    private static final String FIND_ALL = "SELECT id, date, receiver, insurancerate, state FROM Invoice";
    private static final String INSERT = "INSERT INTO Invoice (id, date, receiver, insurancerate, state) VALUES (DEFAULT, ?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE Invoice SET date = ?, receiver = ?, insurancerate = ?, state = ? WHERE id = ?";
    private static final String DELETE = "DELETE FROM Invoice where id = ?";
    // not supported
    // private static final String DELETE = "DELETE Invoice, consumed FROM Invoice, consumed WHERE id = ? AND Invoice.id = consumed.invoice_id";

    private static final String SELECT_CONSUMED_BY_ID = "SELECT horse_ID, amount FROM consumed WHERE invoice_id = ?";
    private static final String DELETE_CONSUMED = "DELETE FROM consumed WHERE invoice_id = ?";
    private static final String INSERT_CONSUMED = "INSERT INTO consumed (invoice_id, horse_id, amount) VALUES (?, ?, ?)";
    private static final String GET_LAST_ID = "CALL IDENTITY()";


    @Override
    public void saveInvoice(Invoice invoice) throws PersistenceException {
        try {
            PreparedStatement ps;
            Invoice in = findInvoice(invoice.getId());
            if(in != null) {
                ps = DatabaseConnection.getConnection().prepareStatement(UPDATE);
                ps.setInt(5, in.getId());
            } else {
                ps = DatabaseConnection.getConnection().prepareStatement(INSERT);
            }
            ps.setDate(1, new java.sql.Date(invoice.getDate().getTime()));
            ps.setString(2, invoice.getReceiver());
            ps.setInt(3, invoice.getInsurancerate());
            ps.setInt(4, invoice.getState().ordinal());
            ps.executeUpdate();
            ResultSet rs = DatabaseConnection.getConnection().prepareStatement(GET_LAST_ID).executeQuery();
            if(rs.next())
                invoice.setId(rs.getInt(1));
            else
                throw new SQLException("couldnt set new id");

            DatabaseConnection.getConnection().commit();

            PreparedStatement ps2 = DatabaseConnection.getConnection().prepareStatement(DELETE_CONSUMED);
            ps2.setInt(1, invoice.getId());
            ps2.executeUpdate();
            for(Horse h : invoice.getHorses().keySet()) {
                ps2 = DatabaseConnection.getConnection().prepareStatement(INSERT_CONSUMED);
                ps2.setInt(1, invoice.getId());
                ps2.setInt(2, h.getId());
                ps2.setInt(3, invoice.getHorses().get(h));
                if(horseDao.findHorse(h.getId()) == null)
                    horseDao.saveHorse(h);
                ps2.executeUpdate();
            }

            DatabaseConnection.getConnection().commit();

        } catch (SQLException e) {
            logger.error("invoice saving failed", e);
            throw new PersistenceException("invoice saving failed");
        } catch (PersistenceException e) {
            logger.error("horse saving failed", e);
            throw new PersistenceException("horse saving failed");
        }
    }

    @Override
    public void deleteInvoice(Invoice invoice) throws PersistenceException {
        try {
            PreparedStatement ps;
            ps = DatabaseConnection.getConnection().prepareStatement(DELETE_CONSUMED);
            ps.setInt(1, invoice.getId());
            ps.executeUpdate();
            ps = DatabaseConnection.getConnection().prepareStatement(DELETE);
            ps.setInt(1, invoice.getId());
            ps.executeUpdate();

            DatabaseConnection.getConnection().commit();
        } catch (SQLException e) {
            logger.error("invoice deleting failed", e);
            throw new PersistenceException("invoice deleting failed");
        }
    }

    @Override
    public Invoice findInvoice(int id) throws PersistenceException {
        try {
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(FIND_BY_ID);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            DatabaseConnection.getConnection().commit();
            if(rs.next()) {
                return createInvoiceFromResultSet(rs);
            }
        } catch (SQLException e) {
            logger.error("horse loading failed", e);
            throw new PersistenceException("horse loading failed");
        } catch (IOException e) {
            logger.error("horse loading (image loading) failed", e);
            throw new PersistenceException("horse loading (image loading) failed");
        }
        return null;
    }

    @Override
    public ArrayList<Invoice> findAllInvoices() throws PersistenceException {
        try {
            ArrayList<Invoice> invoices = new ArrayList<Invoice>();
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(FIND_ALL);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                invoices.add(createInvoiceFromResultSet(rs));
            }
            DatabaseConnection.getConnection().commit();
            return invoices;
        } catch (SQLException e) {
            logger.error("horse loading failed", e);
            throw new PersistenceException("horse loading failed");
        } catch (IOException e) {
            logger.error("horse loading (image loading) failed", e);
            throw new PersistenceException("horse loading (image loading) failed");
        }
    }
   /*
    @Override
    public ArrayList<Invoice> findInvoices(InvoiceState state) throws PersistenceException {
        try {
            ArrayList<Invoice> invoices = new ArrayList<Invoice>();
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(FIND_BY_STATE);
            ps.setInt(1, state.ordinal());
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                invoices.add(createInvoiceFromResultSet(rs));
            }
            DatabaseConnection.getConnection().commit();
            return invoices;
        } catch (SQLException e) {
            logger.error("horse loading failed", e);
            throw new PersistenceException("horse loading failed");
        } catch (IOException e) {
            logger.error("horse loading (image loading) failed", e);
            throw new PersistenceException("horse loading (image loading) failed");
        }
    }

    @Override
    public ArrayList<Invoice> findInvoices(String receiver)
            throws PersistenceException {
        try {
            ArrayList<Invoice> invoices = new ArrayList<Invoice>();
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(FIND_BY_RECEIVER);
            ps.setString(1, receiver);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                invoices.add(createInvoiceFromResultSet(rs));
            }
            DatabaseConnection.getConnection().commit();
            return invoices;
        } catch (SQLException e) {
            logger.error("horse loading failed", e);
            throw new PersistenceException("horse loading failed");
        } catch (IOException e) {
            logger.error("horse loading (image loading) failed", e);
            throw new PersistenceException("horse loading (image loading) failed");
        }
    }

    @Override
    public ArrayList<Invoice> findInvoices(Date from, Date to)
            throws PersistenceException {
        try {
            ArrayList<Invoice> invoices = new ArrayList<Invoice>();
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(FIND_BY_DATE);
            ps.setDate(1, from);
            ps.setDate(2, to);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                invoices.add(createInvoiceFromResultSet(rs));
            }
            DatabaseConnection.getConnection().commit();
            return invoices;
        } catch (SQLException e) {
            logger.error("horse loading failed", e);
            throw new PersistenceException("horse loading failed");
        } catch (IOException e) {
            logger.error("horse loading (image loading) failed", e);
            throw new PersistenceException("horse loading (image loading) failed");
        }
    }
    */

    private Invoice createInvoiceFromResultSet(ResultSet rs) throws SQLException, IOException, PersistenceException {
        Invoice invoice = new Invoice();
        invoice.setId(rs.getInt(1));
        invoice.setDate(rs.getDate(2));
        invoice.setReceiver(rs.getString(3));
        invoice.setInsurancerate(rs.getInt(4));
        invoice.setState(InvoiceState.values()[rs.getInt(5)]);

        PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(SELECT_CONSUMED_BY_ID);
        ps.setInt(1, rs.getInt(1));
        ResultSet rs2 = ps.executeQuery();
        HashMap<Horse, Integer> horses = new HashMap<Horse, Integer>();
        while (rs2.next()) {
            try {
                horses.put(horseDao.findHorse(rs2.getInt(1)), rs2.getInt(2));
            } catch (PersistenceException e) {
                logger.error("invoice - horses loading failed", e);
                throw new SQLException("invoice - horses loading failed");
            }
        }
        invoice.setHorses(horses);
        return invoice;
    }
}
