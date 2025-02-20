
package com.example.filepicker;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;

public class FilePicker extends CordovaPlugin {
    private static final int FILE_SELECT_CODE = 1;
    private CallbackContext callbackContext;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
        if (action.equals("pickFile")) {
            this.callbackContext = callbackContext;
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    pickFile();
                }
            });
            return true;
        }
        return false;
    }

    private void pickFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        
        try {
            cordova.startActivityForResult(this, Intent.createChooser(intent, "Select a file"), FILE_SELECT_CODE);
        } catch (Exception e) {
            callbackContext.error("Failed to open file picker: " + e.getMessage());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILE_SELECT_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    Uri uri = data.getData();
                    if (uri != null) {
                        try {
                            JSONObject result = new JSONObject();
                            result.put("uri", uri.toString());
                            
                            // Get file metadata
                            Cursor cursor = cordova.getActivity().getContentResolver()
                                .query(uri, null, null, null, null);
                                
                            if (cursor != null && cursor.moveToFirst()) {
                                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                                
                                String fileName = cursor.getString(nameIndex);
                                long fileSize = cursor.getLong(sizeIndex);
                                
                                result.put("fileName", fileName);
                                result.put("fileSize", fileSize);
                                
                                cursor.close();
                            }
                            
                            callbackContext.success(result);
                        } catch (JSONException e) {
                            callbackContext.error("Error creating result: " + e.getMessage());
                        }
                    } else {
                        callbackContext.error("File URI is null");
                    }
                } else {
                    callbackContext.error("No data received");
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                callbackContext.error("File selection cancelled");
            }
        }
    }
}
