package com.overtake.socketio.client;

import com.overtake.base.OTJson;
import com.overtake.utils.OTLog;

import io.socket.SocketIO;

/**
 * Created by kevinhoo on 14-4-10.
 */
public interface IIOClientSubscriber {

    public static final String connect = "connect";
    public static final String disconnect = "disconnect";

    public void IOReceiveEvent(SocketIO client, String eventName, OTJson eventJson);

    public void IOConnect(SocketIO client);

    public void IODisconnect(SocketIO client);
}
