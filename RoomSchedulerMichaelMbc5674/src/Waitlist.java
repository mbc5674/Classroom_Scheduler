import java.sql.Connection;
import java.sql.Timestamp;
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
public class Waitlist {
    private static Connection connection;
    private static ArrayList<String> faculty = new ArrayList<String>();
    private static ArrayList<Date> dates = new ArrayList<Date>();
    private static ArrayList<Integer> seats = new ArrayList<Integer>();
    private static ArrayList<Timestamp> times = new ArrayList<Timestamp>();
    private static PreparedStatement getFaculty;
    private static PreparedStatement getDates;
    private static PreparedStatement getSeats;
    private static PreparedStatement getTimes;
    private static PreparedStatement getReservation;
    private static PreparedStatement removeReservation;
    private static ResultSet reservations;
    private static ResultSet facultyResult;
    private static ResultSet dateResult;
    private static ResultSet seatResult;
    private static ResultSet timeResult;
    
    public void Waitlist() {
        faculty = this.getFaculty();
        dates = this.getDates();
        seats = this.getSeats();
        times = this.getTimes();
    }
    
    public int removeReservation(int row) {
        connection = DBConnection.getConnection();
        try {
            getReservation = connection.prepareStatement("select faculty, date from waitlist order by date, timestamp");
            reservations = getReservation.executeQuery();
            
            removeReservation = connection.prepareStatement("delete from waitlist where faculty = (?) and date = (?)");
            while( reservations.next() ) {
                if( reservations.getRow() == row ) {
                    removeReservation.setString(1, reservations.getString(1));
                    removeReservation.setString(2, reservations.getString(2));
                    removeReservation.executeUpdate();
                    return 1;
                }
            }
        } catch(SQLException e) {
            e.printStackTrace();
        } 
        return 0;
    }
    
    public ArrayList<Timestamp> getTimes() {
        ArrayList<Timestamp> times = new ArrayList<Timestamp>();
        connection = DBConnection.getConnection();
        try {
            getTimes = connection.prepareStatement("select timestamp from waitlist order by date, timestamp");
            timeResult = getTimes.executeQuery();
            
            while(timeResult.next()) {
                times.add(timeResult.getTimestamp(1));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return times;
    }
    
    public ArrayList<Integer> getSeats() {
        ArrayList<Integer> seats = new ArrayList<Integer>();
        connection = DBConnection.getConnection();
        try {
            getSeats = connection.prepareStatement("select seats from waitlist order by date, timestamp");
            seatResult = getSeats.executeQuery();
            
            while(seatResult.next()) {
                seats.add(seatResult.getInt(1));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return seats;
    }
    
    public ArrayList<String> getFaculty() {
        ArrayList<String> faculty = new ArrayList<String>();
        connection = DBConnection.getConnection();
        try {
            getFaculty = connection.prepareStatement("select faculty from waitlist order by date, timestamp");
            facultyResult = getFaculty.executeQuery();
            
            while(facultyResult.next()) {
                faculty.add(facultyResult.getString(1));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return faculty;
    }
    
    public ArrayList<Date> getDates() {
        ArrayList<Date> dates = new ArrayList<Date>();
        connection = DBConnection.getConnection();
        try {
            getDates = connection.prepareStatement("select date from waitlist order by date, timestamp");
            dateResult = getDates.executeQuery();
            
            while(dateResult.next()) {
                dates.add(dateResult.getDate(1));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return dates;
    }
}
