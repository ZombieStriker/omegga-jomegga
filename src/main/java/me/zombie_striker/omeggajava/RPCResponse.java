package me.zombie_striker.omeggajava;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;

public abstract class RPCResponse {

    public abstract void onResponse(JSONRPC2Response response);
}
