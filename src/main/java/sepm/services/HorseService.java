package sepm.services;

import org.apache.log4j.Logger;
import sepm.dao.HorseDAO;
import sepm.dao.hsqldb.DBHorseDAO;
import sepm.entities.Gender;
import sepm.entities.Horse;
import sepm.entities.Therapy;
import sepm.exceptions.PersistenceException;
import sepm.exceptions.ServiceException;
import sepm.exceptions.ValidationException;

import java.util.ArrayList;

public class HorseService {
    private HorseDAO horseDao;
    private static Logger logger = Logger.getLogger(HorseService.class);

    public HorseService() {
        this.horseDao = new DBHorseDAO();
        logger.info("initialized");
    }

    public ArrayList<Horse> findAllHorses() throws ServiceException {
        try {
            return horseDao.findAllNonDeletedHorses();
        } catch (PersistenceException e) {
            throw new ServiceException("cannot get horses", e);
        }
    }

    public ArrayList<Horse> findHorsesByName(String name) throws ServiceException {
        try {
            return horseDao.findHorses(name);
        } catch (PersistenceException e) {
            throw new ServiceException("cannot get horses", e);
        }
    }

    public ArrayList<Horse> findHorsesByTherpy(Therapy therapy) throws ServiceException {
        try {
            return horseDao.findHorses(therapy);
        } catch (PersistenceException e) {
            throw new ServiceException("cannot get horses", e);
        }
    }

    public ArrayList<Horse> findHorsesByGender(Gender gender) throws ServiceException {
        try {
            return horseDao.findHorses(gender);
        } catch (PersistenceException e) {
            throw new ServiceException("cannot get horses", e);
        }
    }

    public ArrayList<Horse> findHorsesByPrice(double from, double to) throws ServiceException {
        try {
            return horseDao.findHorses(from, to);
        } catch (PersistenceException e) {
            throw new ServiceException("cannot get horses", e);
        }
    }

    public void createHorse(Horse h) throws ServiceException, ValidationException {
         updateHorse(h);
    }

    public void updateHorse(Horse h) throws ServiceException, ValidationException {
        validateHorse(h, false);

        try {
            horseDao.saveHorse(h);
        } catch (PersistenceException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public void deleteHorse(Horse h) throws ServiceException, ValidationException {
        validateHorse(h);
        try {
            horseDao.deleteHorse(h);
        } catch (PersistenceException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public void validateHorse(Horse h) throws ValidationException {
        if(h == null)
            throw new ValidationException("ungültiges pferd");

        if(h.getId() != -1 && h.getId() < 0)
            throw new ValidationException("ungültige id");

        if(h.getName() == null || h.getName().isEmpty())
            throw new ValidationException("ungültiger name");

        if(h.getPrice() < 0.00)
            throw new ValidationException("ungültiger preis");

        if(h.getPicture() == null)
            throw new ValidationException("kein bild");

        if(h.getTherapy() == null)
            throw new ValidationException("keine therapie");

        if(h.getGender() == null)
            throw new ValidationException("keine geschlecht");

    }

    public void validateHorse(Horse h, boolean deleted) throws ValidationException {
        validateHorse(h);

        if(h.isDeleted() != deleted)
            throw new ValidationException("invalid deletion state");
    }
}
