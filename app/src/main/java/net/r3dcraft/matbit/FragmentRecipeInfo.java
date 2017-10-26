package net.r3dcraft.matbit;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by unibl on 24.10.2017.
 */

public class FragmentRecipeInfo extends Fragment {
    private static final String TAG = "FragmentRecipeInfo";
    private Recipe recipe;
    private String recipeID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootViewInfo = inflater.inflate(R.layout.fragment_recipe_info, container, false);

        recipeID = getArguments().getString("recipeID");

        final ImageView recipe_photo = (ImageView) rootViewInfo.findViewById(R.id.fragment_recipe_image);
        final TextView textview_title = (TextView) rootViewInfo.findViewById(R.id.fragment_recipe_title);
        final TextView txt_rating = (TextView) rootViewInfo.findViewById(R.id.fragment_recipe_rating);
        final TextView txt_time = (TextView) rootViewInfo.findViewById(R.id.fragment_recipe_time);
        final TextView txt_views = (TextView) rootViewInfo.findViewById(R.id.fragment_recipe_views);
        final ImageView user_photo = (ImageView) rootViewInfo.findViewById(R.id.fragment_recipe_user_photo);
        final TextView txt_username = (TextView) rootViewInfo.findViewById(R.id.fragment_recipe_username);
        final TextView txt_info = (TextView) rootViewInfo.findViewById(R.id.fragment_recipe_info);
        final Button btn_create = (Button) rootViewInfo.findViewById(R.id.fragment_recipe_create_button);
        final Button btn_follow = (Button) rootViewInfo.findViewById(R.id.fragment_recipe_follow_button);

        MatbitDatabase.RECIPE_PHOTOS.child(recipeID + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(RecipeActivity.context)
                        .load(uri)
                        .into(recipe_photo);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

        // Get recipe information
        MatbitDatabase.RECIPES.child(recipeID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                recipe = new Recipe(dataSnapshot);
                textview_title.setText(recipe.getData().getTitle());
                //created = recipe.getData().getDatetime_created();
                //updated = recipe.getData().getDatetime_updated();
                txt_info.setText(dataSnapshot.child("info").getValue(String.class));

                MatbitDatabase.USERS.child(recipe.getData().getUser()).addListenerForSingleValueEvent(new ValueEventListener()  {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        txt_rating.setText(Double.toString(recipe.getRatingAverage()) + "%");
                        txt_time.setText(Integer.toString(recipe.getData().getTime()));
                        txt_views.setText(Integer.toString(recipe.getData().getViews()));
                        txt_username.setText(dataSnapshot.child("nickname").getValue(String.class));
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "Load User: onCancelled", databaseError.toException());
                    }
                });

                MatbitDatabase.USER_PHOTOS.child(recipe.getData().getUser() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(RecipeActivity.context)
                                .load(uri)
                                .into(user_photo);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.d(TAG, "Could not load user photo");
                        user_photo.setImageResource(R.drawable.icon_profile);
                    }
                });

                btn_create.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Toast.makeText(RecipeActivity.context, "Splashy effects!", Toast.LENGTH_SHORT).show();
                    }
                });

                btn_follow.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(RecipeActivity.context, ProfileActivity.class);
                        intent.putExtra("userUID", recipe.getData().getUser());
                        startActivity(intent);
                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "createRecipeFromDatabase: Cancelled", databaseError.toException());
            }
        });

        return rootViewInfo;
    }
}
