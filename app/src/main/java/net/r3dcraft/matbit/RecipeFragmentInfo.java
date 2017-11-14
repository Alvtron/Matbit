package net.r3dcraft.matbit;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 24.10.2017.
 */

public class RecipeFragmentInfo extends Fragment {
    private static final String TAG = "RecipeFragmentInfo";
    private Recipe recipe;
    private String recipeID;
    private User user;
    private String authorID;
    private Context context;
    private ImageView img_recipe_photo;
    private TextView txt_title;
    private TextView txt_views;
    private TextView txt_thumbs_up;
    private TextView txt_thumbs_down;
    private ImageView icon_thumbs_up;
    private ImageView icon_thumbs_down;
    private TextView txt_time;
    private Button btn_create;
    private ImageView img_user_photo;
    private TextView txt_username;
    private TextView txt_info;
    private LinearLayout layout_follow;
    private TextView txt_follow;
    private ImageView icon_follow;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootViewInfo = inflater.inflate(R.layout.fragment_recipe_info, container, false);
        recipeID = getArguments().getString("recipeID");
        authorID = getArguments().getString("authorID");
        context = getActivity();
        img_recipe_photo = (ImageView) rootViewInfo.findViewById(R.id.activity_recipe_fragment_info_recipe_photo_img);
        txt_title = (TextView) rootViewInfo.findViewById(R.id.activity_recipe_fragment_info_recipe_title_txt);
        txt_thumbs_up = (TextView) rootViewInfo.findViewById(R.id.activity_recipe_fragment_info_thumbs_up_txt);
        txt_thumbs_down = (TextView) rootViewInfo.findViewById(R.id.activity_recipe_fragment_info_thumbs_down_txt);
        icon_thumbs_up = (ImageView) rootViewInfo.findViewById(R.id.activity_recipe_fragment_info_thumbs_up_icon);
        icon_thumbs_down = (ImageView) rootViewInfo.findViewById(R.id.activity_recipe_fragment_info_thumbs_down_icon);
        txt_time = (TextView) rootViewInfo.findViewById(R.id.activity_recipe_fragment_info_time_txt);
        txt_views = (TextView) rootViewInfo.findViewById(R.id.activity_recipe_fragment_info_views_txt);
        img_user_photo = (ImageView) rootViewInfo.findViewById(R.id.activity_recipe_fragment_info_user_img);
        txt_username = (TextView) rootViewInfo.findViewById(R.id.activity_recipe_fragment_info_user_txt);
        txt_info = (TextView) rootViewInfo.findViewById(R.id.activity_recipe_fragment_info_info_txt);
        btn_create = (Button) rootViewInfo.findViewById(R.id.activity_recipe_fragment_info_create_btn);
        layout_follow =(LinearLayout) rootViewInfo.findViewById(R.id.activity_recipe_fragment_info_follow_layout);
        txt_follow = (TextView) rootViewInfo.findViewById(R.id.activity_recipe_fragment_info_follow_txt);
        icon_follow = (ImageView) rootViewInfo.findViewById(R.id.activity_recipe_fragment_info_follow_icon);

        return rootViewInfo;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (authorID.equals(MatbitDatabase.getCurrentUserID())) {
            layout_follow.setVisibility(View.INVISIBLE);
            icon_follow.setVisibility(View.INVISIBLE);
            txt_follow.setVisibility(View.INVISIBLE);
        }

        MatbitDatabase.recipePictureToImageView(recipeID, context, img_recipe_photo);

