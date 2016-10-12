package com.krkeco.watchit;
import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.net.*;
import android.os.*;
import android.preference.*;
import android.support.v4.app.*;
import android.view.*;
import android.widget.*;
import android.widget.CompoundButton.*;
import com.google.android.youtube.player.*;
import com.google.android.youtube.player.YouTubePlayer.*;
import com.squareup.picasso.*;
import java.util.*;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;


public class DetailMovie extends Fragment{

		public static String title, release, rating, imgurl, plot,trailerURL;
		public static int movieID;

		public static TextView releaseTV, ratingTV,titleTV, plotTV;

		public static GridView mGrid;
		public static FrameLayout mTube;

		public Context mContext;

		public SQLiteDatabase dbw;

		public static SQLASync mDbHelper;
		

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
														 Bundle savedInstanceState) {

				mContext = getActivity().getApplicationContext();

				mDbHelper = new SQLASync(mContext);
				dbw = mDbHelper.getWritableDatabase();

				return inflater.inflate(R.layout.detail, container, false);
    }

		@Override
		public void onViewCreated(View view, Bundle savedInstanceState)
		{

				releaseTV = (TextView) getView().findViewById(R.id.detail_release);
				ratingTV = (TextView) getView().findViewById(R.id.detail_rating);
				titleTV = (TextView) getView().findViewById(R.id.detail_title);
				plotTV = (TextView) getView().findViewById(R.id.detail_plot);

				mGrid = (GridView) getView().findViewById(R.id.perk_gridview);
				mTube = (FrameLayout) getView().findViewById(R.id.youtube_fragment);

				setupConstants(this);

				CheckBox favBox = (CheckBox) getView().findViewById(R.id.detail_favorite);
				Cursor c = getCursor();
				boolean repeatID = false;

				while(c.moveToNext()){

						if(c.getCount() != 0){
								int index = c.getColumnIndexOrThrow(
										FavoriteContract.FavoriteColumns.MOVIE_ID);
								int id = c.getInt(index);
								if(id == movieID){										
										repeatID = true;
								}
						}
				}

				if(repeatID == true){
						favBox.setChecked(true);
				}else{
						favBox.setChecked(false);
				}

				favBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

								SharedPreferences	prefs = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
								SharedPreferences.Editor editor = prefs.edit();

								@Override
								public void onCheckedChanged(CompoundButton favbox, boolean isTrue)
								{
										int movieID= prefs.getInt(MainActivity.PREF_ID,-1);

										Cursor c = getCursor();
										boolean repeatID = false;

										String array ="";
										while(c.moveToNext()){

												if(c.getCount() != 0){
														int index = c.getColumnIndexOrThrow(
																FavoriteContract.FavoriteColumns.MOVIE_ID);
														int id = c.getInt(index);
														array+=id+" ";
														if(id == movieID){
																repeatID = true;
														}
												}
										}										

										if(isTrue){

												ContentValues values = new ContentValues();
												values.put(FavoriteContract.FavoriteColumns.MOVIE_ID, movieID);

												if(repeatID !=true){
														dbw.beginTransaction();
														//dbw.delete(FavoriteContract.FavoriteColumns.TABLE_NAME,null,null);
														dbw.insert(FavoriteContract.FavoriteColumns.TABLE_NAME, null, values);
														dbw.setTransactionSuccessful();
														dbw.endTransaction();

														toast("add to table"+movieID+" now: "+array);
												}
										}//is true
										else{

												ContentValues values = new ContentValues();
												values.put(FavoriteContract.FavoriteColumns.MOVIE_ID, movieID);

												if(repeatID ==true){
														dbw.beginTransaction();
														String[] whereArgs = new String[] { String.valueOf(movieID) };
														dbw.delete(FavoriteContract.FavoriteColumns.TABLE_NAME, FavoriteContract.FavoriteColumns.MOVIE_ID + "=" + movieID, null);
														dbw.setTransactionSuccessful();
														dbw.endTransaction();

														array ="";
														c = getCursor();
														c.moveToFirst();
														while(c.moveToNext()){

																if(c.getCount() != 0){
																		int index = c.getColumnIndexOrThrow(
																				FavoriteContract.FavoriteColumns.MOVIE_ID);
																		int id = c.getInt(index);
																		array+=id+" ";

																}
														}
														toast("delete from table"+movieID+" now: "+array);
												}
										}
								}//is clicked

						});

				populateReviews();
				populateYoutube();

				super.onViewCreated(view, savedInstanceState);
		}

		public static void setupConstants(Fragment solo){


				SharedPreferences	prefs = solo.getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = prefs.edit();

				releaseTV.setText(prefs.getString(MainActivity.PREF_RELEASE,"unknown date"));
				ratingTV.setText(prefs.getString(MainActivity.PREF_RATING,"unknown date"));
				titleTV.setText(prefs.getString(MainActivity.PREF_TITLE,"unknown date"));
				plotTV.setText(prefs.getString(MainActivity.PREF_PLOT,"unknown date"));



				movieID = prefs.getInt(MainActivity.PREF_ID,-1);

		}

		public static Cursor getCursor(){
				SQLiteDatabase dbr = mDbHelper.getReadableDatabase();
				String[] projection = {
						FavoriteContract.FavoriteColumns.MOVIE_ID,
				};
				String sortOrder =
						FavoriteContract.FavoriteColumns.MOVIE_ID+ " DESC";

				Cursor c = dbr.query(
						FavoriteContract.FavoriteColumns.TABLE_NAME,                     // The table to query
						projection,                               // The columns to return
						null,//selection,                                // The columns for the WHERE clause
						null,//selectionArgs,                            // The values for the WHERE clause
						null,                                     // don't group the rows
						null,                                     // don't filter by row groups
						sortOrder                                 // The sort order
				);
				return c;
		}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

				SharedPreferences	prefs = this.getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = prefs.edit();

				String newString;				
				if(savedInstanceState != null){

						imgurl = prefs.getString("imgurl", null);
				}

				if(imgurl == null){
						if (savedInstanceState == null) {
								Bundle extras = getActivity().getIntent().getExtras();
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

				Context context = getActivity().getApplicationContext();
				ImageView iv = (ImageView) getActivity().findViewById(R.id.detail_poster);
				Picasso.with(context)
						.load(imgurl).into(iv);

				super.onActivityCreated(savedInstanceState);
		}


		public void toast(String string) {
				Context context = getActivity().getApplicationContext();
				CharSequence text = string;
				Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
				toast.show();
		}


		public void getExtra(String staticString,String preString,String intent, TextView view,Bundle savedInstanceState){
				String newString;

				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
				SharedPreferences.Editor editor = prefs.edit();

				if(savedInstanceState != null){
						staticString = prefs.getString(preString, null);
				}

				if(staticString == null){
						if (savedInstanceState == null) {
								Bundle extras = getActivity().getIntent().getExtras();
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


		public void populateReviews(){

				if(isNetworkAvailable()){
						MainActivity acto = (MainActivity) getActivity();

						String stringID=Integer.toString(movieID);
						GetReviewTask getReviews =new GetReviewTask(stringID, acto,mGrid,mTube);
						getReviews.execute();
				}

		}

		public static List<String> trailerURLs = Arrays.asList(
				"DfYHeGD7WSM","NR8RdFg6Llc"
		);
		public void populateYoutube(){

				if(isNetworkAvailable()){
						MainActivity acto = (MainActivity) getActivity();

						String stringID=Integer.toString(movieID);
						GetTrailerTask getTrailers =new GetTrailerTask(stringID,acto,this);
						getTrailers.execute();



				}

		}

		private boolean isNetworkAvailable() {
				ConnectivityManager connectivityManager 
						= (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
				return activeNetworkInfo != null && activeNetworkInfo.isConnected();
		}

}
