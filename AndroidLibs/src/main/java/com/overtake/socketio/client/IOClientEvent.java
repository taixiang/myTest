package com.overtake.socketio.client;

import com.overtake.base.OTJson;

import io.socket.IOAcknowledge;

/**
 * Created by kevinhoo on 14-4-10.
 */
public class IOClientEvent {
    public String name;
    public Object data;
    public IOAcknowledge callback;
}
