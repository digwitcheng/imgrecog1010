package cn.hxc.imgrecognition;

import android.graphics.Rect;
import android.util.Log;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Collections;

import cn.hxc.ImageUtil.imageProcess;
import cn.hxc.ToolUtil.UpCompar;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Created by hxc on 201RES_COUNT/5/4.
 */

public class RemoveNoise {
    // public final static int RES_COUNT=7;
    public int count;
    public int forword;
    public int back;
    public float averagePercent;
    public int iTop;
    public int iBottom;
    public int avrageHeight;

    public void CombainAgain(int[] sorta, int width, int height) {
        int averCount = 0;
        int averSum = 0;
        int averW = 0;
        for (int i = 0; i < count; i++) {
            if (sorta[i * 7] == 0) {
                averCount++;
                averSum += sorta[i * 7 + 2] - sorta[i * 7 + 1];
            }
        }
        if (averCount == 0) {
            return;
        }
        averW = averSum / averCount;
        int interval = getAvrageInterval(sorta);
        for (int oldi = 0; oldi < count - 1; oldi++) {
            if (sorta[oldi * 7] != 0) {
                continue;
            }
            for (int i = oldi + 1; i < count; i++) {
                if (sorta[i * 7] != 0) {
                    continue;
                }
                int oldxw = sorta[oldi * 7 + 2] - sorta[oldi * 7 + 1];
                int xw = sorta[i * 7 + 2] - sorta[i * 7 + 1];
                int totalxw = sorta[i * 7 + 2] - sorta[oldi * 7 + 1];
                int internalX = sorta[i * 7 + 1] - sorta[oldi * 7 + 2];

                imageProcess.noequl("oldxw=", oldxw);
                imageProcess.noequl("xw=", xw);
                imageProcess.noequl("totalxw=", totalxw);
                imageProcess.noequl("averW=", averW);
                imageProcess.noequl("-------------------- ", i);

                if (xw < averW && oldxw < averW && totalxw < averW * 2&&internalX<interval) {
                    sorta[(i) * 7] = 1;
                    sorta[oldi * 7] = 0;
                    if (sorta[i * 7 + 2] > sorta[oldi * 7 + 2]) {
                        sorta[oldi * 7 + 2] = sorta[i * 7 + 2];
                    }
                    if (sorta[i * 7 + 3] < sorta[oldi * 7 + 3]) {
                        sorta[oldi * 7 + 3] = sorta[i * 7 + 3];
                    }
                    if (sorta[i * 7 + 4] > sorta[oldi * 7 + 4]) {
                        sorta[oldi * 7 + 4] = sorta[i * 7 + 4];
                    }
                    // imageProcess.noequl("hebing$$$ ", ylen_old - ylen);
                    // Toast.makeText(this, "hebing", 1).show();
                }
                break;

            }

        }


    }

    public Rect RecoginzeRect(int[] a, int width, int height) {
        //寻找上下左右边界
        //TODO 去除无效的连通元
        setComTrue(a,2/3,searchBorad(a));
        setComTrue(a,1,searchBorad(a));

        return searchBorad(a);
    }
   void  setComTrue(int[]a,float radio,Rect rect){
        //把连通元高度三分之二在方框内的连通元设为可用
        for (int i = 0; i < count; i++) {
            int hei = (int) ((a[i * 7 + 4] - a[i * 7 + 3]) *radio);// 2 / 3;
            if (a[i * 7 + 4] > rect.top + hei && a[i * 7 + 3] < rect.bottom - hei) {
                a[i * 7] = 0;
            }
        }
    }
    Rect searchBorad(int []a){
        int minx = Integer.MAX_VALUE;
        int miny = Integer.MAX_VALUE;
        int maxx = 0;
        int maxy = 0;
        for (int i = 0; i < count; i++) {
            if (a[i * 7] == 0) {
                if (a[i * 7 + 1] < minx) {
                    minx = a[i * 7 + 1];
                }
                if (a[i * 7 + 2] > maxx) {
                    maxx = a[i * 7 + 2];
                }
                if (a[i * 7 + 3] < miny) {
                    miny = a[i * 7 + 3];
                }
                if (a[i * 7 + 4] > maxy) {
                    maxy = a[i * 7 + 4];
                }
            }
        }
        if (miny > maxy || minx > maxx) {
            return null;
        }
        return new Rect(minx, miny, maxx, maxy);
    }

