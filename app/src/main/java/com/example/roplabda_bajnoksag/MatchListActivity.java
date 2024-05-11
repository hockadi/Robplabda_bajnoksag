package com.example.roplabda_bajnoksag;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.media.MediaPlayer;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.Initializable;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MatchListActivity extends AppCompatActivity {
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    int tag = 1;
    private static final String LOG_TAG = MatchListActivity.class.getName();
    private FirebaseUser user;
    private  FirebaseAuth mAuth;

    private RecyclerView mRecycleView;
    private ArrayList<MatchItem> mMatchlist;
    private MatchesAdapter mAdapter;

    private FirebaseFirestore mFirestrore;
    private CollectionReference mItems;

    private NotificationHandler mNotificationHandler;
    private AlarmManager mAlarmManager;




    private FrameLayout redCircle;
    private TextView contentTextView;

    private int gridNumber = 1;
    private int cartItems = 0;
    private int queryLimit = 10;
    private boolean viewRow = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_list);
        mAuth = FirebaseAuth.getInstance();

        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            Log.d(LOG_TAG, "Autentikált felhasználó!");
        } else {
            Log.d(LOG_TAG, "Nem autentikált felhasználó!");
            finish();
        }

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mRecycleView = findViewById(R.id.recyclerView);
        mRecycleView.setLayoutManager(new GridLayoutManager(this, gridNumber));
        mMatchlist = new ArrayList<>();

        mAdapter = new MatchesAdapter(this, mMatchlist);
        mRecycleView.setAdapter(mAdapter);

        mFirestrore = FirebaseFirestore.getInstance();
        mItems = mFirestrore.collection("Items");

        queryData();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        this.registerReceiver(powerReceiver, filter);

        mNotificationHandler = new NotificationHandler(this);
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        setAlarmManager();

    }

    BroadcastReceiver powerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action == null) return;

            switch(action) {
                case Intent.ACTION_POWER_CONNECTED:
                    queryLimit = 10;
                    break;
                case Intent.ACTION_POWER_DISCONNECTED:
                    queryLimit = 5;
                    break;
            }
            queryData();
        }
    };


    private void queryData() {

        mMatchlist.clear();

        //mItems.whereEqualTo();
        mItems.orderBy("cartedCount", Query.Direction.DESCENDING).limit(queryLimit).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                MatchItem item = document.toObject(MatchItem.class);
                item.setId(document.getId());
                mMatchlist.add(item);
            }

            if(mMatchlist.size() == 0) {
                initializeData();
                queryData();
            }
            //initializeDate();
            mAdapter.notifyDataSetChanged();

        });
    }


    public void deleteItem(MatchItem item) {
        DocumentReference ref = mItems.document(item._getId());

        ref.delete().addOnSuccessListener(success -> {
            Log.d(LOG_TAG, "Item successfully deleted: "+ item._getId());
        })
        .addOnFailureListener(faliure -> {
            Toast.makeText(this, "Item " + item._getId()+ " cannot be deleted", Toast.LENGTH_LONG).show();
        });

        queryData();
        mNotificationHandler.cancel();
    }



    private void initializeData() {
        String[] matchesList = getResources().getStringArray(R.array.match_item_names);
        String[] matchesInfo= getResources().getStringArray(R.array.match_item_desc);
        String[] matchesPrice = getResources().getStringArray(R.array.match_prices);
        TypedArray matchesImageResource = getResources().obtainTypedArray(R.array.match_images);

        //mMatchlist.clear();

        for (int i = 0; i < matchesList.length; i++)
                mItems.add(new MatchItem(
                matchesList[i],
                matchesInfo[i],
                matchesPrice[i],
                matchesImageResource.getResourceId(i, 0),
                        0));


        matchesImageResource.recycle();
        //mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.match_list_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {return false;}

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(LOG_TAG, newText);
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id == R.id.logout_button) {
            Log.d(LOG_TAG, "Logout clicked!");
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.setting_button) {
            Log.d(LOG_TAG, "Setting clicked!");
            FirebaseAuth.getInstance().signOut();
            finish();
            return true;
        }else if (id == R.id.open_camera_button) {
            openCamera();
            return true;
        }else if (id == R.id.june_matches_button) {
            showJuneMatches();
            return true;
        }else if (id == R.id.all_matches) {
            allMatches();
            return true;
        }else if (id == R.id.cart) {
            Log.d(LOG_TAG, "Cart clicked!");
            return true;
        } else if (id == R.id.view_selector) {
            if (viewRow) {
                changeSpanCount(item, R.drawable.view_grid, 1);
            } else {
                changeSpanCount(item, R.drawable.view_row, 2);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void changeSpanCount(MenuItem item, int drawableId, int spanCount) {
        viewRow = !viewRow;
        item.setIcon(drawableId);
        GridLayoutManager layoutManager = (GridLayoutManager) mRecycleView.getLayoutManager();
        layoutManager.setSpanCount(spanCount);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem alertMenuItem = menu.findItem(R.id.cart);
        FrameLayout rootView = (FrameLayout) alertMenuItem.getActionView();

        redCircle = (FrameLayout) rootView.findViewById(R.id.view_alert_red_circle);
        contentTextView = (TextView) rootView.findViewById(R.id.view_alert_count_textview);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(alertMenuItem);
            }
        });

        return super.onPrepareOptionsMenu(menu);
    }

    public void updateAlertIcon(MatchItem item) {
        cartItems = (cartItems+1);
        if( 0 < cartItems) {
            contentTextView.setText(String.valueOf(cartItems));
        }else {
            contentTextView.setText("");
        }
        redCircle.setVisibility((cartItems > 0) ? VISIBLE : GONE);

        mItems.document(item._getId()).update("cartedCount", item.getCartedCount() + 1)
                .addOnFailureListener(faliure -> {
                    Toast.makeText(this, "Item " + item._getId()+ " cannot be changed", Toast.LENGTH_LONG).show();
                });

        mNotificationHandler.send(item.getName());


        queryData();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(powerReceiver);
    }

    private void setAlarmManager() {
        long repeatInterval = AlarmManager.INTERVAL_HALF_HOUR;
        long triggerTime = SystemClock.elapsedRealtime() + repeatInterval;

        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_MUTABLE);

        mAlarmManager.setInexactRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                triggerTime,
                repeatInterval,
                pendingIntent);

        //mAlarmManager.cancel(pendingIntent);
    }


    public void showJuneMatches() {
        //SELECT * FROM Items WHERE Item.info CONTAINS "Június"
        ArrayList<MatchItem> juneMatches = new ArrayList<>();
        mItems.whereEqualTo("info", "Június").get().addOnSuccessListener(queryDocumentSnapshots ->
        {for (QueryDocumentSnapshot queryDocumentSnapshot: queryDocumentSnapshots){
            MatchItem matchItem = queryDocumentSnapshot.toObject(MatchItem.class);
            juneMatches.add(matchItem);
        }
            mAdapter = new MatchesAdapter(this, juneMatches);
            mRecycleView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
            });
    }

    private void allMatches() {
        Intent intent = new Intent(this, MatchListActivity.class);
        startActivity(intent);
    }


    public void openCamera() {
        checkUserPermission();
    }

    void checkUserPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_ASK_PERMISSIONS);
                return;
            }
        }

        takePicture();
    }

    private void takePicture() {
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, tag);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePicture();
                } else {
                    Toast.makeText(this, "Engedély elutasítva!", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }



}