package edu.wpi.cs3733.d22.teamW.wApp.serviceRequests;

import edu.wpi.cs3733.d22.teamW.wDB.Request;

public class MedicalEquipmentSR extends SR {

  public MedicalEquipmentSR(Request r) {
    super(r);
  }

  public String getRequestType() {
    return "Medical Equipment";
  }
}
