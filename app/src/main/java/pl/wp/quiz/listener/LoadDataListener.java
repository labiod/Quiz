package pl.wp.quiz.listener;

import java.util.List;

/**
 * Created by labio on 18.02.2018.
 */

public interface LoadDataListener<DataType> {
    void onLoadData(DataType dataList);
}
