package com.hybridplay.game.android;

import java.util.ArrayList;

import com.hybridplay.shared.SharedFunctions;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SensorPositionsListAdapter extends BaseAdapter{
	private Context context;
	private ArrayList<String> sensorPositions;
	private LayoutInflater mInflator;
	private SharedPreferences prefs;

	public SensorPositionsListAdapter(Context context) {
		super();
		this.context = context;
		sensorPositions = new ArrayList<String>();
		mInflator =  ((Activity) this.context).getLayoutInflater();
		
		prefs = PreferenceManager.getDefaultSharedPreferences(this.context);
	}

	public void addPosition(String pos) {
		if (!sensorPositions.contains(pos)) {
			sensorPositions.add(pos);
		}
	}

	public String getPosition(int position) {
		return sensorPositions.get(position);
	}

	@Override
	public int getCount() {
		return sensorPositions.size();
	}

	@Override
	public String getItem(int i) {
		return sensorPositions.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	/*private view holder class*/
	private class ViewHolder {
		ImageView imageView;
		TextView txtTitle;
	}

	@Override
	@SuppressLint("InflateParams")
	public View getView(int i, View view, ViewGroup viewGroup) {
		ViewHolder holder = null;
		String sp;
		
		if (view == null){
			view = mInflator.inflate(R.layout.list_sensorposition, null);
			holder = new ViewHolder();
			holder.imageView = (ImageView) view.findViewById(R.id.sensorImage);
			holder.txtTitle = (TextView) view.findViewById(R.id.sensorDescription);
			view.setTag(holder);
		}else{
			holder = (ViewHolder) view.getTag();
		}
		
		if(!prefs.getString(SharedFunctions.HP_PREF_SENSOR_POSITION,"").isEmpty()){
			sp = prefs.getString(SharedFunctions.HP_PREF_SENSOR_POSITION,"");
			int sensorPos = Integer.parseInt(sp);
			if(i == sensorPos){
				view.setBackgroundColor(this.context.getResources().getColor(R.color.hp_yellow));
			}else{
				view.setBackgroundColor(this.context.getResources().getColor(R.color.absolute_white));
			}
		}

		holder.imageView.setImageResource(R.drawable.logo);
		holder.txtTitle.setText(sensorPositions.get(i));

		return view;
	}
}
