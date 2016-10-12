package com.krkeco.watchit;

import android.app.*;
import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.net.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;
import java.io.*;
import java.net.*;
import org.json.*;
import com.google.android.youtube.player.YouTubePlayer.*;
import com.google.android.youtube.player.*;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;


public class MainActivity extends FragmentActivity
{
		public static String apiKey = "";
		public static String youTubeAPI = "";
		
		public static boolean mTwoPane =  false;
		public static final String PREF_PLOT = "plot";
		public static final String PREF_RELEASE= "release";
		public static final String PREF_RATING= "rating";
		public static final String PREF_IMG= "imageurl";
		public static final String PREF_TITLE= "title";
		public static final String PREF_ID= "id";

		public static String[] detailPlot, detailRelease, detailRating;

		public static int[] detailID;

		public static String[] gridText = new String[] { 
				"No Movies yet, please wait or check your internet connection!",
		};

		public static String[] gridImgURL = new String[] { 
				"http://i.imgur.com/DvpvklR.png", 
		};

		public String sortTypeString = "popular";


		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
				MenuInflater inflater = getMenuInflater();
				inflater.inflate(R.menu.main, menu);

				return true;
		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
				switch (item.getItemId()) {
						case R.id.organize:
								organize();
								return true;
						case R.id.shoutout:
								shoutout();
								return true;
						default:
								return super.onOptionsItemSelected(item);
				}
		}


		public void toast(String string) {
				Context context = getApplicationContext();
				CharSequence text = string;
				Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
				toast.show();
		}
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);



        if (findViewById(R.id.main_detail_container) != null) {
            mTwoPane = true;
				} else {
            mTwoPane = false;
        }

				if(savedInstanceState == null){
				refreshMovies();
				}
    }

		public void refreshMovies(){

				if(isNetworkAvailable()){

						GetMoviesTask getMovies =new GetMoviesTask();
						getMovies.execute();
				}

		}

		private boolean isNetworkAvailable() {
				ConnectivityManager connectivityManager 
						= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
				return activeNetworkInfo != null && activeNetworkInfo.isConnected();
		}

		@Override
		public void onSaveInstanceState(Bundle outState)
		{
				int length = gridImgURL.length;
				String[] img = new String[length];
				String[] title= new String[gridImgURL.length];
				for(int x =0;x<gridImgURL.length;x++){
						img[x]="img"+x;
						title[x]="title"+x;
						outState.putString("sislength",""+gridImgURL.length);
						outState.putString(img[x],gridImgURL[x]);
						outState.putString(title[x],gridText[x]);
				}
				super.onSaveInstanceState(outState);
		}

		@Override
		protected void onRestoreInstanceState(Bundle savedInstanceState)
		{
				if(savedInstanceState != null){
						int length = Integer.parseInt(
						savedInstanceState .getString("sislength"));
						
						gridImgURL = new String[length];
						gridText = new String[length];

						
						for(int x =0;x<gridImgURL.length;x++){
								gridImgURL[x] =savedInstanceState.getString("img"+x);
								gridText[x] =savedInstanceState.getString("title"+x);
					
								}
								
								updateGridView();
				}
				
				
				super.onRestoreInstanceState(savedInstanceState);
		}


		
		public void updateGridView(){

				FrameLayout main;
				GridView gridview;
				if(mTwoPane == false){
				main = (FrameLayout) findViewById(R.id.det_container);
				main.removeViewAt(0);
				gridview = new GridView(this);//(GridView) findViewById(R.id.gridview);
						
				}else{
					main = (FrameLayout) findViewById(R.id.main_detail_container);
						gridview = (GridView) findViewById(R.id.gridview);//(GridView) findViewById(R.id.gridview);
						
				}
				

				gridview.setNumColumns(3);
				gridview.setAdapter(new ImageAdapter(this,gridText,gridImgURL));
				gridview.setOnItemClickListener(new OnItemClickListener() {
								public void onItemClick(AdapterView<?> parent, View v,
																				int pos, long id) {

										SharedPreferences	prefs = getSharedPreferences("pref", Context.MODE_PRIVATE);
										SharedPreferences.Editor editor = prefs.edit();

										editor.	putString(PREF_PLOT,detailPlot[pos]);
										editor.putString(PREF_RATING,detailRating[pos]);
										editor.putString(PREF_IMG,gridImgURL[pos]);
										editor.putString(PREF_TITLE,gridText[pos]);
										editor.putString(PREF_RELEASE,detailRelease[pos]);
										editor.putInt(PREF_ID,detailID[pos]);
										editor.commit();

										if(mTwoPane == false){
												
												FrameLayout main = (FrameLayout) findViewById(R.id.det_container);
												main.removeViewAt(0);
												
											
												 Fragment fg = new DetailMovie();
												 int container = R.id.det_container;
												 String tag = null;

												 getSupportFragmentManager()
												 .beginTransaction()
												 .add(container,fg,tag)
												 .addToBackStack("tag")
												 .commit();

										}else if(mTwoPane == true){
									
												Fragment fg = new DetailMovie();
												int container = R.id.main_detail_container;
												String tag = null;

												getSupportFragmentManager()
														.beginTransaction()
														.replace(container,fg,tag)
														.addToBackStack("tag")
														.commit();
										}
								}
						});

						if(mTwoPane == false){
						main.addView(gridview);

						}
		}

		@Override
		public void onBackPressed()
		{
				
			//	refreshMovies();
				updateGridView();
				// TODO: Implement this method
				super.onBackPressed();
		}
		
		
		
		

		public class GetMoviesTask extends AsyncTask<String, Void, String[]>
		{		
				@Override
				protected void onPostExecute(String[] result)
				{
						updateGridView();
						super.onPostExecute(result);
				}

				@Override
				protected String[] doInBackground(String[] p1)
				{
						if(!sortTypeString.equals("favorite")){
								String movieJsonString = getJsonFromURI(getURI());
								try
								{
										return getMovieDataFromJSON(movieJsonString);
								}
								catch (JSONException e)
								{
										Log.e("akrkeco",e.getMessage()+"... epical failure retrieving JSON");
										e.printStackTrace();
										return null;
								}
						}else{
								String[] favoriteString =	getFromDB();
								try
								{	
										return getMovieData(favoriteString);
								}
								catch (JSONException e)
								{
										Log.e("akrkeco",e.getMessage()+"... epical failure retrieving JSON");
										e.printStackTrace();
										return null;
								}
						}
				}

				public JSONArray jray;

				private String[] getFromDB(){

						SQLASync mDbHelper = new SQLASync(getApplicationContext());

						SQLiteDatabase dbr = mDbHelper.getReadableDatabase();
						String[] projection = {
								FavoriteContract.FavoriteColumns.MOVIE_ID,
						};
						String sortOrder =
								FavoriteContract.FavoriteColumns.MOVIE_ID+ " DESC";

						Cursor c = dbr.query(
								FavoriteContract.FavoriteColumns.TABLE_NAME,                     // The table to query
								projection,                            
								null,null,null,null,
								sortOrder);

						c.moveToFirst();

						int leng = c.getCount()-1;
						String[] aray = new String[leng];
						int count = 0;

						while(c.moveToNext()){

								if(c.getCount() != 0){

										int index = c.getColumnIndexOrThrow(
												FavoriteContract.FavoriteColumns.MOVIE_ID);
										int id = c.getInt(index);

										String test = getJsonFromURI(getURI(Integer.toString(id)));

										aray[count] = test;
										count +=1;
								}

						}
						return aray;
				}

				private String getURI(){
						Uri.Builder buildur = new Uri.Builder();
						buildur.scheme("http");
						buildur.authority("api.themoviedb.org")
								.appendPath("3")
								.appendPath("movie")
								.appendPath(sortTypeString)
								.appendQueryParameter("api_key",apiKey);

						return buildur.build().toString();

				}			private String getURI(String movieid){

						Uri.Builder buildur = new Uri.Builder();
						buildur.scheme("http");
						buildur.authority("api.themoviedb.org")
								.appendPath("3")
								.appendPath("movie")
								.appendPath(movieid)
								.appendQueryParameter("api_key",apiKey);

						return buildur.build().toString();

				}

				private String getJsonFromURI(String uri){

						String urlString = uri;

						HttpURLConnection urlConnect = null;
						BufferedReader reader = null;
						String movieJsonString = null;

						try{
								URL url = new URL(urlString);
								urlConnect = (HttpURLConnection) url.openConnection();
								urlConnect.setRequestMethod("GET");
								urlConnect.connect();

								InputStream inputStream = (InputStream) urlConnect.getInputStream();	
								StringBuffer buffer = new StringBuffer();

								if(inputStream == null){
										movieJsonString = null;
								}
								reader = new BufferedReader(new InputStreamReader(inputStream));

								String line;
								while((line = reader.readLine()) != null){
										buffer.append(line+"\n");
								}

								if(buffer.length() == 0){
										movieJsonString = null;
								}
								movieJsonString = buffer.toString();

						} catch (IOException e) {

								movieJsonString = null;
						} finally{
								if (urlConnect != null) {
										urlConnect.disconnect();
								}
								if (reader != null) {
										try {
												reader.close();
										} catch (final IOException e) {
										}
								}
						}

						return movieJsonString;
				}

				public GetMoviesTask(){
				}

				private String[] getMovieData(String[] movieJsonString)
				throws JSONException{

						final String key_title = "original_title";
						final String key_plot= "overview";
						final String key_imgurl= "poster_path";
						final String key_release= "release_date";
						final String key_rating= "vote_average";

						final String key_id = "id";
						int length = movieJsonString.length;
						JSONArray movieArray = null;

						String[] results = new String[length];

						gridImgURL = new String[length];
						gridText = new String[length];
						detailRelease= new String[length];
						detailRating = new String[length];
						detailPlot= new String[length];

						detailID = new int[length];

						for(int m = 0; m< length;m++){
								JSONObject movie = null;

								movie = new JSONObject(movieJsonString[m]);

								String title, plot, imgurl, release, rating;
								int id;

								title = movie.getString(key_title);
								plot = movie.getString(key_plot);
								imgurl = movie.getString(key_imgurl);
								release = movie.getString(key_release);
								rating = movie.getString(key_rating);

								id = Integer.parseInt(movie.getString(key_id));

								Uri.Builder buildur = new Uri.Builder();
								buildur.scheme("http");
								buildur.authority("image.tmdb.org")
										.appendPath("t")
										.appendPath("p")
										.appendPath("w500")
										.appendEncodedPath(imgurl);

								String fullUrl = 	buildur.build().toString();
								gridImgURL[m] = fullUrl;
								gridText[m] = title;
								detailPlot[m] = plot;
								detailRelease[m] = release;
								detailRating[m] = rating+"/10 ravenous sea monkies";

								detailID[m] = id;
								
							
						}
						return results;
				}
		}


		private String[] getMovieDataFromJSON(String movieJsonString)
		throws JSONException{

				final String key_title = "original_title";
				final String key_plot= "overview";
				final String key_imgurl= "poster_path";
				final String key_release= "release_date";
				final String key_rating= "vote_average";

				final String key_id = "id";

				JSONObject movieJson = new JSONObject(movieJsonString);
				int length = 0;
				JSONArray movieArray = null;

				movieArray = movieJson.getJSONArray("results");
				length = movieArray.length();


				String[] results = new String[length];

				gridImgURL = new String[length];
				gridText = new String[length];
				detailRelease= new String[length];
				detailRating = new String[length];
				detailPlot= new String[length];

				detailID = new int[length];

				for(int m = 0; m< length;m++){
						JSONObject movie = null;
						movie = movieArray.getJSONObject(m);

						String title, plot, imgurl, release, rating;
						int id;

						title = movie.getString(key_title);
						plot = movie.getString(key_plot);
						imgurl = movie.getString(key_imgurl);
						release = movie.getString(key_release);
						rating = movie.getString(key_rating);

						id = Integer.parseInt(movie.getString(key_id));


						Uri.Builder buildur = new Uri.Builder();
						buildur.scheme("http");
						buildur.authority("image.tmdb.org")
								.appendPath("t")
								.appendPath("p")
								.appendPath("w500")
								.appendEncodedPath(imgurl);

						String fullUrl = 	buildur.build().toString();
						gridImgURL[m] = fullUrl;
						gridText[m] = title;
						detailPlot[m] = plot;
						detailRelease[m] = release;
						detailRating[m] = rating+"/10 ravenous sea monkies";

						detailID[m] = id;
				}
				return results;
		}


		public void organize(){

				AlertDialog.Builder b = new AlertDialog.Builder(this);
				b.setTitle(getResources().getString(R.string.dialog_title));
				String[] sortingType = {getResources().getString(R.string.dialog_rating), 
						getResources().getString(R.string.dialog_popular), 
						getResources().getString(R.string.dialog_favorite)};

				b.setItems(sortingType, new AlertDialog.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int index) {

										dialog.dismiss();
										switch(index){
												case 0:
														sortTypeString = "top_rated";
														refreshMovies();
														updateGridView();

														break;
												case 1:
														sortTypeString = "popular";
														refreshMovies();
														updateGridView();

														break;
												case 2:
														sortTypeString = "favorite";
														refreshMovies();
														updateGridView();

														break;
										}
								}

						});

				b.show();
		}

		public void shoutout(){
				AlertDialog.Builder b = new AlertDialog.Builder(this);
				b.setTitle("Shoutouts!");
				String[] sortingType = {"This product uses the TMDb API but is not endorsed or certified by TMDb"
						, "StackExchange for all the awesome tips and advice and code snips"};
				b.setItems(sortingType, new AlertDialog.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int index) {

										dialog.dismiss();
								}});
				b.show();
		}


}
