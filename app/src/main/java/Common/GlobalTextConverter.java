package Common;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Administrator on 15/11/2015.
 */
public class GlobalTextConverter {

    public void ConvertSpinnerText(Context c, final List<String> listofString,  final Spinner spn, final Typeface font) {
       // final Typeface font = Typeface.createFromAsset(getAssets(), "NikoshBAN.ttf");

        ArrayAdapter<String> adptrstatus = new ArrayAdapter<String>(c, android.R.layout.simple_spinner_item, listofString) {
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTypeface(font);
                return v;
            }


            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                ((TextView) v).setTypeface(font);
                return v;
            }

        };
       //return adptrstatus;
        spn.setAdapter(adptrstatus);
    }

    public void SetTextViewTypeFace(final TextView tv, final Typeface font)
    {
        tv.setTypeface(font);
    }


    public void SetRadioBtnViewTypeFace(final RadioButton tv, final Typeface font)
    {
        tv.setTypeface(font);
    }

}

