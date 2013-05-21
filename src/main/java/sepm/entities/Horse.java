package sepm.entities;

import java.awt.image.BufferedImage;

public class Horse {
    private int id = -1;
    private String name;
    private double price;
    private BufferedImage picture;
    private Gender gender = Gender.MALE;
    private Therapy therapy = Therapy.HIPPOTHERAPHY;
    private boolean deleted;

    public Horse() {
        this.id = id;
        this.name = name;
        this.price = price;
        this.picture = picture;
        this.gender = gender;
        this.therapy = therapy;
        this.deleted = deleted;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }
    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * @return the price
     */
    public double getPrice() {
        return price;
    }
    /**
     * @param price the price to set
     */
    public void setPrice(double price) {
        this.price = price;
    }
    /**
     * @return the picture
     */
    public BufferedImage getPicture() {
        return picture;
    }
    /**
     * @param picture the picture to set
     */
    public void setPicture(BufferedImage picture) {
        this.picture = picture;
    }
    /**
     * @return the therapy
     */
    public Therapy getTherapy() {
        return therapy;
    }
    /**
     * @param therapy the therapy to set
     */
    public void setTherapy(Therapy therapy) {
        this.therapy = therapy;
    }
    /**
     * @return the deleted
     */
    public boolean isDeleted() {
        return deleted;
    }

    /**
     * @param deleted the deleted to set
     */

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
    /**
     * @return the gender
     */
    public Gender getGender() {
        return gender;
    }
    /**
     * @param gender the gender to set
     */
    public void setGender(Gender gender) {
        this.gender = gender;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Horse horse = (Horse) o;

        if (id != horse.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        String therapy;
        switch(this.therapy) {
            case HIPPOTHERAPHY:
                therapy = "Hippotheraphy";
                break;
            case HPV:
                therapy = "Heilpädagogisches Voltigieren (HPV)";
                break;
            case HPR:
                therapy = "Heilpädagogisches Reiten (HPR)";
                break;
            default:
                therapy = "keine Therapie";
                break;
        }
        return name + ": " + price + " - " + therapy;
    }

}
