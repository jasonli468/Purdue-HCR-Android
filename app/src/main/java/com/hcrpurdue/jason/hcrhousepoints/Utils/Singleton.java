package com.hcrpurdue.jason.hcrhousepoints.Utils;

import android.content.Context;
import android.util.Pair;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.hcrpurdue.jason.hcrhousepoints.Models.House;
import com.hcrpurdue.jason.hcrhousepoints.Models.Link;
import com.hcrpurdue.jason.hcrhousepoints.Models.PointLog;
import com.hcrpurdue.jason.hcrhousepoints.Models.PointLogMessage;
import com.hcrpurdue.jason.hcrhousepoints.Models.PointType;
import com.hcrpurdue.jason.hcrhousepoints.Models.Reward;
import com.hcrpurdue.jason.hcrhousepoints.Models.SystemPreferences;
import com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces.FirebaseUtilInterface;
import com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces.SingletonInterface;

// Because non-global variables are for people who care about technical debt
public class Singleton {
    private static Singleton instance = null;
    private FirebaseUtil fbutil = new FirebaseUtil();
    private CacheUtil cacheUtil = new CacheUtil();
    private List<PointType> pointTypeList = null;
    private ArrayList<PointLog> unconfirmedPointList = null;
    private ArrayList<PointLog> confirmedPointList = null;
    private String userID = null;
    private String floorName = null;
    private String houseName = null;
    private String name = null;
    private String firstName = null;
    private String lastName = null;
    private int permissionLevel = 0;
    private int totalPoints = 0;
    private List<House> houseList = null;
    private List<Reward> rewardList = null;
    private ArrayList<Link> userCreatedQRCodes = null;
    private SystemPreferences sysPrefs = null;

    private Singleton() {
        // Exists only to defeat instantiation. Get rekt, instantiation
    }

    private void setApplicationContext(Context c) {
        fbutil.setApplicationContext(c);
        cacheUtil.setApplicationContext(c);
    }

    public static Singleton getInstance(Context context) {
        if (instance == null) {
            synchronized (Singleton.class) {
                if (instance == null) {
                    instance = new Singleton();
                    instance.setApplicationContext(context);
                    return instance;
                }
            }
        }
        return instance;
    }

    public String getUserId(){
        return this.userID;
    }


    public PointType getPointTypeWithID(int pointID) {

        for (int i = 0; i < pointTypeList.size(); i++) {
            if(pointTypeList.get(i).getPointID() == pointID) {
                return pointTypeList.get(i);
            }
        }
        return null;
    }

    public void getPointTypes(SingletonInterface si) {
        if (pointTypeList == null)
            fbutil.getPointTypes(new FirebaseUtilInterface() {
                @Override
                public void onPointTypeComplete(List<PointType> data) {
                    if (data != null && !data.isEmpty()) {
                        pointTypeList = data;
                        si.onPointTypeComplete(data);
                    } else {
                        si.onError(new IllegalStateException("Point Type list is empty"), fbutil.getContext());
                    }
                }
            });
        else {
            si.onPointTypeComplete(pointTypeList);
            fbutil.getPointTypes(new FirebaseUtilInterface() {
                @Override
                public void onPointTypeComplete(List<PointType> data) {
                if (data != null && !data.isEmpty()) {
                    pointTypeList = data;
                }
                }
            });
        }
    }

    /**
     *
     * @param si
     *
     * Gets system preferences for House
     */
    public void getSystemPreferences(SingletonInterface si) {
        fbutil.getSystemPreferences(new FirebaseUtilInterface() {
            @Override
            public void onGetSystemPreferencesSuccess(SystemPreferences systemPreferences) {
                if(systemPreferences != null) {
                    sysPrefs = systemPreferences;
                    si.onGetSystemPreferencesSuccess(sysPrefs);
                }
                else {
                    si.onError(new IllegalStateException("System preferences is null"), fbutil.getContext());
                }
            }

            @Override
            public void onError(Exception e, Context context) {
                si.onError(e, context);
            }
        });
    }

