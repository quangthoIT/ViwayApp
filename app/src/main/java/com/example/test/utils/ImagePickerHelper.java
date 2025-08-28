package com.example.test.utils;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import java.io.*;

public class ImagePickerHelper {

    public static final int REQUEST_PICK_IMAGE = 1001;

    public interface ImagePickCallback {
        void onImagePicked(File file, Uri uri);
    }

    private static ImagePickCallback callback;

    public static void pickImageFromGallery(Activity activity, ImagePickCallback cb) {
        callback = cb;
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT); // Dùng cái này để tương thích Google Photos
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        activity.startActivityForResult(intent, REQUEST_PICK_IMAGE);
    }

    public static void handleActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null && callback != null) {
                try {
                    File file = copyUriToTempFile(activity, uri);
                    callback.onImagePicked(file, uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Copy ảnh từ Uri vào file tạm để xử lý OK với mọi URI
    private static File copyUriToTempFile(Activity activity, Uri uri) throws IOException {
        String fileName = getFileName(activity, uri);
        if (fileName == null) fileName = "temp_image.jpg";

        File tempFile = new File(activity.getCacheDir(), fileName);
        try (InputStream inputStream = activity.getContentResolver().openInputStream(uri);
             OutputStream outputStream = new FileOutputStream(tempFile)) {

            byte[] buffer = new byte[4096];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        }

        return tempFile;
    }

    // Lấy tên file gốc từ URI
    private static String getFileName(Activity activity, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = activity.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }
}
