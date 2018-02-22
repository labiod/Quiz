package pl.wp.quiz.synchronizer;

/**
 * @author Krzysztof Betlej <labiod@wp.pl>.
 * @date 2/22/18
 */

public class ImageLoadItem {
    private String mImageUrl;
    private String mTableName;
    private String mColumnName;
    private long mItemId;

    public ImageLoadItem(String imageUrl, String tableName, String columnName, long itemId) {
        mImageUrl = imageUrl;
        mTableName = tableName;
        mColumnName = columnName;
        mItemId = itemId;
    }

    public String getUrl() {
        return mImageUrl;
    }

    public String getTableName() {
        return mTableName;
    }

    public String getImageColumn() {
        return mColumnName;
    }

    public long getId() {
        return mItemId;
    }
}
