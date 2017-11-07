package net.r3dcraft.matbit;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 24.10.2017.
 */

public class RecipeFragmentComments extends Fragment {
    private static final String TAG = "RecipeFragmentComments";
    private Context context;
    private String recipeID;
    private Recipe recipe;
    private CommentAdapter commentAdaptert;
    private EditText editText_comment;
    private Button btn_comment;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootViewInfo = inflater.inflate(R.layout.fragment_recipe_comments, container, false);
        context = getActivity();
        recipeID = getArguments().getString("recipeID");
        editText_comment = (EditText) rootViewInfo.findViewById(R.id.fragment_recipe_comments_comment_text);

        recyclerView = (RecyclerView)rootViewInfo.findViewById(R.id.fragment_recipe_comments_recycler_view);
        LinearLayoutManager llm = new LinearLayoutManager(context);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        commentAdaptert = new CommentAdapter(context, recipeID, Comment.DATE_COMPARATOR_DESC);
        recyclerView.setAdapter(commentAdaptert);

        // Add new comment
        btn_comment = (Button) rootViewInfo.findViewById(R.id.fragment_recipe_comments_comment_button);
        btn_comment.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String comment_string = editText_comment.getText().toString();
                if (comment_string == null || comment_string.trim().length() < 1) {
                    editText_comment.setError("Kommentar kan ikke være tom!");
                } else {
                    recipe.addComment(comment_string);
                    editText_comment.setText("");
                    editText_comment.clearFocus();
                    // Hide keyboard
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editText_comment.getWindowToken(), 0);

                    // Scroll to top
                    recyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.smoothScrollToPosition(0);
                        }
                    });
                }
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

                commentAdaptert.add(comments);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "OnStart: Cancelled", databaseError.toException());
            }
        });
    }
}