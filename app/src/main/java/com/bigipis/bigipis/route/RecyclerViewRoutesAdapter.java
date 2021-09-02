package com.bigipis.bigipis.route;

import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bigipis.bigipis.R;
import com.bigipis.bigipis.source.Route;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.view.View.GONE;

public class RecyclerViewRoutesAdapter extends RecyclerView.Adapter<RecyclerViewRoutesAdapter.ViewHolderRoutes> implements View.OnClickListener {

    private Context context;
    private LayoutInflater inflater;
    private List<Route> routesList;

    RecyclerViewRoutesAdapter(Context context, List<Route> routesList) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.routesList = routesList;
    }

    @NonNull
    @Override
    public ViewHolderRoutes onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_route, parent, false);
        return new RecyclerViewRoutesAdapter.ViewHolderRoutes(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderRoutes holder, int position) {
        final Route itemRoute = routesList.get(position);
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Map<String, Double>> routePointsList = itemRoute.getPointsList();
        LatLng itemStartLatLng = new LatLng(routePointsList.get(0).get("latitude"), routePointsList.get(0).get("longitude"));
        LatLng itemFinishLatLng = new LatLng(routePointsList.get(routePointsList.size() - 1).get("latitude"), routePointsList.get(routePointsList.size() - 1).get("longitude"));
        String description = "";
        try {
            Address addressStart = geocoder.getFromLocation(itemStartLatLng.latitude, itemStartLatLng.longitude, 1).get(0);
            Address addressFinish = geocoder.getFromLocation(itemFinishLatLng.latitude, itemFinishLatLng.longitude, 1).get(0);
            String adressStart = addressStart.getAddressLine(0);
            String adressFinish = addressFinish.getAddressLine(0);

            description = "Старт: " + adressStart + "\nФиниш: " + adressFinish;
            Log.i("RECYCLER", adressStart);
            Log.i("RECYCLER", adressFinish);
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("RECYCLER", description);
        }
        //if (!description.equals(""))
            //Toast.makeText(context, description, Toast.LENGTH_SHORT).show();
        //else
           // Toast.makeText(context, "Пусто :(", Toast.LENGTH_SHORT).show();

        holder.textViewName.setText(itemRoute.getUserName());
        holder.textViewCreateDate.setText(String.valueOf(itemRoute.getDate()));
        holder.textViewDescription.setText(description);
        holder.textViewRating.setText(String.valueOf(itemRoute.getRating()));

        holder.imageButtonArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.mapView.getVisibility() == GONE) {
                    // Open
                    RotateAnimation rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(context, R.anim.rotation);
                    holder.imageButtonArrow.startAnimation(rotateAnimation);
                    holder.cardView.animate().scaleY(1.5f);
                    holder.mapView.setVisibility(View.VISIBLE);
                } else {
                    // Close
                    RotateAnimation rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(context, R.anim.rotation);
                    holder.imageButtonArrow.startAnimation(rotateAnimation);
                    holder.cardView.animate().scaleY(0.5f);
                    holder.mapView.setVisibility(View.GONE);
                }
            }
        });

        holder.imageButtonDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Update Recycler Item
                itemRoute.setRating(itemRoute.getRating()-1);
                holder.textViewRating.setText(String.valueOf(Integer.parseInt(holder.textViewRating.getText().toString()) - 1));
                holder.imageButtonLike.setColorFilter(Color.parseColor("#7D7D7D"));
                holder.imageButtonDislike.setColorFilter(Color.RED);
                // Update Recycler Item

                // Update Database
                final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                CollectionReference collectionReference = firestore.collection("routes");
                collectionReference
                        .whereEqualTo("userID", itemRoute.getUserID())
                        .whereEqualTo("date", itemRoute.getDate())
                        .whereEqualTo("pointsList", itemRoute.getPointsList())
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot querySnapshot) {
                                DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                                Route route = document.toObject(Route.class);

                                updateUserRating(document.getId(), route.getRating() - 1);
                            }
                        });
                // Update Database
            }
        });

        holder.imageButtonLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                CollectionReference colRef = firestore.collection("routes");
                colRef
                        .whereEqualTo("userID", itemRoute.getUserID())
                        .whereEqualTo("date", itemRoute.getDate())
                        .whereEqualTo("pointsList", itemRoute.getPointsList())
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot querySnapshot) {
                                DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                                Route route = document.toObject(Route.class);

                                updateUserRating(document.getId(), route.getRating() + 1);
                            }
                        });
                holder.textViewRating.setText(String.valueOf(Integer.parseInt(holder.textViewRating.getText().toString()) + 1));
                holder.imageButtonDislike.setColorFilter(Color.parseColor("#7D7D7D"));
                holder.imageButtonLike.setColorFilter(Color.GREEN);
            }
        });
        holder.imageButtonUserIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        for (int i = 0; i < itemRoute.getChipsNames().size(); i++) {
            Chip chip = new Chip(context);
            chip.setText(itemRoute.getChipsNames().get(i));
            holder.chipGroup.addView(chip, i, 0);
        }
    }

    private void updateUserRating(String uid, int rating) {
        FirebaseFirestore.getInstance()
                .collection("routes")
                .document(uid)
                .update("rating", rating)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firebase", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firebase", "Error writing document", e);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return routesList.size();
    }

    @Override
    public void onClick(View v) {

    }

    static class ViewHolderRoutes extends RecyclerView.ViewHolder {
        TextView textViewName, textViewCreateDate, textViewDescription, textViewRating;
        ImageButton imageButtonArrow, imageButtonLike, imageButtonDislike, imageButtonUserIcon;
        ConstraintLayout constraintLayoutMap;
        MapView mapView;
        ChipGroup chipGroup;
        CardView cardView;

        ViewHolderRoutes(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewItemName);
            textViewCreateDate = itemView.findViewById(R.id.textViewItemDate);
            textViewDescription = itemView.findViewById(R.id.textViewItemDescription);
            textViewRating = itemView.findViewById(R.id.textViewItemRating);

            imageButtonArrow = itemView.findViewById(R.id.imageButtonItemArrowDown);
            imageButtonLike = itemView.findViewById(R.id.imageButtonItemLike);
            imageButtonDislike = itemView.findViewById(R.id.imageButtonItemDislike);
            imageButtonUserIcon = itemView.findViewById(R.id.imageButtonItemIcon);

            constraintLayoutMap = itemView.findViewById(R.id.constraintLayoutItemMap);

            mapView = itemView.findViewById(R.id.mapViewItem);

            chipGroup = itemView.findViewById(R.id.chipGroupItem);

            cardView = itemView.findViewById(R.id.cardViewItem);
        }
    }
}
