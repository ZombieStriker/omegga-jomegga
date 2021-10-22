package me.zombie_striker.omeggajava.events;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;

public class RPCResponseEvent extends RPCReadEvent {

    private JSONRPC2Response request;

    public RPCResponseEvent(JSONRPC2Response request){
        super(request.toJSONString());
        this.request = request;
    }

    public JSONRPC2Response getResponse() {
        return request;
    }
}
