package net.r3dcraft.matbit;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 24.10.2017.
 */

public class FragmentRecipeComments extends Fragment {
    private static final String TAG = "FragmentRecipeComments";
    private Context context;
    private String recipeID;
    private Recipe recipe;
    private ListView listview;
    FloatingActionButton floatingActionButton;
    private List<Comment> comments;
    private CommentAdapter commentAdaptert;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootViewInfo = inflater.inflate(R.layout.fragment_recipe_comments, container, false);
        context = getActivity();
        comments = new ArrayList<Comment>();
        recipeID = getArguments().getString("recipeID");
        listview = (ListView) rootViewInfo.findViewById(R.id.fragment_recipe_comments_listview);

        return rootViewInfo;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Get recipe information
        MatbitDatabase.RECIPES.child(recipeID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                recipe = new Recipe(dataSnapshot);

                for (Comment comment : recipe.getData().getComments().values()) {
                    comments.add(comment);
                }

                commentAdaptert = new CommentAdapter(context, comments);
                listview.setAdapter(commentAdaptert);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "createRecipeFromDatabase: Cancelled", databaseError.toException());
            }
        });
    }
}