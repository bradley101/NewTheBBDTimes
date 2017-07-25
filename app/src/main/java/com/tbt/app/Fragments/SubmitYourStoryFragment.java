package com.tbt.app.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.internal.http.multipart.MultipartEntity;
import com.android.internal.http.multipart.Part;
import com.tbt.app.Constants.Config;
import com.tbt.app.ListItems.NavListItem;
import com.tbt.app.R;
import com.tbt.app.Tools.NWManager;
import com.tbt.app.Tools.UserManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;
import static com.tbt.app.Constants.Config.APP_DIR;
import static com.tbt.app.Constants.Config.SERVER;
import static com.tbt.app.Constants.Config.SUB_DIR;
import static com.tbt.app.Constants.Config.UPLOAD_FILE;
import static com.tbt.app.Constants.Config.log;

/**
 * Created by bradley on 09-03-2017.
 */

public class SubmitYourStoryFragment extends Fragment {
    View v;
    ArrayList<NavListItem> list;
    ListView listView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.submit_story_fragment, container, false);
        initializeElements();
        initializeData();
        initializeAdapter();
        return v;
    }

    private void initializeAdapter() {
        listView.setAdapter(new SubmitListAdapter(getContext(), R.layout.submit_list_item));
    }

    private void initializeData() {
        list = new ArrayList<>();
        list.add(new NavListItem("Submit Article", R.drawable.ic_pen));
        list.add(new NavListItem("Submit Photograph", R.drawable.ic_add_a_photo));
    }

    private void initializeElements() {
        listView = (ListView) v.findViewById(R.id.submit_list_view);
        listView.setOnItemClickListener(new SubmitItemClickListener());
    }

    public class SubmitListAdapter extends ArrayAdapter<NavListItem> {
        Context context;
        int resource;

        public SubmitListAdapter(@NonNull Context context, @LayoutRes int resource) {
            super(context, resource);
            this.context = context;
            this.resource = resource;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.submit_list_item, parent, false);
            }
            NavListItem item = list.get(position);
            ImageView img = (ImageView) view.findViewById(R.id.submit_list_item_img);
            TextView name = (TextView) view.findViewById(R.id.submit_list_item_name);
            img.setImageDrawable(getResources().getDrawable(item.getImgRes()));
            name.setText(item.getName());
            return view;
        }
    }

    class SubmitItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            switch (i) { // Article
                case 0:
                    startActivityForResult(Intent.createChooser(intent, "Choose Document File"), 10000);
                    break;
                case 1:
                    startActivityForResult(Intent.createChooser(intent, "Choose Image File"), 10001);
                    break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri fileUri;
        String filePath = "";
        if (requestCode >= 10000 && requestCode <= 10001 && resultCode == RESULT_OK) {
            fileUri = data.getData();
            filePath = getPathFromUri(fileUri);

            switch (requestCode) {
                case 10000:
                    uploadFileToServer(filePath, Config.DOC_TYPES);
                    break;
                case 10001:
                    uploadFileToServer(filePath, Config.IMG_TYPES);
                    break;
            }
        }
    }

    void copyFileToNewDest(File src, File dst) {
        try {
            FileInputStream fis = new FileInputStream(src);
            FileOutputStream fos = new FileOutputStream(dst);
            FileChannel inChannel = fis.getChannel();
            FileChannel outChannel = fos.getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
            fis.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    boolean hasSymbols(String name) {
        if (name.indexOf(" ") != -1) return true;
        if (name.indexOf("`") != -1) return true;
        if (name.indexOf("~") != -1) return true;
        if (name.indexOf("!") != -1) return true;
        if (name.indexOf("@") != -1) return true;
        if (name.indexOf("#") != -1) return true;
        if (name.indexOf("$") != -1) return true;
        if (name.indexOf("%") != -1) return true;
        if (name.indexOf("^") != -1) return true;
        if (name.indexOf("&") != -1) return true;
        if (name.indexOf("*") != -1) return true;
        if (name.indexOf("(") != -1) return true;
        if (name.indexOf(")") != -1) return true;
        if (name.indexOf("+") != -1) return true;
        if (name.indexOf("=") != -1) return true;
        if (name.indexOf("{") != -1) return true;
        if (name.indexOf("[") != -1) return true;
        if (name.indexOf("}") != -1) return true;
        if (name.indexOf("]") != -1) return true;
        if (name.indexOf("\\") != -1) return true;
        if (name.indexOf("|") != -1) return true;
        if (name.indexOf(":") != -1) return true;
        if (name.indexOf(";") != -1) return true;
        if (name.indexOf("'") != -1) return true;
        if (name.indexOf("\"") != -1) return true;
        if (name.indexOf("<") != -1) return true;
        if (name.indexOf(",") != -1) return true;
        if (name.indexOf(">") != -1) return true;
        if (name.indexOf("/") != -1) return true;
        if (name.indexOf("?") != -1) return true;
        return false;
    }

    void uploadFileToServer(String filePath, String[] exts) {
        if (filePath == null) {
            Snackbar.make((View) getActivity().findViewById(R.id.main_frame_layout), "No file selected", Snackbar.LENGTH_LONG).show();
        } else {
            int i = filePath.lastIndexOf(".");
            String ext = filePath.substring(i + 1, filePath.length());
            boolean flag = false;
            for (String e : exts) {
                if (e.equals(ext)) {
                    flag = true;
                    break;
                }
            }
            if (flag) {
                File oldfile = new File(filePath);
//                if (hasSymbols(oldfile.getName())) {
//                    Snackbar.make((View) getActivity().findViewById(R.id.main_frame_layout), "Your file name containes spaces and symbols. Try removing them first", Snackbar.LENGTH_LONG).show();
//                    getActivity().onBackPressed();
//                    return;
//                }
                String oldFileName = oldfile.getName().toString();
                File f2 = new File("/storage/sdcard0/tmp" + oldFileName.substring(oldFileName.lastIndexOf(".")));
                copyFileToNewDest(oldfile, f2);
                final File file = f2;

                final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                final EditText caption = new EditText(getContext());
                alert.setView(caption);
                alert.setMessage("Enter upload caption");
                alert.setCancelable(false);
                alert.setPositiveButton("Upload", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (caption.getText().toString() == null || caption.getText().toString().equals(""))
                            caption.setText("");
                        Bitmap img = BitmapFactory.decodeFile(file.getAbsolutePath());
                        double ar = (img.getWidth() + 0.0) / img.getHeight();
                        double w = Resources.getSystem().getDisplayMetrics().widthPixels;
                        double h = w / ar;
                        Bitmap imgScaled = Bitmap.createScaledBitmap(img, (int) w, (int) h, false);

                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(file);
                            imgScaled.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } finally {
                            if (fos != null) {
                                try {
                                    fos.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        asyncTask.execute(file, caption.getText().toString());
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Snackbar.make((View) getActivity().findViewById(R.id.main_frame_layout), "File upload was cancelled", Snackbar.LENGTH_LONG).show();
                    }
                });
                alert.show();
            } else {
                Snackbar.make((View) getActivity().findViewById(R.id.main_frame_layout), "Unidentified file type", Snackbar.LENGTH_LONG).show();
            }
        }

    }

    AsyncTask<Object, Object, Object> asyncTask = new AsyncTask<Object, Object, Object>() {
        String id;
        ProgressDialog dialog;
        String response;
        File f;
        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(getContext());
            if (NWManager.isNetworkConnected(getContext())) {
                dialog.setMessage("Please wait while your file is being uploaded..");
                dialog.setCancelable(false);
                dialog.show();
                id = new UserManager(getContext()).getUserId();
            } else {
                cancel(true);
            }
        }

        @Override
        protected void onCancelled() {
            Snackbar.make((View) getActivity().findViewById(R.id.main_frame_layout), "Internet is not available", Snackbar.LENGTH_LONG).show();
        }

        @Override
        protected void onPostExecute(Object o) {
            dialog.dismiss();
            if (response == null) {
                Snackbar.make((View) getActivity().findViewById(R.id.main_frame_layout), "Error in uploading. Try again", Snackbar.LENGTH_LONG).show();
            } else {
                try {
                    JSONObject ob = new JSONObject(response);
                    if (ob.getString("code").equals("200")) {
                        Snackbar.make((View) getActivity().findViewById(R.id.main_frame_layout), "File was successfully uploaded. We will be replying shortly", Snackbar.LENGTH_LONG).show();
                        getActivity().onBackPressed();
                    } else {
                        Snackbar.make((View) getActivity().findViewById(R.id.main_frame_layout), "Error in uploading. Try again", Snackbar.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (f.exists()) {
                f.delete();
            }
        }

        @Override
        protected Object doInBackground(Object... objects) {
            File file = (File) objects[0];
            this.f = file;
            String caption = (String) objects[1];
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("user_id", id)
                    .addFormDataPart("caption", caption)
                    .addFormDataPart("file", file.getName(),
                            RequestBody.create(MediaType.parse(getMimeType(file.getAbsolutePath())), file))
//                                .addFormDataPart("bhai", "okhttp")
                    .build();
            Request request = new Request.Builder()
                    .url(SERVER + SUB_DIR + APP_DIR + UPLOAD_FILE)
                    .post(requestBody)
                    .build();
            try {
                response = (new OkHttpClient().newCall(request).execute()).body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    };

    String getPathFromUri(Uri uri) {
        String path = "";
        if (uri.getScheme().equalsIgnoreCase("content")) {
            String[] projection = {"_data"};
            Cursor cursor = null;
            try {
                cursor = getContext().getContentResolver().query(uri, projection, null, null, null);
                int colIdx = cursor.getColumnIndex("_data");
                if (cursor.moveToFirst()) {
                    path = cursor.getString(colIdx);
                }
            } catch (Exception e) {
            }
        } else if (uri.getScheme().equalsIgnoreCase("file")) {
            path = uri.getPath();
        }
        return path;
    }

    String getMimeType(String url) {
        String type = null;
        String ext = MimeTypeMap.getFileExtensionFromUrl(url);
        if (ext != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
        }
        return type;
    }
}