    /**
     *
     * Returns cached system preferences
     * @return
     */
    public SystemPreferences getCachedSystemPreferences() {
        return sysPrefs;
    }

    public void getUnconfirmedPoints(SingletonInterface si) {
        getPointTypes(new SingletonInterface() {
            @Override
            public void onPointTypeComplete(List<PointType> data) {
                fbutil.getUnconfirmedPoints(houseName, floorName, new FirebaseUtilInterface() {
                    @Override
                    public void onGetUnconfirmedPointsSuccess(ArrayList<PointLog> logs) {
                        unconfirmedPointList = logs;
                        si.onUnconfirmedPointsSuccess(logs);
                    }
                });
            }
        });
    }

    public List<PointType> getPointTypeList() {
        return pointTypeList;
    }

    public void setUserData(String floor, String house, String first, String last, int permission, String id) {
        floorName = floor;
        houseName = house;
        firstName = first;
        lastName = last;
        permissionLevel = permission;
        userID = id;
        cacheUtil.writeToCache(id, floor, house, first, last, permission);
    }

    public boolean cacheFileExists() {
        return cacheUtil.cacheFileExists();
    }

    public void getUserDataNoCache(SingletonInterface si) {
        String id = FirebaseAuth.getInstance().getUid();
        if (houseName == null) {
            fbutil.getUserData(id, new FirebaseUtilInterface() {
                @Override
                public void onUserGetSuccess(String floor, String house, String firstName, String lastName, int permission) {
                    setUserData(floor, house, firstName,lastName, permission, id);
                    si.onSuccess();
                }
                @Override
                public void onError(Exception e, Context context) {
                    si.onError(e,context);
                }
            });
        } else
            si.onSuccess();
    }

    public void getUserData(SingletonInterface si) {
        String id = FirebaseAuth.getInstance().getUid();
        if (houseName == null) {
            cacheUtil.getCacheData(this);
            fbutil.getUserData(id, new FirebaseUtilInterface() {
                @Override
                public void onUserGetSuccess(String floor, String house, String firstName, String lastName, int permission) {
                    setUserData(floor, house, firstName,lastName, permission, id);
                    si.onSuccess();
                }

                @Override
                public void onError(Exception e, Context context) {
                    si.onError(e,context);
                }
            });
        } else
            si.onSuccess();
    }

    public void getCachedData() {
        if (houseName == null) {
            cacheUtil.getCacheData(this);
        }
    }

    public String getName() {
        return name;
    }

    public String getHouse() {
        return houseName;
    }

    public int getPermissionLevel() {
        return permissionLevel;
    }

    public void submitPoints(String description, PointType type, SingletonInterface si) {
        PointLog log = new PointLog(description, name, type, floorName, fbutil.getUserReference(userID));
        boolean preApproved = permissionLevel > 0;
        fbutil.submitPointLog(log, null, houseName, userID, preApproved, sysPrefs, new FirebaseUtilInterface() {
            @Override
            public void onSuccess() {
                si.onSuccess();
            }
        });
    }

    public void submitPointWithLink(Link link, SingletonInterface si) {
        if (link.isEnabled()) {
            getPointTypes(new SingletonInterface() {
                              @Override
                              public void onPointTypeComplete(List<PointType> data) {
                                  PointType type = null;
                                  for (PointType pointType : pointTypeList) {
                                      if (pointType.getPointID() == link.getPointTypeId()) {
                                          type = pointType;
                                      }
                                  }
                                  PointLog log = new PointLog(link.getDescription(), name, type, floorName, fbutil.getUserReference(userID));
                                  fbutil.submitPointLog(log, (link.isSingleUse()) ? link.getLinkId() : null, houseName, userID, link.isSingleUse(), sysPrefs, new FirebaseUtilInterface() {

                                    //TODO: Step 3
                                      @Override
                                      public void onSuccess() {
                                          si.onSuccess();
                                      }

                                      @Override
                                      public void onError(Exception e, Context c) {
                                          if (e.getLocalizedMessage().equals("Code was already submitted")) {
                                              Toast.makeText(c, "You have already submitted this code.",
                                                      Toast.LENGTH_SHORT).show();
                                          } else {
                                              si.onError(e, c);
                                          }
                                      }
                                  });
                              }
                          }
            );
        } else {
            si.onError(new Exception("QR is not enabled."), fbutil.getContext());
        }
    }

