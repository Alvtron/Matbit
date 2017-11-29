package net.r3dcraft.matbit;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
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
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 24.10.2017.
 *
 * This is one of the fragments in RecipeActivity that is created in the view pager. This displays
 * the recipe comments in a recycler list.
 */
public class RecipeFragmentComments extends Fragment {
    private static final String TAG = "RecipeFragmentComments";
    private Context context;
    private String recipeID;
    private Recipe recipe;
    private CommentAdapter commentAdapter;
    private EditText editText_comment;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootViewInfo = inflater.inflate(R.layout.fragment_recipe_comments, container, false);
        context = getActivity();
        recipeID = getArguments().getString(getResources().getString(R.string.key_recipe_id));
        editText_comment = rootViewInfo.findViewById(R.id.fragment_recipe_comments_write_comment_edittext);
        TextView comment_message = rootViewInfo.findViewById(R.id.fragment_recipe_comments_write_comment_message);
        Button btn_comment = rootViewInfo.findViewById(R.id.fragment_recipe_comments_write_comment_button);
        TextInputLayout textInputLayout = rootViewInfo.findViewById(R.id.fragment_recipe_comments_write_comment_txt_layout);

        // If user is signed in, display write-comment layout so that the user can write comments
        if (MatbitDatabase.hasUser()) {
            comment_message.setVisibility(View.GONE);
            btn_comment.setVisibility(View.VISIBLE);
            textInputLayout.setVisibility(View.VISIBLE);
        }

        // Set up recycler view
        recyclerView = rootViewInfo.findViewById(R.id.fragment_recipe_comments_recycler_view);
        LinearLayoutManager llm = new LinearLayoutManager(context);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        // Create & set comment adapter
        commentAdapter = new CommentAdapter(context, recipeID, Comment.DATE_COMPARATOR_DESC);
        recyclerView.setAdapter(commentAdapter);

        // On click --> Add new comment to the database and to this recycler list
        btn_comment.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String comment_string = editText_comment.getText().toString();
                if (comment_string.trim().length() < 1) {
                    editText_comment.setError(getString(R.string.error_comment_cant_be_empty));
                } else {
                    Recipe.addComment(recipe.getId(), recipe.getData().getUser_nickname(), comment_string);
                    editText_comment.setText("");
                    editText_comment.clearFocus();
                    // Hide keyboard
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(editText_comment.getWindowToken(), 0);
                    }

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
        // Get recipe information and fill comment recycle list at activity start.
        MatbitDatabase.recipe(recipeID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                commentAdapter.clear();
                recipe = new Recipe(dataSnapshot);
                for (Map.Entry<String, Comment> ratingSet : recipe.getData().getComments().entrySet()) {
                    Comment comment = ratingSet.getValue();
                    comment.setKey(ratingSet.getKey());
                    commentAdapter.add(comment);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "OnStart: Cancelled", databaseError.toException());
            }
        });
    }
}