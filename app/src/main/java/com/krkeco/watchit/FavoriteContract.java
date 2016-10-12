package com.krkeco.watchit;
import android.provider.*;

public final class FavoriteContract
{
		private FavoriteContract(){}
		
		public static class FavoriteColumns implements BaseColumns{
				public static final String TABLE_NAME = "entry";
				public static final String MOVIE_ID = "movie_id";
				
		}
}


