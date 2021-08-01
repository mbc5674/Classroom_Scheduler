import java.sql.Connection;
import java.sql.Timestamp;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Mbc2255
 */
public class Reservation {
    private static Connection connection;
    private static ArrayList<String> reservedRooms = new ArrayList<String>();
    private static ArrayList<Timestamp> times = new ArrayList<Timestamp>();
    private static ArrayList<Integer> seats = new ArrayList<Integer>();
    private static ArrayList<String> faculty = new ArrayList<String>();
    private static ArrayList<Date> dates = new ArrayList<Date>();
    private static PreparedStatement getReservedRooms;
    private static PreparedStatement getReservedRoomsSeats;
    private static PreparedStatement getReservedTimes;
    private static PreparedStatement getFacultyList;
    private static PreparedStatement getDateList;
    private static PreparedStatement reserve;
    private static PreparedStatement addToWaitlist;
    private static PreparedStatement removeReservation;
    private static PreparedStatement getReservation;
    private static PreparedStatement removeReservationByRoom;
    private static ResultSet reservations;
    private static ResultSet resultReservedRooms;
    private static ResultSet resultReservedRoomsSeats;
    private static ResultSet resultReservedTimes;
    private static ResultSet resultFaculty;
    private static ResultSet resultSet;
    
    public void Reservation() {
        reservedRooms = this.getReservedRooms();
        seats = this.getReservedSeats();
        times = this.getReservedTimes();
        faculty = this.getFacultyList();
        dates = this.getAllDates();
    }
    
    public int removeReservation(int row) {
        connection = DBConnection.getConnection();
        try {
            getReservation = connection.prepareStatement("select room, date, seats from reservations order by room");
            reservations = getReservation.executeQuery();
            
            removeReservation = connection.prepareStatement("delete from reservations where room = (?) and date = (?)");
            while( reservations.next() ) {
                if( reservations.getRow() == row ) {
                    String room = reservations.getString(1);
                    String date = reservations.getDate(2).toString();
                    int seatAmount = reservations.getInt(3);
                    removeReservation.setString(1, room);
                    removeReservation.setString(2, date);
                    removeReservation.executeUpdate();
                    //
                    //
                    //
                    Waitlist waitlist = new Waitlist();
                    ArrayList<Date> dates = waitlist.getDates();
                    ArrayList<String>  faculty = waitlist.getFaculty();
                    ArrayList<Timestamp> times = waitlist.getTimes();
                    ArrayList<Integer> seats = waitlist.getSeats();
                    int waitlistSize = seats.size();
                    for( int i = 0; i < waitlistSize; i++ ) {
                        if( dates.get(i).toString().equals(date) && seats.get(i) <= seatAmount ) {
                            this.reserveRoomByRoom(faculty.get(i), date, room, times.get(i), seatAmount);
                            waitlist.removeReservation(i + 1);
                            return 1;
                        }
                    }                    
                    //
                    //
                    //
                    return 1;
                }
            }
        } catch(SQLException e) {
            e.printStackTrace();
        } 
        return 0;
    }
    
    public int updateFromWaitlist() {
        Waitlist waitlist = new Waitlist();
        ArrayList<Date> dates = waitlist.getDates();
        ArrayList<String>  faculty = waitlist.getFaculty();
        ArrayList<Timestamp> times = waitlist.getTimes();
        ArrayList<Integer> seatsList = waitlist.getSeats();
        int size = seatsList.size();
        for( int i = 0; i < size; i++ ) {
            if( this.reserveRoom(faculty.get(i), dates.get(i).toString(), seatsList.get(i)) > 0 ) {
                waitlist.removeReservation(i + 1);
                return 1;
            }
        }
        
        
        return 0;
    }
        
    public int reserveRoomByRoom(String faculty, String date, String room, Timestamp time, int seats) {
        connection = DBConnection.getConnection();
        try {
            reserve = connection.prepareStatement("insert into reservations (faculty, room, date, seats, timestamp) values (?,?,?,?,?)");
            reserve.setString(1, faculty);
            reserve.setString(2, room);
            reserve.setString(3, date);
            reserve.setString(4, "" + seats);
            reserve.setString(5, time.toString());
            reserve.executeUpdate();
            return 1;
        } catch(SQLException e) {
            e.printStackTrace();
        } 
        return 0;
    }
    
