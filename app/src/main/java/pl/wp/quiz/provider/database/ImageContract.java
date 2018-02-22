package pl.wp.quiz.provider.database;

import android.net.Uri;

/**
 * @author Krzysztof Betlej <k.betlej@samsung.com>.
 * @date 2/22/18
 * @copyright Copyright (c) 2016 by Samsung Electronics Polska Sp. z o. o.
 */

public class ImageContract {
    public static final String AUTHORITY = "pl.wp.quiz.provider.images";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static class ImageEntry {
        public static final String TABLE_NAME = "images";
        public static final String ID = "image_url";
        public static final String IMAGE_URL = "image_url";
        public static final String IMAGE_URI = "image_uri";
        public static final String SYNC_DATE = "sync_date";

        public static final String CREATE = "CREATE TABLE " + TABLE_NAME + "(" +
                ID + " INTEGER PRIMARY KEY," +
                IMAGE_URI + " TEXT," +
                IMAGE_URL + " TEXT," +
                SYNC_DATE + " INTEGER)";
        public static final String DELETE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

}
