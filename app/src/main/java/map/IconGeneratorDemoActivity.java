/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package map;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import data.mirzapurdss.Connection;
import data.mirzapurdss.Global;
import map.ui.IconGenerator;

import static android.graphics.Typeface.BOLD;
import static android.graphics.Typeface.ITALIC;

public class IconGeneratorDemoActivity extends BaseDemoActivity {
    Connection C;
    Global g;
    Cursor cur;

    String CurrentVillage;
    String CurrentBari;
    Bundle IDbundle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        C=new Connection(this);
        g=Global.getInstance();

        IDbundle       = getIntent().getExtras();
        CurrentVillage = IDbundle.getString("village");
        CurrentBari   = IDbundle.getString("bari");

        //cur = C.ReadData("select vill as vill,bari as bari,hh as hh, lat as lat,lon as lon from visits where vill='"+ CurrentVillage +"' and length(lat)>0 and bari in('"+ CurrentBari +"')");
        cur = C.ReadData("select substr(idno,1,3)vill,substr(idno,4,4)bari,lat as lat,lon as lon from gpsdata where substr(idno,1,3)='"+ CurrentVillage +"'");

    }

    @Override
    protected void startDemo() {
        IconGenerator iconFactory = new IconGenerator(this);

      //  Cursor cur = C.ReadData("select vill as vill,bari as bari,hh as hh, lat as lat,lon as lon from visits where vill='228' and length(lat)>0 and rnd='23' and bari='0158'");

        cur.moveToFirst();
        while(!cur.isAfterLast())
        {
            if (cur.isLast()) {
                getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(cur.getString(cur.getColumnIndex("lat"))), Double.parseDouble(cur.getString(cur.getColumnIndex("lon")))), 17));
            }
            addIcon(iconFactory, cur.getString(cur.getColumnIndex("bari")),cur.getString(cur.getColumnIndex("bari")), new LatLng(Double.parseDouble(cur.getString(cur.getColumnIndex("lat"))), Double.parseDouble(cur.getString(cur.getColumnIndex("lon")))));

            cur.moveToNext();
        }
        cur.close();

/*        getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(23.77736395502335, 90.40017179490631), 23));

        addIcon(iconFactory, "1111", new LatLng(23.77736395502335, 90.40017179490631));

        iconFactory.setColor(Color.CYAN);
        addIcon(iconFactory, "2222", new LatLng(23.777963935869932, 90.40017321978951));

        //iconFactory.setRotation(90);
        iconFactory.setStyle(IconGenerator.STYLE_RED);
        addIcon(iconFactory, "3222", new LatLng(23.77750400683053, 90.40021576075672));

        //  iconFactory.setContentRotation(-90);
        iconFactory.setStyle(IconGenerator.STYLE_PURPLE);
        addIcon(iconFactory, "4222", new LatLng(23.77850400683053, 90.40021576075672));*/

/*        iconFactory.setRotation(0);
        iconFactory.setContentRotation(90);
        iconFactory.setStyle(IconGenerator.STYLE_GREEN);
        addIcon(iconFactory, "2225", new LatLng(-33.7677, 151.244));

        iconFactory.setRotation(0);
        iconFactory.setContentRotation(0);
        iconFactory.setStyle(IconGenerator.STYLE_ORANGE);
        addIcon(iconFactory, makeCharSequence(), new LatLng(-33.77720, 151.12412));*/
    }

    private void addIcon(IconGenerator iconFactory, CharSequence bari, String hh, LatLng position) {
        MarkerOptions markerOptions = new MarkerOptions().
                icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(bari))).title("Bari").snippet(hh).
                position(position).
                anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        getMap().setMyLocationEnabled(true);
        getMap().setBuildingsEnabled(true);
        getMap().addMarker(markerOptions);
        getMap().setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                return false;
            }
        });
    }

    private CharSequence makeCharSequence() {
        String prefix = "Mixing ";
        String suffix = "different fonts";
        String sequence = prefix + suffix;
        SpannableStringBuilder ssb = new SpannableStringBuilder(sequence);
        ssb.setSpan(new StyleSpan(ITALIC), 0, prefix.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.setSpan(new StyleSpan(BOLD), prefix.length(), sequence.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ssb;
    }
}
