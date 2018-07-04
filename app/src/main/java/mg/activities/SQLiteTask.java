package mg.activities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class SQLiteTask extends AppCompatActivity{
    SQLiteDatabase db;
    Button insertBtn;
    Button selectBtn;
    Button updateBtn;
    Button deleteBtn;
    TableLayout tableLayout;
    TextView inputTV;
    EditText inputED;
    Button inputBtn;
    String newName;
    String newCity;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sqlite_task);
        insertBtn=findViewById(R.id.insertBtn);
        selectBtn=findViewById(R.id.selectBtn);
        updateBtn=findViewById(R.id.updateBtn);
        deleteBtn=findViewById(R.id.deleteBtn);
        tableLayout=findViewById(R.id.tableLayout);

        insertBtn.setOnClickListener(insertListner);
        selectBtn.setOnClickListener(selectListner);
        updateBtn.setOnClickListener(updateListner);
        deleteBtn.setOnClickListener(deleteListner);

        db=openOrCreateDatabase("myDB",MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS mytable(id int,name VARCHAR,city VARCHAR); ");
        FetchAllData();


    }
    View.OnClickListener updateListner=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AlertDialog.Builder builder=new AlertDialog.Builder(SQLiteTask.this);
            View box=getLayoutInflater().inflate(R.layout.input_data,null);
            final TextView tv=box.findViewById(R.id.input_tv);
            final EditText et=box.findViewById(R.id.input_ed);
            final Button btn=box.findViewById(R.id.inputBtn);
            tv.setText("Provide ID");
            et.setHint("Enter ID");
            et.setInputType(InputType.TYPE_CLASS_NUMBER);
            builder.setView(box);
            final AlertDialog alertDialog=builder.create();
            alertDialog.show();
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final String id = et.getText().toString().trim();

                    et.setInputType(InputType.TYPE_CLASS_TEXT);
                    et.setText("");
                    if(id!=null && !id.isEmpty()){
                        final String oldName;
                        final String oldCity;
                        Toast.makeText(SQLiteTask.this, id, Toast.LENGTH_SHORT).show();

                        Cursor cursor=db.rawQuery("select name,city from mytable where id="+Integer.parseInt(id),null);
                        if(cursor.moveToFirst()){
                            oldName=cursor.getString(0);
                            oldCity=cursor.getString(1);
                            Toast.makeText(SQLiteTask.this, oldName+" & "+oldCity, Toast.LENGTH_SHORT).show();
                            tv.setText("Change name from "+oldName+" to:");
                            et.setHint("Enter new name");
                            btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    newName=et.getText().toString().trim();
                                    et.setText("");
                                    if(newName!=null && !newName.isEmpty()){
                                        tv.setText("Change city from "+oldCity+" to:");
                                        et.setHint("Enter new city");
                                        btn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                newCity=et.getText().toString().trim();
                                                if(newCity!=null && !newCity.isEmpty()){
                                                    db.execSQL("update mytable set name=\""+newName+"\",city=\""+newCity+"\" where id="+Integer.parseInt(id));
                                                    Toast.makeText(SQLiteTask.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                                                    alertDialog.cancel();
                                                    recreate();
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        }
                        else
                        {Toast.makeText(SQLiteTask.this, "Invalid ID", Toast.LENGTH_SHORT).show();
                        alertDialog.cancel();}
                    }else
                        Toast.makeText(SQLiteTask.this, "Write Something", Toast.LENGTH_SHORT).show();

                }
            });
        }
    };
    View.OnClickListener deleteListner=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AlertDialog.Builder builder=new AlertDialog.Builder(SQLiteTask.this);
            View box=getLayoutInflater().inflate(R.layout.input_data,null);
            TextView tv=box.findViewById(R.id.input_tv);
            final EditText et=box.findViewById(R.id.input_ed);
            Button btn=box.findViewById(R.id.inputBtn);
            tv.setText("Provide ID");
            et.setInputType(InputType.TYPE_CLASS_NUMBER);
            et.setHint("Enter ID");
            builder.setView(box);
            final AlertDialog alertDialog=builder.create();
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.cancel();
                    String id=et.getText().toString().trim();
                    db.execSQL("delete from mytable where id="+id);
                    recreate();
                }
            });
            alertDialog.show();
        }
    };

    View.OnClickListener selectListner=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AlertDialog.Builder builder=new AlertDialog.Builder(SQLiteTask.this);
            View box=getLayoutInflater().inflate(R.layout.input_data,null);
            TextView tv=box.findViewById(R.id.input_tv);
            final EditText et=box.findViewById(R.id.input_ed);
            Button btn=box.findViewById(R.id.inputBtn);
            tv.setText("Provide Name");
            et.setHint("search name");
            builder.setView(box);
            final AlertDialog alertDialog=builder.create();
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.cancel();
                    String sname=et.getText().toString().trim();
                    Cursor resultset;
                    if(sname!=null && !sname.isEmpty())
                        resultset=db.rawQuery("select * from mytable where name=?",new String[]{sname});
                    else
                        resultset=db.rawQuery("select * from mytable",null);

                    tableLayout.removeViews(1,tableLayout.getChildCount()-1);
                    if (resultset.moveToFirst()) {
                        do{
                            String id=resultset.getInt(0)+"";
                            String name = resultset.getString(1);
                            String city = resultset.getString(2);
                            TableRow tableRow = new TableRow(SQLiteTask.this);
                            TextView tv0 = new TextView(SQLiteTask.this);
                            tv0.setText(id);
                            tv0.setBackground(getDrawable(R.drawable.td_style));
                            tv0.setTextColor(getColor(R.color.white));
                            tv0.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
                            tv0.setPadding(0,15,0,15);
                            tv0.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            TextView tv1 = new TextView(SQLiteTask.this);
                            tv1.setText(name);
                            tv1.setBackground(getDrawable(R.drawable.td_style));
                            tv1.setTextColor(getColor(R.color.white));
                            tv1.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
                            tv1.setPadding(0,15,0,15);
                            tv1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            TextView tv2 = new TextView(SQLiteTask.this);
                            tv2.setText(city);
                            tv2.setBackground(getDrawable(R.drawable.td_style));
                            tv2.setTextColor(getColor(R.color.white));
                            tv2.setPadding(0,15,0,15);
                            tv2.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
                            tv2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            tableRow.addView(tv0, 0);
                            tableRow.addView(tv1, 1);
                            tableRow.addView(tv2, 2);
                            tableLayout.addView(tableRow);
                        } while (resultset.moveToNext());
                    }

                }
            });
            alertDialog.show();

        }
    };



    void FetchAllData() {
        Cursor resultset = db.rawQuery("SELECT * FROM mytable", null);
        if (resultset.moveToFirst()) {
            do{
                String id=resultset.getInt(0)+"";
                String name = resultset.getString(1);
                String city = resultset.getString(2);
                TableRow tableRow = new TableRow(SQLiteTask.this);
                TextView tv0 = new TextView(SQLiteTask.this);
                tv0.setText(id);
                tv0.setBackground(getDrawable(R.drawable.td_style));
                tv0.setTextColor(getColor(R.color.white));
                tv0.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
                tv0.setPadding(0,15,0,15);
                tv0.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                TextView tv1 = new TextView(SQLiteTask.this);
                tv1.setText(name);
                tv1.setBackground(getDrawable(R.drawable.td_style));
                tv1.setTextColor(getColor(R.color.white));
                tv1.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
                tv1.setPadding(0,15,0,15);
                tv1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                TextView tv2 = new TextView(SQLiteTask.this);
                tv2.setText(city);
                tv2.setBackground(getDrawable(R.drawable.td_style));
                tv2.setTextColor(getColor(R.color.white));
                tv2.setPadding(0,15,0,15);
                tv2.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
                tv2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                tableRow.addView(tv0, 0);
                tableRow.addView(tv1, 1);
                tableRow.addView(tv2, 2);
                tableLayout.addView(tableRow);
            } while (resultset.moveToNext());
        }
    }
    View.OnClickListener insertListner=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            View boxView=getLayoutInflater().inflate(R.layout.input_data,null);
            inputTV=boxView.findViewById(R.id.input_tv);
            inputED=boxView.findViewById(R.id.input_ed);
            inputBtn=boxView.findViewById(R.id.inputBtn);


            AlertDialog.Builder builder=new AlertDialog.Builder(SQLiteTask.this);
            inputTV.setText("Insert Data");
            inputED.setHint("Enter Name");
            builder.setCancelable(true);
            builder.setView(boxView);
            final AlertDialog alertDialog=builder.create();

            alertDialog.show();
            alertDialog.setCanceledOnTouchOutside(true);

            inputBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String name=inputED.getText().toString().trim();
                    Toast.makeText(SQLiteTask.this, name, Toast.LENGTH_SHORT).show();
                    if(name!=null && !name.isEmpty()){
                        inputTV.setText("Enter City");
                        inputED.setText("");
                        inputBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String city=inputED.getText().toString().trim();
                                if(city!=null && !city.isEmpty()){
                                    alertDialog.cancel();
                                    int id;
                                    Cursor cursor=db.rawQuery("select max(id) from mytable",null);
                                    if(cursor.moveToFirst()){
                                        id=cursor.getInt(0)+1;
                                    }
                                    else
                                        id=1;
                                    db.execSQL("INSERT INTO mytable VALUES("+id+",\"" + name + "\",\"" + city + "\");");
                                    recreate();
                                }
                            }
                        });

                    }
                }
            });

        }
    };
}