    public void PreRecoginzeRect(int[] a, int width, int height) {
        //去除太高的噪音，避免下一步确定识别框时过大
        //   removeMaxNoise(a, width, height);

        //寻找上下左右边界
        int minx = width;
        int miny = height;
        int maxx = 0;
        int maxy = 0;
        int sumMiny = 0;
        int sumMaxy = 0;
        int sumCount = 0;
        for (int i = 0; i < count; i++) {
            if (a[i * 7] == 0) {
                sumMiny += a[i * 7 + 3];
                sumMaxy += a[i * 7 + 4];
                sumCount++;
            }
        }
        if (sumCount == 0) {
            return;
        }
        miny = sumMiny / sumCount;
        maxy = sumMaxy / sumCount;
        if (minx > maxx || miny > maxy) {
            return;
        }
        //把在方框内的连通元设为可用
        for (int i = 0; i < count; i++) {
            if (a[i * 7 + 1] >= minx - 1 && a[i * 7 + 2] <= maxx + 1 && a[i * 7 + 3] >= miny - 1 && a[i * 7 + 4] <= maxy + 1) {
                a[i * 7] = 0;
            }
        }
        //把连通元三分之一在方框内的连通元设为可用
        setComTrue(a,1/3,searchBorad(a));
//        for (int i = 0; i < count; i++) {
//            int hei = (a[i * 7 + 4] - a[i * 7 + 3]) / 3;
//            if (a[i * 7 + 4] > miny + hei && a[i * 7 + 3] < maxy - hei) {
//                a[i * 7] = 0;
//            }
//        }
    }


    /**
     * 移除比较大的或比较小的前景像素
     *
     * @param a      原图像
     * @param width
     * @param height
     */
    public void removeMaxNoise(int[] a, int width, int height) {
        count = a.length / 7;
        avrageHeight = getAvrageHeight(a);

        int countHeight = 0;
        for (int i = 0; i < count; i++) {
            if (a[i * 7] == 0) {
                countHeight++;
            }
        }
        imageProcess.noequl("", 1);

        for (int i = 0; i < count; i++) {
            if (a[i * 7] == 0) {
                if (Math.abs(a[i * 7 + 2] - a[i * 7 + 1]) >= avrageHeight * 2) {
                    a[i * 7] = 1;
                }
                if ((Math.abs(a[i * 7 + 4] - a[i * 7 + 3]) >= avrageHeight * 2)) {
                    a[i * 7] = 1;
                }
            }
        }

        //********测试代码***************//
        int countHeight1 = 0;
        for (int i = 0; i < count; i++) {
            if (a[i * 7] == 0) {
                countHeight1++;
            }
        }
        imageProcess.noequl("", 1);
        //********测试代码结束************//
    }

    public void CombainOne_jing(int[] a, int width, int height) {

        int countHeight = 0;
        for (int i = 0; i < count; i++) {
            if (a[i * 7] == 0) {
                countHeight++;
            }
        }
        imageProcess.noequl("", 1);

        int count_new = 0;
        int a1[] = new int[200];
        for (int i = 0; i < count; i++) {
            if (a[i * 7] == 1)
                continue;
            for (int j = 0; j < count; j++) {
                if (i != j && a[j * 7] == 0) {
                    if (a[7 * i + 2] - a[7 * i + 1] + 1 + a[7 * j + 2] - a[7 * j + 1] + 1
                            > max(a[7 * i + 2], a[7 * j + 2]) - min(a[7 * i + 1], a[7 * j + 1]) + 1
                            && a[7 * i + 4] - a[7 * i + 3] + 1 + a[7 * j + 4] - a[7 * j + 3] + 1
                            > max(a[7 * i + 4], a[7 * j + 4]) - min(a[7 * i + 3], a[7 * j + 3]) + 1)
                    //	&&max(a[7*i+2],a[7*j+2])-min(a[7*i+1],a[7*j+1])+1<temp_wid+10
                    //	&&max(a[7*i+4],a[7*j+4])-min(a[7*i+3],a[7*j+3])+1<temp_hei+10)
                    {
                        if (i > j)//20090922做了修改
                        {
                            a[7 * i] = 1;
                            a[7 * j + 1] = min(a[7 * i + 1], a[7 * j + 1]);
                            a[7 * j + 2] = max(a[7 * i + 2], a[7 * j + 2]);
                            a[7 * j + 3] = min(a[7 * i + 3], a[7 * j + 3]);
                            a[7 * j + 4] = max(a[7 * i + 4], a[7 * j + 4]);
                            a[7 * j + 5] = a[7 * i + 5] + a[7 * j + 5];
                            i = j - 1;
                            break;
                        } else {
                            a[7 * j] = 1;
                            a[7 * i + 1] = min(a[7 * i + 1], a[7 * j + 1]);
                            a[7 * i + 2] = max(a[7 * i + 2], a[7 * j + 2]);
                            a[7 * i + 3] = min(a[7 * i + 3], a[7 * j + 3]);
                            a[7 * i + 4] = max(a[7 * i + 4], a[7 * j + 4]);
                            a[7 * i + 5] = a[7 * i + 5] + a[7 * j + 5];
                            break;
                        }
                    }
                }
            }
        }

        for (int i = 0; i < count; i++) {
            if (a[7 * i] == 0) {
                //	count_new++;
                a1[count_new] = 0;
                a1[count_new + 1] = a[7 * i + 1];
                a1[count_new + 2] = a[7 * i + 2];
                a1[count_new + 3] = a[7 * i + 3];
                a1[count_new + 4] = a[7 * i + 4];
                a1[count_new + 6] = a[7 * i + 6];
                a1[count_new + 5] = a[7 * i + 5];
                count_new++;
            }
            if (count_new > 199)
                break;
        }
        // count=count_new;

        int countHeight1 = 0;
        for (int i = 0; i < count; i++) {
            if (a[i * 7] == 0) {
                countHeight1++;
            }
        }
        imageProcess.noequl("", 1);
        SortAgain(a);
        getAvrageHeight(a);
    }

