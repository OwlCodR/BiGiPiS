package com.bigipis.bigipis.route;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bigipis.bigipis.R;
import com.bigipis.bigipis.source.Route;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;

public class FragmentRoutes extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private View myInflatedView;
    private List<Route> listRoutes;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView recyclerView;
    private DocumentSnapshot lastVisibleDocumentSnapshot;

    final public static String USER_FILTER = "USER";                // Some user's routes
    final public static String DATE_FILTER = "DATE";                // Routes which had benn created in some day/month/year
    final public static String NEAR_FILTER = "NEAR";                // Routes which are near from your position on map
    final public static String NO_FILTER = "NO";                    // All route
    final public static String PHONE_UPLOAD_LOCATION = "SQLITE";    // Upload data from SQLite
    final public static String SERVER_UPLOAD_LOCATION = "FIRESTORE";// Upload data from Firestore


    final public static String TAG_FILTER = "FILTER";
    final public static String TAG_DATA = "DATA";
    final public static String TAG_UPLOAD_LOCATION = "UPLOAD_LOCATION";

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myInflatedView = inflater.inflate(R.layout.fragment_list_routes, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) myInflatedView.findViewById(R.id.swipeRefreshLayoutRoutes);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        recyclerView = (RecyclerView) myInflatedView.findViewById(R.id.RECYCLERVIEW);

        listRoutes = new ArrayList<>();
        lastVisibleDocumentSnapshot = null;
        showRoutes();

        mSwipeRefreshLayout.setRefreshing(true);
        loadShowRoutes();
        return myInflatedView;
    }

    private void loadShowRoutes() {
        //Toast.makeText(getActivity(), "loadShowRoutes", Toast.LENGTH_SHORT).show();
        Bundle bundle = getArguments();
        try {
            if (bundle != null && Objects.equals(bundle.getString(TAG_FILTER), NO_FILTER)) {
                if (bundle.getString(TAG_FILTER) != null) {
                    loadShowServerData(bundle.getString(TAG_FILTER), bundle.getString(TAG_DATA));
                } else if (Objects.equals(bundle.getString(TAG_UPLOAD_LOCATION), SERVER_UPLOAD_LOCATION)) {

                }
            } else {
                // @TODO Delete it and always make bundle. Here will may be stop refreshing
                loadShowServerData(NO_FILTER, null);
                Log.i("FragmentRoutes", "bundle == null or NO_FILTER");
            }
        } catch (Exception e) {
            Log.e("FragmentRoute", e.getMessage());
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onRefresh() {
        loadShowRoutes();
        mSwipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 4000);
    }

    private void showRoutes() {
        RecyclerViewRoutesAdapter adapter = new RecyclerViewRoutesAdapter(getActivity(), listRoutes);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        recyclerView.setItemAnimator(itemAnimator);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mSwipeRefreshLayout.setRefreshing(false);

        //Toast.makeText(getActivity(), "showRoutes", Toast.LENGTH_SHORT).show();
    }

    private void loadShowServerData(String filter, Object data) {
        /*
            USER_FILTER: data - UUID (String)
            DATE_FILTER: data - date dd.mm.yyyy (String)
            NEAR_FILTER: data - position (String)
            NO_FILTER: data - null
        */

        // TO START AFTER LAST ROUTE USE FirebaseFirestore.getInstance().collection("routes").startAfter(lastVisibleDocumentSnapshot)...
        //Toast.makeText(getActivity(), "loadShowServerData", Toast.LENGTH_SHORT).show();

        if (filter != null) {
            switch (filter) {
                case USER_FILTER:
                {
                    if (data != null) {
                        Query query = FirebaseFirestore.getInstance()
                                .collection("routes")
                                .whereEqualTo("userID", data.toString())
                                .limit(10);
                        if (lastVisibleDocumentSnapshot != null) {
                            query = query.startAfter(lastVisibleDocumentSnapshot);
                        }
                        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                Log.d("Firebase", document.getId() + " => " + document.getData());
                                                listRoutes.add(document.toObject(Route.class));
                                            }

                                            List<DocumentSnapshot> documents = task.getResult().getDocuments();
                                            if (documents.size() != 0)
                                                lastVisibleDocumentSnapshot = documents
                                                        .get(documents.size() - 1);
                                            showRoutes();
                                        } else {
                                            Log.d("Firebase", "Error getting documents: ", task.getException());

                                        }
                                        mSwipeRefreshLayout.setRefreshing(false);
                                    }
                                });
                    }
                    break;
                }
                case DATE_FILTER:
                {
                    if (data != null) {
                        Query query = FirebaseFirestore.getInstance()
                                .collection("routes")
                                .whereEqualTo("date", data.toString())
                                .limit(10);
                        if (lastVisibleDocumentSnapshot != null) {
                            query = query.startAfter(lastVisibleDocumentSnapshot);
                        }
                        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Log.d("Firebase", document.getId() + " => " + document.getData());
                                            listRoutes.add(document.toObject(Route.class));
                                        }

                                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                                        if (documents.size() != 0)
                                            lastVisibleDocumentSnapshot = documents
                                                    .get(documents.size() - 1);
                                        showRoutes();
                                    } else {
                                        Log.d("Firebase", "Error getting documents: ", task.getException());
                                    }
                                    mSwipeRefreshLayout.setRefreshing(false);
                                }
                            });}
                    break;
                }
                case NEAR_FILTER:
                {
                    if (data != null) {
                    }
                    mSwipeRefreshLayout.setRefreshing(false);
                    break;
                }
                case NO_FILTER: {
                    Query query = FirebaseFirestore.getInstance()
                            .collection("routes")
                            .limit(10);
                    if (lastVisibleDocumentSnapshot != null) {
                        query = query.startAfter(lastVisibleDocumentSnapshot);
                    }

                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Log.d("Firebase", document.getId() + " => " + document.getData());
                                            listRoutes.add(document.toObject(Route.class));
                                            //Toast.makeText(getActivity(), "onComplete", Toast.LENGTH_SHORT).show();
                                        }

                                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                                        if (documents.size() != 0)
                                            lastVisibleDocumentSnapshot = documents
                                                .get(documents.size() - 1);
                                        showRoutes();
                                    } else {
                                        Log.d("Firebase", "Error getting documents: ", task.getException());
                                    }
                                    mSwipeRefreshLayout.setRefreshing(false);
                                }
                            });
                    break;
                }
            }
        }
    }
}
