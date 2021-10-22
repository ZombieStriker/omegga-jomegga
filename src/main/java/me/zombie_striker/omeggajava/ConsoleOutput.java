package me.zombie_striker.omeggajava;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Message;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Notification;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConsoleOutput {

    private List<JSONRPC2Message> messages = new LinkedList<>();
    private boolean reading = false;

    public void tick(){
        reading=true;
        LinkedList<JSONRPC2Message> messages1 = new LinkedList<>(messages);
        messages.clear();
        reading=false;
        for(JSONRPC2Message m : messages1){
            String message = m.toJSONString();
            if(m instanceof JSONRPC2Request){
                message = message.replaceAll("params\":\\[\"","params\":\"").replaceAll("\\],\"jsonrpc\"",",\"jsonrpc\"");
            }else if (m instanceof JSONRPC2Notification){
               /* if(((JSONRPC2Notification) m).getMethod().equals("saveBricks")) {
                    message = message.replaceAll("params\":\\[\"","params\":\"").replaceAll("\\],\"jsonrpc\"",",\"jsonrpc\"");
                }*/

            }
            System.out.println(message);
        }
    }

     public void addToQueue(JSONRPC2Message message){
         while(reading){
             //Wait;
         }
         messages.add(message);
     }
}
