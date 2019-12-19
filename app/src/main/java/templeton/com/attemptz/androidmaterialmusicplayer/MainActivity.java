package templeton.com.attemptz.androidmaterialmusicplayer;

import java.lang.*;
import java.util.Random;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.res.AssetFileDescriptor;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import com.mikhaellopez.circularimageview.CircularImageView;



public class MainActivity extends AppCompatActivity {

    private View parent_view;
    private AppCompatSeekBar seek_song_progressbar;
    private FloatingActionButton btn_play;
    private TextView tv_song_current_duration, tv_song_total_duration;
    private CircularImageView image;

    private MediaPlayer mp;
    private Handler mHandler = new Handler();
    //public String index[] = new String[10]; If needed for dynamic load array
    private MusicUtils utils;
    boolean flag = false;
    String[] index = new String[] {
            "music/06 Back In Black.mp3", "music/11 Jailhouse Rock.mp3",
            "music/bensound-clearday.mp3", "music/Black Sabbath(Dio) -  Heaven And Hell.mp3",
            "music/John Cena.mp3"};

    public int counter = 0;
    public int count = 0;
    Random rgen = new Random();  // Random number generator
  /*  public void buildArray(){
        File folder = new File("C:\\Users\\Matthew Templeton\\Desktop\\AndroidMusic\\AndroidMaterialMusicPlayer-master\\app\\src\\main\\assets\\music");

        File[] listOfFiles =  folder.listFiles();

        String[] names = new String[listOfFiles.length];

        for (int i = 0; i < listOfFiles.length; i++) {
            names[i] = listOfFiles[i].getName();
        }

        while (counter < names.length) {

            names[counter]= "music/" + names[counter];
            counter++;
        }
        counter = 0;
        index = names;
    }

 Code if reading from external memory card
if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
{
       ///mounted
}

File dir= new File(android.os.Environment.getExternalStorageDirectory());
walkdir(dir);

 ArrayList<String> filepath= new ArrayList<String>();//contains list of all files ending with

public void walkdir(File dir) {
 File listFile[] = dir.listFiles();

if (listFile != null) {
 for (int i = 0; i < listFile.length; i++) {

 if (listFile[i].isDirectory()) {// if its a directory need to get the files under that directory
  walkdir(listFile[i]);
} else {// add path of  files to your arraylist for later use

  //Do what ever u want
  filepath.add( listFile[i].getAbsolutePath());

 }
 }
}
}
shareimprove this answer

 */





    public String getFileName(){
        return index[counter];
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // buildArray();
        setContentView(R.layout.activity_main);
        setMusicPlayerComponents();
    }






