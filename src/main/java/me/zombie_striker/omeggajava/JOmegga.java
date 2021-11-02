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
import me.zombie_striker.omeggajava.objects.*;
import me.zombie_striker.omeggajava.util.JsonHelper;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.io.File;
import java.net.URISyntaxException;
import java.util.*;

public class JOmegga {


    private ConsoleInput consoleinput;
    private ConsoleOutput consoleoutput;

    private List<Event> events = new LinkedList<>();

    private List<Listener> listeners = new LinkedList<>();

    private boolean running = true;

    private final LinkedList<Player> players = new LinkedList<>();

    private final HashMap<Long, RPCResponse> responseHandlers = new HashMap<>();

    private JOmeggaThread corethread = new JOmeggaThread();

    private Config config = new Config();
    private Store store = new Store();
    private MinigameManager minigameManager = new MinigameManager();

    private String pluginname;

    private String servername;
    private String description;
    private long brickcount = 0;


    private long lastUpdatePlayerPos = 0;
    private long lastUpdateMinigame=0;

    private static JOmegga instance = null;
    protected static JOmegga getInstance(){
        if(instance==null){
            return instance = new JOmegga();
        }
        return instance;
    }

    private static UUID hostUUID = null;

    @Deprecated
    public static void setHostUUID(UUID uuid){
        JOmegga.hostUUID = uuid;
    }

    public static UUID getHostUUID() {
        if(hostUUID==null){
            JOmegga.getRPCValue(new RPCResponse() {
                @Override
                public void onResponse(JSONRPC2Response response) {
                    hostUUID = UUID.fromString((String) response.getResult());
                }

                @Override
                public Object getReturnValue() {
                    return null;
                }
            },"getHostId",(String)"undefined");
        }
        return hostUUID;
    }

    public static String getPluginName() {
        return getInstance().pluginname;
    }

    public static void init(String pluginname) {
        JOmegga.getInstance().pluginname = pluginname;
        JOmegga.registerListener(new RPCListener());
        getInstance().consoleinput = new ConsoleInput();
        getInstance().consoleoutput = new ConsoleOutput();
        getInstance().corethread.start();
    }

    public static Config getConfig() {
        return getInstance().config;
    }


    public static boolean isRunning() {
        return getInstance().running;
    }


    public static void stop() {
        getInstance().running = false;
    }

    protected static void callResponse(long input, JSONRPC2Response response) {
        if (getInstance().responseHandlers.containsKey(input)) {
            getInstance().responseHandlers.remove(input).onResponse(response);
        }
    }

    protected static long registerResponseHandler(RPCResponse response) {
        int length = getInstance().responseHandlers.size();
        for (long i = 0; i < length; i++) {
            if (getInstance().responseHandlers.containsKey(i)) {
                if (System.currentTimeMillis() - getInstance().responseHandlers.get(i).getSentTime() > 10000) {
                    getInstance(). responseHandlers.remove(i);
                }
            }
        }
        for (long i = 0; i < Long.MAX_VALUE; i++) {
            if (!getInstance().responseHandlers.containsKey(i)) {
                getInstance().responseHandlers.put(i, response);
                return i;
            }
        }
        return -1;
    }

    public static List<Event> getEvents() {
        return getInstance().events;
    }

    public static void callEvent(Event event) {
        while (JOmeggaThread.callingEvents) {
        }
        getInstance().  events.add(event);
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

    public static void whisper(String player, String message) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("target", player);
        params.put("line", message);
        JSONRPC2Notification notification = new JSONRPC2Notification("whisper", params);
        getOutput().addToQueue(notification);
    }

