package fr.stanyslasbres.picturetags;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;

import fr.stanyslasbres.picturetags.persistence.AppDatabase;
import fr.stanyslasbres.picturetags.persistence.dao.PicturesDao;
import fr.stanyslasbres.picturetags.persistence.entities.Picture;

public final class PicturesListActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_TO_ANNOTATE = 0;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pictures_list);

        FloatingActionButton fab = findViewById(R.id.fab_select_image);
        fab.setOnClickListener(view -> pickImageToAnnotate());

        imageView = findViewById(R.id.image_view);

        LoadImagesTask task = new LoadImagesTask();
        task.execute(getApplicationContext());
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
                imageView.setImageURI(data.getData());
            }
        } else {
            Toast.makeText(this.getApplicationContext(), "Unable to pick the image...", Toast.LENGTH_LONG).show();
        }
    }

    private static class LoadImagesTask extends AsyncTask<Context, Void, List<Picture>> {
        @Override
        protected List<Picture> doInBackground(Context... context) {
            AppDatabase db = PictureTagsApplication.getDatabase();

            PicturesDao picturesDao = db.getPicturesDao();
            List<Picture> pictures = picturesDao.all();

            Log.d("[PICTURES LIST]", "Number of saved pictures : " + pictures.size());

            // delete all pictures !
            picturesDao.delete(pictures.toArray(new Picture[0]));

            return pictures;
        }
    }
}
