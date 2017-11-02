package net.r3dcraft.matbit;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.util.SortedList;
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

import java.util.Comparator;
import java.util.List;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 16.10.2017.
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    private static final String TAG = "RecipeAdapter";

    private final SortedList<Recipe> sortedRecipeList = new SortedList<>(Recipe.class, new SortedList.Callback<Recipe>() {
        @Override
        public int compare(Recipe a, Recipe b) {
            return comparator.compare(a, b);
        }

        @Override
        public void onInserted(int position, int count) {
            notifyItemRangeInserted(position, count);
        }

        @Override
        public void onRemoved(int position, int count) {
            notifyItemRangeRemoved(position, count);
        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {
            notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public void onChanged(int position, int count) {
            notifyItemRangeChanged(position, count);
        }

        @Override
        public boolean areContentsTheSame(Recipe oldItem, Recipe newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areItemsTheSame(Recipe item1, Recipe item2) {
            return item1.getId() == item2.getId();
        }
    });

    private static Context context;
    private Comparator<Recipe> comparator;

    public RecipeAdapter(Context context, Comparator<Recipe> comparator) {
        this.context = context;
        this.comparator = comparator;
    }

    public void setComparator(Comparator<Recipe> comparator) {
        this.comparator = comparator;
    }

    public void add(Recipe model) {
        sortedRecipeList.add(model);
    }

    public void remove(Recipe model) {
        sortedRecipeList.remove(model);
    }

    public void add(List<Recipe> models) {
        sortedRecipeList.addAll(models);
    }

    public void remove(List<Recipe> models) {
        sortedRecipeList.beginBatchedUpdates();
        for (Recipe model : models) {
            sortedRecipeList.remove(model);
        }
        sortedRecipeList.endBatchedUpdates();
    }

    public void replaceAll(List<Recipe> models) {
        sortedRecipeList.beginBatchedUpdates();
        for (int i = sortedRecipeList.size() - 1; i >= 0; i--) {
            final Recipe model = sortedRecipeList.get(i);
            if (!models.contains(model)) {
                sortedRecipeList.remove(model);
            }
        }
        sortedRecipeList.addAll(models);
        sortedRecipeList.endBatchedUpdates();
    }

    @Override
    public int getItemCount() {
        return sortedRecipeList.size();
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

    @Override
    public void onBindViewHolder(final RecipeViewHolder recipeViewHolder, int i) {
        final Recipe RECIPE = sortedRecipeList.get(i);
        recipeViewHolder.recipe = RECIPE;
        recipeViewHolder.vRecipeTitle.setText(RECIPE.getData().getTitle());

        // Insert user photo from database
        MatbitDatabase.USERS.child(RECIPE.getData().getUser()).addListenerForSingleValueEvent(new ValueEventListener()  {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                recipeViewHolder.vRecipeInfo.setText(
                        dataSnapshot.child("nickname").getValue(String.class)
                                + " • " + RECIPE.getRatingAverage()
                                + "% liked • " + RECIPE.getTimeToText()
                );
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });

        MatbitDatabase.recipePictureToImageView(RECIPE.getId(), context, recipeViewHolder.vRecipePhoto);
        MatbitDatabase.userPictureToImageView(RECIPE.getData().getUser(), context, recipeViewHolder.vUserPhoto);
    }
}