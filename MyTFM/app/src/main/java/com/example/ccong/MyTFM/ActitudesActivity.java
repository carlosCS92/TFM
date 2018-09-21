package com.example.ccong.MyTFM;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Actividad con el listado de actitudes
 */
public class ActitudesActivity extends AppCompatActivity {


    public class Item {
        boolean checked;
        Drawable ItemDrawable;
        String ItemString;
        Item(Drawable drawable, String t, boolean b){
            ItemDrawable = drawable;
            ItemString = t;
            checked = b;
        }

        public boolean isChecked(){
            return checked;
        }
    }

    static class ViewHolder {
        CheckBox checkBox;
        TextView text;
    }

    public class ItemsListAdapter extends BaseAdapter {

        private Context context;
        private List<Item> list;

        ItemsListAdapter(Context c, List<Item> l) {
            context = c;
            list = l;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public boolean isChecked(int position) {
            return list.get(position).checked;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View rowView = convertView;

            // reuse views
            ViewHolder viewHolder = new ViewHolder();
            if (rowView == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                rowView = inflater.inflate(R.layout.row, null);

                viewHolder.checkBox = (CheckBox) rowView.findViewById(R.id.rowCheckBox);
               // viewHolder.icon = (ImageView) rowView.findViewById(R.id.rowImageView);
                viewHolder.text = (TextView) rowView.findViewById(R.id.rowTextView);
                rowView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) rowView.getTag();
            }

            //viewHolder.icon.setImageDrawable(list.get(position).ItemDrawable);
            viewHolder.checkBox.setChecked(list.get(position).checked);

            final String itemStr = list.get(position).ItemString;
            viewHolder.text.setText(itemStr);

            viewHolder.checkBox.setTag(position);



            viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean newState = !list.get(position).isChecked();
                    list.get(position).checked = newState;

                }
            });

            viewHolder.checkBox.setChecked(isChecked(position));

            return rowView;
        }
    }

    Button btnLookup;
    Button back;
    List<Item> items;
    ListView listView;
    ItemsListAdapter myItemsListAdapter;
    Usuario user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actitudes);
        listView = (ListView)findViewById(R.id.actitudesList);
        btnLookup = (Button)findViewById(R.id.lookup);
        back = (Button)findViewById(R.id.settingBack);
        user = SaveInfo.load(this);

        initItems();
        myItemsListAdapter = new ItemsListAdapter(this, items);
        listView.setAdapter(myItemsListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(ActitudesActivity.this,
                        ((Item)(parent.getItemAtPosition(position))).ItemString,
                        Toast.LENGTH_LONG).show();
            }});

        btnLookup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modifyCharacters();

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view){
                backtoSetting();
            }
        });
    }

    private void backtoSetting() {
        Intent intent = new Intent(this, SettingActivity.class);
        intent.putExtra("usuario", true);
        startActivity(intent);
    }

    private void modifyCharacters() {
        ArrayList<String> selected = new ArrayList<>();
        for (int i = 0; i < items.size(); i++)
        {
            if(items.get(i).isChecked()) {
                selected.add(items.get(i).ItemString);
            }
        }

        this.user.setConocimientos(selected);

        SaveInfo.save(this.user, this);

        guardarAct();

        Intent intent = new Intent(this, MenuActivity.class);
        intent.putExtra("usuario", true);
        startActivity(intent);
    }

    public void guardarAct(){
        AsyncHttpClient client = new AsyncHttpClient( );
        String url = "http://adminweb.ddns.net/connect.php";
        RequestParams parametros = new RequestParams(  );
        parametros.put("operation", "act");
        parametros.put("ma", this.user.getEmail());
        parametros.put("con", this.user.getConocimientosString());


        client.post( url, parametros, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode == 200)
                {
                    Toast.makeText(getApplicationContext(), "Conocimientos guardados", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        } );
    }




    private void initItems(){
        items = new ArrayList<Item>();

        //TypedArray arrayDrawable = getResources().obtainTypedArray(R.array.resicon);
        TypedArray arrayText = getResources().obtainTypedArray(R.array.restext);

        for(int i=0; i<arrayText.length(); i++){
            //Drawable d = arrayDrawable.getDrawable(i);

            String s = arrayText.getString(i);

            boolean b = this.user.getConocimientos().contains(s);

            Item item = new Item(null, s, b);
            items.add(item);
        }

        //arrayDrawable.recycle();
        arrayText.recycle();
    }



}