package fr.stanyslasbres.picturetags.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fr.stanyslasbres.picturetags.R;
import fr.stanyslasbres.picturetags.adapter.ContactsAdapter;

public class ContactListFragment extends Fragment {

    public static ContactListFragment newInstance() {
        return new ContactListFragment();
    }

    private ContactsAdapter adapter;

    public ContactListFragment() {
        super();
        adapter = new ContactsAdapter();
    }

    public ContactsAdapter getAdapter() {
        return adapter;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.contacts_list_fragment, container, false);

        // create the recyclerView and attach the adapter
        RecyclerView eventList = v.findViewById(R.id.contact_list_recycler_view);
        eventList.setAdapter(adapter);
        eventList.setLayoutManager(new LinearLayoutManager(v.getContext()));

        return v;
    }
}
