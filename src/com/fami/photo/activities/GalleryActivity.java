package com.fami.photo.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.quickblox.core.QBProgressCallback;
import com.fami.MainActivity;
import com.fami.R;
import com.fami.photo.adapter.GalleryAdapter;
import com.fami.photo.helper.PhotoDataHolder;
import com.fami.photo.utils.GetImageFileTask;
import com.fami.photo.utils.ImageHelper;
import com.fami.photo.utils.OnGetImageFileListener;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;

import java.io.File;
import java.util.List;

public class GalleryActivity extends BaseActivity implements AdapterView.OnItemClickListener, OnGetImageFileListener {

    private final boolean PUBLIC_ACCESS_TRUE = true;

    private GridView galleryGridView;
    private GalleryAdapter galleryAdapter;
    private ImageHelper imageHelper;
    private ImageView selectedImageImageView;

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_new_image_button:
                imageHelper.getImage();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        startShowImgActivity(position);
    }

    @Override
    public void onGotImageFile(File imageFile) {
        uploadSelectedImage(imageFile);
    }

    private void startShowImgActivity(int position) {
        Intent intent = new Intent(this, ShowImageActivity.class);
        intent.putExtra(POSITION, position);
        startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        Log.v("here5","here");
        initUI();
        Log.v("here6","here");
        initGalleryView();
        Log.v("here7","here");

        imageHelper = new ImageHelper(this);
        Log.v("here8","here");
    }

    private void initUI() {
        galleryGridView = (GridView) findViewById(R.id.gallery_gridview);
        selectedImageImageView = (ImageView) findViewById(R.id.image_imageview);
    }

    private void initGalleryView() {
        galleryAdapter = new GalleryAdapter(getApplicationContext());
        galleryGridView.setAdapter(galleryAdapter);
        //galleryGridView.setOnItemClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri originalUri = data.getData();
            selectedImageImageView.setImageURI(originalUri);
            selectedImageImageView.setVisibility(View.VISIBLE);
            progressDialog.setProgress(0);
            progressDialog.show();

            new GetImageFileTask(this).execute(imageHelper, selectedImageImageView);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadSelectedImage(File imageFile) {

        // Upload new file
        //
        QBContent.uploadFileTask(imageFile, PUBLIC_ACCESS_TRUE, null, new QBEntityCallbackImpl<QBFile>() {
            @Override
            public void onSuccess(QBFile qbFile, Bundle bundle) {
                PhotoDataHolder.getDataHolder().addQbFile(qbFile);
                selectedImageImageView.setVisibility(View.GONE);
                galleryAdapter.notifyDataSetChanged();

                progressDialog.hide();
            }

            @Override
            public void onError(List<String> strings) {

            }
        }, new QBProgressCallback() {
            @Override
            public void onProgressUpdate(int progress) {
                progressDialog.setProgress(progress);
            }
        });
    }
    public void onBackPressed(){
    	Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
	}
}