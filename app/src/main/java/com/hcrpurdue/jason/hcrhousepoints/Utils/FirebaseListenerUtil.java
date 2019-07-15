/**
 * Created By Brian Johncox on 7/14/19
 *
 * This class is designed to hold all information about Listeners for the app. At initialization of
 * the app, the listeners will be set to call the run all method for that specific collection or
 * record. As a page is loaded, if needed it can add a ListenerCallback to the queue to be called.
 * Please make sure that when the page is killed, the Callback is removed from the active list.
 */


package com.hcrpurdue.jason.hcrhousepoints.Utils;

import com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces.ListenerCallbackInterface;

import java.util.HashMap;
import java.util.Map;

public class FirebaseListenerUtil {

    static FirebaseListenerUtil fluListener;

    private Map<String, ListenerCallbackInterface> personalPointListeners = new HashMap<>();

    /**
     * Get the instance of the FirebaseUtilListener to be used in the app.
     * @return
     */
    public static FirebaseListenerUtil getInstance(){
        if(fluListener == null){
            fluListener = new FirebaseListenerUtil();
        }
        return fluListener;
    }

    /**
     * Add a Callback to the list of events to be called when an update is made to any Point Log owned by this user
     * @param id    String id for this event handler
     * @param lcu   ListenerCallbackInterface that implements the handleUserPointLogUpdates method
     */
    public void addPersonalPointCallback(String id, ListenerCallbackInterface lcu){
        personalPointListeners.put(id,lcu);
    }

    /**
     * Remove a Callback from the list of events to be called.
     * @param id    String id for this callback
     */
    public void removePersonalPointCallback(String id){
        if(personalPointListeners.containsKey(id)){
            personalPointListeners.remove(id);
        }
    }

    /**
     * Method to be called by FirebaseUtil upon update to Point Log owned by this user.
     */
    public void runAllPersonalPointCallbacks(){
        for(ListenerCallbackInterface lci: personalPointListeners.values()){
            lci.handleUserPointLogUpdate();
        }
    }

}
