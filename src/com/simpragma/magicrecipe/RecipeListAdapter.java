package com.simpragma.magicrecipe;

import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RecipeListAdapter extends BaseAdapter {

	private ArrayList<Recipe> listData;

	private LayoutInflater layoutInflater;

	public RecipeListAdapter(Context context, ArrayList<Recipe> listData) {
		this.listData = listData;
		layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return listData.size();
	}

	@Override
	public Object getItem(int position) {
		return listData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.recipe_puppy_item,
					null);
			holder = new ViewHolder();
			holder.titleView = (TextView) convertView.findViewById(R.id.title);
			holder.ingredientsView = (TextView) convertView
					.findViewById(R.id.ingredients);
			holder.imageView = (ImageView) convertView
					.findViewById(R.id.thumbImage);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Recipe recipe = (Recipe) listData.get(position);

		holder.titleView.setText(recipe.getTitle());
		holder.ingredientsView.setText("Ingredients: "
				+ recipe.getIngredients());

		if (holder.imageView != null) {
			new ThumbnailLoaderTask(holder.imageView).execute(recipe
					.getThumbnailUrl());
		}

		return convertView;
	}

	public void add(Recipe recipe) {
		this.listData.add(recipe);
	}
	
	public void clear() {
		this.listData.clear();
	}

	static class ViewHolder {
		TextView titleView;
		TextView ingredientsView;
		ImageView imageView;
	}

}
