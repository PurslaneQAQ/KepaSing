package com.example.kepa.kepasing;

/**
 * Created by ASUS on 2017/10/26.
 */
import com.example.kepa.kepasing.LrcRow;

import java.util.List;

public interface ILrcBuilder {
    List<LrcRow> getLrcRows(String rawLrc);
}
