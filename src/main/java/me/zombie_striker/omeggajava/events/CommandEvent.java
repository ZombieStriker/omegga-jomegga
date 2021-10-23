package me.zombie_striker.omeggajava.events;

public class CommandEvent implements Event{

    private String message;
    private String[] args;
    private String command;
    private String playername;

    public CommandEvent(String playername, String command, String message){
        this.playername = playername;
        this.message = message;
        this.command = command;
        if(message!=null) {
            this.args = message.split(" ");
        }else{
            this.args=new String[0];
        }
    }

    public String getCommand(){
        return command;
    }
    public String[] getArgs(){
        return args;
    }

    public String getMessage() {
        return message;
    }

    public String getPlayername() {
        return playername;
    }
}
