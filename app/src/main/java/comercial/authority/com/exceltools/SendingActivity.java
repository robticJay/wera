package comercial.authority.com.exceltools;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SendingActivity extends AppCompatActivity {

    TextView textView;
    EditText editText;
    EditText editText1;
    EditText editText2;
    EditText editText3;
    EditText editText4;
    EditText editText5;
    EditText editText6;
    EditText editText7;
    EditText editText8;
    EditText editText9;
    EditText editText10;
    Button button, clear, retrieve;
    private static final String DB_URL = "jdbc:mysql://192.168.43.217/data_base_name_here";
    private static final String USER = "zzz";
    private static final String PASS = "1234";

    private ResultSet resultSet = null;
    private Statement statment = null;
    private Connection connect = null;
    private String TAG = "readDatabase";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sending);
        if (android.os.Build.VERSION.SDK_INT >=15) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        Toolbar toolbar =  findViewById(R.id.toolBar);
        toolbar.setTitle("Excel tool");
        setSupportActionBar(toolbar);


        textView = findViewById(R.id.textView);
        editText = findViewById(R.id.editText);
        editText1 = findViewById(R.id.editText1);
        editText2 = findViewById(R.id.editText2);
        editText3 = findViewById(R.id.editText3);
        editText4 = findViewById(R.id.editText4);
        editText5 = findViewById(R.id.editText5);
        editText6 = findViewById(R.id.editText6);
        editText7 = findViewById(R.id.editText7);
        editText8 = findViewById(R.id.editText8);
        editText9 = findViewById(R.id.editText9);
        editText10 = findViewById(R.id.editText10);

        button = findViewById(R.id.button);
        clear = findViewById(R.id.clear);
        retrieve = findViewById(R.id.retrieve);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Send objSend = new Send();
                objSend.execute("");
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editText.setText("");
                editText1.setText("");
                editText2.setText("");
                editText3.setText("");
                editText4.setText("");
                editText5.setText("");
                editText6.setText("");
                editText7.setText("");
                editText8.setText("");
                editText9.setText("");
                editText10.setText("");


            }
        });

        retrieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Send send = new Send();
                send.readDatabase();


            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.import_id:
                Intent intent = new Intent(SendingActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.export_id:
                Toast.makeText(this, "exporting", Toast.LENGTH_SHORT).show();

                changeDataIntoExcelFile();

                break;
            case R.id.aboutus_id:
                Toast.makeText(this, "contacting developers", Toast.LENGTH_SHORT).show();


            default:
                return super.onOptionsItemSelected(item);

        }
        return true;

    }

    private void changeDataIntoExcelFile() {


    }

    //writing and retrieving data are running in two diffrent threads in tis class
    /*
     @runInBackground() &  readDatabase();
    */



    private class Send extends AsyncTask<String, String, String> {


        String msg = "";
        String text = editText.getText().toString();
        String text1 = editText1.getText().toString();
        String text2 = editText2.getText().toString();
        String text3 = editText3.getText().toString();
        String text4 = editText4.getText().toString();
        String text5 = editText5.getText().toString();
        String text6 = editText6.getText().toString();
        String text7 = editText7.getText().toString();
        String text8 = editText8.getText().toString();
        String text9 = editText9.getText().toString();
        String text10 = editText10.getText().toString();


        @Override
        protected String doInBackground(String... strings) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
                if (con == null) {
                    msg = "connection goes wrong";
                } else {
                    String query = "INSERT INTO data_base_table_here(col_name_index,col_stud_name,col_mark_one,col_mark_two,col_mark_three,col_mark_four,col_mark_five,col_mark_six,col_mark_seven,col_mark_eight,col_mark_nine)" +
                            " VALUES('" + text + "','" + text1 + "','" + text2 + "','" + text3 + "','" + text4 + "','" + text5 + "','" + text6 + "','" + text7 + "','" + text8 + "','" + text9 + "','" + text10 + "');";
                    Statement statement = con.createStatement();
                    statement.execute(query);

                    msg = "insert successful";
                }
                con.close();
            } catch (ClassNotFoundException e) {
                msg = "problems my friend";
                e.printStackTrace();
            } catch (SQLException e) {
                msg = e.getMessage();
                e.printStackTrace();
            }


            return msg;
        }

        @Override
        protected void onPreExecute() {
            textView.setText("please wait data inserting");
        }

        @Override
        protected void onPostExecute(String s) {
            textView.setText(msg);
        }

        public void readDatabase() {
            Log.d(TAG,"READDATABASE is started");

            try {
                Thread thread = new Thread() {
                    @Override
                    public void run() {


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Class.forName("com.mysql.jdbc.Driver");
                                    connect = DriverManager.getConnection(DB_URL, USER, PASS);
                                    Statement statement = connect.createStatement();
                                    ResultSet resultSet = statement.executeQuery("SELECT * FROM data_base_table_here");

                                    //polpulate the edit text fields with data from the recent row

                                    while (resultSet.next()){
                                        String i = resultSet.getString(1);
                                        String s  = resultSet.getString(2);
                                        String o = resultSet.getString(3);
                                        String t = resultSet.getString(4);
                                        String th = resultSet.getString(5);
                                        String f = resultSet.getString(6);
                                        String fv = resultSet.getString(7);
                                        String sx = resultSet.getString(8);
                                        String sv = resultSet.getString(9);
                                        String e = resultSet.getString(10);
                                        String n = resultSet.getString(11);


                                        editText.setText(i);
                                        editText1.setText(s);
                                        editText2.setText(o);
                                        editText3.setText(t);
                                        editText4.setText(th);
                                        editText5.setText(f);
                                        editText6.setText(fv);
                                        editText7.setText(sx);
                                        editText8.setText(sv);
                                        editText9.setText(e);
                                        editText10.setText(n);



                                       // Toast.makeText(SendingActivity.this, "resultset"+" "+ i +" "+ s  +" "+  o  +" "+  t  +" "+  th  +" "+  f  +" "+  fv  +" "+  sx  +" "+  sv  +" "+  e  +" "+  n , Toast.LENGTH_SHORT).show();
                                    }


                                } catch (ClassNotFoundException e) {
                                    Log.d(TAG,e.getMessage());
                                    e.printStackTrace();
                                    Toast.makeText(SendingActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                    Toast.makeText(SendingActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                } finally {
                                    close();
                                }


                            }


                        });


                    }


                };
                thread.start();
            }catch (Exception e){
                Toast.makeText(SendingActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }


        private void close() {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statment != null) {
                    statment.close();
                }
                if (connect != null) {
                    connect.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        }


    }


