package com.overtake.socketio.client;

import com.overtake.utils.OTLog;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.Collection;
import java.util.HashMap;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

/**
 * Created by kevinhoo on 14-4-10.
 */
public class IOClientManager {

    public static String DefaultClientKey = "default_client";

    volatile private HashMap<String, SocketIO> mIOClientMap = new HashMap<String, SocketIO>();

    private static class SingletonHolder {
        public final static IOClientManager INSTANCE = new IOClientManager();
    }

    public static IOClientManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public SocketIO findClientByKey(String key) {
        SocketIO client = null;
        if (key != null) {
            client = mIOClientMap.get(key);
        }
        return client;
    }

    public Collection<SocketIO> getClients() {
        return mIOClientMap.values();
    }

    public void connectServer(final SocketIO client, final String key) {
        if (client == null) {
            OTLog.i(this, "server should not be null");
            return;
        }

        if (key == null) {
            OTLog.i(this, "key should not be null");
            return;
        }

        SocketIO oldClient = findClientByKey(key);
        if (oldClient != null) {
            oldClient.disconnect();
        }

        mIOClientMap.put(key, client);
        client.connect(new IOCallback() {
            @Override
            public void onDisconnect() {
                IOClientEventManager.getInstance().onDisconnect(client);
            }

            @Override
            public void onConnect() {
                IOClientEventManager.getInstance().onConnect(client);
            }

            @Override
            public void onMessage(String s, IOAcknowledge ioAcknowledge) {
                IOClientEventManager.getInstance().onMessage(client, s, ioAcknowledge);
            }

            @Override
            public void onMessage(JSONObject jsonObject, IOAcknowledge ioAcknowledge) {
                IOClientEventManager.getInstance().onMessage(client, jsonObject, ioAcknowledge);
            }

            @Override
            public void on(String s, IOAcknowledge ioAcknowledge, Object... objects) {
                IOClientEventManager.getInstance().on(client, s, ioAcknowledge, objects);
            }

            @Override
            public void onError(SocketIOException e) {
                IOClientEventManager.getInstance().onError(client, e);
            }
        });
        //make mIOClientMap volatile
    }

    public void connectDefaultServer(String url) {
        try {
            SocketIO client = new SocketIO(url);
            connectServer(client, DefaultClientKey);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public SocketIO getDefaultClient() {
        return findClientByKey(DefaultClientKey);
    }

    public void disconnectAllClient() {
        OTLog.i(this, "disconnectAllClient :");
        for (String key : mIOClientMap.keySet()) {
            disconnectClient(key);
        }
    }

    public void disconnectDefaultClient() {
        OTLog.i(this, "disconnectDefaultClient :");
        disconnectClient(DefaultClientKey);
    }

    public void disconnectClient(final String key) {
        SocketIO client = findClientByKey(key);
        if (client != null) {
            OTLog.i(this, "stop client :" + client.toString());
            client.disconnect();
            mIOClientMap.remove(key);
        }
    }

}
