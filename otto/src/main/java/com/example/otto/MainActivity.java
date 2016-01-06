package com.example.otto;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;

import com.overtake.objectlist.AdaptableViewManager;
import com.overtake.objectlist.view.PullToRefreshListViewContainer;
import com.squareup.otto.Produce;

import java.util.Random;

public class MainActivity extends FragmentActivity {
    public static final float DEFAULT_LAT = 40.440866f;
    public static final float DEFAULT_LON = -79.994085f;
    private static final float OFFSET = 0.1f;
    private static final Random RANDOM = new Random();

    private static float lastLatitude = DEFAULT_LAT;
    private static float lastLongitude = DEFAULT_LON;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        

        findViewById(R.id.clear_location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tell everyone to clear their location history.
                BusProvider.getInstance().post(new LocationClearEvent());

                // Post new location event for the default location.
                lastLatitude = DEFAULT_LAT;
                lastLongitude = DEFAULT_LON;
                BusProvider.getInstance().post(produceLocationEvent());
            }
        });

        findViewById(R.id.move_location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastLatitude += (RANDOM.nextFloat() * OFFSET * 2) - OFFSET;
                lastLongitude += (RANDOM.nextFloat() * OFFSET * 2) - OFFSET;
                BusProvider.getInstance().post(produceLocationEvent());
            }
        });
    }

    @Override protected void onResume() {
        super.onResume();

        // Register ourselves so that we can provide the initial value.
        BusProvider.getInstance().register(this);
    }

    @Override protected void onPause() {
        super.onPause();

        // Always unregister when an object no longer should be on the bus.
        BusProvider.getInstance().unregister(this);
    }

    @Produce
    public LocationChangedEvent produceLocationEvent() {
        // Provide an initial value for location based on the last known position.
        return new LocationChangedEvent(lastLatitude, lastLongitude);
    }

}