    public int removeReservationByRoom(String room) {
        connection = DBConnection.getConnection();
        try {
            int size = this.getReservedRooms().size();
            for( int i = 0; i < size; i++ ) {
                if( this.getReservedRooms().get(i).equals(room)  ) {
                    removeReservationByRoom = connection.prepareStatement("delete from reservations where room = (?)");
                    removeReservationByRoom.setString(1, room);
                    removeReservationByRoom.executeUpdate();
                    i--;
                    size--;
                }
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public int reserveRoom(String faculty, String date, Integer seats) {
        connection = DBConnection.getConnection();
        Rooms room = new Rooms();
        boolean taken = false;
        Waitlist waitlist = new Waitlist();
        java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
        ArrayList<Integer> roomSizeList = room.getRoomsSizesList();
        ArrayList<String> roomRoomNames = room.getRoomsList();
        ArrayList<Date> roomDates = this.getAllDates();
        ArrayList<String> roomNames = this.getReservedRooms();
        try {
            if( this.getFacultyList().contains(faculty) ) {
                return 0;
            }
            int size = roomRoomNames.size();
            for( int i = 0; i < size; i++ ) {
                if( seats <= roomSizeList.get(i) ) {
                    int size2 = roomNames.size();
                    for( int j = 0; j < size2; j++ ) {
                        if( roomNames.get(j).equals(roomRoomNames.get(i)) && roomDates.get(j).toString().equals(date) ) {
                            taken = true;
                        }
                    }
                    if( !taken ) {
                        reserve = connection.prepareStatement("insert into reservations (faculty, room, date, seats, timestamp) values (?,?,?,?,?)");
                        reserve.setString(1, faculty);
                        reserve.setString(2, room.getRoomsList().get(i));
                        reserve.setString(3, date);
                        reserve.setString(4, roomSizeList.get(i).toString());
                        reserve.setString(5, currentTimestamp.toString());
                        reserve.executeUpdate();
                        return 1;
                    }
                }
                taken = false;
            }
            if( waitlist.getFaculty().contains(faculty) ) {
                return 1;
            }
            addToWaitlist = connection.prepareStatement("insert into waitlist (faculty, date, seats, timestamp) values (?,?,?,?)");
            addToWaitlist.setString(1, faculty);
            addToWaitlist.setString(2, date);
            addToWaitlist.setString(3, seats.toString());
            addToWaitlist.setString(4, currentTimestamp.toString());
            addToWaitlist.executeUpdate();
            return 2;
        } catch(SQLException e) {
            e.printStackTrace();
        } 
        return 0;
    }
    
    public ArrayList<Date> getAllDates() {
        ArrayList<Date> dates = new ArrayList<Date>();
        connection = DBConnection.getConnection();
        try {
            getDateList = connection.prepareStatement("select date from reservations order by room");
            resultSet = getDateList.executeQuery();
            
            while(resultSet.next()) {
                dates.add(resultSet.getDate(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dates;
    }
    
    public ArrayList<String> getFacultyList() {
        connection = DBConnection.getConnection();
        ArrayList<String> faculty = new ArrayList<String>();
        try {
            getFacultyList = connection.prepareStatement("select faculty from reservations order by room");
            resultFaculty = getFacultyList.executeQuery();
            
            while(resultFaculty.next()) {
                faculty.add(resultFaculty.getString(1));
            }
        }
        catch(SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return faculty;
    }
    
    
    
    public ArrayList<Integer> getReservedSeats() {
        ArrayList<Integer> seats = new ArrayList<Integer>();
        connection = DBConnection.getConnection();
        try {
            getReservedRoomsSeats = connection.prepareStatement("select seats from reservations order by room");
            resultReservedRoomsSeats = getReservedRoomsSeats.executeQuery();
            
            while(resultReservedRoomsSeats.next()) {
                seats.add(resultReservedRoomsSeats.getInt(1));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return seats;
    }
    
    public ArrayList<String> getReservedRooms() {
        ArrayList<String> reservedRooms = new ArrayList<String>();
        connection = DBConnection.getConnection();
        try {
            getReservedRooms = connection.prepareStatement("select room from reservations order by room");
            resultReservedRooms = getReservedRooms.executeQuery();
            
            while(resultReservedRooms.next()) {
               reservedRooms.add(resultReservedRooms.getString(1));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return reservedRooms;
    }
    
    public ArrayList<Timestamp> getReservedTimes() {
        ArrayList<Timestamp> times = new ArrayList<Timestamp>();
        connection = DBConnection.getConnection();
        try {
            getReservedTimes = connection.prepareStatement("select timestamp from reservations order by room");
            resultReservedTimes = getReservedTimes.executeQuery();
            
            while(resultReservedTimes.next()) {
               times.add(resultReservedTimes.getTimestamp(1));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return times;
    }   
}
