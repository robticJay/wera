package comercial.authority.com.exceltools;

//CustomAdapter
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CustomAdapter extends BaseAdapter {
    Context mContext;
    ArrayList<XYValue> data;//modify here

    public CustomAdapter(Context mContext, ArrayList<XYValue> data) {
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();// # of items in your arraylist
    }
    @Override
    public Object getItem(int position) {
        return data.get(position);// get the actual item
    }
    @Override
    public long getItemId(int id) {
        return id;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(R.layout.item_layout, parent,false);//modify here
            viewHolder = new ViewHolder();
            //modify this block of code
            viewHolder.tvnames = (TextView) convertView.findViewById(R.id.tvX);
            viewHolder.tvemail = (TextView) convertView.findViewById(R.id.tvY);
            //Up to here
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //MODIFY THIS BLOCK OF CODE
        XYValue person = data.get(position);//modify here
        viewHolder.tvnames.setText(person.getX()+"");//modify here
        viewHolder.tvemail.setText(person.getY()+"");//modify here
        viewHolder.tvnames.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Touched", Toast.LENGTH_SHORT).show();
            }
        });
        return convertView;

    }
    static class ViewHolder {
        TextView tvnames;
        TextView tvemail;
    }

}
