package edu.wpi.cs3733.d22.teamW.wDB.Managers;

import edu.wpi.cs3733.d22.teamW.wDB.DAO.MedEquipRequestDao;
import edu.wpi.cs3733.d22.teamW.wDB.Errors.CannotStart;
import edu.wpi.cs3733.d22.teamW.wDB.Errors.NoAvailableEquipment;
import edu.wpi.cs3733.d22.teamW.wDB.Errors.NonExistingRequestID;
import edu.wpi.cs3733.d22.teamW.wDB.Errors.StatusError;
import edu.wpi.cs3733.d22.teamW.wDB.RequestFacade;
import edu.wpi.cs3733.d22.teamW.wDB.RequestFactory;
import edu.wpi.cs3733.d22.teamW.wDB.entity.MedEquip;
import edu.wpi.cs3733.d22.teamW.wDB.entity.MedEquipRequest;
import edu.wpi.cs3733.d22.teamW.wDB.entity.Request;
import edu.wpi.cs3733.d22.teamW.wDB.enums.Automation;
import edu.wpi.cs3733.d22.teamW.wDB.enums.RequestStatus;
import edu.wpi.cs3733.d22.teamW.wDB.enums.RequestType;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

public class MedEquipRequestManager implements RequestManager {

  private MedEquipRequestDao merd;
  private MedEquipManager mem = MedEquipManager.getMedEquipManager();

  private static MedEquipRequestManager medEquipRequestManager = new MedEquipRequestManager();

  public static MedEquipRequestManager getMedEquipRequestManager() {
    return medEquipRequestManager;
  }

  private MedEquipRequestManager() {}

  public ArrayList<MedEquipRequest> getType(String type) throws SQLException {
    return merd.getTypeMedEquipRequests(type);
  }

  public void setMedEquipRequestDao(MedEquipRequestDao merdi) {
    this.merd = merdi;
  }

