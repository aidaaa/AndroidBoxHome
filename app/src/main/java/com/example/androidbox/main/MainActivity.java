package com.example.androidbox.main;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import com.example.androidbox.BuildConfig;
import com.example.androidbox.R;
import com.example.androidbox.confg.ConfigActivity;
import com.example.androidbox.dagger.DaggerPlayerComponent;
import com.example.androidbox.dagger.PlayerComponent;
import com.example.androidbox.dagger.app.App;
import com.example.androidbox.databinding.ActivityMainBinding;
import com.example.androidbox.xml.VersionXml;
import com.example.androidbox.xml.Xml;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements Observer<List<ArrayList<String>>> {

    PlayerView player_view;

    @Inject
    SimpleExoPlayer simpleExoPlayer;
    @Inject
    DefaultTrackSelector trackSelector;
    @Inject
    DataSource.Factory daFactory;
    @Inject
    SharedPreferences sharedPreferences;

    Xml xml = new Xml();
    VersionXml versionXml = new VersionXml();

    ArrayList<String> urls = new ArrayList<>();
    ArrayList<String> chNames = new ArrayList<>();

    TextView edt, title;

    ArrayList<String> idStr = new ArrayList<>();
    int counter = 0;
    boolean shortPress = false;
    boolean longPress = false;
    String chNumber = "";

    MediaSource mediaSource = null;
    Uri uri;
    int ok = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        ViewModelMain viewModelMain = new ViewModelMain();
        binding.setMain(viewModelMain);

       // App.getApp().getDaggerComponent(this).getPlayer(this);
        PlayerComponent playerComponent= DaggerPlayerComponent.builder()
                .context(this)
                .build();

        playerComponent.getPlayer(this);


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        player_view = binding.playerView;

        edt = binding.edt;

        edt.setVisibility(View.INVISIBLE);

        title = binding.title;

        int appVersion = BuildConfig.VERSION_CODE;

        Toast.makeText(this, String.valueOf(appVersion), Toast.LENGTH_SHORT).show();

        Observable<ArrayList<String>> observableVersion = versionXml.getObservableVersion();
        observableVersion.subscribe(new Observer<ArrayList<String>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ArrayList<String> strings) {
                if (strings.get(0).equals("error")) {
                    Toast.makeText(MainActivity.this, "فایل xml ورژن یافت نشد", Toast.LENGTH_SHORT).show();
                } else {
                    String version = strings.get(1);
                    String url = strings.get(0);
                    if (!version.equals(String.valueOf(appVersion))) {
                        newVersion(url);
                    }
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

        sharedPreferences = getSharedPreferences("shared", Context.MODE_PRIVATE);
        String ip = sharedPreferences.getString("ip", "5.160.10.54:8090");
        //String ip="http://5.160.10.54:8090/channel.xml";

        Observable<List<ArrayList<String>>> observable = xml.getObservableXml(ip);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);
    }

    public void setPlayer(int pos) {
        if (pos < urls.size()) {
            String str = urls.get(pos);
            if (str == "") {
                Toast.makeText(this, "not find url", Toast.LENGTH_LONG).show();
                uri = Uri.parse(urls.get(pos));

                String titleText = chNames.get(pos);
                title.setText(titleText);

                mediaSource = new ProgressiveMediaSource.Factory(daFactory).createMediaSource(uri);

                player_view.setPlayer(simpleExoPlayer);
                simpleExoPlayer.prepare(mediaSource);

                simpleExoPlayer.setPlayWhenReady(true);
                chNumber = "";
                clearText();
            } else {
                uri = Uri.parse(urls.get(pos));

                String titleText = chNames.get(pos);
                title.setText(titleText);

                mediaSource = new ProgressiveMediaSource.Factory(daFactory).createMediaSource(uri);

                player_view.setPlayer(simpleExoPlayer);
                simpleExoPlayer.prepare(mediaSource);

                simpleExoPlayer.setPlayWhenReady(true);
                chNumber = "";
                clearText();
            }

            //simpleExoPlayer.getPlaybackState();

            simpleExoPlayer.addListener(new Player.EventListener() {

                @Override
                public void onPlayerError(ExoPlaybackException error) {
                    System.out.println(error.getMessage());
                    // edt.setVisibility(View.INVISIBLE);
                    //img.setVisibility(View.INVISIBLE);
                }
            });
        } else {
            //  edt.setText("");
            //  edt.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "خارج از محدوده", Toast.LENGTH_SHORT).show();
            chNumber = "";
            clearText();
        }

    }

    public void channelNum(String chNum) {
        idStr.add(chNum);
        //edt.setText("");

        if (urls.size() <= 10) {
            channelChange();
        } else {
            Observable.timer(3000, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Observer<Long>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(Long aLong) {
                            //   idStr.add(chNum);
                            channelChange();
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }


      /*  handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (idStr.size()!=0)
                {
                    String fId;
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < idStr.size(); i++) {
                        sb.append(idStr.get(i));
                    }
                    fId=sb.toString();
                    setPlayer(Integer.parseInt(fId));
                    idStr.clear();
                }
            }
        },5000);*/
    }

    public void channelChange() {
        //idStr.add(a);
        if (idStr.size() != 0) {
            String fId;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < idStr.size(); i++) {
                sb.append(idStr.get(i));
            }
            fId = sb.toString();
            setPlayer(Integer.parseInt(fId));
            idStr.clear();
        }
    }

    public DisposableObserver<Long> clearText() {
        DisposableObserver<Long> observer = Observable.just(1).timer(5000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getObserver());
        return observer;

      /*  Observable.timer(5000,TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Long aLong) {
                        edt.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });*/
    }

    public DisposableObserver<Long> getObserver() {
        return new DisposableObserver<Long>() {
            @Override
            public void onNext(Long aLong) {
                edt.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }

    public void dispose(DisposableObserver<Long> disposableObserver) {
        disposableObserver.dispose();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == 7 || keyCode == 8 || keyCode == 9 || keyCode == 10 || keyCode == 11 || keyCode == 12 ||
                keyCode == 13 || keyCode == 14 || keyCode == 15 || keyCode == 16 || keyCode == 19 || keyCode == 20 ||
                keyCode == 23) {
            //   event.startTracking();

            if (longPress == true) {
                shortPress = false;
            } else {
                shortPress = true;
                longPress = false;

                if (urls != null) {
                    int id = keyCode;
                    System.out.println(String.valueOf(id));
                    dispose(clearText());

                    switch (keyCode) {
                        //Up
                        case 19:
                            if (counter == urls.size() - 1) {
                                edt.setVisibility(View.VISIBLE);
                                edt.setText(String.valueOf(counter));
                                setPlayer(counter);
                            } else if (counter < urls.size() - 1) {
                                counter++;
                                edt.setVisibility(View.VISIBLE);
                                edt.setText(String.valueOf(counter));
                                setPlayer(counter);
                            }
                            break;

                        //Down
                        case 20:
                            if (counter == 0) {
                                edt.setVisibility(View.VISIBLE);
                                edt.setText(String.valueOf(counter));
                                setPlayer(counter);
                            } else if (counter > 0 && counter <= urls.size() - 1) {
                                counter--;
                                edt.setVisibility(View.VISIBLE);
                                edt.setText(String.valueOf(counter));
                                setPlayer(counter);
                            }

                            break;

                        //Ok
                        case 23:
                            if (ok == 3) {
                                ok = 0;
                                Intent intent = new Intent(MainActivity.this, ConfigActivity.class);
                                startActivity(intent);
                            } else {
                                ok++;
                            }
                            break;

                        //0
                        case 7:
                            chNumber = chNumber + "0";
                            edt.setVisibility(View.VISIBLE);
                            edt.setText(chNumber);
                            counter = Integer.parseInt(chNumber);
                            channelNum("0");
                            break;

                        //1
                        case 8:
                            chNumber = chNumber + "1";
                            edt.setVisibility(View.VISIBLE);
                            edt.setText(chNumber);
                            counter = Integer.parseInt(chNumber);
                            channelNum("1");
                            break;

                        //2
                        case 9:
                            chNumber = chNumber + "2";
                            edt.setVisibility(View.VISIBLE);
                            edt.setText(chNumber);
                            counter = Integer.parseInt(chNumber);
                            channelNum("2");
                            break;

                        //3
                        case 10:
                            chNumber = chNumber + "3";
                            edt.setVisibility(View.VISIBLE);
                            edt.setText(chNumber);
                            counter = Integer.parseInt(chNumber);
                            channelNum("3");
                            break;

                        //4
                        case 11:
                            chNumber = chNumber + "4";
                            edt.setVisibility(View.VISIBLE);
                            edt.setText(chNumber);
                            counter = Integer.parseInt(chNumber);
                            channelNum("4");
                            break;

                        //5
                        case 12:
                            chNumber = chNumber + "5";
                            edt.setVisibility(View.VISIBLE);
                            edt.setText(chNumber);
                            counter = Integer.parseInt(chNumber);
                            channelNum("5");
                            break;

                        //6
                        case 13:
                            chNumber = chNumber + "6";
                            edt.setVisibility(View.VISIBLE);
                            edt.setText(chNumber);
                            counter = Integer.parseInt(chNumber);
                            channelNum("6");
                            break;

                        //7
                        case 14:
                            chNumber = chNumber + "7";
                            edt.setVisibility(View.VISIBLE);
                            edt.setText(chNumber);
                            counter = Integer.parseInt(chNumber);
                            channelNum("7");
                            break;

                        //8
                        case 15:
                            chNumber = chNumber + "8";
                            edt.setVisibility(View.VISIBLE);
                            edt.setText(chNumber);
                            counter = Integer.parseInt(chNumber);
                            channelNum("8");
                            break;

                        //9
                        case 16:
                            chNumber = chNumber + "9";
                            edt.setVisibility(View.VISIBLE);
                            edt.setText(chNumber);
                            counter = Integer.parseInt(chNumber);
                            channelNum("9");
                            break;
                    }

                }

            }

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(List<ArrayList<String>> strings) {
        urls = strings.get(0);
        String str = urls.get(0);

        if (str.equals("error")) {
            Toast.makeText(this, "فایل xml یافت نشد", Toast.LENGTH_SHORT).show();
        } else {
            urls = strings.get(0);
            chNames = strings.get(1);
            int len = urls.size();
            if (len >= 0 && len <= 10)
                edt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1)});
            else if (len > 10 && len < 100)
                edt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(2)});
        }
    }

    @Override
    public void onError(Throwable e) {
        System.out.println(e.getMessage());
    }

    @Override
    public void onComplete() {
        setPlayer(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (simpleExoPlayer != null)
            simpleExoPlayer.release();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (simpleExoPlayer != null)
            simpleExoPlayer.release();
    }

    public void newVersion(String url) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("CAcompAdda");
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setMessage("Latest Version is Available. Click on OK to update");
        builder.getContext().setTheme(R.style.AppTheme);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                download(url);
            }
        });
        builder.setNegativeButton("Remind Me Later", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void downloadapk(String urlV) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                try {
                    URL url = new URL(urlV);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setDoOutput(true);
                    urlConnection.connect();

                    File sdcard = Environment.getExternalStorageDirectory();
                    File file = new File(sdcard, "app-debug.apk");

                    FileOutputStream fileOutput = new FileOutputStream(file);
                    InputStream inputStream = urlConnection.getInputStream();

                    byte[] buffer = new byte[1024];
                    int bufferLength = 0;

                    while ((bufferLength = inputStream.read(buffer)) > 0) {
                        fileOutput.write(buffer, 0, bufferLength);
                    }
                    fileOutput.close();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object o) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        installApk();
                    }
                });
    }

    private void installApk() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(new File("/sdcard/app-debug.apk"));
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        startActivity(intent);
    }

    public void download(String url) {
        String destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
        String fileName = "AppName.apk";
        destination += fileName;
        final Uri uri = Uri.parse("file://" + destination);

        //Delete update file if exists
        File file = new File(destination);
        if (file.exists())
            //file.delete() - test this, I think sometimes it doesnt work
            file.delete();

        //set downloadmanager
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
       // request.setDescription("new");
      //  request.setTitle("new");

        //set destination
        request.setDestinationUri(uri);

        // get download service and enqueue file
        final DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        final long downloadId = manager.enqueue(request);

        //set BroadcastReceiver to install app when .apk is downloaded
        BroadcastReceiver onComplete = new BroadcastReceiver() {
            public void onReceive(Context ctxt, Intent intent) {
               /* Intent install = new Intent(Intent.ACTION_VIEW);
                install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                install.setDataAndType(uri,
                        manager.getMimeTypeForDownloadedFile(downloadId));
                startActivity(install);*/

                Intent install = new Intent(Intent.ACTION_VIEW);
                install.setDataAndType(FileProvider.getUriForFile(ctxt, getApplicationContext().getPackageName() + ".provider", file), "application/vnd.android.package-archive");
                install.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(install);
                unregisterReceiver(this);
                finish();
            }
        };
        //register receiver for when .apk download is compete
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

 /*   public class Reciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);
                String destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
                String flieName = "Live_TV.apk";
                destination += flieName;
                File file = new File(destination);
                Uri apkURI = FileProvider.getUriForFile(context, getApplicationContext().getPackageName() + ".provider", file);

                Intent install = new Intent(Intent.ACTION_VIEW);
                install.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                install.setDataAndType(apkURI, "application/vnd.android.package-archive");
                // install.setDataAndType(uri, manager.getMimeTypeForDownloadedFile(downloadId));
                MainActivity.this.startActivity(install);
                unregisterReceiver(this);
                finish();
            }
        }
    }*/
      /* trackSelector.setParameters(
               trackSelector.getParameters().buildUpon().setMaxVideoSizeSd()
                            .setPreferredTextLanguage("eng")
                            .setPreferredAudioLanguage("eng").build());*/
}
