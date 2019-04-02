package projetannuel.idc.masterinfo.unicaen.mobilenany;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.loader.content.CursorLoader;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import projetannuel.idc.masterinfo.unicaen.mobilenany.entities.ApiError;
import projetannuel.idc.masterinfo.unicaen.mobilenany.entities.Area;
import projetannuel.idc.masterinfo.unicaen.mobilenany.entities.Child;
import projetannuel.idc.masterinfo.unicaen.mobilenany.entities.Photo;
import projetannuel.idc.masterinfo.unicaen.mobilenany.network.ApiService;
import projetannuel.idc.masterinfo.unicaen.mobilenany.network.RetrofitBuilder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddChildPhotoFragment extends Fragment {

    private static final String TAG = "AddChildPhotoFragment";
    @BindView(R.id.child_item_img)
    ImageView childImg;

    @BindView(R.id.child_btn_validate)
    MaterialButton upload;

    Child child;
    ApiService service;
    TokenManager tokenManager;
    Call<Photo> call;
    String imagePath;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_child_photo, container, false);
        ButterKnife.bind(this, view);

        tokenManager = TokenManager.getInstance(this.getActivity().getSharedPreferences("prefs", getContext().MODE_PRIVATE));
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class,tokenManager);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            child = bundle.getParcelable("Child");
        }

        return view;
    }

    @OnClick(R.id.child_btn_img)
    void getGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 0);
        this.upload.setEnabled(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null){
            Toast.makeText(getActivity(), "Impossible de recupérer une image",Toast.LENGTH_SHORT).show();
            return;
        }

        Uri uri = data.getData();
        imagePath =this.getPathFromUri(uri);
        childImg.setImageBitmap(BitmapFactory.decodeFile(imagePath));
    }

    private String getPathFromUri(Uri uri){
        String[] projection = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(getActivity().getApplicationContext(),uri, projection, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int columnIdx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(columnIdx);
        cursor.close();
        return result;
    }

    @OnClick(R.id.child_btn_validate)
    void upload(){
        File file = new File(imagePath);
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image_url", file.getName(), requestBody);

        call = service.uploadChildPhoto(body, child.getId());
        call.enqueue(new Callback<Photo>() {
            @Override
            public void onResponse(Call<Photo> call, Response<Photo> response) {
                Log.w(TAG, "onResponse: " + response);
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), "Image Téléchargée avec succès", Toast.LENGTH_SHORT).show();
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    DetailEnfantFragment detailEnfantFragment = new DetailEnfantFragment();
                    Bundle bundle = new Bundle();
                    child.setImageUrl(response.body().getImage());
                    bundle.putParcelable("Child", child);
                    detailEnfantFragment.setArguments(bundle);
                    ft.replace(R.id.layout_container, detailEnfantFragment);
                    ft.commit();

                }
            }

            @Override
            public void onFailure(Call<Photo> call, Throwable t) {
                Log.w(TAG, "onFailure " + t.getMessage());
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (call != null){
            call.cancel();
            call = null;
        }
    }

}
