package me.zombie_striker.omeggajava.events;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;

public class RPCRequestEvent extends RPCReadEvent {

    private JSONRPC2Request request;

    public RPCRequestEvent(JSONRPC2Request request){
        super(request.toJSONString());
        this.request = request;
    }

    public JSONRPC2Request getRequest() {
        return request;
    }
}