    public int getAvrageHeight(int[] sorta) {
        int sumHeight = 0;
        int countHeight = 0;
        ArrayList<Integer> arrayHeight = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            if (sorta[i * 7] == 0) {
                int tempHeight = Math.abs(sorta[i * 7 + 4] - sorta[i * 7 + 3]);
                arrayHeight.add(tempHeight);

            }
        }
        if (arrayHeight.size() >= 1) {
            for (int i = arrayHeight.size() / 3; i <= arrayHeight.size() * 2 / 3; i++) {
                sumHeight += arrayHeight.get(i);
                countHeight++;
            }
        }
        if (countHeight == 0) {
            return -1;
        }
        avrageHeight = sumHeight / countHeight;
        return avrageHeight;

    }

    public void initCount(int[] a) {
        count = a.length / 7;
        iTop = 0;
        iBottom = count;
    }

    /**
     * 合并同一个字符
     */
    public void CombainOneCharact(int[] sorta, int width, int height) {
        int countHeight = 0;
        for (int i = 0; i < count; i++) {
            if (sorta[i * 7] == 0) {
                countHeight++;
            }
        }
        imageProcess.noequl("", 1);

        // 合并同一个字符
        int oldi1 = 0;
        // imageProcess.noequl("**********height****** ", height);
        int averCount = 1;
        int averSum = 0;
        int averW = 0;
        int averH = 0;
        for (int i = oldi1 + 1; i < count; i++) {
            if (sorta[i * 7] == 0) {
                boolean flag = false;
                int xw = sorta[i * 7 + 2] - sorta[i * 7 + 1];
                int xmin = sorta[(i) * 7 + 1] - sorta[oldi1 * 7 + 1];
                int xmax = sorta[(i) * 7 + 2] - sorta[oldi1 * 7 + 2];
                int xmax_oldmin = sorta[(i) * 7 + 1] - sorta[oldi1 * 7 + 2];
                int ylen_old = sorta[oldi1 * 7 + 4] - sorta[oldi1 * 7 + 3];
                int ylen = sorta[i * 7 + 4] - sorta[i * 7 + 3];
                // imageProcess.noequl("xmin------------ ", xmin);
                // imageProcess.noequl("xmax ", xmax);
                // imageProcess.noequl("ylen_old ", ylen_old);
                // imageProcess.noequl("--------------------", 0);

                // || ((xmax_oldmin < ylen_old / 3) && (Math.abs(ylen_old-
                // ylen)
                // < ylen_old / 3))
                if (xmin < ylen_old / 2 || (xmin) < 4 || (xmax) < 4
                        || (xmax_oldmin < 3 && Math.abs(ylen_old - ylen) > avrageHeight / 4)) {
                    flag = true;
                    sorta[(i) * 7] = 1;
                    sorta[oldi1 * 7] = 0;
                    if (sorta[i * 7 + 2] > sorta[oldi1 * 7 + 2]) {
                        sorta[oldi1 * 7 + 2] = sorta[i * 7 + 2];
                    }
                    if (sorta[i * 7 + 3] < sorta[oldi1 * 7 + 3]) {
                        sorta[oldi1 * 7 + 3] = sorta[i * 7 + 3];
                    }
                    if (sorta[i * 7 + 4] > sorta[oldi1 * 7 + 4]) {
                        sorta[oldi1 * 7 + 4] = sorta[i * 7 + 4];
                    }
                    // imageProcess.noequl("hebing$$$ ", ylen_old - ylen);
                    // Toast.makeText(this, "hebing", 1).show();
                } else {
                    oldi1 = i;
                    averSum += xw;
                    averW = averSum / averCount++;
                    // imageProcess.noequl("averW ", averW);
                }

            }
        }
        averW = averSum / averCount;


        int countHeight1 = 0;
        for (int i = 0; i < count; i++) {
            if (sorta[i * 7] == 0) {
                countHeight1++;
            }
        }
        imageProcess.noequl("", 1);

        SortAgain(sorta);
        getAvrageHeight(sorta);
    }

    //根据过度行分割，确定所需要的连通元
    public void RestoreCom(int[] a, int[] by1, int width, int height) {
        //********测试代码***************//
        int countHeight1 = 0;
        for (int i = 0; i < count; i++) {
            if (a[i * 7] == 0) {
                countHeight1++;
            }
        }
        imageProcess.noequl("", 1);
        //********测试代码结束************//


        //把所有的连通元都设为不可用
        int countNum = 0;
        for (int i = 0; i < count; i++) {
            if (a[i * 7] == 0) {
                countNum++;
            }
            a[i * 7] = 1;
        }
        //检测每个联通元区域，对应的by1是否有像素，有的联通圆才设为可用
        for (int i = 0; i < count; i++) {
            for (int p = a[i * 7 + 3]; p < a[i * 7 + 4]; p++) {
                for (int q = a[i * 7 + 1]; q < a[i * 7 + 2]; q++) {
                    int gray = by1[p * width + q];
                    if (gray == 0) {
                        a[i * 7] = 0;
                        break;
                    }
                }
            }
        }
        //********测试代码***************//
        int countHeight = 0;
        for (int i = 0; i < count; i++) {
            if (a[i * 7] == 0) {
                countHeight++;
            }
        }
        imageProcess.noequl("", 1);
        //********测试代码结束************//
    }

    //根据过度行分割，确定所需要的连通元
    public void RestoreComBasedRect(int[] a, int width, int height) {
        //********测试代码***************//
        int countHeight1 = 0;
        for (int i = 0; i < count; i++) {
            if (a[i * 7] == 0) {
                countHeight1++;
            }
        }
        imageProcess.noequl("", 1);
        //********测试代码结束************//


//        //把所有的连通元都设为不可用
//        int countNum = 0;
//        for (int i = 0; i < count; i++) {
//            if (a[i * 7] == 0) {
//                countNum++;
//            }
//            a[i * 7] = 1;
//        }
        //
        for (int i = 0; i < count; i++) {
            int hei = (a[i * 7 + 4] - a[i * 7 + 3]) / 3;
            if (a[i * 7 + 4] > iTop + hei && a[i * 7 + 3] < iBottom - hei) {
                a[i * 7] = 0;
            }
        }

        //********测试代码***************//
        int countHeight = 0;
        for (int i = 0; i < count; i++) {
            if (a[i * 7] == 0) {
                countHeight++;
            }
        }
        imageProcess.noequl("", 1);
        //********测试代码结束************//

    }


    //号码行预提取
    public int PreSegment_Line_hxc(int[] proImage, int width, int height) {
        //TODO 行分割hxc
        int[] linePix = new int[width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (proImage[i * width + j] < 255) {
                    linePix[i]++;
                }
            }
        }
