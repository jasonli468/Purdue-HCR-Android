package Models;


import java.util.HashMap;
import java.util.Map;

public class PointLogMessage {

    private String message;
    private String senderFirstName;
    private String senderLastName;
    private int senderPermissionLevel;

    private static final String MESSAGE_KEY = "Message";
    private static final String SENDER_FIRST_NAME_KEY = "SenderFirstName";
    private static final String SENDER_LAST_NAME_KEY = "SenderLastName";
    private static final String SENDER_PERMISSION_LEVEL_KEY = "SenderPermissionLevel";


    public PointLogMessage(String message, String senderFirstName, String senderLastName, int senderPermissionLevel) {
        this.message = message;
        this.senderFirstName = senderFirstName;
        this.senderLastName = senderLastName;
        this.senderPermissionLevel = senderPermissionLevel;
    }

    public PointLogMessage(Map<String, Object> document) {
        this.message = (String) document.get(MESSAGE_KEY);
        this.senderFirstName = (String) document.get(SENDER_FIRST_NAME_KEY);
        this.senderLastName = (String) document.get(SENDER_LAST_NAME_KEY);
        this.senderPermissionLevel = ((Long) document.get(SENDER_PERMISSION_LEVEL_KEY)).intValue();

    }

    public Map<String, Object> generateFirebaseMap(){
        HashMap<String,Object> map = new HashMap<>();
        map.put(MESSAGE_KEY,this.message);
        map.put(SENDER_FIRST_NAME_KEY,this.senderFirstName);
        map.put(SENDER_LAST_NAME_KEY,this.senderLastName);
        map.put(SENDER_PERMISSION_LEVEL_KEY,this.senderPermissionLevel);
        return map;
    }


    public String getMessage() {
        return message;
    }

    public String getSenderFirstName() {
        return senderFirstName;
    }

    public String getSenderLastName() {
        return senderLastName;
    }

    public int getSenderPermissionLevel() {
        return senderPermissionLevel;
    }
}
