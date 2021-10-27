package me.zombie_striker.omeggajava.objects;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import me.zombie_striker.omeggajava.JOmegga;
import me.zombie_striker.omeggajava.RPCResponse;
import net.minidev.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Config {

    private HashMap<String, Object> config = new HashMap<>();

    public Config(){}

    public void set(String key, Object value) {
        config.put(key,value);
    }
    public Object get(String key){
        return config.get(key);
    }
    public Set<String> getKeys(){
        return config.keySet();
    }
}
