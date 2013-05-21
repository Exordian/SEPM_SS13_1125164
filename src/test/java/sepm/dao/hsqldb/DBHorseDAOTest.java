package sepm.dao.hsqldb;

import org.junit.After;
import org.junit.Before;
import sepm.dao.HorseDAOTest;
import sepm.entities.Gender;
import sepm.entities.Horse;
import sepm.entities.Therapy;

import java.awt.image.BufferedImage;

public class DBHorseDAOTest extends HorseDAOTest {
    public DBHorseDAOTest() {
        setHorseDAO(new DBHorseDAO());
    }


    @Before
    public void setUp() throws Exception {
        validHorse = new Horse();
        validHorse.setName("Look at my Horse");
        validHorse.setGender(Gender.MALE);
        validHorse.setTherapy(Therapy.HIPPOTHERAPHY);
        validHorse.setPrice(1.00);
        validHorse.setPicture(new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB));
        validHorse.setDeleted(false);

        corruptHorse = new Horse();
        corruptHorse.setName(null);
        corruptHorse.setGender(null);
        corruptHorse.setTherapy(null);
        corruptHorse.setPrice(-1.00);
        corruptHorse.setPicture(null);
        corruptHorse.setDeleted(false);
    }

    @After
    public void tearDown() throws Exception {
        DatabaseConnection.getConnection().prepareStatement("DELETE FROM Horse").executeUpdate();
        DatabaseConnection.getConnection().commit();
    }
}
