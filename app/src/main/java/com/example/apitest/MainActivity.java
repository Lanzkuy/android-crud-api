package com.example.apitest;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.apitest.RequestHandler;

public class MainActivity extends AppCompatActivity {

    private EditText etName, etEmail, etNoHp, etID;
    private Spinner spinKeterangan;
    private Button save;
    private ArrayList dataList;
    private ListView listView;
    private boolean isUpdating;
    final LoadingDialog loadingDialog=new LoadingDialog(MainActivity.this);
    public int CODE_POST_REQUEST = 1, CODE_GET_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etID=(EditText)findViewById(R.id.id);
        etName=(EditText)findViewById(R.id.etNama);
        etEmail=(EditText)findViewById(R.id.etEmail);
        etNoHp=(EditText)findViewById(R.id.etNoHp);
        spinKeterangan=(Spinner)findViewById(R.id.spinKeterangan);
        save=(Button)findViewById(R.id.btnAdd);
        listView=(ListView)findViewById(R.id.listview);
        dataList=new ArrayList<biodataModel>();

        readData();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isUpdating)
                {
                    updateData();
                }
                else
                {
                    createData();
                }
            }
        });
    }

    private void createData()
    {
        String nama = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String nohp = etNoHp.getText().toString().trim();
        String keterangan = spinKeterangan.getSelectedItem().toString();

        if (TextUtils.isEmpty(nama)) {
            etName.setError("Name Field Must Be Filled !");
            etName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email Field Must Be Filled !");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(nohp)) {
            etNoHp.setError("No.Hp Field Must Be Filled !");
            etNoHp.requestFocus();
            return;
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("nama", nama);
        params.put("email", email);
        params.put("nohp", nohp);
        params.put("keterangan", keterangan);

        PerformNetworkRequest request = new PerformNetworkRequest(API.CREATE_URL, params, CODE_POST_REQUEST);
        request.execute();
    }

    private void updateData() {
        String id = etID.getText().toString();
        String nama = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String nohp = etNoHp.getText().toString().trim();
        String keterangan = spinKeterangan.getSelectedItem().toString();


        if (TextUtils.isEmpty(nama)) {
            etName.setError("Please enter nama");
            etName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Please enter e-mail");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            etNoHp.setError("Please enter Nomor HP");
            etNoHp.requestFocus();
            return;
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("id", id);
        params.put("nama", nama);
        params.put("email", email);
        params.put("nohp", nohp);
        params.put("keterangan", keterangan);


        PerformNetworkRequest request = new PerformNetworkRequest(API.UPDATE_URL, params, CODE_POST_REQUEST);
        request.execute();

        save.setText("Add");

        etName.setText("");
        etEmail.setText("");
        etNoHp.setText("");
        spinKeterangan.setSelection(0);

        isUpdating = false;
    }


    private void readData() {
        PerformNetworkRequest request = new PerformNetworkRequest(API.GET_URL, null,CODE_GET_REQUEST );
        request.execute();
    }

    private void deleteData(int id) {
        PerformNetworkRequest request = new PerformNetworkRequest(API.DELETE_URL + id, null, CODE_GET_REQUEST);
        request.execute();
    }

    private class PerformNetworkRequest extends AsyncTask<Void, Void, String>
    {
        int requestCode;
        String url;
        HashMap<String, String> params;

        public PerformNetworkRequest(String url, HashMap<String, String> params, int requestCode ) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog.startLoadingDialog();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            loadingDialog.dismissDialog();
            try {
                JSONObject object = new JSONObject(s);
                if (!object.getBoolean("error")) {
                    Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();
                    refreshData(object.getJSONArray("heroes"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler requestHandler = new RequestHandler();
            if (requestCode == CODE_POST_REQUEST)
                return requestHandler.sendPostRequest(url, params);
            if (requestCode == CODE_GET_REQUEST)
                return requestHandler.sendGetRequest(url);
            return null;
        }
    }

    private void refreshData(JSONArray dataarray) throws JSONException {
        dataList.clear();

        for (int i = 0; i < dataarray.length(); i++) {
            //getting each hero object
            JSONObject obj = dataarray.getJSONObject(i);

            //adding to the list
            dataList.add(new biodataModel(
                    obj.getInt("id"),
                    obj.getString("name"),
                    obj.getString("email"),
                    obj.getString("nohp"),
                    obj.getString("status")
            ));
        }

        //creating the adapter and setting it to the listview
        DataAdapter adapter = new DataAdapter(dataList);
        listView.setAdapter(adapter);
    }

    class DataAdapter extends ArrayAdapter<biodataModel> {

        List<biodataModel> dataList;

        public DataAdapter(List<biodataModel> dataList) {
            super(MainActivity.this, R.layout.data_list, dataList );
            this.dataList = dataList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View listViewItem = inflater.inflate(R.layout.data_list, null, true);
            TextView textViewName = listViewItem.findViewById(R.id.textViewName);
            TextView textViewUpdate = listViewItem.findViewById(R.id.textViewUpdate);
            TextView textViewDelete = listViewItem.findViewById(R.id.textViewDelete);
            ImageView imgKeterangan= listViewItem.findViewById((R.id.imgKeterangan));

            final biodataModel data = dataList.get(position);

            if(data.getKeterangan().equals("Friend"))
            {
                imgKeterangan.setBackgroundResource(R.drawable.friends);
            }
            else if(data.getKeterangan().equals("Biasa Saja"))
            {
                imgKeterangan.setBackgroundResource(R.drawable.neutral);
            }
            else if(data.getKeterangan().equals("Bagaimana Ya?"))
            {
                imgKeterangan.setBackgroundResource(R.drawable.thinking);
            }
            else if(data.getKeterangan().equals("Nothing"))
            {
                imgKeterangan.setBackgroundResource(R.drawable.nothing);
            }

            textViewName.setText(data.getNama());
            textViewUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isUpdating = true;

                    etID.setText(String.valueOf(data.getId()));
                    etName.setText(data.getNama());
                    etEmail.setText(data.getNohp());
                    etNoHp.setText(data.getEmail());
                    spinKeterangan.setSelection(((ArrayAdapter<String>) spinKeterangan.getAdapter()).getPosition(data.getKeterangan()));

                    save.setText("Update");
                }
            });

            textViewDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                    builder.setTitle("Delete " + data.getNama())
                            .setMessage("Are you sure you want to delete it?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteData(data.getId());
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                }
            });

            return listViewItem;
        }
    }
}


