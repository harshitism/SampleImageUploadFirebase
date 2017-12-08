package com.sample.lbb;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sample.lbb.Model.CameraPreview;
import com.sample.lbb.Model.GalleryPickerAdapter;
import com.sample.lbb.Model.PhotosData;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UploadActivity extends AppCompatActivity {


    public static final Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    public static final String[] projections =
            {MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                    MediaStore.Images.Media.BUCKET_ID,
                    MediaStore.Images.Media.DISPLAY_NAME};
    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private static final int REQUEST_TAKE_PHOTO = 102;
    public static String sortOrder = MediaStore.Images.Media.DATA + " DESC";
    private static final int URL_LOADER = 1;

    @BindView(R.id.camera_preview)
    FrameLayout preview;

    @BindView(R.id.image_grid)
    RecyclerView rv;

    @BindView(R.id.take_photo)
    RelativeLayout takePhoto;

    @BindView(R.id.upload_layout)
    LinearLayout uploadLayout;

    @BindView(R.id.upload_text)
    TextView uploadText;

    @BindView(R.id.upload_button)
    Button uploadButton;

    private Camera mCamera;
    private CameraPreview mCameraPreview;
    private GalleryPickerAdapter adapter;
    private GridLayoutManager glm;
    private Uri mCurrentPhotoPath;
    private Uri photoUri;
    private List<Uri> imageUploadQueue = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        ButterKnife.bind(this);
        showImages();
        getSupportLoaderManager().initLoader(URL_LOADER, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                if (id == URL_LOADER) {
                    return new CursorLoader(UploadActivity.this,
                            GalleryPickerAdapter.uri,
                            GalleryPickerAdapter.projections,
                            null,
                            null,
                            GalleryPickerAdapter.sortOrder);
                } else {
                    return null;
                }
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                adapter.setData(PhotosData.getData(false, data));
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mCameraPreview != null) mCameraPreview.getHolder().removeCallback(mCameraPreview);
        mCamera = getCameraInstance();
        mCameraPreview = new CameraPreview(this, mCamera);
        preview.addView(mCameraPreview);

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (Uri treeKey : adapter.selectedMap.keySet()) {
                    imageUploadQueue.add(adapter.selectedMap.get(treeKey));
                }

                uploadImage(0, imageUploadQueue.size());

            }
        });


    }

    public void showUpload(int selectedCount) {
        if (selectedCount > 0) {
            uploadLayout.setVisibility(View.VISIBLE);
            uploadText.setText("" + selectedCount + " Image(s) selected");
        } else {
            uploadLayout.setVisibility(View.GONE);
        }
    }

    private void showImages() {
        if (adapter == null) {
            adapter = new GalleryPickerAdapter(this);
        }
        glm = new GridLayoutManager(this, 3);
        rv.setLayoutManager(glm);
        rv.setAdapter(adapter);
    }


    private Camera getCameraInstance() {
        Camera camera = null;
        try {
            camera = Camera.open();
        } catch (Exception e) {
            // cannot get camera or does not exist
        }
        return camera;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            if (Build.VERSION.SDK_INT >= 24) {
                try {
                    Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                    m.invoke(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            photoUri = createImageFile();
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private Uri createImageFile() throws IOException {
        // Create an image file name

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = Uri.fromFile(image);
        return Uri.fromFile(image);
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(mCurrentPhotoPath);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            galleryAddPic();
            restartLoader();
        }
    }

    private void uploadImage(final int position, final int total) {

        if (position >= total || imageUploadQueue.size() <= 0) return;

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Uploading... 0% " + position + " of " + total);
        dialog.setCancelable(false);
        dialog.show();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference("documents");


        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        StorageReference riversRef = storageRef.child("images/" + imageUploadQueue.get(position).getLastPathSegment());
        UploadTask uploadTask = riversRef.putFile(Uri.parse("file://" + imageUploadQueue.get(position).toString()));

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(UploadActivity.this, "Upload Failed " + (position + 1) + " of " + total, Toast.LENGTH_SHORT).show();
                uploadImage(position + 1, total);
                dialog.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Toast.makeText(UploadActivity.this, "Upload Success " + (position + 1) + " of " + total, Toast.LENGTH_SHORT).show();
                dialog.dismiss();

                String key = databaseReference.push().getKey();
                databaseReference.child(key).child("url").setValue(taskSnapshot.getDownloadUrl().toString());

                if (position + 1 >= total) {
                    finish();
                } else {
                    uploadImage(position + 1, total);
                }
                //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                //System.out.println("Upload done " + downloadUrl);
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                DecimalFormat df = new DecimalFormat("#.##");
                dialog.setMessage("Uploading (" + (position + 1) + " of " + total + ") ... " + df.format(progress) + "% ");
            }
        });

    }

    public void restartLoader() {
        getSupportLoaderManager().restartLoader(URL_LOADER, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                if (id == URL_LOADER) {
                    return new CursorLoader(UploadActivity.this,
                            GalleryPickerAdapter.uri,
                            GalleryPickerAdapter.projections,
                            null,
                            null,
                            GalleryPickerAdapter.sortOrder);
                } else {
                    return null;
                }
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                adapter.setData(PhotosData.getData(false, data));
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {

            }
        });
    }


}
