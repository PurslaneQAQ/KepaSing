package com.example.kepa.kepasing;

/**
 * Created by ASUS on 2017/10/26.
 */
import com.example.kepa.kepasing.LrcRow;

public interface ILrcViewListener {
    void onLrcSeeked(int newPosition, LrcRow row);
}
