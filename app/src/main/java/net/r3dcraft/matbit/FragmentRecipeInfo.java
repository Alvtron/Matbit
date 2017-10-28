package net.r3dcraft.matbit;

import android.content.Context;
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
 * Created by Thomas Angeland, student at Ostfold University College, on 24.10.2017.
 */

public class FragmentRecipeInfo extends Fragment {
    private static final String TAG = "FragmentRecipeInfo";
    private Recipe recipe;
    private String recipeID;
    private Context context;
    private ImageView recipe_photo;
    private TextView textview_title;
    private TextView txt_rating;
    private TextView txt_time;
    private TextView txt_views;
    private ImageView user_photo;
    private TextView txt_username;
    private TextView txt_info;
    private Button btn_create;
    private Button btn_follow;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootViewInfo = inflater.inflate(R.layout.fragment_recipe_info, container, false);
        recipeID = getArguments().getString("recipeID");
        context = getActivity();
        recipe_photo = (ImageView) rootViewInfo.findViewById(R.id.fragment_recipe_image);
        textview_title = (TextView) rootViewInfo.findViewById(R.id.fragment_recipe_title);
        txt_rating = (TextView) rootViewInfo.findViewById(R.id.fragment_recipe_rating);
        txt_time = (TextView) rootViewInfo.findViewById(R.id.fragment_recipe_time);
        txt_views = (TextView) rootViewInfo.findViewById(R.id.fragment_recipe_views);
        user_photo = (ImageView) rootViewInfo.findViewById(R.id.fragment_recipe_user_photo);
        txt_username = (TextView) rootViewInfo.findViewById(R.id.fragment_recipe_username);
        txt_info = (TextView) rootViewInfo.findViewById(R.id.fragment_recipe_info);
        btn_create = (Button) rootViewInfo.findViewById(R.id.fragment_recipe_create_button);
        btn_follow = (Button) rootViewInfo.findViewById(R.id.fragment_recipe_follow_button);

        return rootViewInfo;
    }

    @Override
    public void onStart() {
        super.onStart();

        MatbitDatabase.recipePictureToImageView(recipeID, context, recipe_photo);

        // Get recipe information
        MatbitDatabase.RECIPES.child(recipeID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                recipe = new Recipe(dataSnapshot);
                textview_title.setText(recipe.getData().getTitle());
                //created = recipe.getData().getDatetime_created();
                //updated = recipe.getData().getDatetime_updated();
                txt_info.setText(dataSnapshot.child("info").getValue(String.class));
                txt_rating.setText(Double.toString(recipe.getRatingAverage()) + "%");
                txt_time.setText(Integer.toString(recipe.getData().getTime()));
                txt_views.setText(Integer.toString(recipe.getData().getViews()));

                MatbitDatabase.userNicknameToTextView(recipe.getData().getUser(), txt_username);
                MatbitDatabase.userPictureToImageView(recipe.getData().getUser(), context, user_photo);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "createRecipeFromDatabase: Cancelled", databaseError.toException());
            }
        });

        btn_create.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(context, "Splashy effects!", Toast.LENGTH_SHORT).show();
            }
        });

        btn_follow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("userUID", recipe.getData().getUser());
                startActivity(intent);
            }
        });
    }
}
