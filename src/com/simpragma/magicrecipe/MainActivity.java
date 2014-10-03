package com.simpragma.magicrecipe;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.simpragma.util.Constants;
import com.simpragma.util.WebServiceUtil;
import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private RecipeListAdapter recipeListAdapter;
	private ArrayList<Recipe> recipeItems;
	private int page;
	private boolean loadingMore = false;
	String item = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recipe_puppy);
		page = 1;
		final ListView recipeList = (ListView) findViewById(R.id.recipe_list);
		recipeItems = new ArrayList<Recipe>();
		recipeListAdapter = new RecipeListAdapter(this, recipeItems);
		recipeList.setAdapter(recipeListAdapter);

		final EditText filterText = (EditText) findViewById(R.id.search_box);
		Button searchButton = (Button) findViewById(R.id.searchButton);
		searchButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				item = filterText.getText().toString();
				if (item != null) {
					String param = "?i=" + item;
					getDataFromUrl(Constants.url + param);
					recipeListAdapter.clear();
				}

			}
		});

		recipeList.setOnItemClickListener(new OnItemClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Object recipe = recipeList.getItemAtPosition(position);
				Recipe newsData = (Recipe) recipe;
				if (newsData.getRecipeUrl() != null
						&& !newsData.getRecipeUrl().trim().equals("")) {

					if (WebServiceUtil.isConnectionAvailable(MainActivity.this)) {
						Intent i = new Intent(MainActivity.this,
								RecipeDetails.class);
						i.putExtra("url", newsData.getRecipeUrl());
						startActivity(i);
					} else {
						AlertDialog alertDialog = new AlertDialog.Builder(
								MainActivity.this).create();
						alertDialog.setMessage(getResources().getString(
								R.string.no_network_conn));
						alertDialog.setButton("OK",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.cancel();
									}
								});
						alertDialog.show();
					}

				}

			}

		});

		recipeList.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

				int lastInScreen = firstVisibleItem + visibleItemCount;
				if ((lastInScreen == totalItemCount) && !(loadingMore)
						&& totalItemCount > 9) {
					page++;
					String param = "?p=" + page;
					if (item != null) {
						param = "&i=" + item;
					}
					getDataFromUrl(Constants.url + param);
				}
			}
		});

		getDataFromUrl(Constants.url);
	}

	@SuppressWarnings("deprecation")
	private void getDataFromUrl(String url) {
		if (WebServiceUtil.isConnectionAvailable(MainActivity.this)) {
			new LoadData().execute(url);
		} else {
			AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
					.create();
			alertDialog.setMessage(getResources().getString(
					R.string.no_network_conn));
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
			alertDialog.show();
		}
	}

	private class LoadData extends AsyncTask<String, Void, JSONObject> {

		private ProgressDialog dialog = new ProgressDialog(MainActivity.this);

		protected void onPreExecute() {
			dialog.setMessage(getResources().getString(R.string.getting_data));
			dialog.show();
		}

		@Override
		protected JSONObject doInBackground(String... urls) {
			loadingMore = true;
			Log.d("MainActivity", "Url--->" + urls[0]);
			JSONObject serviceResult = WebServiceUtil
					.requestWebService(urls[0]);
			return serviceResult;
		}

		protected void onCancelled() {
			dialog.dismiss();
			Toast toast = Toast.makeText(MainActivity.this, getResources()
					.getString(R.string.error), Toast.LENGTH_LONG);
			toast.setGravity(Gravity.TOP, 25, 400);
			toast.show();

		}

		protected void onPostExecute(JSONObject content) {
			dialog.dismiss();
			getListData(content);

		}

	}

	private ArrayList<Recipe> getListData(JSONObject serviceResult) {

		try {

			if (serviceResult != null) {
				Log.d("MainActivity", "serviceResult=" + serviceResult);
				JSONArray items = serviceResult.getJSONArray("results");

				for (int i = 0; i < items.length(); i++) {
					JSONObject obj = items.getJSONObject(i);
					Recipe recipe = new Recipe();
					recipe.setTitle(obj.getString("title"));
					recipe.setIngredients(obj.getString("ingredients"));
					recipe.setRecipeUrl(obj.getString("href"));
					recipe.setThumbnailUrl(obj.getString("thumbnail"));
					recipeListAdapter.add(recipe);
				}
				recipeListAdapter.notifyDataSetChanged();
				loadingMore = false;
			} else {
				Toast toast = Toast.makeText(MainActivity.this, getResources()
						.getString(R.string.error), Toast.LENGTH_LONG);
				toast.setGravity(Gravity.TOP, 25, 400);
				toast.show();
			}

		} catch (JSONException e) {
			// handle exception
		}

		return recipeItems;
	}

}
