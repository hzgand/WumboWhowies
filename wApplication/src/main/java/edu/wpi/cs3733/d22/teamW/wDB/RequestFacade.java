package edu.wpi.cs3733.d22.teamW.wDB;

import edu.wpi.cs3733.d22.teamW.wDB.Managers.*;
import edu.wpi.cs3733.d22.teamW.wDB.entity.Request;
import edu.wpi.cs3733.d22.teamW.wDB.enums.RequestType;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

public class RequestFacade {

  private MedEquipRequestManager merm = MedEquipRequestManager.getMedEquipRequestManager();
  private LabServiceRequestManager lsrm = LabServiceRequestManager.getLabServiceRequestManager();
  private MedRequestManager mrm = MedRequestManager.getMedRequestManager();
  private CleaningRequestManager crm = CleaningRequestManager.getCleaningRequestManager();

  private static RequestFacade requestFacade = new RequestFacade();

  private RequestFacade() {}

  public static RequestFacade getRequestFacade() {
    return requestFacade;
  }

  public ArrayList<Request> getRequests(ArrayList<RequestType> requestTypes) throws SQLException {
    return getRequests(requestTypes.toArray(new RequestType[requestTypes.size()]));
  }

  public ArrayList<Request> getRequests(RequestType... requestTypes) throws SQLException {
    if (requestTypes == null || requestTypes.length == 0) {
      return getRequestsByType();
    }
    ArrayList<Request> requests = new ArrayList<>();
    for (RequestType r : requestTypes) {
      requests.addAll(getRequestsByType(r));
    }
    return requests;
  }

  public ArrayList<Request> getRequestsByType(RequestType requestType) throws SQLException {
    ArrayList<Request> requests = new ArrayList<Request>();

    switch (requestType) {
      case MedicalEquipmentRequest:
        requests.addAll(merm.getAllRequests());
        break;
      case MedicineDelivery:
        requests.addAll(mrm.getAllRequests());
        break;
      case LabServiceRequest:
        requests.addAll(lsrm.getAllRequests());
        break;
      case CleaningRequest:
        requests.addAll(crm.getAllRequests());
        break;
      default:
        // requests.addAll(getRequestsByType());
        break;
    }
    Collections.sort(requests);

    return requests;
  }

  public ArrayList<Request> getRequestsByType() throws SQLException {
    ArrayList<Request> requests = new ArrayList<Request>();
    requests.addAll(mrm.getAllRequests());
    requests.addAll(merm.getAllRequests());
    requests.addAll(lsrm.getAllRequests());
    requests.addAll(crm.getAllRequests());
    Collections.sort(requests);
    return requests;
  }

  public Request findRequest(Integer requestID, RequestType type) throws SQLException {
    Request request = null;
    switch (type) {
      case MedicalEquipmentRequest:
        request = merm.getRequest(requestID);
        break;
      case LabServiceRequest:
        request = lsrm.getRequest(requestID);
        break;
      case MedicineDelivery:
        request = mrm.getRequest(requestID);
        break;
      case CleaningRequest:
        request = crm.getRequest(requestID);
      default:
        request = null;
    }
    return request;
  }

  public void completeRequest(Integer requestID, RequestType type, String nodeID) throws Exception {

    switch (type) {
      case MedicalEquipmentRequest:
        merm.complete(requestID);
        break;
      case LabServiceRequest:
        lsrm.complete(requestID);
        break;
      case MedicineDelivery:
        mrm.complete(requestID);
        break;
      case CleaningRequest:
        crm.complete(requestID, nodeID);
    }
  }

  public void cancelRequest(Integer requestID, RequestType type) throws Exception {

    switch (type) {
      case MedicalEquipmentRequest:
        merm.cancel(requestID);
        break;
      case LabServiceRequest:
        lsrm.cancel(requestID);
        break;
      case MedicineDelivery:
        mrm.cancel(requestID);
        break;
      case CleaningRequest:
        crm.cancel(requestID);
    }
  }

  // TODO might want to change this to use requests
  public boolean startRequest(Integer requestID, RequestType type) throws Exception {

    switch (type) {
      case MedicalEquipmentRequest:
        return merm.start(requestID);
        // break;
      case LabServiceRequest:
        return lsrm.start(requestID);
      case MedicineDelivery:
        return mrm.start(requestID);
      case CleaningRequest:
        crm.start(requestID);
    }
    return false;
  }

  public void requeueRequest(Integer requestID, RequestType type) throws Exception {

    switch (type) {
      case MedicalEquipmentRequest:
        merm.reQueue(requestID);
        break;
      case LabServiceRequest:
        lsrm.reQueue(requestID);
        break;
      case MedicineDelivery:
        mrm.reQueue(requestID);
        break;
      case CleaningRequest:
        crm.reQueue(requestID);
    }
  }
}