  public void startNext(String itemType) throws Exception {
    MedEquipRequest mer = getNext(itemType);
    if (mer != null) {
      System.out.println(mer.toValuesString());
      try {
        AutoStart(mer.getRequestID());
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      System.out.println("Nothing to start");
    }
  }

  public MedEquipRequest getNext(String itemType) throws SQLException {
    ArrayList<MedEquipRequest> requests = merd.getTypeMedEquipRequests(itemType);
    for (MedEquipRequest mer : requests) {
      if (mer.getEmergency() == 1 && mer.getStatus().equals(RequestStatus.InQueue)) {
        return mer;
      }
    }
    for (MedEquipRequest mer : requests) {
      if (mer.getStatus().equals(RequestStatus.InQueue)) {
        return mer;
      }
    }
    return null;
  }

  public void start(Integer requestID)
      throws SQLException, NoAvailableEquipment, CannotStart, NonExistingRequestID {
    MedEquipRequest request = (MedEquipRequest) getRequest(requestID);
    if (request != (null)) {
      // Can only start requests that are in queue
      if (request.getStatus().equals(RequestStatus.InQueue)) {
        // Can only start if a medEquip of that type is available
        MedEquip medEquip = MedEquipManager.getMedEquipManager().getNextFree(request.getItemType());
        if (medEquip != null) {
          // If available, we mark it in use and set the request to in progress
          MedEquipManager.getMedEquipManager().markInUse(medEquip.getMedID(), medEquip.getNodeID());
          request.setStatus(RequestStatus.InProgress);
          request.setUpdatedTimestamp(new Timestamp(System.currentTimeMillis()));
          merd.changeMedEquipRequest(request);
        } else {
          throw new NoAvailableEquipment();
        }
      } else {
        throw new CannotStart();
      }
    } else {
      throw new NonExistingRequestID();
    }
  }

  public void AutoStart(Integer requestID) throws SQLException {
    MedEquipRequest request = (MedEquipRequest) getRequest(requestID);
    if (request != (null)) {
      // Can only start requests that are in queue
      if (request.getStatus().equals(RequestStatus.InQueue)) {
        // Can only start if a medEquip of that type is available
        MedEquip medEquip = MedEquipManager.getMedEquipManager().getNextFree(request.getItemType());
        if (medEquip != null) {
          // If available, we mark it in use and set the request to in progress
          MedEquipManager.getMedEquipManager().markInUse(medEquip.getMedID(), medEquip.getNodeID());
          request.setStatus(RequestStatus.InProgress);
          request.setUpdatedTimestamp(new Timestamp(System.currentTimeMillis()));
          merd.changeMedEquipRequest(request);
        } else {
        }
      } else {
        // throw (new Exception("Cannot start, not in queue"));
      }
    } else {
      // throw (new Exception("Request:" + requestID + " does not exist"));
    }
  }

  public void complete(Integer requestID) throws SQLException, StatusError {
    MedEquipRequest request =
        (MedEquipRequest)
            RequestFacade.getRequestFacade()
                .findRequest(requestID, RequestType.MedicalEquipmentRequest);
    // Can only complete requests that are started
    if (request.getStatus().equals(RequestStatus.InProgress)) {
      MedEquipManager.getMedEquipManager().markInUse(request.getItemID(), request.getNodeID());
      request.setStatus(RequestStatus.Completed);
      request.setUpdatedTimestamp(new Timestamp(System.currentTimeMillis()));
      merd.changeMedEquipRequest(request);
    }
  }

  public void cancel(Integer requestID) throws Exception {
    MedEquipRequest request =
        (MedEquipRequest)
            RequestFacade.getRequestFacade()
                .findRequest(requestID, RequestType.MedicalEquipmentRequest);
    // Cannot cancel requests that are completed bc it makes no sense
    if (!request.getStatus().equals(RequestStatus.Completed)) {
      if (request.getStatus() == RequestStatus.InProgress) {
        MedEquip item = MedEquipManager.getMedEquipManager().getMedEquip(request.getItemID());
        MedEquipManager.getMedEquipManager().markClean(item.getMedID(), item.getNodeID());
        if (Automation.Automation.getAuto()) {
          startNext(request.getItemType());
        }
      }
      request.setStatus(RequestStatus.Cancelled);
      request.setUpdatedTimestamp(new Timestamp(System.currentTimeMillis()));
      merd.changeMedEquipRequest(request);
    }
  }

  public void reQueue(Integer requestID) throws Exception {
    MedEquipRequest request =
        (MedEquipRequest)
            RequestFacade.getRequestFacade()
                .findRequest(requestID, RequestType.MedicalEquipmentRequest);
    // Only requeue cancelled requests
    if (request.getStatus().equals(RequestStatus.Cancelled)) {
      request.setStatus(RequestStatus.InQueue);
      request.setUpdatedTimestamp(new Timestamp(System.currentTimeMillis()));
      request.dropItem();
      merd.changeMedEquipRequest(request);
      if (Automation.Automation.getAuto()) {
        startNext(request.getItemType());
      }
    }
  }

  // TODO might wanna rework to just use sql
  @Override
  public Request getRequest(Integer reqID) throws SQLException {
    return merd.getRequest(reqID);
  }

  @Override
  public Request addNewRequest(Integer num, ArrayList<String> fields) throws Exception {
    MedEquipRequest mER;
    // Set status to in queue if it is not already included (from CSVs)
    fields.add(0, Integer.toString(num));
    fields.add(1, "NONE");
    fields.add(Integer.toString(RequestStatus.InQueue.getValue()));
    fields.add(new Timestamp(System.currentTimeMillis()).toString());
    fields.add(new Timestamp(System.currentTimeMillis()).toString());
    mER = new MedEquipRequest(fields);
    /*
    // If the request does not have an item, aka has not been started
    if (mER.getItemID().equals("NONE") && mER.getStatusInt() == 0) {
      // System.out.println("CHECKING REQUEST " + mER.getRequestID());
      String itemID = checkStart(mER);
      if (itemID != null) {
        // System.out.println("STARTING REQUEST " + mER.getRequestID());
        mER.start(itemID);
      }
    }*/
    // TODO special exception
    if (RequestFactory.getRequestFactory().getReqIDList().add(mER.getRequestID())) {
      merd.addMedEquipRequest(mER);

      if (Automation.Automation.getAuto()) {
        try {
          AutoStart(mER.getRequestID());
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    } else {
      mER = null;
    }
    return mER;
  }

  @Override
  public Request addExistingRequest(ArrayList<String> fields) throws SQLException, StatusError {
    MedEquipRequest mER;
    mER = new MedEquipRequest(fields);

    if (RequestFactory.getRequestFactory().getReqIDList().add(mER.getRequestID())) {
      merd.addMedEquipRequest(mER);

      if (Automation.Automation.getAuto()) {
        try {
          AutoStart(mER.getRequestID());
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    } else {
      mER = null;
    }
    return mER;
  }

  public ArrayList<Request> getAllRequests() throws SQLException {
    return this.merd.getAllMedEquipRequests();
  }

  public void changeReq(MedEquipRequest req, String locID) throws SQLException {
    req.setNodeID(locID);
    merd.changeMedEquipRequest(req);
  }

  public void changeReq(MedEquipRequest req) throws SQLException {
    merd.changeMedEquipRequest(req);
  }

  public void exportMedEquipRequestCSV(String filename) {
    merd.exportMedEquipReqCSV(filename);
  }

  public void exportReqCSV(String filename) {
    merd.exportMedEquipReqCSV(filename);
  }
}
