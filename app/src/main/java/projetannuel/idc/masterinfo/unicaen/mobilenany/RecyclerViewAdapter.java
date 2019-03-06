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
import projetannuel.idc.masterinfo.unicaen.mobilenany.entities.Child;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>{

    Context context;
    private List<Child> children;

    public RecyclerViewAdapter(Context context, List<Child> children) {
        this.context = context;
        this.children = children;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_child, parent,false);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToDetailEnfant(v);
            }
        });
        MyViewHolder myViewHolder = new MyViewHolder(v);
        return myViewHolder;
    }

    private void goToDetailEnfant(View view) {

        FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        DetailEnfantFragment detailEnfantFragment = new DetailEnfantFragment();

        Bundle bundle = new Bundle();
        TextView childId = (TextView) view.findViewById(R.id.id);
        Child child = children.get(Integer.valueOf(childId.getText().toString()));
        bundle.putParcelable("Child", child);
        detailEnfantFragment.setArguments(bundle);
        ft.replace(R.id.layout_container, detailEnfantFragment);
        ft.commit();
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Toast.makeText(context, "position : "+ position, Toast.LENGTH_SHORT).show();
        holder.id.setText(String.valueOf(position));
        holder.nom.setText(children.get(position).getNom());
        holder.prenom.setText(children.get(position).getPrenom());
        holder.adresse.setText(children.get(position).getAdresse());
    }

    @Override
    public int getItemCount() {
        return children.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView id, nom, prenom, adresse;

        public MyViewHolder(View view) {
            super(view);
            id = (TextView) view.findViewById(R.id.id);
            nom = (TextView) view.findViewById(R.id.child_name);
            prenom = (TextView) view.findViewById(R.id.child_first_name);
            adresse = (TextView) view.findViewById(R.id.child_address);
        }
    }
}
