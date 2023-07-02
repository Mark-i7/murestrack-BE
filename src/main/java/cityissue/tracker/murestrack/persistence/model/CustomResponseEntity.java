package cityissue.tracker.murestrack.persistence.model;

import java.util.HashMap;
import java.util.Map;

public class CustomResponseEntity {
    public static Map<String, String> getMessage(String value){
        Map<String, String> response = new HashMap<>();
        response.put("message", value);
        return response;
    }

    public static Map<String, String> getError(String value){
        Map<String, String> response = new HashMap<>();
        response.put("error", value);
        return response;
    }

}