//        ArrayList<Integer> linePixSort=new ArrayList<>();
//        Collections.sort(linePixSort);
//        for (int i = 0; i < height; i++) {
//            if(linePix[i]>1){
//                linePixSort.add(linePix[i]);
//            }
//        }
//        if(linePixSort.size()<1){
//            return  -1;
//        }
//        int threshold=linePixSort.get(linePixSort.size()/2);

        int lineCount = 0;
        int sum = 0;
        for (int i = 0; i < height; i++) {
            if (linePix[i] > 5) {
                lineCount++;
                sum += linePix[i];
            }
        }
        if (lineCount == 0) {
            return -1;
        }
        int threshold = sum / lineCount;
        ArrayList<textLine> lines = new ArrayList<>();
        for (int i = 0; i < height; i++) {
            textLine line = new textLine();
            if (linePix[i] <= threshold || i == height - 1) {
                continue;
            }
            line.setTop(i);
            int j = i + 1;
            for (; j < height; j++) {
                if (linePix[j] <= threshold) {
                    line.setBottom(j - 1);
                    break;
                }
            }
            i = j;
            if (j >= height - 1) {
                line.setBottom(j);
            }
            lines.add(line);
        }
        if (lines.size() > 0) {
            int min = Integer.MAX_VALUE;
            textLine midLine = lines.get(0);
            for (textLine line : lines
                    ) {
                int temp = Math.abs((line.getBottom() + line.getTop()) / 2 - height / 2);
                if (min > temp) {
                    min = temp;
                    midLine = line;
                }
            }
//            iTop=lines.get(0).getTop();
//            iBottom=lines.get(0).getBottom();
            iTop = midLine.getTop();
            iBottom = midLine.getBottom();
            if (iBottom - iTop >= 0) {
                for (int i = 0; i < iTop; i++) {
                    for (int j = 0; j < width; j++)
                        proImage[i * width + j] = 255;
                }
                for (int i = iBottom + 1; i < height; i++) {
                    for (int j = 0; j < width; j++)
                        proImage[i * width + j] = 255;
                }
            }
            return lines.size();
        } else {
            return -1;
        }

    }

    /**
     * 从中间开始，去除上下非中间行
     */
    //号码行预提取
    void PreSegment_Line(int[] proImage, int width, int height) {
        int i, j;
        int iLen;
        int iMaxLen;

        //找上边缘
        iTop = 0;
        for (i = height / 2; i > 0; i--) {
            iLen = 0;
            iMaxLen = 0;
            for (j = 0; j < width; j++) {
                if (proImage[i * width + j] == 255)
                    iLen++;
                else if (proImage[i * width + j] == 0 && iLen > 0) {
                    if (iLen > iMaxLen)
                        iMaxLen = iLen;
                    iLen = 0;
                }

            }
            if (iLen > iMaxLen)
                iMaxLen = iLen;
            if (iMaxLen > width * 2 / 3) {
                iTop = i - 1;
                break;
            }
        }
        //找下边缘
        iBottom = height - 1;
        for (i = height / 2; i < height - 1; i++) {
            iLen = 0;
            iMaxLen = 0;
            for (j = 0; j < width; j++) {
                if (proImage[i * width + j] == 255)
                    iLen++;
                else if (proImage[i * width + j] == 0 && iLen > 0) {
                    if (iLen > iMaxLen)
                        iMaxLen = iLen;
                    iLen = 0;
                }

            }
            if (iLen > iMaxLen)
                iMaxLen = iLen;
            if (iMaxLen > width * 2 / 3) {
                iBottom = i + 1;
                break;
            }
        }
        //
        if (iBottom - iTop > 20) {
            for (i = 0; i < iTop; i++) {
                for (j = 0; j < width; j++)
                    proImage[i * width + j] = 255;
            }
            for (i = iBottom; i < height; i++) {
                for (j = 0; j < width; j++)
                    proImage[i * width + j] = 255;
            }
        } else {
            iTop = 0;
            iBottom = height - 1;
        }

    }

    /**
     * 根据像素点的多少去除噪音
     */
    public void aaa() {
        // TODO 根据像素点的多少去除噪音
        /*
        // 去除像素大块的
        // 计算中心位置
        int newcount = 0;
        for (int i = 0; i < count; i++) {
            if (sorta[i * 7] == 0) {
                newcount++;
            }

        }
        if (newcount < 1) {
            return;
        }
        int newHeight = sorta[newcount / 2 * 7 + 4] - sorta[newcount / 2 * 7 + 3];
        int ii = newcount / 2;
        for (; ii < count; ii++) {
            if (newHeight > height / 3) {
                break;
            } else {
                newHeight = sorta[ii / 2 * 7 + 4] - sorta[ii / 2 * 7 + 3];
            }
        }
        if (ii == count - 1) {
            newHeight = sorta[newcount / 2 * 7 + 4] - sorta[newcount / 2 * 7 + 3];
        }
        // 计算平均像素点，根据像素的多少来去除噪音
        int average_pix = 0;
        int count_num = 0;
        for (int i = 0; i < count; i++) {
            if (sorta[i * 7] == 0) {
                average_pix += sorta[i * 7 + 5];
                count_num++;
            }
        }
        if (count_num == 0) {
            return;
        } else {
            average_pix = average_pix / count_num;
        }
        // imageProcess.noequl("average_pix", average_pix);
        for (int i = 0; i < count; i++) {
            if (sorta[i * 7] == 0) {// ||sorta[i * 7 + 5] < 4 *
                // average_pix||sorta[i * 7 + 5] < 15
                if (sorta[i * 7 + 5] > 3 * average_pix) {
                    sorta[i * 7] = 1;
                }
            }
        }
         */
    }

