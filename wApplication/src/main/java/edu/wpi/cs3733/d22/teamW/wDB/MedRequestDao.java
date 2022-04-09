package edu.wpi.cs3733.d22.teamW.wDB;

import java.sql.SQLException;
import java.util.ArrayList;

public interface MedRequestDao {

    void addMedRequest(MedRequest mr) throws SQLException;
    void changeMedRequest(Integer id,String m, String n, String en, Integer ie, Integer rs) throws SQLException;
    void deleteMedRequest(Integer id) throws SQLException;
    Request getMedRequest(Integer id) throws SQLException;
    ArrayList<Request> getAllMedRequest() throws SQLException;

}
