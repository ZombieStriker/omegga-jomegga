package me.zombie_striker.omeggajava;

import com.kmschr.brs.*;
import com.kmschr.brs.enums.Direction;
import com.kmschr.brs.enums.Rotation;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Notification;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import me.zombie_striker.omeggajava.events.Event;
import me.zombie_striker.omeggajava.events.Listener;
import me.zombie_striker.omeggajava.logic.listeners.RPCListener;
import me.zombie_striker.omeggajava.objects.Player;
import me.zombie_striker.omeggajava.objects.PromisedObject;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.io.File;
import java.net.URISyntaxException;
import java.util.*;

public class JOmegga {


    private static ConsoleInput consoleinput;
    private static ConsoleOutput consoleoutput;

    private static List<Event> events = new LinkedList<>();

    private static List<Listener> listeners = new LinkedList<>();

    private static boolean running = true;

    private static final LinkedList<Player> players = new LinkedList<>();

    private static final HashMap<Long, RPCResponse> responseHandlers = new HashMap<>();

    private static JOmeggaThread corethread = new JOmeggaThread();


    public static void init() {
        JOmegga.registerListener(new RPCListener());
        consoleinput = new ConsoleInput();
        consoleoutput = new ConsoleOutput();
        corethread.start();
    }


    public static boolean isRunning() {
        return running;
    }


    public static void stop() {
        running = false;
    }

    protected static void callResponse(long input, JSONRPC2Response response) {
        if (responseHandlers.containsKey(input)) {
            responseHandlers.remove(input).onResponse(response);
        }
    }

    protected static long registerResponseHandler(RPCResponse response) {
        for (long i = 0; i < 999; i++) {
            if (!responseHandlers.containsKey(i)) {
                responseHandlers.put(i, response);
                return i;
            }
        }
        return -1;
    }

    public static List<Event> getEvents() {
        return events;
    }

    public static void callEvent(Event event) {
        while (JOmeggaThread.callingEvents) {
        }
        events.add(event);
    }


