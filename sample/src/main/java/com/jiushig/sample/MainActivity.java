package com.jiushig.sample;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.jiushig.imgpreview.ImageBuilder;
import com.jiushig.imgpreview.ui.ImageActivity;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn).setOnClickListener(v -> {
            new ImageBuilder(this)
                    .setUrls(new String[]{"https://images.unsplash.com/photo-1458668383970-8ddd3927deed?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjE2ODQ0fQ&s=3a819ffc36f7749d2cb076e572b9d790&auto=format&fit=crop&w=747&q=80"
                            , "http://upload.wikimedia.org/wikipedia/commons/3/33/Physical_Political_World_Map.jpg"
                            , "http://oss.jiushig.com/content/mood/14/3c5de4250df14c1b96a37f09bcb149c0.jpg"})
                    .start();
        });


        findViewById(R.id.btn1).setOnClickListener(v -> {
            new ImageBuilder(this)
                    .setUrls(new String[]{"http://oss.jiushig.com/content/mood/14/27d6ae9201c24a1897b9cc5fbbd6bbe6.png"
                            , "http://oss.jiushig.com/content/mood/14/3a919d724f3f40dba512e1157f1f6e99.jpg"
                            , "http://oss.jiushig.com/content/mood/14/3c5de4250df14c1b96a37f09bcb149c0.jpg"})
                    .setCurrentUrl("http://oss.jiushig.com/content/mood/14/3a919d724f3f40dba512e1157f1f6e99.jpg")
                    .setSavePath("jiushig/img")
                    .start();
        });

        findViewById(R.id.btn2).setOnClickListener(v -> {
            new ImageBuilder(this)
                    .setUrls(new String[]{"http://oss.jiushig.com/content/mood/14/27d6ae9201c24a1897b9cc5fbbd6bbe6.png"
                            , "http://oss.jiushig.com/content/mood/14/3a919d724f3f40dba512e1157f1f6e99.jpg"
                            , "http://oss.jiushig.com/content/mood/14/3c5de4250df14c1b96a37f09bcb149c0.jpg"})
                    .setModel(ImageBuilder.MODEL_SAVE | ImageBuilder.MODEL_DELETE)
                    .start();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImageActivity.REQUEST_CODE && resultCode == RESULT_OK) {
            String[] deletes = data.getStringArrayExtra(ImageBuilder.MODEL_DELETE + "");
            if (deletes == null) return;

            String msg = "";
            for (String str : deletes) {
                msg += str;
            }

            if (!msg.isEmpty())
                Toast.makeText(this, "删除了：" + msg, Toast.LENGTH_LONG).show();
        }

    }
}
