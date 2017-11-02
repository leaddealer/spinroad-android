package com.leaddealer.spinroad.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.leaddealer.spinroad.R;
import com.leaddealer.spinroad.models.User;

import java.util.ArrayList;

public class ScoreArrayAdapter extends ArrayAdapter<User> {
    private final Context context;
    private final int myScore;
    private ArrayList<User> users;

    public ScoreArrayAdapter(Context context, ArrayList<User> users, int myScore) {
        super(context, R.layout.list_item, users);
        this.context = context;
        this.users = users;
        this.myScore = myScore;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item, parent, false);

        Typeface tf = Typeface.createFromAsset(context.getAssets(),"NotoSans-Bold.ttf");

        TextView positionTextView = (TextView) rowView.findViewById(R.id.position);
        TextView nameTextView = (TextView) rowView.findViewById(R.id.name);
        TextView scoreTextView = (TextView) rowView.findViewById(R.id.score);

        positionTextView.setTypeface(tf);
        nameTextView.setTypeface(tf);
        scoreTextView.setTypeface(tf);

        if(myScore != -1 && position == 11){
            nameTextView.setText("");
            scoreTextView.setText("");
            positionTextView.setText("");
        } else if(myScore == -1 && position == 11) {
            nameTextView.setText(users.get(11 - position).getName());
            scoreTextView.setText(String.valueOf(users.get(11 - position).getScore()));
            positionTextView.setText("");
            rowView.findViewById(R.id.list_item).setBackgroundDrawable(context.getResources()
                    .getDrawable(R.drawable.my_score));
        } else {
            nameTextView.setText(users.get(11 - position).getName());
            if (users.get(11 - position).getScore() == -1) {
                scoreTextView.setText("SCORE");
                positionTextView.setText("");
            } else {
                scoreTextView.setText(String.valueOf(users.get(11 - position).getScore()));
                positionTextView.setText(String.valueOf(position));
            }

            if (position == 11 - myScore) {
                rowView.findViewById(R.id.list_item).setBackgroundDrawable(context.getResources()
                        .getDrawable(R.drawable.my_score));
            }
        }



        return rowView;
    }
}
