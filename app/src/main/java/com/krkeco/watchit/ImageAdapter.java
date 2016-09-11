package com.krkeco.watchit;

import android.content.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.squareup.picasso.*;


public class ImageAdapter extends BaseAdapter{   
    String [] result;
    Context context;
	String [] imageId;
	private static LayoutInflater inflater=null;
    public ImageAdapter(MainActivity mainActivity, String[] prgmNameList, String[] prgmImages) {
        // TODO Auto-generated constructor stub
        result=prgmNameList;
        context=mainActivity;
        imageId=prgmImages;
		inflater = ( LayoutInflater )context.
			getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return result.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder
    {
        TextView tv;
        ImageView img;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;       
		rowView = inflater.inflate(R.layout.item, null);
		holder.tv=(TextView) rowView.findViewById(R.id.item_text);
		holder.img=(ImageView) rowView.findViewById(R.id.item_icon);       
		holder.tv.setText(result[position]);
		
		//ImageView iv = (ImageView) findViewById(R.id.detail_poster);
		Picasso.with(context)
			.load(imageId[position]).into(holder.img);
		 
        return rowView;
    }

	
}
