package com.krkeco.watchit;

import android.content.*;
import android.net.*;
import android.os.*;
import android.util.*;
import android.widget.*;
import com.google.android.youtube.player.*;
import com.google.android.youtube.player.YouTubePlayer.*;
import java.io.*;
import java.net.*;
import java.util.*;
import org.json.*;
import android.support.v4.app.*;


public class GetTrailerTask extends AsyncTask<String, Void, String[]>
{		public static String movieID;
		private Fragment mFrag;
		private Context mContext;
		private MainActivity mActivity;
		private YouTubePlayer YTPlayer;

		public static String[] trailerURL;

		public GetTrailerTask(String id, MainActivity activity, Fragment frag){
				movieID = id;
				mFrag = frag;
				mActivity = activity;
				mContext = mActivity.getApplicationContext();

		}

		private static		List<String> trailerList = Arrays.asList(
				"dKrVegVI0Us","DfYHeGD7WSM"
		);//result;
		@Override
		protected void onPostExecute(String[] result)
		{
				trailerList= Arrays.asList(result);

				for(int x = 0; x<trailerList.size();x++){

						YouTubePlayerSupportFragment youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();

						youTubePlayerFragment.initialize(MainActivity.youTubeAPI, new OnInitializedListener() {

										@Override
										public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {

												if (!wasRestored) {
														YTPlayer = player;
														YTPlayer.setFullscreen(false);
														YTPlayer.loadVideos(trailerList);
														YTPlayer.play();
												}

										}

										@Override
										public void onInitializationFailure(Provider arg0, YouTubeInitializationResult arg1) {

										}
								});

						FragmentTransaction transaction = mFrag.getChildFragmentManager().beginTransaction();
						transaction.add(R.id.youtube_fragment, youTubePlayerFragment).commit();
				}
				super.onPostExecute(result);
		}

		@Override
		protected String[] doInBackground(String[] p1)
		{

				String reviewString = getJsonFromURI(getURI(movieID,"videos"));

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
		public static String urltrailer;

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

				final String key_url= "key";//this is actually not a url key but the youtube tag

				JSONObject movieJson = new JSONObject(movieJsonString);
				int length = 0;
				JSONArray movieArray = null;

				movieArray = movieJson.getJSONArray("results");
				length = movieArray.length();


				trailerURL= new String[length];


				for(int m = 0; m< length;m++){
						JSONObject movie = null;
						movie = movieArray.getJSONObject(m);

						String url;

						url = movie.getString(key_url);
						trailerURL[m] = url;
				}
				return trailerURL;
		}

}

