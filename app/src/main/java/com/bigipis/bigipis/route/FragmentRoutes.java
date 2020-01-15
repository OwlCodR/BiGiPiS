package com.bigipis.bigipis.route;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bigipis.bigipis.R;
import com.bigipis.bigipis.source.Route;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FragmentRoutes extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private View myInflatedView;
    private List<Route> listRoutes;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView recyclerView;
    private String filter;


    final public static String USER_FILTER = "USER";  // Some user's routes
    final public static String DATE_FILTER = "DATE";  // Routes which had benn created in some day/month/year
    final public static String NEAR_FILTER = "NEAR";  // Routes which are near from your position on map
    final public static String NO_FILTER = "NO";      // All route

    final public static String TAG_FILTER = "FILTER";
    final public static String TAG_DATA = "DATA";

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myInflatedView = inflater.inflate(R.layout.fragment_list_routes, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) myInflatedView.findViewById(R.id.swipeRefreshLayoutRoutes);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));

        Bundle bundle = getArguments();
        if (bundle != null)
            listRoutes = getData(bundle.getString(TAG_FILTER), bundle.getString(TAG_DATA));
        else
            listRoutes = getData(null, null);

        recyclerView = (RecyclerView) myInflatedView.findViewById(R.id.recyclerViewRoutes);
        RecyclerViewRoutesAdapter adapter = new RecyclerViewRoutesAdapter(getActivity(), listRoutes);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        recyclerView.setItemAnimator(itemAnimator);
        recyclerView.setAdapter(adapter);
        return myInflatedView;
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        mSwipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {

        Bundle bundle = getArguments();
        if (bundle != null)
            listRoutes = getData(bundle.getString(TAG_FILTER), bundle.getString(TAG_DATA));
        else
            listRoutes = getData(null, null);

        RecyclerViewRoutesAdapter adapter = new RecyclerViewRoutesAdapter(getActivity(), listRoutes);
        recyclerView.setAdapter(adapter);
        mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 3000);
    }

    private List<Route> getData(String filter, Object data) {
        /*
            USER_FILTER: data - UUID (String)
            DATE_FILTER: data - date dd.mm.yy (String)
            NEAR_FILTER: data - position (String)
            NO_FILTER: data - null
        */
        final List<Route> list = new ArrayList<>();

        if (filter != null && data != null && !filter.equals(NO_FILTER)) {
            switch (filter) {
                case USER_FILTER:
                {
                    FirebaseFirestore.getInstance().collection("routes")
                        .whereEqualTo("user", data.toString())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d("Firebase", document.getId() + " => " + document.getData());
                                        list.add(document.toObject(Route.class));
                                    }
                                } else {
                                    Log.d("Firebase", "Error getting documents: ", task.getException());
                                }
                            }
                        });
                    break;
                }
                case DATE_FILTER:
                {
                    //@TODO DATE_FILTER
                    break;
                }
                case NEAR_FILTER:
                {
                    //@TODO NEAR_FILTER
                    break;
                }
            }
        }
        else {
            FirebaseFirestore.getInstance().collection("routes")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("Firebase", document.getId() + " => " + document.getData());
                                    list.add(document.toObject(Route.class));
                                }
                            } else {
                                Log.d("Firebase", "Error getting documents: ", task.getException());
                            }
                        }
                    });
            // @TODO Check errors
        }
        return list;
    }
}
