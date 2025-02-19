
package com.example.filepicker;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.File;

public class FilePicker extends CordovaPlugin {
    private static final int FILE_SELECT_CODE = 1;
    private CallbackContext callbackContext;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
        if (action.equals("pickFile")) {
            this.callbackContext = callbackContext;
            pickFile();
            return true;
        }
        return false;
    }

    private void pickFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        cordova.startActivityForResult(this, Intent.createChooser(intent, "Select a file"), FILE_SELECT_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILE_SELECT_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                if (uri != null) {
                    String filePath = uri.toString();
                    callbackContext.success(filePath);
                } else {
                    callbackContext.error("File path not found");
                }
            }
        }
    }
}
