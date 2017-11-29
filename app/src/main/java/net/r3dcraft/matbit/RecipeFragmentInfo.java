package net.r3dcraft.matbit;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 24.10.2017.
 *
 * This is one of the fragments in RecipeActivity that is created in the view pager. This displays
 * the recipe information and thumbs up/thumbs down buttons, follow user button and a create-recipe
 * button.
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
    private LinearLayoutCompat layout_follow;
    private TextView txt_follow;
    private ImageView icon_follow;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootViewInfo = inflater.inflate(R.layout.fragment_recipe_info, container, false);
        recipeID = getArguments().getString(getResources().getString(R.string.key_recipe_id));
        authorID = getArguments().getString(getResources().getString(R.string.key_user_id));
        context = getActivity();

        // Initialize all the layout
        img_recipe_photo = rootViewInfo.findViewById(R.id.activity_recipe_fragment_info_recipe_photo_img);
        txt_title = rootViewInfo.findViewById(R.id.activity_recipe_fragment_info_recipe_title_txt);
        txt_thumbs_up = rootViewInfo.findViewById(R.id.activity_recipe_fragment_info_thumbs_up_txt);
        txt_thumbs_down = rootViewInfo.findViewById(R.id.activity_recipe_fragment_info_thumbs_down_txt);
        icon_thumbs_up = rootViewInfo.findViewById(R.id.activity_recipe_fragment_info_thumbs_up_icon);
        icon_thumbs_down = rootViewInfo.findViewById(R.id.activity_recipe_fragment_info_thumbs_down_icon);
        txt_time = rootViewInfo.findViewById(R.id.activity_recipe_fragment_info_time_txt);
        txt_views = rootViewInfo.findViewById(R.id.activity_recipe_fragment_info_views_txt);
        img_user_photo = rootViewInfo.findViewById(R.id.activity_recipe_fragment_info_user_img);
        txt_username = rootViewInfo.findViewById(R.id.activity_recipe_fragment_info_user_txt);
        txt_info = rootViewInfo.findViewById(R.id.activity_recipe_fragment_info_info_txt);
        btn_create = rootViewInfo.findViewById(R.id.activity_recipe_fragment_info_create_btn);
        layout_follow = rootViewInfo.findViewById(R.id.activity_recipe_fragment_info_follow_layout);
        txt_follow = rootViewInfo.findViewById(R.id.activity_recipe_fragment_info_follow_txt);
        icon_follow = rootViewInfo.findViewById(R.id.activity_recipe_fragment_info_follow_icon);

        return rootViewInfo;
    }

    @Override
    public void onStart() {
        super.onStart();

        // If user is logged in and author is this user, remove follow icon.
        if (!MatbitDatabase.hasUser() || authorID.equals(MatbitDatabase.getCurrentUserUID())) {
            layout_follow.setVisibility(View.INVISIBLE);
            icon_follow.setVisibility(View.INVISIBLE);
            txt_follow.setVisibility(View.INVISIBLE);
        }

        // Load recipe photo from Matbit storage
        MatbitDatabase.recipePictureToImageView(recipeID, context, img_recipe_photo);

        // Get recipe information
        MatbitDatabase.recipe(recipeID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                recipe = new Recipe(dataSnapshot);
                txt_title.setText(recipe.getData().getTitle());
                txt_info.setText(dataSnapshot.child("info").getValue(String.class));
                txt_thumbs_up.setText(Integer.toString(recipe.getData().getThumbs_up()));
                txt_thumbs_down.setText(Integer.toString(recipe.getData().getThumbs_down()));
                txt_time.setText(recipe.getTimeToText());
                txt_views.setText(Integer.toString(recipe.getData().getViews()));

                if (recipe.getCurrentUserRating() == Recipe.THUMB.UP) {
                    icon_thumbs_up.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);
                }
                else if (recipe.getCurrentUserRating() == Recipe.THUMB.DOWN) {
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

        // Load user from database and update follow button if the user follows the author
        MatbitDatabase.user(authorID).addListenerForSingleValueEvent(new ValueEventListener() {
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

        // Load CreateRecipeFragment which is a full screen window that displays all the steps
        // fragment by fragment.
        btn_create.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            if (recipe.getId() != null) {
                Intent intent = new Intent(context, CreateRecipeActivity.class);

                intent.putExtra(getResources().getString(R.string.key_recipe_id), recipe.getId());
                context.startActivity(intent);
            }
            else
                Toast.makeText(context, R.string.error_try_refreshing_this_page, Toast.LENGTH_SHORT).show();
            }
        });

        img_user_photo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MatbitDatabase.goToUser(context, authorID);
            }
        });

        // If user is logged in, add onclick listeners to thumbs up, thumbs down and follow button.
        if (MatbitDatabase.hasUser()) {
            icon_thumbs_up.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // User has already rated thumbs up --> remove it.
                    if (recipe.getCurrentUserRating() == Recipe.THUMB.UP) {
                        recipe.removeUserRating();
                        txt_thumbs_up.setText(Integer.toString(recipe.getData().getThumbs_up()));
                        icon_thumbs_up.setColorFilter(ContextCompat.getColor(context, R.color.grey_500), android.graphics.PorterDuff.Mode.SRC_IN);
                    }
                    // User has already rated thumbs down --> remove it and add thumbs up.
                    else if (recipe.getCurrentUserRating() == Recipe.THUMB.DOWN) {
                        recipe.addRating(Recipe.THUMB.UP);
                        txt_thumbs_up.setText(Integer.toString(recipe.getData().getThumbs_up()));
                        txt_thumbs_down.setText(Integer.toString(recipe.getData().getThumbs_down()));
                        icon_thumbs_up.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);
                        icon_thumbs_down.setColorFilter(ContextCompat.getColor(context, R.color.grey_500), android.graphics.PorterDuff.Mode.SRC_IN);

                    }
                    // User has not rated --> add rating.
                    else if (recipe.getCurrentUserRating() == Recipe.THUMB.NOTHING) {
                        recipe.addRating(Recipe.THUMB.UP);
                        txt_thumbs_up.setText(Integer.toString(recipe.getData().getThumbs_up()));
                        icon_thumbs_up.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);
                    }
                }
            });

            icon_thumbs_down.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // User has already rated thumbs down --> remove it.
                    if (recipe.getCurrentUserRating() == Recipe.THUMB.DOWN) {
                        recipe.removeUserRating();
                        txt_thumbs_down.setText(Integer.toString(recipe.getData().getThumbs_down()));
                        icon_thumbs_down.setColorFilter(ContextCompat.getColor(context, R.color.grey_500), android.graphics.PorterDuff.Mode.SRC_IN);
                    }
                    // User has already rated thumbs up --> remove it and add thumbs down.
                    else if (recipe.getCurrentUserRating() == Recipe.THUMB.UP) {
                        recipe.addRating(Recipe.THUMB.DOWN);
                        txt_thumbs_down.setText(Integer.toString(recipe.getData().getThumbs_down()));
                        txt_thumbs_up.setText(Integer.toString(recipe.getData().getThumbs_up()));
                        icon_thumbs_down.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);
                        icon_thumbs_up.setColorFilter(ContextCompat.getColor(context, R.color.grey_500), android.graphics.PorterDuff.Mode.SRC_IN);

                    }
                    // User has not rated --> add rating.
                    else if (recipe.getCurrentUserRating() == Recipe.THUMB.NOTHING) {
                        recipe.addRating(Recipe.THUMB.DOWN);
                        txt_thumbs_down.setText(Integer.toString(recipe.getData().getThumbs_down()));
                        icon_thumbs_down.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);
                    }
                }
            });

            // Handle when user clicks the follow button.
            layout_follow.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (user.hasFollower(MatbitDatabase.getCurrentUserUID()))
                        user.removeFollower(MatbitDatabase.getCurrentUserUID());
                    else
                        user.addFollower(MatbitDatabase.getCurrentUserUID());
                    updateFollowAppearance();
                }
            });
        }
    }

    // Update the follow button appearance according to if the user follows the author or not.
    private void updateFollowAppearance() {
        if (user.hasFollower(MatbitDatabase.getCurrentUserUID())) {
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
