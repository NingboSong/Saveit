package helpclass;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * The Type spinner array adapter.
 */
public class TypeSpinnerArrayAdapter extends ArrayAdapter<String> {
    private Context mContext;
    private String [] mStringArray;

    /**
     * Instantiates a new Type spinner array adapter.
     *
     * @param context     the context
     * @param stringArray the string array
     */
    public TypeSpinnerArrayAdapter(Context context, String[] stringArray) {
        super(context, android.R.layout.simple_spinner_item, stringArray);
        mContext = context;
        mStringArray=stringArray;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent,false);
        }

        TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
        tv.setText(mStringArray[position]);
        tv.setTextSize(25f);
        tv.setTextColor(Color.BLACK);

        return convertView;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(android.R.layout.simple_spinner_item, parent, false);
        }

        TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
        tv.setText(mStringArray[position]);
        tv.setTextSize(20f);
        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);//textAlignment
        tv.setTypeface(Typeface.DEFAULT_BOLD);
        if (tv.getText().toString().replaceAll("\u00A0", "").equals("Cost")){
            tv.setTextColor(Color.GREEN);
        }
        else
            tv.setTextColor(Color.RED);

        return convertView;
    }

}
