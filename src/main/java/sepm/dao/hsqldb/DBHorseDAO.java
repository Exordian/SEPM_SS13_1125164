package sepm.dao.hsqldb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import sepm.dao.HorseDAO;
import sepm.entities.Gender;
import sepm.entities.Horse;
import sepm.entities.Therapy;
import sepm.exceptions.PersistenceException;

public class DBHorseDAO implements HorseDAO {
    private static Logger logger = Logger.getLogger(DBHorseDAO.class);

    private static final String FIND_ALL_NONDELETED = "SELECT id, name, price, picture, therapy, gender, deleted FROM Horse WHERE deleted = false";
    private static final String FIND_BY_ID = "SELECT id, name, price, picture, therapy, gender, deleted FROM Horse WHERE id = ?";
    private static final String FIND_BY_NAME = "SELECT id, name, price, picture, therapy, gender, deleted FROM Horse WHERE deleted = false AND name LIKE ?";
    private static final String FIND_BY_THERAPY = "SELECT id, name, price, picture, therapy, gender, deleted FROM Horse WHERE deleted = false AND therapy = ?";
    private static final String FIND_BY_GENDER = "SELECT id, name, price, picture, therapy, gender, deleted FROM Horse WHERE deleted = false AND gender = ?";
    private static final String FIND_BY_PRICE = "SELECT id, name, price, picture, therapy, gender, deleted FROM Horse WHERE deleted = false AND price >= ? AND price <= ?";
    private static final String UPDATE_DELETED = "UPDATE Horse SET deleted = 1 WHERE id = ?";
    private static final String INSERT = "INSERT INTO Horse (id, name, price, picture, therapy, gender, deleted) VALUES (DEFAULT, ?, ?, ?, ?, ?, false)";
    private static final String GET_LAST_ID = "CALL IDENTITY()";

    @Override
    public void saveHorse(Horse horse) throws PersistenceException {
        try {
            Horse h = findHorse(horse.getId());
            if(h != null)
                deleteHorse(h);

            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(INSERT);
            ps.setString(1, horse.getName());
            ps.setDouble(2, horse.getPrice());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(horse.getPicture(), "png", baos);
            baos.flush();
            ps.setBinaryStream(3, new ByteArrayInputStream(baos.toByteArray()));
            ps.setInt(4, horse.getTherapy().ordinal());
            ps.setInt(5, horse.getGender().ordinal());
            ps.executeUpdate();
            ResultSet rs = DatabaseConnection.getConnection().prepareStatement(GET_LAST_ID).executeQuery();
            if(rs.next())
                horse.setId(rs.getInt(1));
            else
                throw new SQLException("couldnt set new id");
            DatabaseConnection.getConnection().commit();
        } catch (SQLException e) {
            logger.error("horse saving failed", e);
            throw new PersistenceException("horse saving failed");
        } catch (IllegalArgumentException e)  {
            logger.error("horse saving (image saving) failed", e);
            throw new PersistenceException("horse saving (image saving) failed");
        } catch (IOException e) {
            logger.error("horse saving (image saving) failed", e);
            throw new PersistenceException("horse saving (image saving) failed");
        }
    }

    @Override
    public void deleteHorse(Horse horse) throws PersistenceException {
        try {
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(UPDATE_DELETED);
            ps.setInt(1, horse.getId());
            ps.executeUpdate();
            DatabaseConnection.getConnection().commit();
        } catch (SQLException e) {
            logger.error("horse delete failed", e);
            throw new PersistenceException("horse delete failed");
        }
    }

    @Override
    public Horse findHorse(int id) throws PersistenceException {
        try {
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(FIND_BY_ID);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            DatabaseConnection.getConnection().commit();
            if(rs.next()) {
                return createHorseFromResultSet(rs);
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
    public ArrayList<Horse> findAllNonDeletedHorses() throws PersistenceException {
        try {
            ArrayList<Horse> horses = new ArrayList<Horse>();
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(FIND_ALL_NONDELETED);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                horses.add(createHorseFromResultSet(rs));
            }
            DatabaseConnection.getConnection().commit();
            return horses;
        } catch (SQLException e) {
            logger.error("horse loading failed", e);
            throw new PersistenceException("horse loading failed");
        } catch (IOException e) {
            logger.error("horse loading (image loading) failed", e);
            throw new PersistenceException("horse loading (image loading) failed");
        }
    }

    @Override
    public ArrayList<Horse> findHorses(String name) throws PersistenceException {
        try {
            ArrayList<Horse> horses = new ArrayList<Horse>();
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(FIND_BY_NAME);
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                horses.add(createHorseFromResultSet(rs));
            }
            DatabaseConnection.getConnection().commit();
            return horses;
        } catch (SQLException e) {
            logger.error("horse loading failed", e);
            throw new PersistenceException("horse loading failed");
        } catch (IOException e) {
            logger.error("horse loading (image loading) failed", e);
            throw new PersistenceException("horse loading (image loading) failed");
        }
    }

    @Override
    public ArrayList<Horse> findHorses(Therapy therapy)
            throws PersistenceException {
        try {
            ArrayList<Horse> horses = new ArrayList<Horse>();
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(FIND_BY_THERAPY);
            ps.setInt(1, therapy.ordinal());
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                horses.add(createHorseFromResultSet(rs));
            }
            DatabaseConnection.getConnection().commit();
            return horses;
        } catch (SQLException e) {
            logger.error("horse loading failed", e);
            throw new PersistenceException("horse loading failed");
        } catch (IOException e) {
            logger.error("horse loading (image loading) failed", e);
            throw new PersistenceException("horse loading (image loading) failed");
        }
    }

    @Override
    public ArrayList<Horse> findHorses(Gender gender) throws PersistenceException {
        try {
            ArrayList<Horse> horses = new ArrayList<Horse>();
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(FIND_BY_GENDER);
            ps.setInt(1, gender.ordinal());
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                horses.add(createHorseFromResultSet(rs));
            }
            DatabaseConnection.getConnection().commit();
            return horses;
        } catch (SQLException e) {
            logger.error("horse loading failed", e);
            throw new PersistenceException("horse loading failed");
        } catch (IOException e) {
            logger.error("horse loading (image loading) failed", e);
            throw new PersistenceException("horse loading (image loading) failed");
        }
    }

    @Override
    public ArrayList<Horse> findHorses(double fromPrice, double toPrice)
            throws PersistenceException {
        try {
            ArrayList<Horse> horses = new ArrayList<Horse>();
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(FIND_BY_PRICE);
            ps.setDouble(1, fromPrice);
            ps.setDouble(2, toPrice);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                horses.add(createHorseFromResultSet(rs));
            }
            DatabaseConnection.getConnection().commit();
            return horses;
        } catch (SQLException e) {
            logger.error("horse loading failed", e);
            throw new PersistenceException("horse loading failed");
        } catch (IOException e) {
            logger.error("horse loading (image loading) failed", e);
            throw new PersistenceException("horse loading (image loading) failed");
        }
    }

    private Horse createHorseFromResultSet(ResultSet rs) throws SQLException, IOException {
        Horse horse = new Horse();
        horse.setId(rs.getInt(1));
        horse.setName(rs.getString(2));
        horse.setPrice(rs.getDouble(3));
        horse.setPicture(ImageIO.read(rs.getBinaryStream(4)));
        horse.setTherapy(Therapy.values()[rs.getInt(5)]);
        horse.setGender(Gender.values()[rs.getInt(6)]);
        horse.setDeleted(rs.getBoolean(7));
        return horse;
    }
}
