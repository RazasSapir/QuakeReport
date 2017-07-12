package com.example.android.quakereport;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.graphics.drawable.GradientDrawable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.DecimalFormat;


import java.util.ArrayList;

public class EarthquakeAdapter extends ArrayAdapter<Earthquake> {

    public EarthquakeAdapter(Context context, ArrayList<Earthquake> earthquakes) {
        super(context, 0, earthquakes);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.earthquake_item, parent, false);
        }
        Earthquake currEarthquake = getItem(position);
        TextView magTextView = (TextView) listItemView.findViewById(R.id.mag_text_view);
        TextView locationTextView = (TextView) listItemView.findViewById(R.id.location_text_view);
        TextView dateTextView = (TextView) listItemView.findViewById(R.id.date_text_view);
        TextView timeTextView = (TextView) listItemView.findViewById(R.id.time_text_view);
        TextView distanceFromPlace = (TextView) listItemView.findViewById(R.id.specific_place_text_view);

        Date dateObject = new Date(currEarthquake.getTimeInMilliseconds());
        String formattedDate = formatDate(dateObject);
        String formattedTime = formatTime(dateObject);

        String wholePlace = currEarthquake.getLocation();
        String place;
        String distanceFrom;
        int searchFor = wholePlace.lastIndexOf(" of ");
        if (searchFor == -1) {
            place = wholePlace;
            distanceFrom = "Near the";
        } else {
            place = wholePlace.substring(searchFor + 4);
            distanceFrom = wholePlace.substring(0, searchFor + 4);
        }

        double magDouble = currEarthquake.getMag();
        DecimalFormat formatter = new DecimalFormat("0.0");
        String magString = formatter.format(magDouble);

        magTextView.setText(magString);
        locationTextView.setText(place);
        distanceFromPlace.setText(distanceFrom);
        dateTextView.setText(formattedDate);
        timeTextView.setText(formattedTime);

        GradientDrawable magnitudeCircle = (GradientDrawable) magTextView.getBackground();
        int magnitudeColor = getMagnitudeColor(currEarthquake.getMag());
        magnitudeCircle.setColor(magnitudeColor);

        return listItemView;
    }

    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        return dateFormat.format(dateObject);
    }

    private String formatTime(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(dateObject);
    }

    private int getMagnitudeColor (double mag) {
        int magInt = (int) mag;
        switch (magInt) {
            case 0:
            case 1:
                int magnitude1Color = ContextCompat.getColor(getContext(), R.color.magnitude1);
                return magnitude1Color;
            case 2:
                int magnitude2Color = ContextCompat.getColor(getContext(), R.color.magnitude2);
                return magnitude2Color;
            case 3:
                int magnitude3Color = ContextCompat.getColor(getContext(), R.color.magnitude3);
                return magnitude3Color;
            case 4:
                int magnitude4Color = ContextCompat.getColor(getContext(), R.color.magnitude4);
                return magnitude4Color;
            case 5:
                int magnitude5Color = ContextCompat.getColor(getContext(), R.color.magnitude5);
                return magnitude5Color;
            case 6:
                int magnitude6Color = ContextCompat.getColor(getContext(), R.color.magnitude6);
                return magnitude6Color;
            case 7:
                int magnitude7Color = ContextCompat.getColor(getContext(), R.color.magnitude7);
                return magnitude7Color;
            case 8:
                int magnitude8Color = ContextCompat.getColor(getContext(), R.color.magnitude8);
                return magnitude8Color;
            case 9:
                int magnitude9Color = ContextCompat.getColor(getContext(), R.color.magnitude9);
                return magnitude9Color;
            default:
                int magnitude10Color = ContextCompat.getColor(getContext(), R.color.magnitude10plus);
                return magnitude10Color;
        }
    }
}
