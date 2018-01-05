package com.adilevi.phonealert;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder> {

    private List<String> names;
    private List<String> phones;
    private ContactsAdapterCallback callback;

    public interface ContactsAdapterCallback {
        void onItemRemoved(String name, String phone);
    }

    ContactsAdapter(ArrayList<String> contactNames, ArrayList<String> contactPhones, @NonNull ContactsAdapterCallback callback) {
        this.callback = callback;
        names = new ArrayList<>(contactNames);
        phones = new ArrayList<>(contactPhones);
    }

    public void addNewContact(String name, String phone) {
        names.add(name);
        phones.add(phone);
        notifyItemInserted(names.size() - 1);
    }

    @Override
    public ContactsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_item, parent, false);
        return new ContactsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactsViewHolder holder, int position) {
        holder.setData(names.get(position), phones.get(position));
    }

    @Override
    public int getItemCount() {
        return names.size();
    }


    class ContactsViewHolder extends RecyclerView.ViewHolder {

        private final TextView name;
        private final TextView phone;
        private final CheckBox checkbox;

        ContactsViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            phone = itemView.findViewById(R.id.phone);
            checkbox = itemView.findViewById(R.id.checkbox);
        }

        void setData(final String name, final String phone) {
            this.name.setText(name);
            this.phone.setText(phone);

            checkbox.setChecked(true);
            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    if (!checked) {
                        int position = getAdapterPosition();
                        names.remove(position);
                        phones.remove(position);
                        notifyItemRemoved(position);
                        callback.onItemRemoved(name, phone);
                    }
                }
            });
        }
    }
}
