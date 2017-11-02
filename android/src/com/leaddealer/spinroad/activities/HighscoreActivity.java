package com.leaddealer.spinroad.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.leaddealer.spinroad.MainActivity;
import com.leaddealer.spinroad.R;
import com.leaddealer.spinroad.models.User;
import com.leaddealer.spinroad.utils.LoadingDialog;
import com.leaddealer.spinroad.utils.Score;
import com.leaddealer.spinroad.utils.ScoreArrayAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class HighscoreActivity extends Activity {

    CallbackManager callbackManager;
    private FirebaseAuth mAuth;

    private DatabaseReference mDatabase;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.highscore_activity);

        listView = (ListView) findViewById(R.id.list_view);
        listView.setDividerHeight(0);
        listView.setDivider(null);
        showLoadingDialog();

        findViewById(R.id.close).setOnClickListener(v -> {
            Intent intent = new Intent(HighscoreActivity.this, MainActivity.class);
            intent.putExtra("FROM_ANOTHER", true);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.share).setOnClickListener(v -> {
            sharePicture(drawTextToBitmap("My score: " + String.valueOf(Score.getHighScore(this))),
                    "My high score is " + String.valueOf(Score.getHighScore(this))
                    + "! Can you beat me?\n"
                    + "Spinspace - https://play.google.com/store/apps/details?id=com.leaddealer.spinspace");
        });

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("record_table").keepSynced(true);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
            callbackManager = CallbackManager.Factory.create();
            LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>(){
                @Override
                public void onSuccess(LoginResult loginResult) {
                    handleFacebookAccessToken(loginResult.getAccessToken());
                }

                @Override
                public void onCancel() {
                    Intent intent = new Intent(HighscoreActivity.this, MainActivity.class);
                    intent.putExtra("FROM_ANOTHER", true);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onError(FacebookException error) {
                    finish();
                }
            });
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
        } else {
            getDataFromFirebase();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void writeRecord() {
        User user = new User(mAuth.getCurrentUser().getDisplayName().split(" ")[0], Score.getHighScore(this));
        mDatabase.child("record_table").child(mAuth.getCurrentUser().getUid()).setValue(user);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("TAG", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        getDataFromFirebase();
                    } else {
                        Toast.makeText(HighscoreActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getDataFromFirebase(){
        writeRecord();
        Query records = mDatabase.child("record_table").orderByChild("score").limitToLast(10);
        records.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int myScore = -1;
                int i = 0;
                ArrayList<User> users = new ArrayList<>();

                users.add(new User(mAuth.getCurrentUser().getDisplayName().split(" ")[0], Score.getHighScore(HighscoreActivity.this)));

                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    i++;
                    users.add(new User(snapshot.child("name").getValue().toString(),
                            Integer.valueOf(snapshot.child("score").getValue().toString())));
                    if(snapshot.getKey().equals(mAuth.getCurrentUser().getUid())){
                        myScore = i;
                    }
                }
                users.add(new User("NAME", -1));
                ScoreArrayAdapter adapter = new ScoreArrayAdapter(HighscoreActivity.this, users, myScore);
                listView.setAdapter(adapter);
                dismissLoadingDialog();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void sharePicture(Bitmap bitmap, String text) {
        try {
            File file = new File(getApplicationContext().getCacheDir(), "highscore.png");
            FileOutputStream fOut = new FileOutputStream(file);

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);

            fOut.flush();
            fOut.close();
            file.setReadable(true, false);
            final Intent intent = new Intent(android.content.Intent.ACTION_SEND);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            intent.putExtra(android.content.Intent.EXTRA_TEXT, text);
            intent.setType("image/png");
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Bitmap drawTextToBitmap(String text) {
        Resources resources = getResources();
        float scale = resources.getDisplayMetrics().density;
        Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_launcher);

        android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();
        if(bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        // resource bitmaps are imutable,
        // so we need to convert it to mutable one
        bitmap = bitmap.copy(bitmapConfig, true);

        Canvas canvas = new Canvas(bitmap);
        // new antialised Paint
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // text color - #3D3D3D
        paint.setColor(Color.parseColor("#036DB6"));
        // text size in pixels
        paint.setTextSize((int) (14 * scale));
        // text shadow
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);

        // draw text to the Canvas center
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        int x = bitmap.getWidth()/20;
        int y = bitmap.getHeight()/20 + bounds.height();

        canvas.drawText(text, x, y, paint);

        return bitmap;
    }

    LoadingDialog mLoadingDialog;

    public void showLoadingDialog(){
        mLoadingDialog = new LoadingDialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        mLoadingDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        WindowManager.LayoutParams layoutParams = mLoadingDialog.getWindow().getAttributes();
        layoutParams.dimAmount = .7f;
        mLoadingDialog.getWindow().setAttributes(layoutParams);
        mLoadingDialog.show();
    }

    public void dismissLoadingDialog(){
        if(mLoadingDialog != null)
            mLoadingDialog.dismiss();
        mLoadingDialog = null;
    }
}
