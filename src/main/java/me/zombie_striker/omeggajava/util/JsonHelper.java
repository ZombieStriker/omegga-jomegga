package me.zombie_striker.omeggajava.util;

import me.zombie_striker.omeggajava.JOmegga;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class JsonHelper {

    public static HashMap<String,Object> convertJsonToHashMap(Object json){
        try {
            if(json instanceof JSONArray){
                HashMap<String,Object> returnVal = new HashMap<>();
                for(int j = 0; j < ((JSONArray)json).size(); j++){
                    Object temp = ((JSONArray)json).get(j);
                    if(temp instanceof JSONObject){
                        HashMap<String, Object> returnval2 = convertJsonToHashMap(temp);
                        for (Map.Entry<String, Object> entry : returnval2.entrySet()) {
                            returnVal.put(j + "." + entry.getKey(), entry.getValue());
                        }
                    }else if(temp instanceof JSONArray) {
                        HashMap<String, Object> returnval2 = convertJsonToHashMap(temp);
                        for (Map.Entry<String, Object> entry : returnval2.entrySet()) {
                            returnVal.put(j + "." + entry.getKey(), entry.getValue());
                        }
                    }else if (temp instanceof String){
                        returnVal.put(j+"",temp);
                    }else if (temp instanceof Long){
                        returnVal.put(j+"",temp);
                    }else if (temp instanceof Integer){
                        returnVal.put(j+"",temp);
                    }else if (temp instanceof Short){
                        returnVal.put(j+"",temp);
                    }else if (temp instanceof Byte){
                        returnVal.put(j+"",temp);
                    }else if (temp instanceof Double){
                        returnVal.put(j+"",temp);
                    }else if (temp instanceof Boolean){
                        returnVal.put(j+"",temp);
                    }else{
                        JOmegga.log("JSON VAL IS "+temp.getClass().getName());
                    }
                }
                return returnVal;
            }else if (json instanceof JSONObject){
               return (JSONObject)(json);
            }else{
                JOmegga.log("JSON IS "+json.getClass().getName());
            }
        } catch (Error | Exception e4) {
            JOmegga.log(json+"  -- JSON ERR");
        }
        return null;
    }
}