        // Get recipe information
        MatbitDatabase.RECIPES.child(recipeID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                recipe = new Recipe(dataSnapshot);
                txt_title.setText(recipe.getData().getTitle());
                txt_info.setText(dataSnapshot.child("info").getValue(String.class));
                txt_thumbs_up.setText(Integer.toString(recipe.getData().getThumbs_up()));
                txt_thumbs_down.setText(Integer.toString(recipe.getData().getThumbs_down()));
                txt_time.setText(recipe.getTimeToText());
                txt_views.setText(Integer.toString(recipe.getData().getViews()));

                if (recipe.hasUserRated() == Recipe.THUMB.UP) {
                    icon_thumbs_up.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);
                }
                else if (recipe.hasUserRated() == Recipe.THUMB.DOWN) {
                    icon_thumbs_down.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);
                }

                MatbitDatabase.userNicknameToTextView(recipe.getData().getUser(), txt_username);
                MatbitDatabase.userPictureToImageView(recipe.getData().getUser(), context, img_user_photo);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "createRecipeFromDatabase: Cancelled", databaseError.toException());
            }
        });

        MatbitDatabase.USERS.child(authorID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = new User(dataSnapshot);
                updateFollowAppearance();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "createUserFromDatabase: Cancelled", databaseError.toException());
            }
        });

        icon_thumbs_up.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // User has already rated thumbs up --> remove it.
                if (recipe.hasUserRated() == Recipe.THUMB.UP) {
                    recipe.removeUserRating();
                    txt_thumbs_up.setText(Integer.toString(recipe.getData().getThumbs_up()));
                    icon_thumbs_up.setColorFilter(ContextCompat.getColor(context, R.color.grey_500), android.graphics.PorterDuff.Mode.SRC_IN);
                }
                // User has already rated thumbs down --> remove it and add thumbs up.
                else if (recipe.hasUserRated() == Recipe.THUMB.DOWN) {
                    recipe.addRating(Recipe.THUMB.UP);
                    txt_thumbs_up.setText(Integer.toString(recipe.getData().getThumbs_up()));
                    txt_thumbs_down.setText(Integer.toString(recipe.getData().getThumbs_down()));
                    icon_thumbs_up.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);
                    icon_thumbs_down.setColorFilter(ContextCompat.getColor(context, R.color.grey_500), android.graphics.PorterDuff.Mode.SRC_IN);

                }
                // User has not rated --> add rating.
                else if (recipe.hasUserRated() == Recipe.THUMB.NOTHING) {
                    recipe.addRating(Recipe.THUMB.UP);
                    txt_thumbs_up.setText(Integer.toString(recipe.getData().getThumbs_up()));
                    icon_thumbs_up.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);
                }
            }
        });

        icon_thumbs_down.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // User has already rated thumbs down --> remove it.
                if (recipe.hasUserRated() == Recipe.THUMB.DOWN) {
                    recipe.removeUserRating();
                    txt_thumbs_down.setText(Integer.toString(recipe.getData().getThumbs_down()));
                    icon_thumbs_down.setColorFilter(ContextCompat.getColor(context, R.color.grey_500), android.graphics.PorterDuff.Mode.SRC_IN);
                }
                // User has already rated thumbs up --> remove it and add thumbs down.
                else if (recipe.hasUserRated() == Recipe.THUMB.UP) {
                    recipe.addRating(Recipe.THUMB.DOWN);
                    txt_thumbs_down.setText(Integer.toString(recipe.getData().getThumbs_down()));
                    txt_thumbs_up.setText(Integer.toString(recipe.getData().getThumbs_up()));
                    icon_thumbs_down.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);
                    icon_thumbs_up.setColorFilter(ContextCompat.getColor(context, R.color.grey_500), android.graphics.PorterDuff.Mode.SRC_IN);

                }
                // User has not rated --> add rating.
                else if (recipe.hasUserRated() == Recipe.THUMB.NOTHING) {
                    recipe.addRating(Recipe.THUMB.DOWN);
                    txt_thumbs_down.setText(Integer.toString(recipe.getData().getThumbs_down()));
                    icon_thumbs_down.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);
                }
            }
        });

        btn_create.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(context, CreateRecipeActivity.class);
                intent.putExtra("recipeID", recipe.getId());
                context.startActivity(intent);
            }
        });

        layout_follow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (user.hasFollower(MatbitDatabase.getCurrentUserID()))
                    user.removeFollower(MatbitDatabase.getCurrentUserID());
                else
                    user.addFollower(MatbitDatabase.getCurrentUserID());
                updateFollowAppearance();
            }
        });

        img_user_photo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MatbitDatabase.goToUser(context, authorID);
            }
        });
    }

    private void updateFollowAppearance() {
        if (user.hasFollower(MatbitDatabase.getCurrentUserID())) {
            txt_follow.setText("Følger");
            txt_follow.setTextColor(getResources().getColor(R.color.grey_500));
            icon_follow.setImageDrawable(getResources().getDrawable(R.drawable.icon_check_black_24dp));
            icon_follow.setColorFilter(ContextCompat.getColor(context, R.color.grey_500), android.graphics.PorterDuff.Mode.SRC_IN);
        }
        else {
            txt_follow.setText("Følg");
            txt_follow.setTextColor(getResources().getColor(R.color.colorPrimary));
            icon_follow.setImageDrawable(getResources().getDrawable(R.drawable.icon_add_circle_black_24dp));
            icon_follow.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);
        }
    }
}
