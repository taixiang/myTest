package com.example.otto;

import com.squareup.otto.Bus;

/**
 * Created by taixiang on 2015/10/22.
 */
public class BusProvider {
    private static final Bus BUS = new Bus();

    public static Bus getInstance(){
        return BUS;
    }

    private BusProvider(){

    }

}