    public static void whisper(Player player, String message) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("target", player.getName());
        params.put("line", message);
        JSONRPC2Notification notification = new JSONRPC2Notification("whisper", params);
        getOutput().addToQueue(notification);
    }

    public static void updatePlayersLocation(HashMap<String, Object> hashMap) {
        if (hashMap.size() < 4) {
            JOmegga.log("Updating player's position returned a hashmap with " + hashMap.size() + " values.");
            return;
        }
        for (int i = 0; i < hashMap.size() / 4; i++) {
            JSONObject player = (JSONObject) hashMap.get(i + ".player");
            JSONArray pos = (JSONArray) hashMap.get(i + ".pos");
            boolean isdead = (boolean) hashMap.get(i + ".isDead");
            Player playerinst = JOmegga.getPlayer((String) player.get("name"));
            if (playerinst == null) {
                JOmegga.log("Player " + player + "'s name is null");
            } else {
                playerinst.setAlive(!isdead);
                if (!isdead)
                    try {
                        playerinst.setPosition(new Vector3D((double) pos.get(0), (double) pos.get(1), (double) pos.get(2)));
                    } catch (Exception e4) {
                    }
            }
        }
    }

    public static void getAllPlayerPositions() {
        if (getPlayers().size() == 0)
            return;
        if(System.currentTimeMillis()-getInstance().lastUpdatePlayerPos > 500) {
            getInstance().  lastUpdatePlayerPos = System.currentTimeMillis();
            RPCResponse rpc = new RPCResponse() {

                private HashMap<String, Object> playerPositions = null;

                @Override
                public void onResponse(JSONRPC2Response response) {
                    playerPositions = JsonHelper.convertJsonToHashMap(response.getResult());
                    updatePlayersLocation(playerPositions);
                }

                @Override
                public Object getReturnValue() {
                    return playerPositions;
                }
            };
            getRPCValue(rpc, "getAllPlayerPositions", "undefined");
        }
    }

    public static void updateMinigames() {
        if(System.currentTimeMillis()-getInstance().lastUpdateMinigame > 500) {
            getInstance().lastUpdateMinigame = System.currentTimeMillis();
            getRPCValue(new RPCResponse() {
                @Override
                public void onResponse(JSONRPC2Response response) {
                    HashMap<String, Object> map = JsonHelper.convertJsonToHashMap(response.getResult());
                    if (map == null) {
                        return;
                    }
                    for (int i = 0; i < map.size() / 4; i++) {
                        //String ruleset = (String) map.get(i + ".ruleset");
                        String name = (String) map.get(i + ".name");
                        //JSONArray members = (JSONArray) map.get(i + ".members");
                        JSONArray teams = (JSONArray) map.get(i + ".teams");

                        Minigame minigame = null;
                        if ((minigame = JOmegga.getMinigameManager().getMinigame(name)) == null) {
                            minigame = new Minigame(name);
                            JOmegga.getInstance().minigameManager.registerMinigame(minigame);
                        }

                        for (Object teamobj : teams) {
                            JSONObject team = (JSONObject) teamobj;
                            String teamname = (String) team.get("name");
                            Team team1 = null;
                            if ((team1 = minigame.getTeam(teamname)) == null) {
                                team1 = new Team(teamname);
                                minigame.registerTeam(team1);
                            }


                            JSONArray players = (JSONArray) team.get("members");
                            team1.getPlayers().clear();
                            for (Object p : players) {
                                JSONObject po = (JSONObject) p;
                                String playername = (String) po.get("name");
                                Player player = JOmegga.getPlayer(playername);
                                if (player != null) {
                                    team1.getPlayers().add(player);
                                }
                            }
                        }
                    }
                }

                @Override
                public Object getReturnValue() {
                    return null;
                }
            }, "getMinigames", (String) null);
        }
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
                JSONArray materialsarray = (JSONArray) data.get("materials");
                List<String> materials = new LinkedList<>();
                for(int i = 0; i < materialsarray.size();i++){
                    materials.add((String) materialsarray.get(i));
                }
                saveData.setMaterials(materials);

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
                    brick.setMaterialIndex(((int) ((long) obj.get("material_index"))));
                    if (obj.get("color") instanceof Long) {
                        brick.setColor(new ColorMode((int) ((long) obj.get("color"))));
                    } else {
                        JSONArray colorarray = (JSONArray) obj.get("color");
                        brick.setColor(new Color((int) (long) colorarray.get(2), (int) (long) colorarray.get(1), (int) (long) colorarray.get(1), 255));
                    }
                    brick.setCollision((Boolean) ((JSONObject) obj.get("collision")).get("player"));
                    brick.setWeaponcollision((Boolean) ((JSONObject) obj.get("collision")).get("weapon"));
                    brick.setToolcollision((Boolean) ((JSONObject) obj.get("collision")).get("tool"));
                    brick.setInteractioncollision((Boolean) ((JSONObject) obj.get("collision")).get("interaction"));
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
        getInstance().listeners.add(listener);
    }

    public static void unregisterListener(Listener listener) {
        getInstance().listeners.remove(listener);

    }

    public static void sendRPCNotification(String method, Object... params) {
        JSONRPC2Notification notification = new JSONRPC2Notification(method, Arrays.asList(params));
        getOutput().addToQueue(notification);
    }

    public static void getRPCValue(RPCResponse responseHandler, String method, HashMap<String, Object> namedParams) {
        long id = registerResponseHandler(responseHandler);
        if (id == -1) {
            JOmegga.log("getRPCValue " + method + " could not be send as the id returned is -1");
            return;
        }
        JSONRPC2Request request = new JSONRPC2Request(method, namedParams, id);
        getOutput().addToQueue(request);
    }

    public static void getRPCValue(RPCResponse responseHandler, String method, String param) {
        long id = registerResponseHandler(responseHandler);
        if (id == -1) {
            JOmegga.log("getRPCValue " + method + " could not be send as the id returned is -1");
            return;
        }
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
        return new LinkedList<>(getInstance().players);
    }


    protected static ConsoleOutput getOutput() {
        return getInstance().consoleoutput;
    }

    protected static ConsoleInput getInput() {
        return getInstance().consoleinput;
    }

    public static void addPlayer(Player player) {
        getInstance().players.add(player);
    }

    public static boolean removePlayer(Player player) {
        return getInstance().players.remove(player);
    }


    protected static List<Listener> getListeners() {
        return getInstance().listeners;
    }

    public static void setStore(String key, Object object) {
        JSONRPC2Notification notification = new JSONRPC2Notification("store.set", Arrays.asList(key, object));
        getOutput().addToQueue(notification);
    }

    public static Store getStore() {
        return getInstance().store;
    }

    public static MinigameManager getMinigameManager() {
        return getInstance().minigameManager;
    }

    public static String getServername() {
        return getInstance().servername;
    }

    public static long getBrickcount() {
        return getInstance().brickcount;
    }

    public  static String getDescription() {
        return getInstance().description;
    }

    @Deprecated
    public static void setDescription(String description) {
        JOmegga.getInstance().description = description;
    }

    @Deprecated
    public static void setServername(String servername) {
        JOmegga.getInstance().servername = servername;
    }

    @Deprecated
    public static void setBrickcount(long brickcount) {
        JOmegga.getInstance().brickcount = brickcount;
    }

    public static void deleteStore(String key) {
        JSONRPC2Notification notification = new JSONRPC2Notification("store.delete", Arrays.asList(key));
        getOutput().addToQueue(notification);
    }
}
