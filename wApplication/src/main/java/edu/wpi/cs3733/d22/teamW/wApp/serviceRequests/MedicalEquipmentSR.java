package edu.wpi.cs3733.d22.teamW.wApp.serviceRequests;

import edu.wpi.cs3733.d22.teamW.wDB.entity.MedEquipRequest;
import edu.wpi.cs3733.d22.teamW.wDB.enums.RequestStatus;

public class MedicalEquipmentSR {
  MedEquipRequest mer;

  public MedicalEquipmentSR(MedEquipRequest mer) {
    this.mer = mer;
  }

  public Integer getRequestID() {
    return mer.getRequestID();
  }

  /*public void setRequestID(Integer requestID) {
    mer.setRequestID(requestID);
  }*/

  public String getRequestType() {
    return "Medical Equipment";
  }

  public Integer getEmployeeID() {
    return mer.getEmployeeID();
  }
  /*
    public void setEmployeeName(String employeeName) {
      mer.setEmployeeName(employeeName);
    }
  */
  public RequestStatus getStatus() {
    return mer.getStatus();
  }
  /*
  public void setStatus(int status) {
    mer.setStatus(status);
  }
  */
}
