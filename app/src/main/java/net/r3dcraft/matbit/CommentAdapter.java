package net.r3dcraft.matbit;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
    private String username = "";
    private List<Comment> data;
    private static LayoutInflater inflater = null;

    public CommentAdapter(Context context, List<Comment> data) {
        this.context = context;
        this.data = data;
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

        final ImageView img_user = (ImageView) view.findViewById(R.id.fragment_recipe_comment_user_photo);
        final TextView txt_text = (TextView) view.findViewById(R.id.fragment_recipe_comment_text);
        final TextView txt_info = (TextView) view.findViewById(R.id.fragment_recipe_comment_info);
        final ImageView img_edit = (ImageView) view.findViewById(R.id.fragment_recipe_comment_edit);

        // Read from Firebase database only once (one snapshot)
        MatbitDatabase.USERS.child(data.get(position).getUser()).addListenerForSingleValueEvent(new ValueEventListener()  {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                username = dataSnapshot.child("nickname").getValue(String.class);

                txt_text.setText(Html.fromHtml("<b><font color='#c62828'>"
                        + username + "</font></b> "
                        + data.get(position).getComment()));

                txt_info.setText(data.get(position).getDatetime());

                MatbitDatabase.userPictureToImageView(data.get(position).getUser(), context, img_user);

                if (MatbitDatabase.USER.getUid().equals(data.get(position).getUser())) {
                    img_edit.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Get user:onCancelled", databaseError.toException());
            }
        });

        img_user.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(context, username, Toast.LENGTH_SHORT).show();
            }
        });
        img_edit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(context, "Edit", Toast.LENGTH_SHORT).show();
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