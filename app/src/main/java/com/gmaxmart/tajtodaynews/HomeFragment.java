package com.gmaxmart.tajtodaynews;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * Created by Jauhar xlr on 4/18/2016.
 *  mycreativecodes.in
 */
public class HomeFragment extends Fragment {

    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public static int int_items = 17 ;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /**
         *Inflate tab_layout and setup Views.
         */
            View x =  inflater.inflate(R.layout.home_tab_layout,null);
            tabLayout = (TabLayout) x.findViewById(R.id.tabs);
            viewPager = (ViewPager) x.findViewById(R.id.viewpager);

        /**
         *Set an Apater for the View Pager
         */
        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));

        /**
         * Now , this is a workaround ,
         * The setupWithViewPager dose't works without the runnable .
         * Maybe a Support Library Bug .
         */
        viewPager.setOffscreenPageLimit(17);


        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                    tabLayout.setupWithViewPager(viewPager);
                   }
        });
        /**
         *Setup the DrawerLayout and NavigationView
         */
        return x;


    }

    class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Return fragment with respect to Position .
         */

        @Override
        public Fragment getItem(int position)
        {
          switch (position){
              case 0 : return new Taja();
              case 1 : return new Agraj();
              case 2 : return new Agram();
              case 3 : return new Desh();
              case 4 : return new Rajya();
              case 5 : return new Education();
              case 6 : return new Khel();
              case 7 : return new YuvaJagat();
              case 8 : return new MahilaJagat();
              case 9 : return new Sampadkiya();
              case 10 : return new Interview();
              case 11 : return new lekh();
              case 12 : return new Rajniti();
              case 13 : return new Manoran();
              case 14 : return new Samanaya();
              case 15 : return new Sanskriti();
              case 16 : return new HealthTips();




          }
        return null;
        }

        @Override
        public int getCount() {

            return int_items;

        }

        /**
         * This method returns the title of the tab according to the position.
         */

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position){
                case 0 :
                    return "ताज़ा खबर";
                case 1 :
                    return "आगरा जिला";
                case 2 :
                    return "आगरा मंडल";
                case 3 :
                    return "देश दुनिया";
                case 4 :
                    return "राज्य";
                case 5 :
                    return "शिक्षा";
                case 6:
                    return "खेल";
                case 7:
                    return "युवा जगत";
                case 8 :
                    return "महिला जगत";
                case 9 :
                    return "सम्पादकीय";
                case 10 :
                    return "इंटरव्यू";
                case 11 :
                    return "लेख";
                case 12 :
                    return "चुनावी गणित";
                case 13:
                    return "मनोरंजन";
                case 14:
                    return "समान्य ज्ञान";
                case 15:
                    return "संस्कृति";
                case 16:
                    return "हेल्थ टिप्स";

            }
                return null;
        }
    }

}
