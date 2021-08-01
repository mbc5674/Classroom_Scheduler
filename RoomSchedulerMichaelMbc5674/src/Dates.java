import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Mbc2255
 */
public class Dates {
    private static Connection connection;
    private static ArrayList<Date> dates = new ArrayList<Date>();
    private static PreparedStatement getDateList;
    private static PreparedStatement addDate;
    private static PreparedStatement removeDate;
    private static ResultSet resultSet;
    public static String[][] datesArray = {{"01", "31"}, {"02", "29"}, {"03", "31"}, {"04","30"}, {"05","31"}, {"06","30"}, 
        {"07","31"}, {"08","31"}, {"09","30"}, {"10","31"}, {"11","30"}, {"12","31"}};
    
    public static ArrayList<Date> getAllDates() {
        ArrayList<Date> dates = new ArrayList<Date>();
        connection = DBConnection.getConnection();
        try {
            getDateList = connection.prepareStatement("select date from dates order by date");
            resultSet = getDateList.executeQuery();
            
            while(resultSet.next()) {
                dates.add(resultSet.getDate(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dates;
    }
    
    public static int removeDate(String date) {
        connection = DBConnection.getConnection();
        try {
            removeDate = connection.prepareStatement("delete from dates where date=(?)");
            removeDate.setString(1, date);
            removeDate.executeUpdate();
            return 1;
        } catch(SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    public static int addDate(int month, int day, int year) {
        if( day <= Integer.parseInt(datesArray[month - 1][1]) && day > 0  && year > 0) {
            String dayString;
            String monthString;
            String yearString;
            if( day < 10 ) {
                dayString = "0" + day;
            } else {
                dayString = "" + day;
            }
            if( month < 10 ) {
                monthString = "0" + month;
            } else {
                monthString = "" + month;
            }
            if( year < 10 ) {
                yearString = "000" + year;
            } else if( year < 100 ) {
                yearString = "00" + year;
            } else if ( year < 1000 ) {
                yearString = "0" + year;
            } else {
                yearString = "" + year;
            }
            String date = yearString + "-" + monthString + "-" + dayString;
            connection = DBConnection.getConnection();
            try {
                addDate = connection.prepareStatement("insert into dates (date) values (?)");
                addDate.setString(1, date);
                addDate.executeUpdate();
                return 1;
            } catch(SQLException e) {
                e.printStackTrace();
                return 0;
            }
        }
        return 0;
    }
    
}
