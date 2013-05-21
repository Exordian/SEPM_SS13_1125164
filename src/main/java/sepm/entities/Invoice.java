package sepm.entities;

import java.util.Date;
import java.util.HashMap;

public class Invoice {
    private int id = -1;
    private Date date;
    private String receiver;
    private int insurancerate;
    private InvoiceState state = InvoiceState.CREATED;
    private HashMap<Horse, Integer> horses = new HashMap<Horse, Integer>();

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
     * @return the date
     */
    public Date getDate() {
        return date;
    }
    /**
     * @param date the date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }
    /**
     * @return the receiver
     */
    public String getReceiver() {
        return receiver;
    }
    /**
     * @param receiver the receiver to set
     */
    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
    /**
     * @return the insurancerate
     */
    public int getInsurancerate() {
        return insurancerate;
    }
    /**
     * @param insurancerate the insurancerate to set
     */
    public void setInsurancerate(int insurancerate) {
        this.insurancerate = insurancerate;
    }
    /**
     * @return the state
     */
    public InvoiceState getState() {
        return state;
    }
    /**
     * @param state the state to set
     */
    public void setState(InvoiceState state) {
        this.state = state;
    }
    /**
     * @return the horses
     */
    public HashMap<Horse, Integer> getHorses() {
        return horses;
    }
    /**
     * @param horses the horses to set
     */
    public void setHorses(HashMap<Horse, Integer> horses) {
        this.horses = horses;
    }

    public String toString() {
        String state;
        switch(this.state) {
            case CREATED:
                state = "Erstellt";
                break;
            case CLOSED:
                state = "Ausgestellt";
                break;
            case PAID:
                state = "Bezahlt";
                break;
            default:
                state = "ungültiger Status";
                break;
        }
        return  "ID: " + getId() + " Empfänger: " + getReceiver() + " Status: " + state;
    }
}
