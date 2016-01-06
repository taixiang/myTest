package com.example.otto;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;

import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by taixiang on 2015/10/22.
 */
public class LocationHistoryFragment extends ListFragment {

    private final List<String> locationEvents = new ArrayList<String>();
    private ArrayAdapter<String> adapter;

    @Override public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, locationEvents);
        setListAdapter(adapter);
    }

    @Subscribe public void onLocationChanged(LocationChangedEvent event) {
        locationEvents.add(0, event.toString());
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Subscribe
    public void onLocationCleared(LocationClearEvent event) {
        locationEvents.clear();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

}
