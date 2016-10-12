package com.krkeco.watchit;

import android.content.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.squareup.picasso.*;


public class PerkAdapter extends BaseAdapter{   
    String [] author;
    Context mContext;
		String [] content;
		private static LayoutInflater inflater=null;
    public PerkAdapter(MainActivity mainActivity, String[] auth, String[] cont) {

				author=auth;
        mContext=mainActivity;
        content=cont;
				inflater = ( LayoutInflater )mContext.
						getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return author.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder
    {
        TextView authorholder;
        TextView contentholder;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder=new Holder();
        View rowView;       
				rowView = inflater.inflate(R.layout.perkyitem, null);
				holder.authorholder=(TextView) rowView.findViewById(R.id.perk_author);
				holder.authorholder.setText(author[position]);
				holder.contentholder=(TextView) rowView.findViewById(R.id.perk_content);
				String preview;
				if(content[position].length()>400){
						preview =content[position].substring(0,400)+"..."+"\n"+"click to see full review";
				}else{
						preview =content[position];
				}
				holder.contentholder.setText(preview);

        return rowView;
    }


}
