package com.jiushig.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jiushig.imgpreview.ImageBuilder;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn).setOnClickListener(v->{
            new ImageBuilder(this)
                    .setUrls(new String[]{"http://oss.jiushig.com/content/mood/14/27d6ae9201c24a1897b9cc5fbbd6bbe6.png"
                            ,"http://oss.jiushig.com/content/mood/14/3a919d724f3f40dba512e1157f1f6e99.jpg"
                            ,"http://oss.jiushig.com/content/mood/14/3c5de4250df14c1b96a37f09bcb149c0.jpg"})
                    .start();
        });


        findViewById(R.id.btn1).setOnClickListener(v->{
            new ImageBuilder(this)
                    .setUrls(new String[]{"http://oss.jiushig.com/content/mood/14/27d6ae9201c24a1897b9cc5fbbd6bbe6.png"
                            ,"http://oss.jiushig.com/content/mood/14/3a919d724f3f40dba512e1157f1f6e99.jpg"
                            ,"http://oss.jiushig.com/content/mood/14/3c5de4250df14c1b96a37f09bcb149c0.jpg"})
                    .setCurrentUrl("http://oss.jiushig.com/content/mood/14/3a919d724f3f40dba512e1157f1f6e99.jpg")
                    .start();
        });
    }
}
