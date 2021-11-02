package me.zombie_striker.omeggajava;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Message;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Notification;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import me.zombie_striker.omeggajava.events.*;
import me.zombie_striker.omeggajava.util.JsonHelper;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class ConsoleInput implements Runnable {

    private Thread thread;

    public ConsoleInput() {
       thread =  new Thread(this);
       thread.start();
    }

    public void stop(){
        thread.stop();
    }

    @Override
    public void run() {
        try {
            InputStreamReader is = new InputStreamReader(System.in);
            while (JOmegga.isRunning()) {
                StringBuilder message = new StringBuilder();
                int i;
                while ((i = is.read()) != 10&&i!=-1 ) {
                    message.append((char) i);
                }
                try {
                    JSONRPC2Message m = JSONRPC2Message.parse(message.toString());
                    if (m instanceof JSONRPC2Request) {
                        JSONRPC2Request req = (JSONRPC2Request) m;
                        RPCRequestEvent event = new RPCRequestEvent(req);
                        JOmegga.callEvent(event);
                        if (req.getMethod().equals("init")) {
                            HashMap<String, Object> config = JsonHelper.convertJsonToHashMap(req.getParams());
                            for(Map.Entry<String, Object> key : config.entrySet()){
                                JOmegga.getConfig().set(key.getKey(),key.getValue());
                            }
                            JSONRPC2Response response = new JSONRPC2Response("init", req.getID());
                            JOmegga.sendRPCResponse(response);
                            JOmegga.callEvent(new InitEvent());
                        } else if (req.getMethod().equals("stop")) {
                            JSONRPC2Response response = new JSONRPC2Response("stop", req.getID());
                            JOmegga.sendRPCResponse(response);
                            JOmegga.callEvent(new StopEvent());
                            JOmegga.log("Stopping...");
                            JOmegga.stop();
                        }
                    } else if (m instanceof JSONRPC2Notification) {
                        JSONRPC2Notification notification = (JSONRPC2Notification) m;
                        RPCNotificationEvent event = new RPCNotificationEvent(notification);
                        JOmegga.callEvent(event);
                    } else if (m instanceof JSONRPC2Response) {
                        JSONRPC2Response req = (JSONRPC2Response) m;
                        RPCResponseEvent event = new RPCResponseEvent(req);
                        JOmegga.callEvent(event);

                        JOmegga.callResponse((long) req.getID(), req);
                    }
                } catch (Exception e4) {
                    e4.printStackTrace();
                }
            }
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }
}
