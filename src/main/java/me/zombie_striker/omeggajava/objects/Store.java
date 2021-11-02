package me.zombie_striker.omeggajava.objects;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import me.zombie_striker.omeggajava.JOmegga;
import me.zombie_striker.omeggajava.RPCResponse;

import java.util.HashMap;

public class Store {

    private HashMap<String,Object> localCache = new HashMap<>();

    public void set(String key, Object object){
        localCache.put(key,object);
        JOmegga.deleteStore(key);
        JOmegga.setStore(key, object);
    }
    public boolean containsLocally(String key){
        if(localCache.containsKey(key)){
            return true;
        }
        get(key);
        return false;
    }
    public Object get(String key){
        if(localCache.containsKey(key))
            return localCache.get(key);
        PromisedObject promise = new PromisedObject();
        RPCResponse response = new RPCResponse() {
            @Override
            public void onResponse(JSONRPC2Response response) {
                localCache.put(key,response.getResult());
                promise.setPromise(response.getResult());
            }

            @Override
            public Object getReturnValue() {
                return null;
            }
        };
        JOmegga.getRPCValue(response,"store.get",key);
        return promise;
    }
}