    public String getFloorName() {
        return floorName;
    }

    public void getLinkWithLinkId(String linkId, SingletonInterface si) {
        fbutil.getLinkWithId(linkId, new FirebaseUtilInterface() {
            @Override
            public void onGetLinkWithIdSuccess(Link link) {
                si.onGetLinkWithIdSuccess(link);
            }
        });
    }

    public void getPointStatistics(SingletonInterface si) {
        boolean getRewards = rewardList == null;
        fbutil.getPointStatistics(userID, getRewards, new FirebaseUtilInterface() {
            @Override
            public void onGetPointStatisticsSuccess(List<House> houses, int userPoints, List<Reward> rewards) {
                houseList = houses;
                totalPoints = userPoints;
                if (getRewards)
                    rewardList = rewards;
                si.onGetPointStatisticsSuccess(houseList, totalPoints, rewardList);
            }
        });
    }

    public void clearUserData() {
        floorName = null;
        houseName = null;
        name = null;
        permissionLevel = 0;
        userID = null;

        cacheUtil.deleteCache();
    }

    public void getFloorCodes(SingletonInterface si) {
        fbutil.getFloorCodes(new FirebaseUtilInterface() {
            @Override
            public void onGetFloorCodesSuccess(Map<String, Pair<String, String>> data) {
                si.onGetFloorCodesSuccess(data);
            }
        });
    }

    public boolean showDialog(){
        return cacheUtil.showDialog();
    }


    /**
     * Get the list of QRCodes that were created by the User with userId.
     *
     * @param shouldRefresh  Boolean that represents if the app should request updated information from the server
     * @param si       SingletonInterface that has the methods onError and onGetQRCodesForUserSuccess implemented.
     */
    public void getUserCreatedQRCodes(boolean shouldRefresh, SingletonInterface si){

        if(this.userCreatedQRCodes == null || shouldRefresh) {
            //No data is currently cached or the cache needs to be refreshed
            fbutil.getQRCodesForUser(userID, new FirebaseUtilInterface() {
                @Override
                public void onError(Exception e, Context context) {
                    si.onError(e,context);
                }

                @Override
                public void onGetQRCodesForUserSuccess(ArrayList<Link> qrCodes) {
                    setUserCreatedQRCodes(qrCodes); // Save to local Cache
                    si.onGetQRCodesForUserSuccess(qrCodes);
                }
            });
        }
        else{
            si.onGetQRCodesForUserSuccess(this.userCreatedQRCodes);
        }
    }

    private void setUserCreatedQRCodes(ArrayList<Link> codes){
        this.userCreatedQRCodes = codes;
    }



    /**
     * Create a new QRCode in the database. If the call is succesful, the new LinkId will be saved into the Link object
     *
     * @param link  Link object to be created in the database and the object that will be updated on success
     * @param si   SingletonInterface with methods onError and onSuccess
     */
    public void createQRCode(Link link, SingletonInterface si){
        fbutil.createQRCode(link, new FirebaseUtilInterface() {
            @Override
            public void onSuccess() {
                si.onSuccess();
            }

            @Override
            public void onError(Exception e, Context context) {
                si.onError(e,context);
            }
        });
    }

    /**
     * Update a Link object in the database with a new Enabled Status
     *
     * @param link  Link object to be updated
     * @param si   SingletonInterface with method OnError and onSuccess implemented
     *
     * @Note    If it is easier to just give the link id, then this method can be changed to handle that instead.
     */
    public void setQRCodeEnabledStatus(Link link, boolean isEnabled, SingletonInterface si){
        fbutil.setQRCodeEnabledStatus(link, isEnabled, new FirebaseUtilInterface() {
            @Override
            public void onSuccess() {
                si.onSuccess();
            }

            @Override
            public void onError(Exception e, Context context) {
                si.onError(e,context);
            }
        });
    }

