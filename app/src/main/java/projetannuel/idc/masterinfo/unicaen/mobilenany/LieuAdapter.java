package projetannuel.idc.masterinfo.unicaen.mobilenany;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import projetannuel.idc.masterinfo.unicaen.mobilenany.entities.Area;
import projetannuel.idc.masterinfo.unicaen.mobilenany.entities.Child;

public class LieuAdapter extends RecyclerView.Adapter<LieuAdapter.MyViewHolder>{

    Context context;
    private List<Area> lieux;

    public LieuAdapter(Context context, List<Area> lieux) {
        this.context = context;
        this.lieux = lieux;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_lieu, parent,false);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLieuFragment(v);
            }
        });
        MyViewHolder myViewHolder = new MyViewHolder(v);
        return myViewHolder;
    }

    private void goToLieuFragment(View view) {

        FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        LieuxFragment lieuxFragment = new LieuxFragment();

        Bundle bundle = new Bundle();
        TextView lieuId = (TextView) view.findViewById(R.id.l_id);
//        Area lieu = lieux.get(Integer.valueOf(lieuId.getText().toString()));
//        bundle.putParcelable("Area", lieu);
//        lieuxFragment.setArguments(bundle);
        ft.replace(R.id.layout_container, lieuxFragment);
        ft.commit();
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Toast.makeText(context, "position : "+ position, Toast.LENGTH_SHORT).show();
        holder.id.setText(String.valueOf(position));
        holder.label.setText(lieux.get(position).getLabel());
        holder.category.setText(lieux.get(position).getCategory());
        holder.adresse.setText(lieux.get(position).getAdresse());
    }

    @Override
    public int getItemCount() {
        return lieux.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView id, label, category, adresse;

        public MyViewHolder(View view) {
            super(view);
            id = (TextView) view.findViewById(R.id.l_id);
            label = (TextView) view.findViewById(R.id.lieu_label);
            category = (TextView) view.findViewById(R.id.lieu_category);
            adresse = (TextView) view.findViewById(R.id.lieu_address);
        }
    }
}
