package sepm.dao;

import java.util.ArrayList;
import java.sql.Date;

import sepm.entities.Invoice;
import sepm.entities.InvoiceState;
import sepm.exceptions.PersistenceException;

public interface InvoiceDAO {
	/**
	 * saves an invoice
	 * 
	 * @param invoice a valid invoice entity
	 * @throws PersistenceException if persistence fails, or invoice is invalid
	 */
	public void saveInvoice(Invoice invoice) throws PersistenceException;

    /**
     * deletes an invoice
     *
     * @param invoice a valid invoice entity
     * @throws PersistenceException if persistence fails, or invoice is invalid
     */
    public void deleteInvoice(Invoice invoice) throws PersistenceException;
	
	/**
	 * gets a stored invoice
	 * 
	 * @param id of a stored invoice
	 * @return a stored invoice, or null if invoice does not exist
	 * @throws PersistenceException if persistence not available
	 */
	public Invoice findInvoice(int id) throws PersistenceException;


    /**
     * gets stored invoices
     *
     * @return a list of stored invoices
     * @throws PersistenceException if persistence not available
     */
    public ArrayList<Invoice> findAllInvoices() throws PersistenceException;

    /**
	 * gets stored invoices
	 * 
	 * @param state of invoices to find
	 * @return a list of stored invoices
	 * @throws PersistenceException if persistence not available
	 */
	//public ArrayList<Invoice> findInvoices(InvoiceState state) throws PersistenceException;
	/**
	 * gets stored invoices
	 * 
	 * @param receiver of invoices to find
	 * @return a list of stored invoices
	 * @throws PersistenceException if persistence not available
	 */
    //public ArrayList<Invoice> findInvoices(String receiver) throws PersistenceException;
	/**
	 * gets stored invoices
	 * 
	 * @param from earliest date of an invoice
	 * @param to latest date of an invoice
	 * @return a list of stored invoices
	 * @throws PersistenceException if persistence not available
	 */
    //public ArrayList<Invoice> findInvoices(Date from, Date to) throws PersistenceException;
}