    private void setMusicPlayerComponents() {
        parent_view = findViewById(R.id.parent_view);
        seek_song_progressbar = findViewById(R.id.seek_song_progressbar);
        btn_play = findViewById(R.id.btn_play);

        seek_song_progressbar.setProgress(0);
        seek_song_progressbar.setMax(MusicUtils.MAX_PROGRESS);

        tv_song_current_duration =  findViewById(R.id.tv_song_current_duration);
        tv_song_total_duration = findViewById(R.id.total_duration);
        image =  findViewById(R.id.image);

        mp = new MediaPlayer();
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                btn_play.setImageResource(R.drawable.ic_play_arrow);
            }
        });

        try {
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);

            AssetFileDescriptor afd = getAssets().openFd(getFileName());
            //
            TextView text = findViewById (R.id.displayText);
            text.setText(getFileName());
            //
            mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            mp.prepare();
        } catch (Exception e) {

            Snackbar.make(parent_view, "Initial Load Failure", Snackbar.LENGTH_SHORT).show();


        }

        utils = new MusicUtils();
        seek_song_progressbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mHandler.removeCallbacks(mUpdateTimeTask);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mHandler.removeCallbacks(mUpdateTimeTask);
                int totalDuration = mp.getDuration();
                int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);
                mp.seekTo(currentPosition);
                mHandler.post(mUpdateTimeTask);
            }
        });
        buttonPlayerAction();
        updateTimerAndSeekbar();
    }


    private void buttonPlayerAction() {
        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (mp.isPlaying()) {
                    mp.pause();
                    btn_play.setImageResource(R.drawable.ic_play_arrow);
                } else {
                    mp.start();
                    btn_play.setImageResource(R.drawable.ic_pause);
                    mHandler.post(mUpdateTimeTask);
                }
                rotateTheDisk();
            }
        });
    }

    public void controlClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_repeat: {
                toggleButtonColor((ImageButton) v);
                Snackbar.make(parent_view, "Repeat", Snackbar.LENGTH_SHORT).show();
                count ++;
                if (count%2 == 0){
                flag = false;
                }
                if (count%2 ==1) {
                    flag = true;
                }
                break;
            }
            case R.id.btn_shuffle: {
                toggleButtonColor((ImageButton) v);
                Snackbar.make(parent_view, "Shuffle", Snackbar.LENGTH_SHORT).show();

                for (int i=0; i<index.length; i++) {
                    int randomPosition = rgen.nextInt(index.length);
                    String temp = index[i];
                    index[i] = index[randomPosition];
                    index[randomPosition] = temp;
                }

                break;
            }
            case R.id.btn_prev: {

                toggleButtonColor((ImageButton) v);
                Snackbar.make(parent_view, "Previous", Snackbar.LENGTH_SHORT).show();
                mp.release();
                if (flag == false) {
                    counter--;
                }
                if (counter < 0)
                {
                    counter = 0;
                }
                mp = new MediaPlayer();
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        btn_play.setImageResource(R.drawable.ic_play_arrow);
                    }
                });

                try {
                    mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    AssetFileDescriptor afd = getAssets().openFd(getFileName());
                    //
                    TextView text = findViewById (R.id.displayText);
                    text.setText(getFileName());
                    //
                    mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                    afd.close();
                    mp.prepare();
                    mp.start();
                } catch (Exception e) {
                    Snackbar.make(parent_view, "Cannot load audio file, click next again", Snackbar.LENGTH_SHORT).show();
                    counter=0;
                }
                break;
            }
            case R.id.btn_next: {
                toggleButtonColor((ImageButton) v);
                Snackbar.make(parent_view, "Next", Snackbar.LENGTH_SHORT).show();
                mp.release();
                if(flag == false) {
                    counter++;
                }
                if (counter >= index.length)
                {
                    counter = 0;
                }
                mp = new MediaPlayer();
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        btn_play.setImageResource(R.drawable.ic_play_arrow);
                    }
                });

                try {

                    mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    AssetFileDescriptor afd = getAssets().openFd(getFileName());
                    //
                    TextView text = findViewById (R.id.displayText);
                    text.setText(getFileName());
                    //
                    mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                    afd.close();
                    mp.prepare();
                    mp.start();
                } catch (Exception e) {
                    Snackbar.make(parent_view, "Cannot load audio file, click next again", Snackbar.LENGTH_SHORT).show();
                    counter=0;
                }
                break;
            }
        }
    }

    private boolean toggleButtonColor(ImageButton bt) {
        String selected = (String) bt.getTag(bt.getId());
        if (selected != null) { // selected
            bt.setColorFilter(getResources().getColor(R.color.colorDarkOrange), PorterDuff.Mode.SRC_ATOP);
            bt.setTag(bt.getId(), null);
            return false;
        } else {
            bt.setTag(bt.getId(), "selected");
            bt.setColorFilter(getResources().getColor(R.color.colorYellow), PorterDuff.Mode.SRC_ATOP);
            return true;
        }
    }


    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            updateTimerAndSeekbar();
            if (mp.isPlaying()) {
                mHandler.postDelayed(this, 100);
            }
        }
    };

    private void updateTimerAndSeekbar() {
        long totalDuration = mp.getDuration();
        long currentDuration = mp.getCurrentPosition();

        tv_song_total_duration.setText(utils.milliSecondsToTimer(totalDuration));
        tv_song_current_duration.setText(utils.milliSecondsToTimer(currentDuration));

        int progress = (int) (utils.getProgressSeekBar(currentDuration, totalDuration));
        seek_song_progressbar.setProgress(progress);
    }
    // where imported package was used
    private void rotateTheDisk() {
        if (!mp.isPlaying()) return;
        image.animate().setDuration(100).rotation(image.getRotation() + 2f).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                rotateTheDisk();
                super.onAnimationEnd(animation);
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        counter = 0;
        mHandler.removeCallbacks(mUpdateTimeTask);
        mp.release();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

            finish();
        } else {
            Snackbar.make(parent_view, item.getTitle(), Snackbar.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

}

