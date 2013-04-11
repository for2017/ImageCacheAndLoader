package com.lianghanzhen.image.sample;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import com.lianghanzhen.image.cache.CacheableImageView;
import com.lianghanzhen.image.loaders.ImageLoader;


public class SampleActivity extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ((GridView) findViewById(R.id.gridView)).setAdapter(new SampleAdapter(this));
    }

    private static class SampleAdapter extends BaseAdapter {

        private static final String[] URLS = {
                "http://24.media.tumblr.com/tumblr_liivjs579l1qeqteyo1_500.jpg",
                "http://29.media.tumblr.com/tumblr_lieuvuig5Y1qze17ho1_500.jpg",
                "http://24.media.tumblr.com/tumblr_liwoeqmXH31qh9umso1_500.jpg",
                "http://29.media.tumblr.com/tumblr_lsm50jkqpy1qzhmgco1_500.jpg",
                "http://24.media.tumblr.com/tumblr_lisp2lS2mD1qh2ak3o1_500.jpg",
                "http://28.media.tumblr.com/tumblr_lk12k2pVKQ1qe3m7qo1_500.jpg",
                "http://24.media.tumblr.com/tumblr_ll1h2iI9pG1qe76kxo1_500.jpg",
                "http://24.media.tumblr.com/tumblr_mbfl4luabM1qb08qmo1_500.jpg",
                "http://28.media.tumblr.com/tumblr_lqgh6gSMEm1r0h32io1_500.png",
                "http://24.media.tumblr.com/tumblr_mbncuaqA661qzio10o1_400.jpg",
                "http://28.media.tumblr.com/tumblr_llr9e8mz5F1qaa50yo1_500.jpg",
                "http://29.media.tumblr.com/tumblr_lht5uy6khS1qed3e3o1_500.jpg",
                "http://30.media.tumblr.com/tumblr_lh9bylfRVZ1qe5hjlo1_400.jpg",
                "http://24.media.tumblr.com/tumblr_ljdxbwpZrJ1qexc8jo1_500.jpg",
                "http://26.media.tumblr.com/tumblr_ltee8lg9wd1qb08qmo1_500.jpg",
                "http://28.media.tumblr.com/tumblr_lk5h7hIRFf1qi4pifo1_500.jpg",
                "http://25.media.tumblr.com/tumblr_mbnbhvErFJ1qb08qmo1_500.jpg",
                "http://27.media.tumblr.com/tumblr_ls6tpnEwe71r3ip8io1_500.jpg",
                "http://30.media.tumblr.com/tumblr_liyjcg8NSv1qis1geo1_400.jpg",
                "http://25.media.tumblr.com/tumblr_li0tqgufyN1qzj3syo1_500.jpg",
                "http://29.media.tumblr.com/tumblr_lsu0igEEtP1qiys5ao1_500.png",
                "http://25.media.tumblr.com/tumblr_ll3y1pZDVJ1qb08qmo1_500.jpg",
                "http://26.media.tumblr.com/tumblr_lttbk92Ko01ql9nqgo1_500.jpg",
                "http://27.media.tumblr.com/tumblr_lqfwfyZuyS1qiyqyfo1_500.jpg",
                "http://26.media.tumblr.com/tumblr_ltpd40jAPJ1qbsj0vo1_500.jpg",
                "http://26.media.tumblr.com/tumblr_lil8a1m1YM1qzj3syo1_500.jpg",
                "http://26.media.tumblr.com/tumblr_lswx4kiv5I1qaa50yo1_500.jpg",
                "http://25.media.tumblr.com/tumblr_mbs9uw4Uoy1qaa50yo1_500.jpg",
                "http://24.media.tumblr.com/tumblr_mcqoqdCLqb1qb08qmo1_500.jpg",
                "http://28.media.tumblr.com/tumblr_llayqvxQOG1qaa50yo1_500.jpg",
                "http://25.media.tumblr.com/tumblr_mblaw5jZ0v1qbbpjfo1_500.jpg",
                "http://25.media.tumblr.com/tumblr_lr4hcm0irP1qb08qmo1_500.jpg",
                "http://26.media.tumblr.com/tumblr_ltu635i89f1qjt6klo1_500.png",
                "http://25.media.tumblr.com/tumblr_lm1gf8IEiu1qko977o1_500.jpg",
                "http://25.media.tumblr.com/tumblr_li506pgwiU1qb4i8uo1_500.jpg",
                "http://25.media.tumblr.com/tumblr_ljbgybFmuI1qa02ibo1_500.jpg",
                "http://26.media.tumblr.com/tumblr_l9nfw4NpmH1qb08qmo1_500.jpg",
                "http://24.media.tumblr.com/tumblr_lsr5lluMSu1qh6pbfo1_500.jpg",
                "http://29.media.tumblr.com/tumblr_locj1avomH1qzj3syo1_500.jpg",
                "http://25.media.tumblr.com/tumblr_lhtxw1oZA11qb08qmo1_500.jpg",
                "http://29.media.tumblr.com/tumblr_lsvczkC8e01qzgqodo1_500.jpg",
                "http://28.media.tumblr.com/tumblr_ljppluWZAQ1qaa50yo1_500.jpg",
                "http://25.media.tumblr.com/tumblr_mc4u6lwZHr1qf4k86o1_500.jpg",
                "http://27.media.tumblr.com/tumblr_lteewoQF9A1qb08qmo1_500.jpg",
                "http://29.media.tumblr.com/tumblr_lg0x3aQXQX1qb0a0vo1_500.jpg",
                "http://27.media.tumblr.com/tumblr_lqf1sn86te1qaa50yo1_500.jpg",
                "http://25.media.tumblr.com/tumblr_mcqpch4QbC1qb08qmo1_500.jpg",
                "http://27.media.tumblr.com/tumblr_ljlpu7iX2U1qzgqodo1_500.jpg",
                "http://24.media.tumblr.com/tumblr_lu7ep6O5hr1qbjvpuo1_500.jpg",
                "http://30.media.tumblr.com/tumblr_lj53czBbvd1qzgqodo1_500.jpg",
                "http://25.media.tumblr.com/tumblr_lruas6Ru8M1r2brlzo1_500.jpg",
                "http://26.media.tumblr.com/tumblr_lihpotlmBi1qgdpfco1_500.jpg",
                "http://30.media.tumblr.com/tumblr_liyjwy0bKP1qenyvto1_500.jpg",
                "http://25.media.tumblr.com/tumblr_mco9nsT0kM1qhk3dno1_500.jpg",
                "http://24.media.tumblr.com/tumblr_lk2bdmdROW1qaa50yo1_500.jpg",
                "http://28.media.tumblr.com/tumblr_lkbkmo9EoE1qaa50yo1_500.jpg",
                "http://25.media.tumblr.com/tumblr_mc00rmFhJI1rh08hdo1_500.jpg",
                "http://25.media.tumblr.com/tumblr_lix8s4bGDx1qhccb4o1_500.jpg",
                "http://29.media.tumblr.com/tumblr_lteeyueSPa1qb08qmo1_500.jpg",
                "http://27.media.tumblr.com/tumblr_llan1lJP8A1qkn0gvo1_500.jpg",
                "http://29.media.tumblr.com/tumblr_lhv84py3Ff1qzj3syo1_500.jpg",
                "http://29.media.tumblr.com/tumblr_lj52ex9ecW1qzgqodo1_500.jpg",
                "http://24.media.tumblr.com/tumblr_lii05kwp5A1qb08qmo1_500.jpg",
                "http://28.media.tumblr.com/tumblr_lk8sapJ13n1qhb62wo1_500.jpg",
                "http://25.media.tumblr.com/tumblr_lsigg3D23h1qz9wudo1_500.jpg",
                "http://29.media.tumblr.com/tumblr_lu7ep6O5hr1qbjvpuo1_500.jpg",
                "http://29.media.tumblr.com/tumblr_lu81luxaZA1qb08qmo1_500.jpg",
                "http://24.media.tumblr.com/tumblr_lhowz2fAnC1qaa50yo1_500.jpg",
                "http://27.media.tumblr.com/tumblr_ltjgdeZFDz1r0cn4to1_500.png",
                "http://27.media.tumblr.com/tumblr_lj9yb6orMV1qdzdkjo1_500.jpg",
                "http://25.media.tumblr.com/tumblr_mcgpapmE3I1qkoat7o1_400.jpg",
                "http://25.media.tumblr.com/tumblr_mcq4zfHkqw1qb08qmo1_500.jpg",
                "http://26.media.tumblr.com/tumblr_lpztxiv48D1qev8yto1_500.jpg"};

        private final Context mContext;
        private final LayoutInflater mInflater;

        public SampleAdapter(Context context) {
            mContext = context;
            mInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            return URLS.length;
        }

        @Override
        public Object getItem(int position) {
            return URLS[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (null == convertView) {
                convertView = LayoutInflater.from(mContext)
                        .inflate(R.layout.gridview_item_layout, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.imageView = (CacheableImageView) convertView.findViewById(R.id.image_view);
                viewHolder.statusView = (TextView) convertView.findViewById(R.id.status_view);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final ImageLoader.CacheStatus fromCache = SampleApplication.getImageLoader().loadImage((CacheableImageView) viewHolder.imageView, (String) getItem(position));

            switch (fromCache) {
                case NONE:
                    viewHolder.statusView.setTextColor(Color.BLACK);
                    viewHolder.statusView.setText("Loading...");
                    viewHolder.statusView.setBackgroundColor(Color.WHITE);
                    break;
                case DISK:
                    viewHolder.statusView.setText("From Disk/Network");
                    viewHolder.statusView.setBackgroundColor(0xCCff4444);
                    break;
                case MEMORY:
                    viewHolder.statusView.setText("From Memory Cache");
                    viewHolder.statusView.setBackgroundColor(0xCC99cc00);
                    break;
            }

            return convertView;
        }

        private static class ViewHolder {
            CacheableImageView imageView;
            TextView statusView;
        }

    }

}