    public static File getJOmeggaJar() {
        try {
            return new File(JOmegga.class.getProtectionDomain().getCodeSource().getLocation()
                    .toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static File getOmeggaDir() {
        return getJOmeggaJar().getParentFile().getParentFile().getParentFile();
    }

    public static void logAndBroadcast(String messaage) {
        log(messaage);
        broadcast(messaage);
    }

    public static PromisedObject writeln(String command) {
        JSONRPC2Notification notification = new JSONRPC2Notification("exec", Arrays.asList(command));
        getOutput().addToQueue(notification);
        PromisedObject promise = new PromisedObject();
        getRPCValue(new RPCResponse() {
            private JSONRPC2Response res;

            @Override
            public void onResponse(JSONRPC2Response response) {
                this.res = response;
                if (res == null || response.getResult() == null) {
                    promise.setPromise("null");
                    return;
                }
                try {
                    promise.setPromise(((JSONObject) response.getResult()).toJSONString());
                } catch (Exception e3) {
                    promise.setPromise(((JSONArray) response.getResult()).toJSONString());
                }
            }

            @Override
            public Object getReturnValue() {
                return res;
            }
        }, "exec", command);
        return promise;
    }

    public static void log(String log) {
        JSONRPC2Notification notification = new JSONRPC2Notification("log", Arrays.asList(log));
        getOutput().addToQueue(notification);
    }

    public static void broadcast(String message) {
        JSONRPC2Notification notification = new JSONRPC2Notification("broadcast", Arrays.asList(message));
        getOutput().addToQueue(notification);
    }

    public static void exec(String message) {
        JSONRPC2Notification notification = new JSONRPC2Notification("exec", Arrays.asList(message));
        getOutput().addToQueue(notification);
    }

    public static void clearallbricks(boolean quiet) {
        JSONRPC2Notification notification = new JSONRPC2Notification("clearAllBricks", Arrays.asList(quiet));
        getOutput().addToQueue(notification);
    }

    public static void clearBricks(Player player, boolean quiet) {
        clearBricks(player.getName(), quiet);
    }

    public static void clearBricks(String target, boolean quiet) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("target", target);
        params.put("quiet", quiet);
        JSONRPC2Notification notification = new JSONRPC2Notification("clearBricks", params);
        getOutput().addToQueue(notification);
    }

    public static void whisper(Player player, String message) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("target", player.getName());
        params.put("line", message);
        JSONRPC2Notification notification = new JSONRPC2Notification("whisper", params);
        getOutput().addToQueue(notification);
    }

    public static void save(String name) {
        //JSONRPC2Notification notification = new JSONRPC2Notification("saveBricks", Arrays.asList(name));
        //getOutput().addToQueue(notification);
       getRPCValue(new RPCResponse() {
            @Override
            public void onResponse(JSONRPC2Response response) {

            }

            @Override
            public Object getReturnValue() {
                return null;
            }
        }, "saveBricks", name);
    }

    public static RPCResponse readSaveData(String name) {
        RPCResponse rpc = new RPCResponse() {

            private JSONObject initial = null;
            private SaveData saveData = null;

            @Override
            public void onResponse(JSONRPC2Response response) {
                JSONObject data = (JSONObject) response.getResult();
                initial = data;
                saveData = new SaveData();
                saveData.setAuthor(new User(UUID.fromString((String) ((JSONObject) data.get("author")).get("id")), (String) ((JSONObject) data.get("author")).get("name")));

                long brickcount = (long) data.get("brick_count");
                JSONArray array = (JSONArray) data.get("brick_owners");
                List<User> brickowners = new LinkedList<>();
                for (Object o : array) {
                    JSONObject obj = (JSONObject) o;
                    BrickOwner brickOwner = new BrickOwner(UUID.fromString((String) obj.get("id")), (String) obj.get("name"), (int) ((long) obj.get("bricks")));
                    brickowners.add(brickOwner);
                }
                saveData.setBrickOwners(brickowners);

                saveData.getColors().clear();
                JSONArray colors = (JSONArray) data.get("colors");
                List<ColorMode> colormodes = new LinkedList<>();
                for (int i = 0; i < colors.size(); i++) {
                    colormodes.add(null);
                }
                for (int i = 0; i < colors.size(); i++) {
                    JSONArray array2 = (JSONArray) colors.get(i);
                    try {
                        colormodes.set(i, new Color((byte) (long) array2.get(2), (byte) (long) array2.get(1), (byte) (long) array2.get(0), (byte) (long) array2.get(3)));
                    } catch (Exception e4) {

                    }
                }
                saveData.setColors(colormodes);

                saveData.setBrickAssets((List<String>) data.get("brick_assets"));
                JSONArray arrayOfBricks = (JSONArray) data.get("bricks");
                List<Brick> bricks = new LinkedList<>();
                for (Object object : arrayOfBricks) {
                    JSONObject obj = (JSONObject) object;
                    Brick brick = new Brick();
                    brick.setAssetNameIndex((int) ((long) obj.get("asset_name_index")));
                    brick.setVisibility((Boolean) obj.get("visibility"));
                    brick.setOwnerIndex((int) ((long) obj.get("owner_index")));
                    brick.setRotation(Rotation.values()[((int) ((long) obj.get("rotation")))]);
                    brick.setDirection(Direction.values()[((int) ((long) obj.get("direction")))]);
                    if (obj.get("color") instanceof Long) {
                        brick.setColor(new ColorMode((int) ((long) obj.get("color"))));
                    } else {
                        JSONArray colorarray = (JSONArray) obj.get("color");
                        brick.setColor(new Color((int) (long) colorarray.get(2), (int) (long) colorarray.get(1), (int) (long) colorarray.get(1), 255));
                    }
                    brick.setCollision((Boolean) ((JSONObject) obj.get("collision")).get("player"));
                    brick.setSize(((int) ((long) ((JSONArray) obj.get("size")).get(0))), ((int) ((long) ((JSONArray) obj.get("size")).get(1))), ((int) ((long) ((JSONArray) obj.get("size")).get(2))));
                    brick.setPosition(((int) ((long) ((JSONArray) obj.get("position")).get(0))), ((int) ((long) ((JSONArray) obj.get("position")).get(1))), ((int) ((long) ((JSONArray) obj.get("position")).get(2))));
                    bricks.add(brick);
                }
                saveData.setBricks(bricks);

                JSONObject out1 = saveData.toRPCData();
                JSONObject out2 = initial;
                boolean craller = false;
                for (String key : out2.keySet()) {
                    if (!out1.containsKey(key)) {
                        JOmegga.log("Does not contain " + key);
                    }
                }
            }

            @Override
            public Object getReturnValue() {
                return saveData;
            }
        };
        getRPCValue(rpc, "readSaveData", name);
        return rpc;

    }

    public static void loadBricks(String name) {
        loadBricks(name, 0, 0, 0, true);
    }

    public static void loadBricks(String name, int x, int y, int z) {
        loadBricks(name, x, y, z, true);
    }

    public static void loadSaveData(JSONObject data, int x, int y, int z, boolean quiet) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("data", data);
        params.put("offX", x);
        params.put("offY", y);
        params.put("offZ", z);
        params.put("quiet", quiet);
        JSONRPC2Notification notification = new JSONRPC2Notification("loadSaveData", params);
        getOutput().addToQueue(notification);
    }

    public static void loadBricks(String name, int x, int y, int z, boolean quiet) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("offX", x);
        params.put("offY", y);
        params.put("offZ", z);
        params.put("quiet", quiet);
        JSONRPC2Notification notification = new JSONRPC2Notification("loadBricks", params);
        getOutput().addToQueue(notification);
    }

    public static void registerListener(Listener listener) {
        listeners.add(listener);
    }

    public static void unregisterListener(Listener listener) {
        listeners.remove(listener);

    }

    public static void sendRPCNotification(String method, Object... params) {
        JSONRPC2Notification notification = new JSONRPC2Notification(method, Arrays.asList(params));
        getOutput().addToQueue(notification);
    }

    public static void getRPCValue(RPCResponse responseHandler, String method, HashMap<String, Object> namedParams) {
        long id = registerResponseHandler(responseHandler);
        JSONRPC2Request request = new JSONRPC2Request(method, namedParams, id);
        getOutput().addToQueue(request);
    }

    public static void getRPCValue(RPCResponse responseHandler, String method, String param) {
        long id = registerResponseHandler(responseHandler);
        JSONRPC2Request request = new JSONRPC2Request(method, Arrays.asList(param), id);
        getOutput().addToQueue(request);
    }

    public static void sendRPCResponse(JSONRPC2Response response) {
        getOutput().addToQueue(response);
    }

    public static Player getPlayer(String name) {
        for (Player player : getPlayers()) {
            if (player.getName().equals(name))
                return player;
        }
        return null;
    }

    public static List<Player> getPlayers() {
        return new LinkedList<>(players);
    }


    protected static ConsoleOutput getOutput() {
        return consoleoutput;
    }

    protected static ConsoleInput getInput() {
        return consoleinput;
    }

    public static void addPlayer(Player player) {
        players.add(player);
    }

    public static boolean removePlayer(Player player) {
        return players.remove(player);
    }


    protected static List<Listener> getListeners() {
        return listeners;
    }
}
