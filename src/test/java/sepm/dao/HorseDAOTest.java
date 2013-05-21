package sepm.dao;

import org.junit.Test;
import sepm.entities.Gender;
import sepm.entities.Horse;
import sepm.entities.Therapy;
import sepm.exceptions.PersistenceException;

import static junit.framework.Assert.assertTrue;

public abstract class HorseDAOTest {
    protected HorseDAO horseDAO;
    protected Horse validHorse;
    protected Horse corruptHorse;

    protected void setHorseDAO(HorseDAO horseDAO) {
        this.horseDAO = horseDAO;
    }

    @Test
    public void testSaveValidHorse() throws Exception {
        horseDAO.saveHorse(validHorse);
    }

    @Test
    public void testUpdateValidHorse() throws Exception {
        horseDAO.saveHorse(validHorse);
        int id = validHorse.getId();
        validHorse.setGender(Gender.FEMALE);
        horseDAO.saveHorse(validHorse);

        // other horse should be deleted
        assertTrue(horseDAO.findAllNonDeletedHorses().size() == 1);

        // can access old horse?
        assertTrue(horseDAO.findHorse(id).isDeleted());
        // old horse unchanged?
        assertTrue(horseDAO.findHorse(id).getGender() == Gender.MALE);

        assertTrue(horseDAO.findHorse(validHorse.getId()).isDeleted() == false);
    }

    @Test(expected = PersistenceException.class)
    public void testSaveInvalidHorse() throws Exception {
        horseDAO.saveHorse(corruptHorse);
    }


    @Test(expected = PersistenceException.class)
    public void testSaveHorseNoPic() throws Exception {
        validHorse.setPicture(null);
        horseDAO.saveHorse(validHorse);
    }

    @Test
    public void testFindAllNonDeletedHorses() throws Exception {
        horseDAO.saveHorse(validHorse);
        assertTrue(horseDAO.findAllNonDeletedHorses().size() == 1);
    }

    @Test
    public void testDeleteHorse() throws Exception {
        horseDAO.saveHorse(validHorse);
        assertTrue(horseDAO.findAllNonDeletedHorses().size() == 1);
        horseDAO.deleteHorse(horseDAO.findAllNonDeletedHorses().get(0));
        assertTrue(horseDAO.findAllNonDeletedHorses().size() == 0);
    }

    @Test
    public void testFindHorseByName() throws Exception {
        horseDAO.saveHorse(validHorse);
        assertTrue(horseDAO.findHorses("Look%").size() == 1);
    }

    @Test
    public void testFindHorseByNameZero() throws Exception {
        horseDAO.saveHorse(validHorse);
        assertTrue(horseDAO.findHorses("No%").size() == 0);
    }

    @Test
    public void testFindHorsesByPrice() throws Exception {
        horseDAO.saveHorse(validHorse);
        assertTrue(horseDAO.findHorses(0.00, 10.00).size() == 1);
    }

    @Test
    public void testFindNoHorsesByPrice() throws Exception {
        horseDAO.saveHorse(validHorse);
        assertTrue(horseDAO.findHorses(5.00, 10.00).size() == 0);
    }

    @Test
    public void testFindHorsesByTherapy() throws Exception {
        horseDAO.saveHorse(validHorse);
        assertTrue(horseDAO.findHorses(Therapy.HIPPOTHERAPHY).size() == 1);
    }

    @Test
    public void testFindNoHorsesByTherapy() throws Exception {
        horseDAO.saveHorse(validHorse);
        assertTrue(horseDAO.findHorses(Therapy.HPV).size() == 0);
    }

    @Test
    public void testFindHorsesByGender() throws Exception {
        horseDAO.saveHorse(validHorse);
        assertTrue(horseDAO.findHorses(Gender.MALE).size() == 1);
    }

    @Test
    public void testFindNoHorsesByGender() throws Exception {
        horseDAO.saveHorse(validHorse);
        assertTrue(horseDAO.findHorses(Gender.FEMALE).size() == 0);
    }

}
