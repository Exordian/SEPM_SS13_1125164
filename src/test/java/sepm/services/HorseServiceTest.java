package sepm.services;

import org.junit.Before;
import org.junit.Test;
import sepm.entities.Gender;
import sepm.entities.Horse;
import sepm.entities.Therapy;
import sepm.exceptions.ValidationException;

import java.awt.image.BufferedImage;

public class HorseServiceTest {
    private Horse validHorse;
    private Horse corruptHorse;
    private HorseService horseService;

    @Before
    public void setUp() throws Exception {
        horseService = new HorseService();

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

    @Test
    public void testValidateHorse() throws Exception {
        horseService.validateHorse(validHorse);
    }

    @Test(expected = ValidationException.class)
    public void testValidateHorseNoGender() throws Exception {
        validHorse.setGender(null);
        horseService.validateHorse(validHorse);
    }

    @Test(expected = ValidationException.class)
    public void testValidateHorseNegativePrice() throws Exception {
        validHorse.setPrice(-1.00);
        horseService.validateHorse(validHorse);
    }

    @Test(expected = ValidationException.class)
    public void testValidateHorseNoName() throws Exception {
        validHorse.setName("");
        horseService.validateHorse(validHorse);
    }

    @Test(expected = ValidationException.class)
    public void testValidateHorseNoPicture() throws Exception {
        validHorse.setPicture(null);
        horseService.validateHorse(validHorse);
    }
}
