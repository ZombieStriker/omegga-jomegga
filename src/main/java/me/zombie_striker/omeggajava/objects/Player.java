package me.zombie_striker.omeggajava.objects;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;;
import me.zombie_striker.omeggajava.JOmegga;
import me.zombie_striker.omeggajava.RPCResponse;
import me.zombie_striker.omeggajava.util.JsonHelper;

import java.util.HashMap;
import java.util.Map;

public class Player {

    private String name;
    private String id;
    private String state;
    private String controller;

    private Vector3D positon;

    public Player(String name, String id, String state, String controller) {
    this.name = name;
    this.id = id;
    this.state = state;
    this.controller = controller;
    }

    public String getName() {
        return name;
    }

    public String getController() {
        return controller;
    }

    public String getId() {
        return id;
    }

    public String getState() {
        return state;
    }

    public Vector3D getPosition() {
        return positon;
    }

    public void updatePositon() {
        JOmegga.getRPCValue(new RPCResponse() {
            @Override
            public void onResponse(JSONRPC2Response response) {
                HashMap<String, Object> result = JsonHelper.convertJsonToHashMap(response.getResult());
                if(result==null)
                    return;
                try {
                    positon = new Vector3D(Double.parseDouble(result.get("0") + ""), Double.parseDouble(result.get("1") + ""), Double.parseDouble(result.get("2") + "")
                    );
                }catch (Exception e){
                    for(Map.Entry<String, Object> entry : result.entrySet()){
                        JOmegga.log(entry.getKey()+" "+entry.getValue());
                    }

                }
            }

            @Override
            public Object getReturnValue() {
                return getPosition();
            }
        },"player.getPosition", name);


    }
}
