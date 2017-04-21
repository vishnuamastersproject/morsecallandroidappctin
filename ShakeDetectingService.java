package cha.com.autodetectsms;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.format.Time;
import android.util.Log;


public class ShakeDetectingService extends Service  implements ShakeDetectionDTO.Callback
{  private ShakeDetectionDTO shakeDetectionDTO =null;
    static int count=0;

    Time time;
    @Override
    public void onCreate()
    {time=new Time();
        shakeDetectionDTO =new ShakeDetectionDTO(this, 2.25d, 500, this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }


    public void shakingStarted() {

    }

    public void shakingStopped() {
        count++;

        if(count>0){
            Log.i("Count",count+"");
            Time t2=new Time();
            t2.setToNow();

            {

                Intent i = new Intent();
                i.setClass(this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);

            }
            count=0;

        }else{

            time.setToNow();

        }

    }
}


