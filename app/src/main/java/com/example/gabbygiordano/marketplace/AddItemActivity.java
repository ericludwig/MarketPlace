package com.example.gabbygiordano.marketplace;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.kosalgeek.android.photoutil.CameraPhoto;
import com.kosalgeek.android.photoutil.GalleryPhoto;
import com.kosalgeek.android.photoutil.ImageLoader;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import static com.example.gabbygiordano.marketplace.R.color.colorGold;


public class AddItemActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_MEDIA = 1 ;
    final int ACTIVITY_START_CAMERA = 1100;
    final int ACTIVITY_SELECT_FILE = 2200;
    private final String TAG = this.getClass().getName();

    CameraPhoto cameraPhoto;
    GalleryPhoto galleryPhoto;
    String selectedPhoto;

    EditText etItemName;
    EditText etItemDescription;
    EditText etItemPrice;
    ImageView ivAddImage;
    ImageView ivEditImage;
    Button ibPostItem;
    ImageView ivImage;
    RatingBar ratingBar;

    BottomNavigationView bottomNavigationView;

    Spinner itemType;
    ArrayAdapter<CharSequence> adaptertwo;

    final ArrayList<ImageView> addImage = new ArrayList<>();

    String condition;
    String type;
    ParseFile file;

    Item item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        getSupportActionBar().setTitle("Add Item to Marketplace");

        cameraPhoto = new CameraPhoto(getApplicationContext());
        galleryPhoto = new GalleryPhoto(getApplicationContext());

        // find view by id lookups
        etItemName = (EditText) findViewById(R.id.tvItemName);
        etItemDescription = (EditText) findViewById(R.id.tvItemDescription);
        etItemPrice = (EditText) findViewById(R.id.tvItemPrice);
        ivAddImage = (ImageView) findViewById(R.id.ivAddImage);
        ivEditImage = (ImageView) findViewById(R.id.ivEditImage);
        ibPostItem = (Button) findViewById(R.id.ibPostItem);
        ivImage = (ImageView) findViewById(R.id.ivImage);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigation);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);

        ivAddImage.bringToFront();
        ivEditImage.bringToFront();

        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(getResources().getColor(colorGold), PorterDuff.Mode.SRC_ATOP);

        ibPostItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean flag = false;

                String name = etItemName.getText().toString();
                String description = etItemDescription.getText().toString();
                String price = etItemPrice.getText().toString();

                if (name.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Enter item name", Toast.LENGTH_LONG).show();
                    flag = true;
                } else if (description.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Enter item description", Toast.LENGTH_LONG).show();
                    flag = true;
                } else if (price.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Enter item price", Toast.LENGTH_LONG).show();
                    flag = true;
                } else if (Double.valueOf(price) > 5000.0) {
                    Toast.makeText(getApplicationContext(), "Maximum price $5000", Toast.LENGTH_LONG).show();
                    flag = true;
                } else if (file == null ) {
                    Toast.makeText(getApplicationContext(), "Upload image file", Toast.LENGTH_LONG).show();
                    flag = true;
                } else if ((int) ratingBar.getRating() == 0) {
                    Toast.makeText(getApplicationContext(), "Provide item condition rating", Toast.LENGTH_LONG).show();
                    flag = true;
                } else {
                    int con = (int) ratingBar.getRating();
                    ParseUser currentUser = ParseUser.getCurrentUser();

                    item = new Item(name, description, price, con, currentUser, type, file);
                    item.setOwner(ParseUser.getCurrentUser());
                }

                if (!flag) {
                    onPostSuccess();
                }
            }
        });

        itemType = (Spinner) findViewById(R.id.spItemType);
        adaptertwo = ArrayAdapter.createFromResource(this, R.array.itemType_array, android.R.layout.simple_spinner_item);
        adaptertwo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemType.setAdapter(adaptertwo);
        itemType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                CharSequence cs = (CharSequence) itemType.getSelectedItem();
                type = (String) cs;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                switch (item.getItemId())
                {
                    case R.id.action_home:
                        //Toast.makeText(HomeActivity.this, "Home Tab Selected", Toast.LENGTH_SHORT).show();
                        Intent i_home = new Intent(AddItemActivity.this, HomeActivity.class);
                        startActivity(i_home);
                        break;


                    case R.id.action_notifications:
                        Intent i_notifications = new Intent(AddItemActivity.this, AppNotificationsActivity.class);
                        startActivity(i_notifications);
                        // Toast.makeText(HomeActivity.this, "Notifications Tab Selected", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.action_profile:
                        Intent i_profile = new Intent(AddItemActivity.this, ProfileActivity.class);
                        startActivity(i_profile);
                        //Toast.makeText(AddItemActivity.this, "Profile Tab Selected", Toast.LENGTH_SHORT).show();
                        break;
                }

                return false;
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_MEDIA);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_MEDIA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    public void onPostSuccess() {
        // save the item
        item.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Intent intent = new Intent();

                String id = item.getObjectId();
                intent.putExtra("item_id", id);
                String type = item.getType();
                intent.putExtra("type", type);
                //intent.putExtra("resource", item.getResource());

                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }


    public void addImage(View v)
    {
        final CharSequence[] items = {"Camera", "Gallery", "Cancel"};

        AlertDialog.Builder builder= new AlertDialog.Builder(AddItemActivity.this);
        builder.setTitle("Add Image");
        builder.setItems(items, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                if(items[i].equals("Camera"))
                {
                        Intent callCamera = new Intent();
                        callCamera.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(callCamera, ACTIVITY_START_CAMERA);
                }
                else if(items[i].equals("Gallery"))
                {
//                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                    intent.setType("image/*");
                    startActivityForResult(galleryPhoto.openGalleryIntent(), ACTIVITY_SELECT_FILE);
                }
                else if (items[i].equals("Cancel"))
                {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }

    public void editImage(View v) {
        final CharSequence[] items = {"Camera", "Gallery", "Cancel"};

        AlertDialog.Builder builder= new AlertDialog.Builder(AddItemActivity.this);
        builder.setTitle("Change Image");
        builder.setItems(items, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                if(items[i].equals("Camera"))
                {
                    Intent callCamera = new Intent();
                    callCamera.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(callCamera, ACTIVITY_START_CAMERA);
                }
                else if(items[i].equals("Gallery"))
                {
//                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                    intent.setType("image/*");
                    startActivityForResult(galleryPhoto.openGalleryIntent(), ACTIVITY_SELECT_FILE);
                }
                else if (items[i].equals("Cancel"))
                {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        if(resultCode == Activity.RESULT_OK)
        {
            if(requestCode == ACTIVITY_START_CAMERA)
            {
                //Toast.makeText(this, "picture was taken", Toast.LENGTH_SHORT).show();
                Bundle extras = data.getExtras();
                Bitmap photoCaptured = (Bitmap) extras.get("data");
                ivImage.setImageBitmap(photoCaptured);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                photoCaptured.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] image = stream.toByteArray();
                file = new ParseFile("itemimage.png", image);
                file.saveInBackground();
                Toast.makeText(AddItemActivity.this, "Image Uploaded",
                        Toast.LENGTH_SHORT).show();

                ivEditImage.setVisibility(View.VISIBLE);
                ivEditImage.setClickable(true);
            }
            else if(requestCode == ACTIVITY_SELECT_FILE)
            {
                Uri uri = data.getData();
                galleryPhoto.setPhotoUri(uri);

                String photoPath = galleryPhoto.getPath();
                selectedPhoto = photoPath;
                try
                {
                    Bitmap bitmap = ImageLoader.init().from(photoPath).requestSize(512,512).getBitmap();
                    ivImage.setImageBitmap(rotateBitmapOrientation(photoPath));
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] image = stream.toByteArray();
                    file = new ParseFile("itemimage.png", image);
                    file.saveInBackground();
                    Toast.makeText(AddItemActivity.this, "Image Uploaded",
                            Toast.LENGTH_SHORT).show();

                    ivEditImage.setVisibility(View.VISIBLE);
                    ivEditImage.setClickable(true);
                }
                catch (FileNotFoundException e)
                {
                    Toast.makeText(getApplicationContext(), "Something went wrong while uploading photo", Toast.LENGTH_SHORT).show();
                }

            }

        }
    }

    public Bitmap rotateBitmapOrientation(String photoFilePath) {
        // Create and configure BitmapFactory
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoFilePath, bounds);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap bm = BitmapFactory.decodeFile(photoFilePath, opts);
        // Read EXIF Data
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(photoFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
        int rotationAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;
        // Rotate Bitmap
        Matrix matrix = new Matrix();
        matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);
        // Return result
        return rotatedBitmap;
    }


    @Override
    public void onBackPressed() {
        Intent i_home = new Intent(AddItemActivity.this, HomeActivity.class);
        i_home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i_home);
    }

}
