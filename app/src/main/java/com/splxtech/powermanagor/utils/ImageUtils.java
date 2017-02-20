package com.splxtech.powermanagor.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by li300 on 2016/10/15 0015.
 */

public class ImageUtils {

    public static final int REQUEST_CODE_FROM_CAMERA = 5001;
    public static final int REQUEST_CODE_FROM_ALBUM = 5002;
    public static final int REQUEST_CODE_CROP = 5003;

    //存放拍照图片的uri地址
    private static Uri imageUriFromALBUM;
    private static Uri imageUriFromCamera;

    //记录是处于什么状态，拍照or相册
    private static int state = 0;

    //显示获取照片不同方式对话框
    public static void showImagePickDialog(final Context activity)
    {
        String title = "选择获取图片方式";
        String[] items = new String[]{"拍照","相册"};

        new AlertDialog.Builder(activity)
                .setTitle(title)
                .setItems(items, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog,int which)
                    {
                        dialog.dismiss();
                        switch (which)
                        {
                            case 0:
                                state = 1;
                                pickImageFromCamera((Activity) activity);
                                break;
                            case 1:
                                state = 2;
                                pickImageFromAlbum((Activity)activity);
                                break;
                            default:
                                break;
                        }
                    }
                })
                .show();
    }
    public static void pickImageFromAlbum(final Activity activity)
    {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        activity.startActivityForResult(intent,REQUEST_CODE_FROM_ALBUM);
    }

    public static void pickImageFromCamera(final Activity activity)
    {
        imageUriFromCamera = getImageUri();

        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUriFromCamera);
        activity.startActivityForResult(intent,REQUEST_CODE_FROM_CAMERA);
    }

    private static Uri getImageUri()
    {
        String imageName = new SimpleDateFormat("yyMMddHHmmss").format(new Date())+".jpg";
        String path = Environment.getExternalStorageDirectory() + "/PowerManager/image/" +imageName;
        return UriUtils.getUriFromFilePath(path);
    }

    public static void copyImageUri(Activity activity,Uri uri)
    {
        String imageName = new SimpleDateFormat("yyMMddHHmmss").format(new Date()) + ".jpg";
        String copyPath = Environment.getExternalStorageDirectory() + "/PowerManager/image/" + imageName;
        FileUtils.copyfile(UriUtils.getRealFilePath(activity,uri),copyPath,true);
        imageUriFromALBUM = UriUtils.getUriFromFilePath(copyPath);
    }

    public static void deleteImageUri(Context context, Uri uri){
        context.getContentResolver().delete(uri, null, null);
    }

    public static void cropImageUri(Activity activity, Uri uri, int outputX, int outputY){

        Intent intent = new Intent("com.android.camera.action.CROP");

        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection

        activity.startActivityForResult(intent, REQUEST_CODE_CROP);

    }
    public static Uri getCurrentUri(){

        if (state == 1){
            return imageUriFromCamera;
        }
        else if (state == 2){
            return imageUriFromALBUM;
        }
        else return null;
    }
}
