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
import android.support.annotation.NonNull;
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
 *
 * This is one of the fragments initialized in the AddRecipePagerAdapter. This collects a photo in
 * a byte array and stores it in the adapter.
 */

public class AddRecipeFragmentPhoto extends Fragment {
    private static final String TAG = "AddRecipeFragmentPhoto";
    private Context context;
    private ViewPager viewPager;
    private AddRecipePagerAdapter pagerAdapter;

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1553;
    private static final int SELECT_IMAGE_ACTIVITY_REQUEST_CODE = 1554;
    private String userChosenTask;

    ByteArrayOutputStream stream;
    private ImageView img_recipe_photo;
    private ImageView img_image_select;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Default layout initialization -----------------------------------------------------------

        context = getActivity();
        View view = inflater.inflate(R.layout.fragment_add_recipe_photo, container, false);
        View header = view.findViewById(R.id.activity_add_recipe_header_photo);
        View bottomNavigation = view.findViewById(R.id.activity_add_recipe_bottom_navigator_photo);

        viewPager = getActivity().findViewById(R.id.activity_add_recipe_viewpager);
        pagerAdapter = (AddRecipePagerAdapter) viewPager.getAdapter();
        TextView txt_page_title = header.findViewById(R.id.fragment_add_recipe_txt_page_title);
        txt_page_title.setText(pagerAdapter.ADD_PHOTO_TITLE);
        ImageView btn_cancel = header.findViewById(R.id.fragment_add_recipe_btn_cancel);
        ImageView btn_next = bottomNavigation.findViewById(R.id.fragment_add_recipe_btn_next);
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

        ImageView btn_delete = header.findViewById(R.id.fragment_add_recipe_btn_delete);
        if (pagerAdapter.getRecipe().getId() == null || pagerAdapter.getRecipe().getId().isEmpty()) {
            btn_delete.setVisibility(View.GONE);
        }
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show dialog box for next step
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.string_delete_recipe)
                        .setCancelable(false)
                        .setMessage(R.string.string_are_you_sure_you_want_to_delete_this_recipe)
                        .setIcon(R.drawable.icon_delete_black_24dp)
                        .setPositiveButton(R.string.string_delete, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                MatbitDatabase.deleteRecipe(pagerAdapter.getRecipe().getId());
                                startActivity(new Intent(getActivity(), MainActivity.class));
                                getActivity().finish();
                            }

                        })
                        .setNegativeButton(getResources().getString(R.string.string_cancel), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.show();
            }
        });

        // -----------------------------------------------------------------------------------------

        // Initialize views
        img_recipe_photo = view.findViewById(R.id.activity_add_recipe_image_img);
        img_image_select = view.findViewById(R.id.activity_add_recipe_image_select);
        img_recipe_photo.setOnClickListener(new View.OnClickListener() {
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
        if (stream != null)
            pagerAdapter.setRecipePhoto(stream.toByteArray());
        if (pagerAdapter.getRecipePhoto() != null) {
            img_image_select.setVisibility(View.INVISIBLE);
            img_recipe_photo.setImageBitmap(BitmapFactory.decodeByteArray(
                    pagerAdapter.getRecipePhoto(),
                    0,
                    pagerAdapter.getRecipePhoto().length)
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionUtility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChosenTask.equals(getString(R.string.string_take_picture)))
                        cameraIntent();
                    else if(userChosenTask.equals(getString(R.string.string_choose_picture)))
                        galleryIntent();
                }
                break;
        }
    }

    /**
     * Create alert dialog and prompt user with the choice to choose photo from gallery or camera.
     */
    private void selectImage() {
        final CharSequence[] items = {
                getString(R.string.string_take_picture),
                getString(R.string.string_choose_picture),
                getString(R.string.string_cancel)
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.string_add_photo);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = PermissionUtility.checkExternalStoragePermission(context);

                if (items[item].equals(getString(R.string.string_take_picture))) {
                    userChosenTask = getString(R.string.string_take_picture);
                    if(result)
                        cameraIntent();
                } else if (items[item].equals(getString(R.string.string_choose_picture))) {
                    userChosenTask = getString(R.string.string_choose_picture);
                    if(result)
                        galleryIntent();
                } else if (items[item].equals(getString(R.string.string_cancel))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    /**
     * Create new phone gallery intent and start it.
     */
    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        Fragment frag = AddRecipeFragmentPhoto.this;
        frag.startActivityForResult(Intent.createChooser(intent, getString(R.string.string_choose_picture)), SELECT_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    /**
     * Create new camera intent and start it.
     */
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
                try {
                    adjustBitmap((Bitmap) data.getExtras().get("data"));
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
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

    /**
     * Adjust provided photo. Resize and compress it.
     * @param source_image provided photo
     */
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

        Bitmap bitmap_recipe_photo = Bitmap.createScaledBitmap(source_image, width, height, true);
        stream = new ByteArrayOutputStream();
        bitmap_recipe_photo.compress(Bitmap.CompressFormat.PNG, 50, stream);
    }

    public ImageView getImg_recipe_photo() {
        return img_recipe_photo;
    }
}
