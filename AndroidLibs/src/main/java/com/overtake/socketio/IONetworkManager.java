package com.overtake.socketio;


import java.util.Observable;

/**
 * Created by kevinhoo on 14-4-10.
 */
public class IONetworkManager extends Observable {

    private static class SingletonHolder {
        public static final IONetworkManager INSTANCE = new IONetworkManager();
    }

    public static IONetworkManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private IONetworkManager() {
    }

    public void reload() {
        setChanged();

        notifyObservers();
    }


}
