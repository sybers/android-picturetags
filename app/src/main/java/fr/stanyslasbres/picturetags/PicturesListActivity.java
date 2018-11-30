package fr.stanyslasbres.picturetags;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

public final class PicturesListActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_TO_ANNOTATE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pictures_list);

        FloatingActionButton fab = findViewById(R.id.fab_select_image);
        fab.setOnClickListener(view -> pickImageToAnnotate());
    }

    private void pickImageToAnnotate() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_TO_ANNOTATE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_TO_ANNOTATE && resultCode == Activity.RESULT_OK) {

            if(data != null) {
                ImageView imgview = findViewById(R.id.image_view);
                imgview.setImageURI(data.getData());
            }
        } else {
            Toast.makeText(this.getApplicationContext(), "Unable to pick the image...", Toast.LENGTH_LONG).show();
        }
    }
}
