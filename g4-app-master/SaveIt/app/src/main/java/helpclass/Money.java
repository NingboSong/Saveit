package helpclass;

import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

/**
 * The type Money.
 * @author Zilin.Song
 */
public class Money implements Serializable {
    private String value;
    private String date;
    private String instructions;
    private String type;
    private String picturePath = null;


    /**
     * Instantiates a new Money.
     *
     * @param value        the value
     * @param date         the date
     * @param instructions the instructions
     * @param type         the type
     * @param picturePath  the picture path
     */
    public Money(String value, String date, String instructions, String type,String picturePath) {
        this.value = value;
        this.instructions = instructions;
        this.type = type;
        this.date = date;
        this.picturePath = picturePath;
    }


    /**
     * Gets value.
     *
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets value.
     *
     * @param value the value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets date.
     *
     * @return the date
     */
    public String getDate() {
        return date;
    }

    /**
     * Sets date.
     *
     * @param date the date
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Gets instructions.
     *
     * @return the instructions
     */
    public String getInstructions() {
        return instructions;
    }

    /**
     * Sets instructions.
     *
     * @param instructions the instructions
     */
    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets type.
     *
     * @param type the type
     */
    public void setType(String type) {
        this.type = type;
    }
    public String toString(){
        return value+type+instructions+date;
    }

    /**
     * Gets picture path.
     *
     * @return the picture path
     */
    public String getPicturePath() {
        return picturePath;
    }

    /**
     * Sets picture path.
     *
     * @param picturePath the picture path
     */
    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    /**
     * Get double value double.
     *
     * @return the double
     */
    public double getDoubleValue(){
        if (value.startsWith("-")){
            return -Double.parseDouble(this.value.substring(1));
        }
        return Double.parseDouble(this.value.substring(1));
    }

    /**
     * Get datesort int.
     *
     * @return the int
     */
    public int getDatesort(){
        String[] Datesort;
        Datesort = date.split("\\.");
        String result = Datesort[2] + Datesort[1] +Datesort[0];
        Integer number = Integer.valueOf(result);
        return number;
    }

    /**
     * Get month int.
     *
     * @return the int
     */
    public int getMonth(){
        String[] Date;
        Date = date.split("\\.");
        return Integer.parseInt(Date[1]);
    }

    /**
     * Get day int.
     *
     * @return the int
     */
    public int getDay(){
        String[] Date;
        Date = date.split("\\.");
        return Integer.parseInt(Date[0]);
    }

    /**
     * Check if the money happens in this Year.
     *
     */
    public Boolean ifthisyear(){
        String[] Date;
        Date = date.split("\\.");
        Date d = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        int dateYear = calendar.get(Calendar.YEAR);
        if (Integer.parseInt(Date[2]) == dateYear){
            return true;
        }else
            return false;
    }

    /**
     * Gets the Date in date format.
     *
     * @return the date1
     * @throws ParseException the parse exception
     */
    public Date getDatedate() throws ParseException {
        Date date1=new SimpleDateFormat("dd.MM.yyyy").parse(this.date);
        return date1;
    }

    /**
     * Check if the money happens in this Month.
     *
     * @return the boolean
     * @throws ParseException the parse exception
     */
    public boolean ifThisMonth() throws ParseException {
        Date date1 = getDatedate();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        String param = sdf.format(date1);//参数时间
        String now = sdf.format(new Date());//当前时间
        if (param.equals(now)) {
            return true;
        }
        return false;
    }

}
