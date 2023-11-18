package com.example.cse535;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.ArrayList;

public class HeartRateService {

    public HeartRateService() {
    }

    public void getHeartRateAsync(Context context, Uri uri, HeartRateListener listener) {
        new HeartRateTask(context, uri, listener).execute();
    }

    private static class HeartRateTask extends AsyncTask<Void, Void, String> {
        private Context context;
        private Uri uri;
        private HeartRateListener listener;

        public HeartRateTask(Context context, Uri uri, HeartRateListener listener) {
            this.context = context;
            this.uri = uri;
            this.listener = listener;
        }

        @Override
        protected String doInBackground(Void... voids) {
            Bitmap m_bitmap = null;
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            ArrayList<Bitmap> frameList = new ArrayList<>();
            try {
                retriever.setDataSource(context, uri);
                System.out.println("Here: ");
                String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_FRAME_COUNT);
                int aduration = Integer.parseInt(duration);
                int i = 10;
                while (i < aduration) {
                    Bitmap bitmap = retriever.getFrameAtIndex(i);
                    frameList.add(bitmap);
                    i += 5;
                }
            } catch (Exception m_e) {

            } finally {
                try {
                    retriever.release();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                long redBucket = 0;
                long pixelCount = 0;
                ArrayList<Long> a = new ArrayList<>();
                for (Bitmap i : frameList) {
                    redBucket = 0;
                    for (int y = 240; y < 360; y++) {
                        for (int x = 136; x < 204; x++) {
                            int c = i.getPixel(x, y);
                            pixelCount++;
                            redBucket += Color.red(c) + Color.blue(c) + Color.green(c);
                        }
                    }
                    a.add(redBucket);
                }
                ArrayList<Long> b = new ArrayList<>();
                for (int i = 0; i < a.size() - 5; i++) {
                    long temp = (a.get(i) + a.get(i + 1) + a.get(i + 2) + a.get(i + 3) + a.get(i + 4)) / 4;
                    b.add(temp);
                }
                long x = b.get(0);
                int count = 0;
                for (int i = 1; i < b.size() - 1; i++) {
                    long p = b.get(i);
                    if ((p - x) > 3500) {
                        count = count + 1;
                    }
                    x = b.get(i);
                }
                int rate = (int) ((count * 1.0f / 45) * 60);
                return String.valueOf(rate / 2);
            }
        }

        @Override
        protected void onPostExecute(String heartRate) {
            super.onPostExecute(heartRate);
            if (listener != null) {
                listener.onHeartRateCalculated(heartRate);
            }
        }
    }

    public interface HeartRateListener {
        void onHeartRateCalculated(String heartRate);
    }
}
