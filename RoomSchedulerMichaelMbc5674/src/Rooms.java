import java.sql.Connection;
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
public class Rooms {
    private static Connection connection;
    private static ArrayList<String> rooms = new ArrayList<String>();
    private static ArrayList<Integer> roomSizes = new ArrayList<Integer>();
    private static PreparedStatement getRoomSize;
    private static PreparedStatement getRooms;
    private static PreparedStatement addRoom;
    private static PreparedStatement removeRoom;
    private static ResultSet roomResults;
    private static ResultSet roomSizeResults;
    
    public void Rooms() {
        rooms = this.getRoomsList();
        roomSizes = this.getRoomsSizesList();
    }
    
    public int addRoom( String name, int seats ) {
        if( !this.getRoomsList().contains(name) ) {
            connection = DBConnection.getConnection();
            try {
                addRoom = connection.prepareStatement("insert into rooms (name, seats) values (?,?)");
                addRoom.setString(1, name);
                addRoom.setString(2, "" + seats);
                addRoom.executeUpdate();
                Reservation reservation = new Reservation();
                reservation.updateFromWaitlist();
                return 1;
            } catch(SQLException e) {
                e.printStackTrace();;
            } 
        }
        return 0;
    }
    
    public void removeRoom(String name) {
        connection = DBConnection.getConnection();
        try {
            Reservation reservation = new Reservation();
            reservation.removeReservationByRoom(name);
            removeRoom = connection.prepareStatement("delete from rooms where name=(?)");
            removeRoom.setString(1, name);
            removeRoom.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
    
    public ArrayList<String> getRoomsList() {
        ArrayList<String> rooms = new ArrayList<String>();
        connection = DBConnection.getConnection();
        try {
            getRooms = connection.prepareStatement("select name from rooms order by seats");
            roomResults = getRooms.executeQuery();
            
            while(roomResults.next()) {
                rooms.add(roomResults.getString(1));
            }
            
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }
    
    public ArrayList<Integer> getRoomsSizesList() {
        ArrayList<Integer> roomSizes = new ArrayList<Integer>();
        connection = DBConnection.getConnection();
        try {
            getRoomSize = connection.prepareStatement("select seats from rooms order by seats");
            roomSizeResults = getRoomSize.executeQuery();
            
            while(roomSizeResults.next()) {
                roomSizes.add(roomSizeResults.getInt(1));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return roomSizes;
    }    
}
