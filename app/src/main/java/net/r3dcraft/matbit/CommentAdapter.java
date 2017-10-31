package net.r3dcraft.matbit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 26.10.2017.
 */

class CommentAdapter extends BaseAdapter {

    private final static String TAG = "CommentAdapter";
    private Context context;
    private List<Comment> data;
    private String recipeUID;
    private static LayoutInflater inflater = null;

    public CommentAdapter(Context context, List<Comment> data, final String RECIPE_UID) {
        this.context = context;
        this.data = data;
        this.recipeUID = RECIPE_UID;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) view = inflater.inflate(R.layout.fragment_recipe_comment_item, null);

        final String USER_UID = data.get(position).getUser();
        final ImageView img_user = (ImageView) view.findViewById(R.id.fragment_recipe_comment_user_photo);
        final TextView txt_text = (TextView) view.findViewById(R.id.fragment_recipe_comment_text);
        final TextView txt_info = (TextView) view.findViewById(R.id.fragment_recipe_comment_info);
        final ImageView img_edit = (ImageView) view.findViewById(R.id.fragment_recipe_comment_edit);

        MatbitDatabase.USERS.child(USER_UID).addListenerForSingleValueEvent(new ValueEventListener()  {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String USERNAME = dataSnapshot.child("nickname").getValue(String.class);

                txt_text.setText(Html.fromHtml("<b><font color='#c62828'>"
                        + USERNAME + "</font></b> "
                        + data.get(position).getComment()));

                txt_info.setText(data.get(position).getDatetimeCreated());

                MatbitDatabase.userPictureToImageView(data.get(position).getUser(), context, img_user);

                if (MatbitDatabase.USER.getUid().equals(USER_UID)) {
                    img_edit.setVisibility(View.VISIBLE);
                }

                img_user.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        MatbitDatabase.goToUser(context, USER_UID);
                    }
                });

                img_edit.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        // Show dialog box for next step
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Endre kommentar");

                        // Set up the input
                        final EditText input = new EditText(context);
                        input.setText(data.get(position).getComment());
                        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                        input.setInputType(InputType.TYPE_CLASS_TEXT);
                        builder.setView(input);

                        // Set up the buttons
                        builder.setPositiveButton("Lagre", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(context, "[Under construction]", Toast.LENGTH_SHORT).show();
                                //txt_text.setText(input.getText().toString());
                                //Recipe.changeComment(recipeUID, COMMENT_UID, input.getText().toString());
                            }
                        });
                        builder.setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        builder.show();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Get user:onCancelled", databaseError.toException());
            }
        });

        return view;
    }

    public List<Comment> getData() {
        return data;
    }

    public void setData(List<Comment> data) {
        this.data = data;
    }
}