    /**
     * Update a Link object in the database with a new Archived Status
     *
     * @param link  Link object to be updated
     * @param si   SingletonInterface with method OnError and onSuccess implemented
     *
     * @Note    If it is easier to just give the link id, then this method can be changed to handle that instead.
     */
    public void setQRCodeArchivedStatus(Link link, boolean isArchived, SingletonInterface si){
        fbutil.setQRCodeArchivedStatus(link, isArchived, new FirebaseUtilInterface() {
            @Override
            public void onSuccess() {
                si.onSuccess();
            }

            @Override
            public void onError(Exception e, Context context) {
                si.onError(e,context);
            }
        });
    }

    public void getAllHousePoints(SingletonInterface si) {
        fbutil.getAllHousePoints(houseName, floorName, new FirebaseUtilInterface() {

            @Override
            public void onGetAllHousePointsSuccess(List<PointLog> houseLogs) {
                si.onGetAllHousePointsSuccess(houseLogs);
            }
        });
    }

    /**
     * Handles the updating the database for approving or denying points. It will update the point in the house and the TotalPoints for both user and house
     *
     * @param log                    PointLog:   The PointLog that is to be either approved or denied
     * @paramad approved               boolean:    Was the log approved?
     * @param sui                    FirebaseUtilInterface: Implement the OnError and onSuccess methods
     */
    public void handlePointLog(PointLog log, boolean approved, boolean updating, SingletonInterface sui){
        fbutil.updatePointLogStatus(log, approved, getHouse(),updating,false, new FirebaseUtilInterface() {
            @Override
            public void onSuccess() {
                String msg = getName()+" rejected the point request.";
                if(approved){
                    msg = getName()+" approved the point request.";
                }
                PointLogMessage plm = new PointLogMessage(msg, getName().split(" ")[0], getName().split(" ")[1],getPermissionLevel());
                fbutil.postMessageToPointLog(log, getHouse(), plm, new FirebaseUtilInterface() {
                    @Override
                    public void onSuccess() {

                        sui.onSuccess();
                    }

                    @Override
                    public void onError(Exception e, Context context) {
                        sui.onError(e,context);
                    }
                });
            }

            @Override
            public void onError(Exception e, Context context) {
                sui.onError(e,context);
            }
        });
    }

    /**
     * Retrieve the updates to a point log object as they occur. Used to update pointlog details page
     * @param log
     * @param sui
     */
    public void handlePointLogUpdates(PointLog log, final SingletonInterface sui){
        fbutil.handlePointLogUpdates(log, houseName, new FirebaseUtilInterface() {
            @Override
            public void onError(Exception e, Context context) {
                sui.onError(e,context);
            }

            @Override
            public void onGetPointLogMessageUpdates(List<PointLogMessage> messages) {
                sui.onGetPointLogMessageUpdates(messages);
            }
        });
    }

    /**
     * Add a message to the point log
     * @param log   Log to which the message should be posted
     * @param plm   PointLogMessage to post
     * @param sui   SingletonInterface with onSuccess and onError
     */
    public void postMessageToPointLog(PointLog log, PointLogMessage plm, SingletonInterface sui){
        fbutil.postMessageToPointLog(log, getHouse(), plm, new FirebaseUtilInterface() {
            @Override
            public void onSuccess() {
                sui.onSuccess();
            }

            @Override
            public void onError(Exception e, Context context) {
                sui.onError(e,context);
            }
        });
    }

    /**
     * Add a message to the point log
     * @param log   Log to which the message should be posted
     * @param message   message to post
     * @param sui   SingletonInterface with onSuccess and onError
     */
    public void postMessageToPointLog(PointLog log, String message, SingletonInterface sui){
        PointLogMessage plm = new PointLogMessage(message, getName().split(" ")[0], getName().split(" ")[1], getPermissionLevel());
        postMessageToPointLog(log,plm,sui);
    }
}