//    /**
//     * 移除比较大的或比较小的前景像素
//     *
//     * @param a      原图像
//     * @param width
//     * @param height
//     * @param id
//     */
//    public void removeMaxMinNoise(int[] a, int width, int height, int id) {
//        for (int i = 0; i < count; i++) {
//            if (a[i * 7] == 0) {
//                if (Math.abs(a[i * 7 + 2] - a[i * 7 + 1]) >= 3 * height) {
//                    a[i * 7] = 1;
//                }
//                if (Math.abs(a[i * 7 + 4] - a[i * 7 + 3]) >= 3 * height) {
//                    a[i * 7] = 1;
//                }
//                if (Math.abs(a[i * 7 + 4] - a[i * 7 + 3]) < height / 2) {
//                    a[i * 7] = 1;
//                }
//            }
//        }
//
//    }

    public void initTopBottom() {
        iTop = 0;
        iBottom = count;
    }

    public void initSorta(int[] a, int[] sorta) {

        // 并按x排序

        int j = 0;
        int k = 0;
        for (int g = 0; g < count; g++) {
            int min = 65535;
            for (int i = 0; i < count; i++) {
                if (a[i * 7] == 0) {
                    if (a[i * 7 + 1] < min) {
                        min = a[i * 7 + 1];
                        j = i;
                    }
                }
            }
            sorta[k++] = a[j * 7];
            sorta[k++] = a[j * 7 + 1];
            sorta[k++] = a[j * 7 + 2];
            sorta[k++] = a[j * 7 + 3];
            sorta[k++] = a[j * 7 + 4];
            sorta[k++] = a[j * 7 + 5];
            sorta[k++] = a[j * 7 + 6];
            a[j * 7] = 1;
        }

    }

    /**
     * // 计算字符平均像素比
     */
    public void getAveragePercent(int[] sorta) {
        averagePercent = 0;
        int sum = 0;
        int count_num = 0;// 重新计数
        for (int i = forword; i <= back; i++) {
            if (sorta[i * 7] == 0) {
                int w = sorta[i * 7 + 2] - sorta[i * 7 + 1];
                int h = sorta[i * 7 + 4] - sorta[i * 7 + 3];
                float percent = (float) sorta[i * 7 + 5] / (float) w / (float) h;
                sum += percent;
                count_num++;
            }
        }
        if (count_num == 0) {
            return;
        } else {
            averagePercent = sum / count_num;
        }

        int recogineNum = 0;
        for (int i = forword; i <= back; i++) {
            if (sorta[i * 7] == 0) {
                recogineNum++;
            }
        }
        for (int i = forword; i <= back; i++) { // 若1残缺，应该是最大比例
            if (sorta[i * 7] == 0) {
                int w = sorta[i * 7 + 2] - sorta[i * 7 + 1];
                int h = sorta[i * 7 + 4] - sorta[i * 7 + 3];
                float percent = (float) sorta[i * 7 + 5] / (float) w / (float) h;
                if (percent > averagePercent) {
                    averagePercent = percent;
                }
            }
        }
    }

    public int getAvrageInterval(int[] sorta) {
        int count_num = 0;
        ArrayList<Integer> array = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            if (sorta[i * 7] == 0) {
                int j = i + 1;
                boolean flag = false;
                for (; j < count; j++) {
                    if (sorta[j * 7] == 0) {
                        flag = true;
                        break;
                    }
                }
                if (flag == true) {
                    int internal = sorta[j * 7 + 1] - sorta[i * 7 + 2];
                    array.add(internal);
                }
            }
        }
        if (array.size() > 0) {
            UpCompar upCompar = new UpCompar();
            Collections.sort(array, upCompar);
            int temp = array.get(array.size() / 2);
            return temp;
        } else {
            return -1;
        }
    }

    public void initForwordAndBack(int[] sorta) {
        int interval = getAvrageInterval(sorta);


        if (interval < 0) {
            interval = iBottom - iTop;
        }
        int count_num1 = 0;
        int k = 0;
        for (int i = 0; i < count; i++) {
            if (sorta[i * 7] == 0) {
                count_num1++;
                k = i;
            }
        }
        if (count_num1 == 0) {
            return;
        }
        forword = k / 2;
        back = k / 2;

        int radio = 2;
        while (back - forword <= 4 && radio < 5) {
            radio++;
            forword = k / 2;
            back = k / 2;
            for (int p = forword; p >= 0; p--) {
                if (sorta[p * 7] == 0) {
                    int q = p - 1;
                    boolean flag = false;
                    for (; q >= 0; q--) {
                        if (sorta[q * 7] == 0) {
                            flag = true;
                            break;
                        }
                    }
                    if (flag == true) {
                        imageProcess.noequl("interval ", interval);
                        imageProcess.noequl("sorta[p * 7 + 1] - sorta[q * 7 + 2] ", sorta[p * 7 + 1] - sorta[q * 7 + 2]);
                        if (sorta[p * 7 + 1] - sorta[q * 7 + 2] > interval * radio) {
                            break;
                        }
                        forword = q;
                    } else {
                        if (sorta[forword * 7 + 1] - sorta[p * 7 + 2] < interval * radio) {
                            forword = p;
                        }

                        // imageProcess.noequl("forword mei you le ", p);
                        break;
                    }
                }

            }
            for (int p = back; p < count; p++) {
                if (sorta[p * 7] == 0) {
                    int q = p + 1;
                    boolean flag = false;
                    for (; q < count; q++) {
                        if (sorta[q * 7] == 0) {
                            flag = true;
                            break;
                        }
                    }
                    if (flag == true) {
                        imageProcess.noequl("back ----ssorta[q * 7 + 1] - sorta[p * 7 + 2] ", sorta[q * 7 + 1] - sorta[p * 7 + 2]);
                        if (sorta[q * 7 + 1] - sorta[p * 7 + 2] > interval * radio) {
                            // imageProcess.noequl("break back ", p);
                            break;
                        }
                        back = q;
                    } else {
                        if (sorta[p * 7 + 1] - sorta[back * 7 + 2] < interval * radio) {
                            back = p;
                        }
                        break;
                    }
                }
            }
        }
        //   }
        getAveragePercent(sorta);
    }

    public void removeBasedOnBouttomTop(int[] sorta, int width, int height, int id) {
        int countHeight = 0;
        for (int i = 0; i < count; i++) {
            if (sorta[i * 7] == 0) {
                countHeight++;
            }
        }
        imageProcess.noequl("", 1);
        //  removeMaxMinNoise(sorta, width, avrageHeight, id);
        int count_num1 = 0;
        for (int i = 0; i < count; i++) {
            if (sorta[i * 7] == 0) {
                count_num1++;
            }
        }
        getAvrageHeight(sorta);
        imageProcess.noequl("avrageHeight=", avrageHeight);
        for (int i = 0; i < count; i++) {
            if (sorta[i * 7] == 0) {
                //宽
                int w = Math.abs(sorta[i * 7 + 2] - sorta[i * 7 + 1]);
                int h = Math.abs(sorta[i * 7 + 4] - sorta[i * 7 + 3]);
                imageProcess.noequl("w =" + w + "  h=", h);
                imageProcess.noequl("------------------------minx=", sorta[i * 7 + 1]);
                if (w >= avrageHeight*5/4) {
                    sorta[i * 7] = 1;
                }
                if (w < 3) {
                    sorta[i * 7] = 1;
                }
                //高
                if (h > avrageHeight * 5 / 4) {
                    sorta[i * 7] = 1;
                }
                if (h < avrageHeight * 3 / 4) {
                    sorta[i * 7] = 1;
                }
            }
        }

        int count_num = 0;
        for (int i = 0; i < count; i++) {
            if (sorta[i * 7] == 0) {
                count_num++;
            }
        }
        imageProcess.noequl("", 1);
    }

    public void removeLine(int[] by, int[] pixels, int width, int height, int id) {
        //排除竖直长线
        int count = 0;
        for (int i = 0; i<width; i++) {
            int len = 0;
            for (int j = 0; j < height; j++) {
                if (by[j * width + i] != 255) {
                    len++;
                }
            }
            if (len >= height-2) {
                count++;
                for (int jj = 0; jj < height; jj++) {
                    by[jj * width + i] = 255;
                    int gray = 255;
                    int newcolor = (gray << 16) | (gray << 8) | (gray);
                    pixels[jj * width + i] = newcolor;
                }
            }
        }


        //排除水平长线
        int level = 3 * height / 4;// 55-id*10;
        for (int j = 0; j < height; j++) {
            int len = 0;
            for (int i = 0; i < width - 1; i++) {
                if (by[j * width + i] != 255) {
                    len++;
                } else {
                    if (by[j * width + i + 1] != 255) {
                        len++;
                    } else {
                        if (len > level) {
                            for (int ii = i - len; ii < i - 1; ii++) {
                                by[j * width + ii] = 255;
                                int gray = 255;
                                int newcolor = (gray << 16) | (gray << 8) | (gray);
                                pixels[j * width + ii] = newcolor;
                            }
                        }
                        len = 0;
                    }
                }
                if (i >= width - 2) {
                    if (len > level) {
                        for (int ii = i; ii > i - len; ii--) {
                            by[j * width + ii] = 255;////// 越界
                            int gray = 255;
                            int newcolor = (gray << 16) | (gray << 8) | (gray);
                            pixels[j * width + ii] = newcolor;
                        }
                    }
                }
            }
        }

    }

    public int byAndPixelsOtsu(int[] by, int[] pixels, int width, int height, int startX, int startY, int localWidth, int localHeight, int id) {
        int thresholdvalueAll = Binaryzation.otsu(by, width, height, 0, 0, localWidth, localHeight) + 10;

//        // otsu二值化
//        int COUNT_W = 3;//width/8;
//        int COUNT_H =1;// height/16;
//        //LOGI("width/COUNT_W=%d...height/COUNT_H=%d",width/COUNT_W,height/COUNT_H);
//        for (int k = 1; k <= COUNT_W; k++) {
//            for (int p = 0; p < COUNT_H; p++) {
//                // otsu(int[] proImage, int w, int h, int x0, int y0, int dx,int
//                // dy)
//                int thresholdValue = Binaryzation.otsu(by, width, height,
//                        (k - 1) * (width / COUNT_W), p * (height / COUNT_H),
//                        width / COUNT_W, height / COUNT_H);
//                //Log("thresholdValue[%d]=%d",k,thresholdValue);
////                if(thresholdValue>thresholdvalueAll){
////                    thresholdValue=thresholdvalueAll;
////                }
////                if(thresholdValue-Binaryzation.minValue<30||Binaryzation.maxValue-thresholdValue<30){
////                    thresholdValue=0;
////                }
//                imageProcess.noequl("minValue  ",thresholdValue-Binaryzation.minValue);
//                imageProcess.noequl("maxValue  ",Binaryzation.maxValue-thresholdValue);
//                imageProcess.noequl("------------------------------ ",k);
//
//                for (int j = p * (height / COUNT_H);
//                     j < (p + 1) * (height / COUNT_H); j++)
//                    for (int i = (k - 1) * (width / COUNT_W);
//                         i < (k) * (width / COUNT_W); i++) {
//                        int gray;
//                        // int rgb = bi.getRGB(i, j);
//                        if (by[j * width + i] > thresholdValue) {
//                            gray = 225;
//                            by[j * width + i] = 255;
//                        } else {
//                            gray = 0;
//                            by[j * width + i] = 0;
//                        }
//                        int newcolor = (gray << 16) | (gray << 8) | (gray);
//                        pixels[j * width + i] = newcolor;
//                    }
//
//            }
//            imageProcess.noequl("",1);
//
//        }

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                int gray;
                if (by[j * width + i] > thresholdvalueAll) {
                    gray = 225;
                    by[j * width + i] = 255;
                } else {
                    gray = 0;
                    by[j * width + i] = 0;
                }
                int newcolor = (gray << 16) | (gray << 8) | (gray);
                pixels[j * width + i] = newcolor;
            }
        }


        removeLine(by, pixels, width, height, id);
        return thresholdvalueAll;
    }


    /*
     * 把数组再次排序
	 */
    public void SortAgain(int[] a) {
        int[] sortaa = new int[a.length];
        int j = 0;
        int k = 0;
        for (int g = 0; g < count; g++) {
            int min = 65535;
            for (int i = 0; i < count; i++) {
                if (a[i * 7] == 0) {
                    if (a[i * 7 + 1] < min) {
                        min = a[i * 7 + 1];
                        j = i;
                    }
                }

            }
            sortaa[k++] = a[j * 7];
            sortaa[k++] = a[j * 7 + 1];
            sortaa[k++] = a[j * 7 + 2];
            sortaa[k++] = a[j * 7 + 3];
            sortaa[k++] = a[j * 7 + 4];
            sortaa[k++] = a[j * 7 + 5];
            sortaa[k++] = a[j * 7 + 6];
            a[j * 7] = 1;
        }
        for (int i = 0; i < sortaa.length; i++) {
            a[i] = sortaa[i];
        }
    }

}
