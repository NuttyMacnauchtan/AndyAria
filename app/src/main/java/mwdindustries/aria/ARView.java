package mwdindustries.aria;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import junit.framework.Test;

import java.util.List;
import mwdindustries.aria.util.GPSPOI ;


public class ARView extends Activity implements SurfaceHolder.Callback{

    final int NEAR = 0;
    final int MID  = 1;
    final int FAR  = 2;
    int scope = -1;
    boolean run = true;//used to continue the run while loop, used to
    GPSPOI test = new GPSPOI("Test Point", "Our default point used for calibration and prototyping",
            "don't expect too much",39.377982,84.06341,500,4,0);
    //used to find a portion of the code that will be used as in iterative loop for program code
    int increment = 0;

    Camera mCamera;
    SurfaceView mPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_arview);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        mPreview = (SurfaceView)findViewById(R.id.mPreview);
        mPreview.getHolder().addCallback(this);
        mPreview.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mCamera = Camera.open();

        //touch to autofocus
        mPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamera.autoFocus(null);
            }
        });

        //doesn't let phone lock/go to sleep
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //begin iterative loop to update and draw images or read beacon signals
        /*activity_arview = (TestText) findViewById(R.id.main_textview);
        TestText.setText("Set in Java!");*/

    }//end onCreate

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.arview, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(mPreview.getHolder());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Camera.Parameters params = mCamera.getParameters();
        List<Camera.Size> sizes = params.getSupportedPreviewSizes();
        Camera.Size selected = sizes.get(0);
        params.setPreviewSize(selected.width,selected.height);
        mCamera.setParameters(params);

        mCamera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        Log.i("PREVIEW","surfaceDestroyed");
    }

    @Override
    public void onPause() {
        super.onPause();
        mCamera.stopPreview();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCamera.release();
    }

    //if poi button clicked
    public void sendPOIList(View view) {

        //create buttons to select inside out outside POIs
        Button inside = (Button) findViewById(R.id.BtnIndoor);
        Button outside = (Button) findViewById(R.id.BtnOutdoor);

        //hide or show buttons
        if(inside.getVisibility() == View.VISIBLE && outside.getVisibility() == View.VISIBLE)
        {
            inside.setVisibility(View.INVISIBLE);
            outside.setVisibility(View.INVISIBLE);
        }
        else
        {
            inside.setVisibility(View.VISIBLE);
            outside.setVisibility(View.VISIBLE);
        }

    }//end sendPOIList

    //for indoor POI selection
    public void indoorPOI(View view)
    {
        Intent indoorPOIcheckBoxes = new Intent(this, insidePOI.class);
        startActivity(indoorPOIcheckBoxes);
    }

    //for outdoor POI selection
    public void outdoorPOI(View view)
    {
        Intent outdoorPOIcheckBoxes = new Intent(this, POIActivity.class);
        startActivity(outdoorPOIcheckBoxes);
    }

    //for schedule button click
    public void scheduleInput(View view)
    {
        Intent scheduler = new Intent(this, scheduleActivity.class);
        startActivity(scheduler);
    }

//    //test to show image
//    public void showImage (View view)
//    {
//        ImageView russImage = (ImageView) findViewById(R.id.russ);
//
//        //int resID = getResources().getIdentifier(imageName , "drawable", getPackageName());
//
//        if(russImage.getVisibility() == View.VISIBLE)
//        {
//            russImage.setVisibility(View.INVISIBLE);
//        }
//        else
//        {
//            russImage.setVisibility(View.VISIBLE);
//        }
//
//    }

    //gear button clicked
    public void gearButton(View view)
    {
        //create buttons
        Button scopeBtn = (Button) findViewById(R.id.BtnScope);
        Button schedule = (Button) findViewById(R.id.BtnSchedule);

        //hide or show buttons
        if(scopeBtn.getVisibility() == View.VISIBLE && schedule.getVisibility() == View.VISIBLE)
        {
            scopeBtn.setVisibility(View.INVISIBLE);
            schedule.setVisibility(View.INVISIBLE);

            Button near = (Button) findViewById(R.id.BtnNear);
            Button mid = (Button) findViewById(R.id.BtnMid);
            Button far = (Button) findViewById(R.id.BtnFar);

            hideButtons(near, mid, far);
        }
        else
        {
            scopeBtn.setVisibility(View.VISIBLE);
            schedule.setVisibility(View.VISIBLE);
        }

//        if(near.getVisibility() == View.VISIBLE &&
//                mid.getVisibility() == View.VISIBLE &&
//                far.getVisibility() == View.VISIBLE)
//        {
//            near.setVisibility(View.INVISIBLE);
//            mid.setVisibility(View.INVISIBLE);
//            far.setVisibility(View.INVISIBLE);
//        }
//
//        if(scopeBtn.getVisibility() == View.VISIBLE && schedule.getVisibility() == View.VISIBLE)
//        {
//            scopeBtn.setVisibility(View.INVISIBLE);
//            schedule.setVisibility(View.INVISIBLE);
//        }
    }

    //scope button clicked
    public void scopePOI(View view)
    {
        //create buttons
        final Button near = (Button) findViewById(R.id.BtnNear);
        final Button mid = (Button) findViewById(R.id.BtnMid);
        final Button far = (Button) findViewById(R.id.BtnFar);

        //hide or show buttons
        if(near.getVisibility() == View.VISIBLE &&
            mid.getVisibility() == View.VISIBLE &&
            far.getVisibility() == View.VISIBLE)
        {
            near.setVisibility(View.INVISIBLE);
            mid.setVisibility(View.INVISIBLE);
            far.setVisibility(View.INVISIBLE);
        }
        else
        {
            near.setVisibility(View.VISIBLE);
            mid.setVisibility(View.VISIBLE);
            far.setVisibility(View.VISIBLE);
        }

        //when near button clicked
        near.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scope = 0;
                near.setVisibility(View.INVISIBLE);
                mid.setVisibility(View.INVISIBLE);
                far.setVisibility(View.INVISIBLE);
            }
        });

        //when mid button clicked
        mid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scope = 1;
                near.setVisibility(View.INVISIBLE);
                mid.setVisibility(View.INVISIBLE);
                far.setVisibility(View.INVISIBLE);
            }
        });

        //when far button clicked
        near.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scope = 2;
                near.setVisibility(View.INVISIBLE);
                mid.setVisibility(View.INVISIBLE);
                far.setVisibility(View.INVISIBLE);
            }
        });

    }

    public void hideButtons(Button button1, Button button2, Button button3)
    {
            button1.setVisibility(View.INVISIBLE);
            button2.setVisibility(View.INVISIBLE);
            button3.setVisibility(View.INVISIBLE);
    }

}//end main class




