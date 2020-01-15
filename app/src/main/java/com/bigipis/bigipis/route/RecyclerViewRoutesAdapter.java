package com.bigipis.bigipis.route;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bigipis.bigipis.R;
import com.bigipis.bigipis.source.Route;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
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
        holder.textViewName.setText(itemRoute.getUser().getNickname());
        holder.textViewCreateDate.setText(itemRoute.getCreateDate().toString());
        holder.textViewDescription.setText(itemRoute.getDescription());
        holder.textViewRating.setText(itemRoute.getRating());

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
                // @TODO Animate cardView
            }
        });

        holder.imageButtonDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Update Recycler Item
                itemRoute.setRating(itemRoute.getRating()-1);
                holder.textViewRating.setText(itemRoute.getRating());

                holder.imageButtonDislike.setColorFilter(Color.RED);
                holder.imageButtonLike.setColorFilter(Color.parseColor("#7D7D7D"));
                // Update Recycler Item

                // Update Database
                final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                DocumentReference docRef = firestore.collection("routes").document(itemRoute.getUser().getUid());
                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Route route = documentSnapshot.toObject(Route.class);
                        Map<String, Object> data = new HashMap<>();
                        route.setRating(route.getRating() - 1);
                        data.put("rating", route.getRating());

                        firestore.collection("routes").document(itemRoute.getUser().getUid())
                                .set(data)
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
                    // @TODO Fix everything
                });
                // Update Database
            }
        });

        holder.imageButtonLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                DocumentReference docRef = firestore.collection("routes").document(itemRoute.getUser().getUid());
                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Route route = documentSnapshot.toObject(Route.class);
                        Map<String, Object> data = new HashMap<>();
                        route.setRating(route.getRating() + 1);
                        data.put("rating", route.getRating());
                        firestore.collection("routes").document(itemRoute.getUser().getUid())
                                .set(data)
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
                    // @TODO Fix everything
                });
                holder.imageButtonDislike.setColorFilter(Color.parseColor("#7D7D7D"));
                holder.imageButtonLike.setColorFilter(Color.GREEN);
            }
        });
        holder.imageButtonUserIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // @TODO Open profile
            }
        });

        for (int i = 0; i < itemRoute.getChipsNames().length; i++) {
            Chip chip = new Chip(context);
            chip.setText(itemRoute.getChipsNames()[i]);
            holder.chipGroup.addView(chip, i, 0);
        }

        // @TODO MAP
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
