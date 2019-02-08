package projetannuel.idc.masterinfo.unicaen.mobilenany;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import projetannuel.idc.masterinfo.unicaen.mobilenany.entities.Child;

public class DetailEnfantFragment extends Fragment {

    @BindView(R.id.dt_nom)
    TextView nom;

    @BindView(R.id.dt_prenom)
    TextView prenom;

    @BindView(R.id.dt_email)
    TextView email;

    @BindView(R.id.dt_adresse)
    TextView adresse;

    @BindView(R.id.dt_tel)
    TextView tel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_enfant, container, false);

        ButterKnife.bind(this, view);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            Child child = bundle.getParcelable("Child");

            this.nom.setText(this.nom.getText() + child.getNom());
            this.prenom.setText(this.prenom.getText() + child.getPrenom());
            this.email.setText(this.email.getText() + child.getEmail());
            this.adresse.setText(this.adresse.getText() + child.getAdresse());
            this.tel.setText(this.tel.getText() + child.getTel());
        }
        return view;
    }

    @OnClick(R.id.add_lieux_btn)
    void addLieu(){
        // Gerer le fragment d'ajout de lieux
    }
}
