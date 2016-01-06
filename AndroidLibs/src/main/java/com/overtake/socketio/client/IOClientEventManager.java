package com.overtake.socketio.client;

import android.os.Handler;

import com.overtake.base.OTJson;
import com.overtake.utils.OTLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import io.socket.IOAcknowledge;
import io.socket.SocketIO;
import io.socket.SocketIOException;

/**
 * Created by kevinhoo on 14-4-10.
 */
public class IOClientEventManager {

    public void onDisconnect(final SocketIO socketIO) {
        OTLog.i(this, "onDisconnect " + socketIO);

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                ArrayList<IIOClientSubscriber> list = findSubscriber(socketIO, IIOClientSubscriber.disconnect);
                if (list != null) {
                    for (IIOClientSubscriber subscriber : list) {
                        subscriber.IODisconnect(socketIO);
                    }
                }

                //remove this socket from hashmap
                mSubscriberMap.remove(socketIO);
            }
        });
    }

    public void onConnect(final SocketIO socketIO) {
        OTLog.i(this, "onConnect " + socketIO);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                ArrayList<IIOClientSubscriber> list = findSubscriber(socketIO, IIOClientSubscriber.connect);

                OTLog.i(this, "post" + list);

                if (list != null) {
                    for (IIOClientSubscriber subscriber : list) {
                        subscriber.IOConnect(socketIO);
                    }
                }
            }
        });
    }

    public void onMessage(SocketIO socketIO, String s, IOAcknowledge ioAcknowledge) {

    }

    public void onMessage(SocketIO socketIO, JSONObject jsonObject, IOAcknowledge ioAcknowledge) {

    }

    public void on(final SocketIO socketIO, final String eventName, IOAcknowledge ioAcknowledge, final Object... objects) {
        OTLog.i(this, "notify subscriber event " + eventName + objects);

        if (objects != null && objects.length > 0 && objects[0] instanceof JSONObject) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    notifySubscriber(socketIO, eventName, (JSONObject) objects[0]);
                }
            });
        } else {
            OTLog.i(this, "on event did not return json object " + eventName);
        }
    }

    public void onError(SocketIO socketIO, SocketIOException e) {

    }

    private static class SingletonHolder {
        public final static IOClientEventManager INSTANCE = new IOClientEventManager();
    }

    public static IOClientEventManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * client=>
     * ********event1=>List
     * *****************subscriber1
     * *****************subscriber2
     * ********event2=>List
     * *****************subscriber1
     * *****************subscriber3
     */
    volatile private HashMap<SocketIO, HashMap<String, ArrayList<IIOClientSubscriber>>> mSubscriberMap;
    private Handler mHandler;

    private IOClientEventManager() {
        mSubscriberMap = new HashMap<SocketIO, HashMap<String, ArrayList<IIOClientSubscriber>>>();
        mHandler = new Handler();
    }

    public void subscribeEvent(final String eventName, IIOClientSubscriber subscriber, final SocketIO client) {
        if (client == null)
            return;

        HashMap<String, ArrayList<IIOClientSubscriber>> map = mSubscriberMap.get(client);
        if (map == null) {
            map = new HashMap<String, ArrayList<IIOClientSubscriber>>();
            mSubscriberMap.put(client, map);
        }

        ArrayList<IIOClientSubscriber> list = map.get(eventName);
        if (list == null) {
            list = new ArrayList<IIOClientSubscriber>();
        }

        if (!list.contains(subscriber)) {
            list.add(subscriber);
        }

        if (!map.containsKey(eventName)) {
            map.put(eventName, list);
        }
    }

    public void subscribeEvent(final String eventName, IIOClientSubscriber subscriber) {
        subscribeEvent(eventName, subscriber, IOClientManager.getInstance().getDefaultClient());
    }

    public void unSubscribeEvent(String eventName, IIOClientSubscriber subscriber, SocketIO client) {
        ArrayList<IIOClientSubscriber> list = findSubscriber(client, eventName);
        if (list != null) {
            list.remove(subscriber);
        }
    }

    public void unSubscribeEvent(String eventName, IIOClientSubscriber subscriber) {
        unSubscribeEvent(eventName, subscriber, IOClientManager.getInstance().getDefaultClient());
    }

    public void unSubscribeAllEventFromDefaultClient(IIOClientSubscriber subscriber) {
        List<String> list = findEventNameBySubscriber(subscriber);
        for (String eventName : list) {
            unSubscribeEvent(eventName, subscriber);
        }
    }

    public void sendEvent(SocketIO client, IOClientEvent event) {
        if (event.callback != null) {
            client.emit(event.name, event.callback, event.data);
        } else {
            client.emit(event.name, event.data);
        }
    }

    public void sendEvent(IOClientEvent event) {
        sendEvent(IOClientManager.getInstance().getDefaultClient(), event);
    }

    private void notifySubscriber(SocketIO client, String eventName, JSONObject data) {
        if (data == null) {
            OTLog.i(this, "notify subscriber event data is null" + client.toString() + eventName + data);
        }
        ArrayList<IIOClientSubscriber> list = findSubscriber(client, eventName);
        if (list != null) {
            //parse
            HashMap<String, String> object = convertJsonToMap(data);
            //json helper
            OTJson json = OTJson.createJson(object);
            //notify
            for (IIOClientSubscriber subscriber : list) {
                subscriber.IOReceiveEvent(client, eventName, json);
            }
        } else {
            OTLog.i(this, "notify subscriber can't find match" + client.toString() + eventName + data);
        }
    }

    private ArrayList<IIOClientSubscriber> findSubscriber(SocketIO client, String eventName) {
        OTLog.i(this, "find" + mSubscriberMap + client + eventName);

        HashMap<String, ArrayList<IIOClientSubscriber>> map = mSubscriberMap.get(client);
        if (map == null) {
            return null;
        }

        ArrayList<IIOClientSubscriber> list = map.get(eventName);
        if (list == null) {
            return null;
        }

        return list;
    }

    public static HashMap<String, String> convertJsonToMap(JSONObject obj) {
        Iterator<String> nameItr = obj.keys();
        HashMap<String, String> outMap = new HashMap<String, String>();
        while (nameItr.hasNext()) {
            String key = nameItr.next();
            try {
                outMap.put(key, obj.getString(key));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return outMap;
    }


    private List<String> findEventNameBySubscriber(IIOClientSubscriber subscriber) {
        List list = new ArrayList<String>();

        for (HashMap<String, ArrayList<IIOClientSubscriber>> map : mSubscriberMap.values()) {
            for (String eventName : map.keySet()) {
                ArrayList<IIOClientSubscriber> subscribers = map.get(eventName);
                if (subscribers != null && subscribers.contains(subscriber) && !list.contains(eventName)) {
                    list.add(eventName);
                }
            }
        }

        return list;
    }

}
