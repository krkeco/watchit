package com.krkeco.watchit;

import android.content.*;
import android.net.*;
import android.os.*;
import android.support.v4.app.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;
import com.google.android.youtube.player.*;
import com.krkeco.watchit.*;
import java.io.*;
import java.net.*;
import java.security.*;
import org.json.*;
import com.krkeco.watchit.YouTubeFailureRecoveryActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.OnInitializedListener;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;


public class GetReviewTask extends AsyncTask<String, Void, String[]>
{		public static String movieID;
		public GridView mGrid;
		public FrameLayout mTube;
		private Context mContext;
		private MainActivity mActivity;
		private YouTubePlayer YTPlayer;

		public static String[] perkyAuthor, perkyContent, perkyURL
		, trailerURL;

		public GetReviewTask(String id, MainActivity activity, GridView gv, FrameLayout fl){
				movieID = id;
				mGrid = gv;
				mTube = fl;
				mActivity = activity;
				mContext = mActivity.getApplicationContext();

		}

		@Override
		protected void onPostExecute(String[] result)
		{
				updateGridView(mGrid);
				super.onPostExecute(result);
		}

		@Override
		protected String[] doInBackground(String[] p1)
		{

				String reviewString = getJsonFromURI(getURI(movieID,"reviews"));

				try
				{
						return getMovieDataFromJSON(reviewString);
				}
				catch (JSONException e)
				{
						Log.e("akrkeco",e.getMessage()+"... epical failure retrieving JSON");
						e.printStackTrace();
						return null;
				}
		}

		public void updateGridView(GridView gridview){

				gridview.setAdapter(new PerkAdapter(mActivity,perkyAuthor,perkyContent));

				gridview.setOnItemClickListener(new OnItemClickListener() {

								@Override
								public void onItemClick(AdapterView<?> p1, View v, int pos, long p4)
								{	
										String url = perkyURL[pos];

										Intent i = new Intent(Intent.ACTION_VIEW);
										i.setData(Uri.parse(url));
										mActivity.startActivity(i);
								}
						});
		}




		public static String urltrailer;
		public void updateTubeLayout(FrameLayout frame){

				YouTubePlayerSupportFragment youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();

				for(int x = 0; x<trailerURL.length;x++){

						urltrailer = trailerURL[x];

						youTubePlayerFragment.initialize(MainActivity.youTubeAPI, new OnInitializedListener() {

										@Override
										public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {

												if (!wasRestored) {
														YTPlayer = player;
														YTPlayer.setFullscreen(false);
														YTPlayer.loadVideo(urltrailer);
														YTPlayer.play();
												}

										}

										@Override
										public void onInitializationFailure(Provider arg0, YouTubeInitializationResult arg1) {

										}
								});

				}
}


		private String getURI(String movieid,String type){

				Uri.Builder buildur = new Uri.Builder();
				buildur.scheme("http");
				buildur.authority("api.themoviedb.org")
						.appendPath("3")
						.appendPath("movie")
						.appendPath(movieid)
						.appendPath(type)
						.appendQueryParameter("api_key",MainActivity.apiKey);

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
						//Log.v("akrkeco",movieJsonString);

				} catch (IOException e) {
						Log.e("akrkeco", "Error ioexception api", e);
						movieJsonString = null;
				} finally{
						if (urlConnect != null) {
								urlConnect.disconnect();
						}
						if (reader != null) {
								try {
										reader.close();
								} catch (final IOException e) {
										Log.e("akrkeco", "Error stream api", e);
								}
						}
				}

				return movieJsonString;
		}

		private String[] getMovieDataFromJSON(String movieJsonString)
		throws JSONException{

				final String key_author= "author";
				final String key_content= "content";
				final String key_url= "url";

				JSONObject movieJson = new JSONObject(movieJsonString);
				int length = 0;
				JSONArray movieArray = null;

				movieArray = movieJson.getJSONArray("results");
				length = movieArray.length();


				String[] results = new String[length];

				perkyAuthor = new String[length];
				perkyContent= new String[length];
				perkyURL= new String[length];


				for(int m = 0; m< length;m++){
						JSONObject movie = null;
						movie = movieArray.getJSONObject(m);

						String author, content, url;

						author = movie.getString(key_author);
						content = movie.getString(key_content);
						url = movie.getString(key_url);
						perkyAuthor[m] = author;
						perkyContent[m] = content;
						perkyURL[m] = url;

				}
				return results;
		}

}

