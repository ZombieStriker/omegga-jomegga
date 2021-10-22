package me.zombie_striker.omeggajava.events;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Notification;

public class RPCNotificationEvent extends RPCReadEvent {

    private JSONRPC2Notification request;

    public RPCNotificationEvent(JSONRPC2Notification request){
        super(request.toJSONString());
        this.request = request;
    }

    public JSONRPC2Notification getNotification() {
        return request;
    }
}
