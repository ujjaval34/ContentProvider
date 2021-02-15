package com.ujjaval.fetchcontactlist.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.ujjaval.fetchcontactlist.ContactList;
import com.ujjaval.fetchcontactlist.R;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

    private List<ContactList> finalList;
    private Context context;

    public Adapter(List<ContactList> finalList, Context context) {
        this.finalList = finalList;
        this.context = context;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.contact_list, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holders, int position) {
        final MyViewHolder holder = holders;
        ContactList model = finalList.get(position);

        holder.tvname.setText(model.getContactName());
        holder.tvphone.setText(model.getContactPhone());


    }

    @Override
    public int getItemCount() {
        return finalList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvname, tvphone;

        public MyViewHolder(View itemView) {
            super(itemView);

            tvname=itemView.findViewById(R.id.tvname);
            tvphone=itemView.findViewById(R.id.tvphone);




        }


    }
}
