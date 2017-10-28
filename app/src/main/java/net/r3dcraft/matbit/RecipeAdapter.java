package net.r3dcraft.matbit;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 16.10.2017.
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    private static final String TAG = "RecipeAdapter";

    private ArrayList<Recipe> recipeList;
    private static Context context;

    public RecipeAdapter(ArrayList<Recipe> recipeList, Context context) {
        this.recipeList = recipeList;
        this.context = context;
    }

    public ArrayList<Recipe> getRecipeList() {
        return recipeList;
    }

    public void setRecipeList(ArrayList<Recipe> recipeList) {
        this.recipeList = recipeList;
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    @Override
    public void onBindViewHolder(final RecipeViewHolder recipeViewHolder, int i) {
        final Recipe RECIPE = recipeList.get(i);
        recipeViewHolder.recipe = RECIPE;
        recipeViewHolder.vRecipeTitle.setText(RECIPE.getData().getTitle());

        // Insert user photo from database
        MatbitDatabase.USERS.child(RECIPE.getData().getUser()).addListenerForSingleValueEvent(new ValueEventListener()  {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                recipeViewHolder.vRecipeInfo.setText(
                        dataSnapshot.child("nickname").getValue(String.class)
                                + " • " + RECIPE.getRatingAverage()
                                + "% liked • " + RECIPE.getData().getTime() + " min"
                );
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });

        MatbitDatabase.RECIPE_PHOTOS.child(RECIPE.getId() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context)
                        .load(uri)
                        .into(recipeViewHolder.vRecipePhoto);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, "Could not load recipe photo");
            }
        });

        MatbitDatabase.USER_PHOTOS.child(RECIPE.getData().getUser() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context)
                        .load(uri)
                        .into(recipeViewHolder.vUserPhoto);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, "Could not load user photo");
                recipeViewHolder.vUserPhoto.setImageResource(R.drawable.icon_profile);
            }
        });

    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.search_item, viewGroup, false);

        return new RecipeViewHolder(itemView);
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        protected Recipe recipe;
        protected ImageView vRecipePhoto;
        protected ImageView vUserPhoto;
        protected TextView vRecipeInfo;
        protected TextView vRecipeTitle;

        public RecipeViewHolder(View view) {
            super(view);
            vRecipePhoto = (ImageView) view.findViewById(R.id.search_item_recipe_photo);
            vUserPhoto = (ImageView) view.findViewById(R.id.search_item_user_photo);
            vRecipeTitle = (TextView)  view.findViewById(R.id.search_item_recipe_title);
            vRecipeInfo = (TextView)  view.findViewById(R.id.search_item_recipe_info);

            vRecipePhoto.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    MatbitDatabase.gotToRecipe(context, recipe);
                }
            });

            vUserPhoto.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    MatbitDatabase.goToUser(context, recipe.getData().getUser());
                }
            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    MatbitDatabase.gotToRecipe(context, recipe);
                }
            });
        }
    }


}