package projetannuel.idc.masterinfo.unicaen.mobilenany;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.os.Bundle;

import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {
    @BindView(R.id.nom)
    TextInputLayout nom;

    @BindView(R.id.prenom)
    TextInputLayout prenom;

    @BindView(R.id.adresse)
    TextInputLayout adresse;

    @BindView(R.id.telephone)
    TextInputLayout telephone;

    @BindView(R.id.email)
    TextInputLayout email;

    @BindView(R.id.password)
    TextInputLayout password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.register_btn)
    void register(){


    }
}
