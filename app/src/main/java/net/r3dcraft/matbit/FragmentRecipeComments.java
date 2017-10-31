package net.r3dcraft.matbit;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
    private CommentAdapter commentAdaptert;
    private EditText editText_comment;
    private Button btn_comment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootViewInfo = inflater.inflate(R.layout.fragment_recipe_comments, container, false);
        context = getActivity();
        recipeID = getArguments().getString("recipeID");
        listview = (ListView) rootViewInfo.findViewById(R.id.fragment_recipe_comments_listview);
        editText_comment = (EditText) rootViewInfo.findViewById(R.id.fragment_recipe_comments_comment_text);
        btn_comment = (Button) rootViewInfo.findViewById(R.id.fragment_recipe_comments_comment_button);
        btn_comment.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String comment_string = editText_comment.getText().toString();
                if (comment_string != null && comment_string.trim().length() > 0)
                    recipe.addComment(comment_string);
                else
                    editText_comment.setError("Kommentar kan ikke være tom!");
            }
        });
        return rootViewInfo;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Get recipe information
        MatbitDatabase.RECIPES.child(recipeID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                recipe = new Recipe(dataSnapshot);

                List<Comment> comments = new ArrayList<Comment>();
                comments.addAll(recipe.getData().getComments().values());

                commentAdaptert = new CommentAdapter(context, comments, recipeID);
                listview.setAdapter(commentAdaptert);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "OnStart: Cancelled", databaseError.toException());
            }
        });
    }
}