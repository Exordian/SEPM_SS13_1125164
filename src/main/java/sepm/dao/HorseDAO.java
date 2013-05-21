package sepm.dao;

import java.util.ArrayList;

import sepm.entities.Gender;
import sepm.entities.Horse;
import sepm.entities.Therapy;
import sepm.exceptions.PersistenceException;

public interface HorseDAO {
	/**
	 * saves a horse
	 * 
	 * @param horse a valid horse entity
	 * @throws PersistenceException if persistence fails, or horse is invalid
	 */
	public void saveHorse(Horse horse) throws PersistenceException;
	/**
	 * deletes a horse
	 * 
	 * @param horse a valid horse entity
	 * @throws PersistenceException if persistence fails
	 */
	public void deleteHorse(Horse horse) throws PersistenceException;

	/**
	 * gets a stored horse
	 * 
	 * @param id of a stored horse
	 * @return a stored horse, or null if horse does not exist
	 * @throws PersistenceException if persistence not available
	 */
	public Horse findHorse(int id) throws PersistenceException;


    /**
     * gets stored horses
     *
     * @return a list of stored horses
     * @throws PersistenceException if persistence not available
     */
    public ArrayList<Horse> findAllNonDeletedHorses() throws PersistenceException;

    /**
	 * gets stored horses
	 * 
	 * @param name of horses to find
	 * @return a list of stored horses
	 * @throws PersistenceException if persistence not available
	 */
	public ArrayList<Horse> findHorses(String name) throws PersistenceException;

    /**
     * gets stored horses
     *
     * @param therapy of horses to find
     * @return a list of stored horses
     * @throws PersistenceException if persistence not available
     */
    public ArrayList<Horse> findHorses(Therapy therapy) throws PersistenceException;

    /**
     * gets stored horses
     *
     * @param gender of horses to find
     * @return a list of stored horses
     * @throws PersistenceException if persistence not available
     */
    public ArrayList<Horse> findHorses(Gender gender) throws PersistenceException;

	/**
	 * gets stored horses
	 * 
	 * @param fromPrice min therapy price of horses to find
	 * @param toPrice max therapy price of horses to find
	 * @return a list of stored horses
	 * @throws PersistenceException if persistence not available
	 */
	public ArrayList<Horse> findHorses(double fromPrice, double toPrice) throws PersistenceException;
}
