package net.r3dcraft.matbit;

import android.content.Context;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Comparator;
import java.util.List;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 16.10.2017.
 *
 * This Recipe adapter extends the RecyclerView and keeps track of recipes as well as sorting them
 * with a specified comparator. Adding and removing Recipe-objects goes automatic with this class.
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    private static final String TAG = "RecipeAdapter";

    /**
     * This is a SortedList that stores and manages Recipe-objects. This is made so that adding and
     * removing Recipe-objects goes automatically, without any need to notify the adapter every time
     * the data-set is changed. This SortedList uses a custom comparator specified in the constructor
     * of this adapter.
     */
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

    private Context context;
    private Comparator<Recipe> comparator;

    /**
     *
     * Construct a RecipeAdapter that keeps track of recipes as well as sorting them with a
     * specified comparator
     *
     * @param context - the context of the Activity that constructs this adapter
     * @param comparator - a custom comparator that compares Recipes-objects
     */
    public RecipeAdapter(Context context, Comparator<Recipe> comparator) {
        this.context = context;
        this.comparator = comparator;
    }

    /**
     * Add a new recipe to this adapter. The data-set will be sorted automatically by the comparator.
     * If the recipe already exists, it won't be added.
     *
     * @param recipe - A Recipe object
     */
    public void add(Recipe recipe) {
        sortedRecipeList.add(recipe);
    }

    /**
     * Remove a recipe. The data-set will be sorted automatically by the comparator.
     * If the recipe don't exist, nothing happens.
     *
     * @param recipe - A Recipe object
     */
    public void remove(Recipe recipe) {
        sortedRecipeList.remove(recipe);
    }

    /**
     * Add recipes to this adapter. The data-set will be sorted automatically by the comparator.
     * If the some recipes already exists, they won't be added.
     *
     * @param recipes - A List of Recipe objects
     */
    public void add(List<Recipe> recipes) {
        sortedRecipeList.addAll(recipes);
    }

    /**
     * Remove multiple recipes. The data-set will be sorted automatically by the comparator.
     * If some of the recipes don't exist, nothing happens.
     *
     * @param recipes - A List of Comment objects
     */
    public void remove(List<Recipe> recipes) {
        sortedRecipeList.beginBatchedUpdates();
        for (Recipe recipe : recipes) {
            sortedRecipeList.remove(recipe);
        }
        sortedRecipeList.endBatchedUpdates();
    }

    /**
     * Replace all Recipe objects in this adapter. The data-set will be sorted automatically by the
     * comparator.
     *
     * @param recipes - A List of Recipe objects
     */
    public void replaceAll(List<Recipe> recipes) {
        sortedRecipeList.beginBatchedUpdates();
        for (int i = sortedRecipeList.size() - 1; i >= 0; i--) {
            final Recipe recipe = sortedRecipeList.get(i);
            if (!recipes.contains(recipe)) {
                sortedRecipeList.remove(recipe);
            }
        }
        sortedRecipeList.addAll(recipes);
        sortedRecipeList.endBatchedUpdates();
    }

    /**
     * Delete all recipes in the sortedList.
     */
    public void clear() {
        sortedRecipeList.clear();
        notifyDataSetChanged();
    }

    /**
     * @return the number of Recipe objects stored in this adapter.
     */
    @Override
    public int getItemCount() {
        return sortedRecipeList.size();
    }

    /**
     * This creates a new RecyclerView.ViewHolder and initializes private fields to be used by
     * RecyclerView.
     *
     * @param viewGroup
     * @param i
     * @return a RecipeViewHolder that holds a inflated Recipe layout.
     */
    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.search_item, viewGroup, false);

        return new RecipeViewHolder(itemView, context);
    }

    /**
     * A RecipeViewHolder class that holds and sets a recipe layout.
     */
    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        protected Recipe recipe;
        private ImageView vRecipePhoto;
        private ImageView vUserPhoto;
        private TextView vRecipeInfo;
        private TextView vRecipeTitle;

        public RecipeViewHolder(final View view, final Context context) {
            super(view);
            vRecipePhoto = view.findViewById(R.id.search_item_recipe_photo);
            vUserPhoto = view.findViewById(R.id.search_item_user_photo);
            vRecipeTitle = view.findViewById(R.id.search_item_recipe_title);
            vRecipeInfo = view.findViewById(R.id.search_item_recipe_info);

            vRecipePhoto.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    MatbitDatabase.goToRecipe(context, recipe);
                }
            });

            vUserPhoto.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    MatbitDatabase.goToUser(context, recipe.getData().getUser());
                }
            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    MatbitDatabase.goToRecipe(context, recipe);
                }
            });
        }
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method updates the
     * contents of the Recipe view to reflect the Recipe object at the given position.
     *
     * @param recipeViewHolder - A RecipeViewHolder class that holds and sets a recipe layout.
     * @param i - The position of the Recipe object
     */
    @Override
    public void onBindViewHolder(final RecipeViewHolder recipeViewHolder, int i) {
        final Recipe RECIPE = sortedRecipeList.get(i);
        recipeViewHolder.recipe = RECIPE;
        recipeViewHolder.vRecipeTitle.setText(RECIPE.getData().getTitle());

        // Insert user photo from database
        MatbitDatabase.user(RECIPE.getData().getUser()).addListenerForSingleValueEvent(new ValueEventListener()  {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                recipeViewHolder.vRecipeInfo.setText(String.format(
                        context.getResources().getString(R.string.format_recipe_information),
                        dataSnapshot.child("nickname").getValue(String.class),
                        RECIPE.getThumbsUp(), RECIPE.getTimeToText(), RECIPE.getData().getCategory())
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