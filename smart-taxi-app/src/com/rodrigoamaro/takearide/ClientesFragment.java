package com.rodrigoamaro.takearide;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class ClientesFragment extends ListFragment{
    public static final String[] TITLES = 
        {
                "Henry IV (1)",   
                "Henry V",
                "Henry VIII",       
                "Richard II",
                "Richard III",
                "Merchant of Venice",  
                "Othello",
                "King Lear",
                "Henry IV (1)",   
                "Henry V",
                "Henry VIII",       
                "Richard II",
                "Richard III",
                "Merchant of Venice",  
                "Othello",
                "King Lear",
                "Henry IV (1)",   
                "Henry V",
                "Henry VIII",       
                "Richard II",
                "Richard III",
                "Merchant of Venice",  
                "Othello",
                "King Lear",
                "Henry IV (1)",   
                "Henry V",
                "Henry VIII",       
                "Richard II",
                "Richard III",
                "Merchant of Venice",  
                "Othello",
                "King Lear"
        };
        
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,TITLES));
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.i("FragmentList", "Item clicked: " + id);
    }
}
