package me.zombie_striker.omeggajava;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;

public abstract class RPCResponse {

    private long sentTime = System.currentTimeMillis();

    public abstract void onResponse(JSONRPC2Response response);

    public abstract Object getReturnValue();

    public long getSentTime() {
        return sentTime;
    }
}
