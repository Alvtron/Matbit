package net.r3dcraft.matbit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 24.10.2017.
 */

public class AddRecipeFragmentPhoto extends Fragment {
    private static final String TAG = "AddRecipeFragmentPhoto";
    private Context context;
    private View view;
    private View header;
    private View bottomNavigation;
    private ViewPager viewPager;
    private AddRecipePagerAdapter pagerAdapter;
    private TextView txt_page_title;
    private ImageView btn_cancel;
    private ImageView btn_next;

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1553;
    private static final int SELECT_IMAGE_ACTIVITY_REQUEST_CODE = 1554;
    private String userChoosenTask;
    private Bitmap bitmap;
    byte[] byteArray;
    private ImageView img_recipe_image;
    private ImageView img_image_select;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        view = inflater.inflate(R.layout.fragment_add_recipe_photo, container, false);
        header = (View) view.findViewById(R.id.activity_add_recipe_header_photo);
        bottomNavigation = (View) view.findViewById(R.id.activity_add_recipe_bottom_navigator_photo);

        viewPager = (ViewPager) getActivity().findViewById(R.id.activity_add_recipe_viewpager);
        pagerAdapter = (AddRecipePagerAdapter) viewPager.getAdapter();
        txt_page_title = (TextView) header.findViewById(R.id.fragment_add_recipe_txt_page_title);
        txt_page_title.setText(pagerAdapter.ADD_PHOTO_TITLE);
        btn_cancel = (ImageView) header.findViewById(R.id.fragment_add_recipe_btn_cancel);
        btn_next = (ImageView) bottomNavigation.findViewById(R.id.fragment_add_recipe_btn_next);
        bottomNavigation.findViewById(R.id.fragment_add_recipe_btn_back).setVisibility(View.INVISIBLE);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
            }
        });

        // -----------------------------------------------------------------------------------------

        img_recipe_image = (ImageView) view.findViewById(R.id.activity_add_recipe_image_img);
        img_image_select = (ImageView) view.findViewById(R.id.activity_add_recipe_image_select);
        img_recipe_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        img_image_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (bitmap != null) {
            img_image_select.setVisibility(View.INVISIBLE);
            img_recipe_image.setImageBitmap(bitmap);
            pagerAdapter.setRecipePhoto(byteArray);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case CameraUtility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                }
                break;
        }
    }

    private void selectImage() {
        final CharSequence[] items = { "Ta bilde", "Velg bilde", "Avbryt" };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = CameraUtility.checkPermission(context);

                if (items[item].equals("Ta bilde")) {
                    userChoosenTask ="Ta bilde";
                    if(result)
                        cameraIntent();
                } else if (items[item].equals("Velg bilde")) {
                    userChoosenTask ="Velg bilde";
                    if(result)
                        galleryIntent();
                } else if (items[item].equals("Avbryt")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        Fragment frag = AddRecipeFragmentPhoto.this;
        frag.startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Fragment frag = AddRecipeFragmentPhoto.this;
        frag.startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK && data != null)
                adjustBitmap((Bitmap) data.getExtras().get("data"));
        }

        else if (requestCode == SELECT_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                try {
                    adjustBitmap(MediaStore.Images.Media.getBitmap(context.getApplicationContext().getContentResolver(), data.getData()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void adjustBitmap(Bitmap source_image) {
        int MAX_WIDTH = 1920, MAX_HEIGHT = 1080;
        int width = source_image.getWidth();
        int height = source_image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = MAX_WIDTH;
            height = (int) (width / bitmapRatio);
        } else {
            height = MAX_HEIGHT;
            width = (int) (height * bitmapRatio);
        }

        bitmap = Bitmap.createScaledBitmap(source_image, width, height, true);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
        byteArray = stream.toByteArray();

        bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }
}
