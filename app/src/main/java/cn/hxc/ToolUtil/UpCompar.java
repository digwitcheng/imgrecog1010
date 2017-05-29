package cn.hxc.ToolUtil;

import java.util.Comparator;

/**
 * Created by hxc on 2017/5/16.
 */

public class UpCompar implements Comparator<Integer> {

    @Override
    public int compare(Integer o1, Integer o2) {
        return o1-o2;
    }
}
