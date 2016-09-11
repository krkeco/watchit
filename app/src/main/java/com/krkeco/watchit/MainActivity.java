package com.krkeco.watchit;

import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.preference.*;
import android.text.format.*;
import android.util.*;
import android.widget.*;
import com.squareup.picasso.*;
import java.io.*;
import java.net.*;
import java.text.*;
import org.json.*;
import android.widget.AdapterView.*;
import android.view.*;
import android.widget.ActionMenuView.*;

public class MainActivity extends Activity implements OnItemClickListener
{


	private final String apiKey = "";
	
	public void organize(){

		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle(getResources().getString(R.string.dialog_title));
		String[] sortingType = {getResources().getString(R.string.dialog_rating), getResources().getString(R.string.dialog_popular)};
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




	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);

		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
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

	@Override
	public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
	{
		// TODO: Implement this method
	}

	private MenuItem button;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		refreshMovies();
		updateGridView();

    }

	public void refreshMovies(){

		if(isNetworkAvailable()){
			GetMoviesTask getMovies =new GetMoviesTask();
			getMovies.execute();

		}

	}

	public void updateGridView(){

		GridView gridview = (GridView) findViewById(R.id.gridview);
		gridview.setAdapter(new ImageAdapter(this,gridText,gridImgURL));

		gridview.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View v,
										int pos, long id) {
					toast(gridText[pos]);
					Intent intent = new Intent(getApplicationContext(), DetailMovie.class)
						.putExtra(Intent.EXTRA_TEXT,detailPlot[pos])
						.putExtra(Intent.EXTRA_SUBJECT,detailRating[pos])//details
						.putExtra(Intent.EXTRA_ORIGINATING_URI,gridImgURL[pos])//high/low
						.putExtra(Intent.EXTRA_TITLE,gridText[pos])//high/low
						.putExtra(Intent.EXTRA_TEMPLATE,detailRelease[pos]);//day
					startActivity(intent);
				}
			});
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager 
			= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	public String[] detailPlot, detailRelease, detailRating;

	public String[] gridText = new String[] { 
		"No Movies yet, please wait or check your internet connection!",
	};

	public String[] gridImgURL = new String[] { 
		"http://i.imgur.com/DvpvklR.png", 
	};

	public String sortTypeString = "popular";


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
			Uri.Builder buildur = new Uri.Builder();
			buildur.scheme("http");
			buildur.authority("api.themoviedb.org")
				.appendPath("3")
				.appendPath("movie")
				.appendPath(sortTypeString)
				.appendQueryParameter("api_key",apiKey);

			String urlString = buildur.build().toString();

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
				Log.v("akrkeco",movieJsonString);

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

		}
		public GetMoviesTask(){
		}

		private String[] getMovieDataFromJSON(String movieJsonString)
		throws JSONException{
			final String key_title = "original_title";
			final String key_plot= "overview";
			final String key_imgurl= "poster_path";
			final String key_release= "release_date";
			final String key_rating= "vote_average";

			JSONObject movieJson = new JSONObject(movieJsonString);
			JSONArray movieArray = movieJson.getJSONArray("results");


			String[] results = new String[movieArray.length()];

			gridImgURL = new String[movieArray.length()];
			gridText = new String[movieArray.length()];
			detailRelease= new String[movieArray.length()];
			detailRating = new String[movieArray.length()];
			detailPlot= new String[movieArray.length()];

			for(int m = 0; m< movieArray.length();m++){

				JSONObject movie = movieArray.getJSONObject(m);

				String title, plot, imgurl, release, rating;

				title = movie.getString(key_title);
				plot = movie.getString(key_plot);
				imgurl = movie.getString(key_imgurl);
				release = movie.getString(key_release);
				rating = movie.getString(key_rating);

				Uri.Builder buildur = new Uri.Builder();
				buildur.scheme("http");
				buildur.authority("image.tmdb.org")
					.appendPath("t")
					.appendPath("p")
					.appendPath("w500")
					.appendEncodedPath(imgurl);

				String fullUrl = 	buildur.build().toString();
				log(fullUrl);
				gridImgURL[m] = fullUrl;
				gridText[m] = title;
				detailPlot[m] = plot;
				detailRelease[m] = release;
				detailRating[m] = rating+"/10 ravenous sea monkies";

			}
			return results;
		}
	}

	public void toast(String string) {
		Context context = getApplicationContext();
		CharSequence text = string;
		Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
		toast.show();
	}

	public void log(String text){
		Log.v("akrkeco",text);
	}


}
