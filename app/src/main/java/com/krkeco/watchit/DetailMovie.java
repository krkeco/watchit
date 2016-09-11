package com.krkeco.watchit;
import android.app.*;
import android.content.*;
import android.os.*;
import android.preference.*;
import android.view.*;
import android.widget.*;
import com.squareup.picasso.*;

public class DetailMovie extends Activity{

	public String title, release, rating, imgurl, plot;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);

		TextView ratingTV = (TextView) findViewById(R.id.detail_rating);
		getExtra(rating,"rating: ",Intent.EXTRA_SUBJECT,(TextView) ratingTV,savedInstanceState);

		TextView plotTV = (TextView) findViewById(R.id.detail_plot);
		getExtra(plot,"synopsis: ",Intent.EXTRA_TEXT,(TextView) plotTV,savedInstanceState);

		TextView titleTV = (TextView) findViewById(R.id.detail_title);
		getExtra(title," ",Intent.EXTRA_TITLE,(TextView) titleTV,savedInstanceState);

		TextView releaseTV = (TextView) findViewById(R.id.detail_release);
		getExtra(release,"release date: ",Intent.EXTRA_TEMPLATE,(TextView) releaseTV,savedInstanceState);

		String newString;

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = prefs.edit();

		if(savedInstanceState != null){
			imgurl = prefs.getString("imgurl", null);
		}

		if(imgurl == null){
			if (savedInstanceState == null) {
				Bundle extras = getIntent().getExtras();
				if(extras == null) {
					newString= null;
				} else {
					newString= extras.getString(Intent.EXTRA_ORIGINATING_URI);
				}
			} else {
				newString= (String) savedInstanceState.getSerializable(Intent.EXTRA_ORIGINATING_URI);
			}
			imgurl = newString;
		}
		editor.putString("imgurl",imgurl);
		editor.commit();

		Context context = getApplicationContext();
		ImageView iv = (ImageView) findViewById(R.id.detail_poster);
		Picasso.with(context)
			.load(imgurl).into(iv);
	}

	public void getExtra(String staticString,String preString,String intent, TextView view,Bundle savedInstanceState){
		String newString;

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = prefs.edit();

		if(savedInstanceState != null){
			staticString = prefs.getString(preString, null);
		}

		if(staticString == null){
			if (savedInstanceState == null) {
				Bundle extras = getIntent().getExtras();
				if(extras == null) {
					newString= null;
				} else {
					newString= extras.getString(intent);
				}
			} else {
				newString= (String) savedInstanceState.getSerializable(intent);
			}
			staticString = newString;
		}
		view.setText(preString+staticString);


		editor.putString(preString,staticString);
		editor.commit();
	}

    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.detail, container, false);
            return rootView;
        }
    }

}
