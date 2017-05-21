package cn.hxc.imgrecognition;

import java.nio.charset.MalformedInputException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by hxc on 201RES_COUNT/5/4.
 */

public class RemoveNoise {
    // public final static int RES_COUNT=7;
     public int count;
     public int forword ;
     public int back;
     public  float averagePercent;
     public  int iTop;
     public int iBottom;
     public  int avrageHeight;

    public  void CombainOne(int []a,int width,int height){
        //TODO Combain
//****合并相交或包含关系的连通元***************
//        for(int i=0;i<count;i++)
//        {
//            if(a[i*7]==1)
//                continue;
//            for(int j=0;j<count;j++)
//            {
//                if(i!=j&&a[j*7]==0)//[j*7].sign==FALSE
//                {
//                    if(a[i] .maxx-a[i] .minx+1+a[j] .maxx-a[j] .minx+1
//                            >max(a[i] .maxx,a[j] .maxx)-min(a[i] .minx,a[j] .minx)+1
//                            &&a[i] .maxy-a[i] .miny+1+a[j] .maxy-a[j] .miny+1
//                            >max(a[i] .maxy,a[j] .maxy)-min(a[i] .miny,a[j] .miny)+1)
//                    //	&&max(a[i] .maxx,a[j] .maxx)-min(a[i] .minx,a[j] .minx)+1<temp_wid+10
//                    //	&&max(a[i] .maxy,a[j] .maxy)-min(a[i] .miny,a[j] .miny)+1<temp_hei+10)
//                    {
//                        if(i>j)//20090922做了修改
//                        {
//                            a[i].sign=TRUE;
//                            a[j] .minx=min(a[i] .minx,a[j] .minx);
//                            a[j] .maxx=max(a[i] .maxx,a[j] .maxx);
//                            a[j] .miny=min(a[i] .miny,a[j] .miny);
//                            a[j] .maxy=max(a[i] .maxy,a[j] .maxy);
//                            a[j].pixelnum=a[i].pixelnum+a[j].pixelnum;
//                            i=j-1;
//                            break;
//                        }
//                        else
//                        {
//                            a[j].sign=TRUE;
//                            a[i] .minx=min(a[i] .minx,a[j] .minx);
//                            a[i] .maxx=max(a[i] .maxx,a[j] .maxx);
//                            a[i] .miny=min(a[i] .miny,a[j] .miny);
//                            a[i] .maxy=max(a[i] .maxy,a[j] .maxy);
//                            a[i].pixelnum=a[i].pixelnum+a[j].pixelnum;
//                            break;
//                        }
//                    }
//                }
//            }
//        }
//
//        for(i=0;i<count;i++)
//        {
//            if(a[i].sign==FALSE)
//            {
//                //	count_new++;
//                a1[count_new].sign=FALSE;
//                a1[count_new] .minx=a[i] .minx;
//                a1[count_new] .maxx=a[i] .maxx;
//                a1[count_new] .miny=a[i] .miny;
//                a1[count_new] .maxy=a[i] .maxy;
//                a1[count_new].value=a[i].value;
//                a1[count_new].pixelnum=a[i].pixelnum;
//                count_new++;
//            }
//            if(count_new>199)
//                break;
//        }
//        count=count_new;
//        //*****合并相交或包含关系的连通元******结束*******
    }

    public  int getAvrageHeight(int []sorta){
        int sumHeight=0;
        int countHeight=0;
        for (int i = 0; i < count; i++) {
            if (sorta[i * 7] == 0) {
                sumHeight+=Math.abs(sorta[i * 7 + 4] - sorta[i * 7 + 3]);
                countHeight++;
            }
        }
        if(countHeight==0){
            return  -1;
        }else {
            avrageHeight = sumHeight / countHeight;
            return avrageHeight;
        }
    }
    public  void initCount(int []a){
        count = a.length / 7;
        iTop=0;
        iBottom=count;
    }

    /**
     *  合并同一个字符
     */
    public void CombainOneCharact(int []sorta,int width,int height){
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
                if (xmin < ylen_old / 2|| (xmin) < 4 || (xmax) < 4
                        || (xmax_oldmin < 3 && Math.abs(ylen_old - ylen) > height/10)) {
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

        SortAgain(sorta);
        getAvrageHeight(sorta);
    }

    //根据过度行分割，确定所需要的连通元
    public void   RestoreCom(int []a,int[]by1,int width,int height){
        //把所有的连通元都设为不可yong
        int countNum=0;
        for (int i = 0; i <count; i++) {
            if(a[i*7]==0){
                countNum++;
            }
            a[i * 7] = 1;
        }
        //检测每个联通元区域，对应的by1是否有像素，有的联通圆才设为可用
        for (int i = 0; i <count; i++) {
            for (int p = a[i*7+3]; p < a[i*7+4]; p++) {
                for (int q = a[i*7+1]; q < a[i*7+2]; q++) {
                    int gray = by1[p*width+q];
                    if(gray==0){
                        a[i*7]=0;
                        break;
                    }
                }
            }
        }

//        int countNum1=0;
//        for (int i = 0; i <count; i++) {
//            if(a[i*7]==0){
//                countNum1++;
//            }
//        }
//        imageProcess.noequl("",1);
    }
    //号码行预提取
   public  int PreSegment_Line_hxc(int[] proImage, int width, int height)
    {
        //TODO 行分割hxc
        int []linePix=new int[width];
        for(int i=0;i<height;i++){
            for (int j = 0; j < width; j++) {
                if(proImage[i*width+j]<255){
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

        int lineCount=0;
        int sum=0;
        for (int i = 0; i < height; i++) {
            if(linePix[i]>5){
                lineCount++;
                sum+=linePix[i];
            }
        }
        if(lineCount==0){
            return  -1;
        }
        int threshold=sum/lineCount;
        ArrayList<textLine> lines=new ArrayList<>();
        for (int i = 0; i < height; i++) {
            textLine line=new textLine();
            if(linePix[i]<=threshold||i==height-1){
                continue;
            }
            line.setTop(i);
            int j=i+1;
            for (; j <height ; j++) {
                if(linePix[j]<=threshold){
                 line.setBottom(j-1);
                    break;
                }
            }
            i=j;
            if(j>=height-1){
                line.setBottom(j);
            }
            lines.add(line);
        }
        if (lines.size()>0) {
            int min = Integer.MAX_VALUE;
            textLine midLine = lines.get(0);
            for (textLine line : lines
                    ) {
                int temp = Math.abs((line.getBottom() + line.getTop())/2 - height / 2);
                if (min > temp) {
                    min = temp;
                    midLine = line;
                }
            }
//            iTop=lines.get(0).getTop();
//            iBottom=lines.get(0).getBottom();
            iTop=midLine.getTop();
            iBottom=midLine.getBottom();
            if(iBottom-iTop>0)
            {
                for(int i=0;i<iTop;i++)
                {
                    for(int j=0;j<width;j++)
                        proImage[i*width+j]=255;
                }
                for(int i=iBottom;i<height;i++)
                {
                    for(int j=0;j<width;j++)
                        proImage[i*width+j]=255;
                }
            }
            return lines.size();
        }else{
            return -1;
        }

    }
    /**
     * 从中间开始，去除上下非中间行
     */
    //号码行预提取
    void PreSegment_Line(int[] proImage, int width, int height)
    {
        int i,j;
        int iLen;
        int iMaxLen;

        //找上边缘
        iTop=0;
        for(i=height/2;i>0;i--)
        {
            iLen=0;
            iMaxLen=0;
            for(j=0;j<width;j++)
            {
                if(proImage[i*width+j]==255)
                    iLen++;
                else if(proImage[i*width+j]==0&&iLen>0)
                {
                    if(iLen>iMaxLen)
                        iMaxLen=iLen;
                    iLen=0;
                }

            }
            if(iLen>iMaxLen)
                iMaxLen=iLen;
            if(iMaxLen>width*2/3)
            {
                iTop=i-1;
                break;
            }
        }
        //找下边缘
        iBottom=height-1;
        for(i=height/2;i<height-1;i++)
        {
            iLen=0;
            iMaxLen=0;
            for(j=0;j<width;j++)
            {
                if(proImage[i*width+j]==255)
                    iLen++;
                else if(proImage[i*width+j]==0&&iLen>0)
                {
                    if(iLen>iMaxLen)
                        iMaxLen=iLen;
                    iLen=0;
                }

            }
            if(iLen>iMaxLen)
                iMaxLen=iLen;
            if(iMaxLen>width*2/3)
            {
                iBottom=i+1;
                break;
            }
        }
        //
        if(iBottom-iTop>20)
        {
            for(i=0;i<iTop;i++)
            {
                for(j=0;j<width;j++)
                    proImage[i*width+j]=255;
            }
            for(i=iBottom;i<height;i++)
            {
                for(j=0;j<width;j++)
                    proImage[i*width+j]=255;
            }
        }else{
            iTop=0;
            iBottom=height-1;
        }

    }

    /**
     * 根据像素点的多少去除噪音
     */
    public void aaa(){
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

    /**
     * 移除比较大的或比较小的前景像素
     * @param a 原图像
     * @param width
     * @param height
     * @param id
     */
    public void removeMaxMinNoise(int []a, int width,int height,int id){
        for (int i = 0; i < count; i++) {
            if (a[i * 7] == 0) {
                if (Math.abs(a[i * 7 + 2] - a[i * 7 + 1]) >= 3*height) {
                    a[i * 7] = 1;
                }
                if (Math.abs(a[i * 7 + 4] - a[i * 7 + 3]) >= 3*height) {
                    a[i * 7] = 1;
                }
                if (Math.abs(a[i * 7 + 4] - a[i * 7 + 3]) <height/2) {
                    a[i * 7] = 1;
                }
            }
        }

    }
    public  void initTopBottom(){
        iTop=0;
        iBottom=count;
    }
    public  void initSorta(int []a,int[] sorta){

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
    public void getAveragePercent(int[]sorta){
        averagePercent = 0;
        int sum = 0;
        int   count_num = 0;// 重新计数
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
        for (int i = forword; i <=back; i++) { // 若1残缺，应该是最大比例
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
    public int  getAvrageInterval(int []sorta){
        int count_num = 0;
        ArrayList<Integer> array=new ArrayList<>();

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
                    int internal=sorta[j * 7 + 1] - sorta[i * 7 + 2];
                    array.add(internal);
                }
            }
        }
        if(array.size()>0) {
            UpCompar upCompar = new UpCompar();
            Collections.sort(array, upCompar);
            int temp = array.get(array.size() / 2);
            return temp;
        }else {
            return -1;
        }
    }
    public void initForwordAndBack(int[] sorta){
        int interval= getAvrageInterval(sorta)*3;

        if(interval<0){
            interval=iBottom-iTop;
        }
        int count_num1 = 0;
        for (int i = 0; i < count; i++) {
            if (sorta[i * 7] == 0) {
                count_num1++;
            }
        }
        if (count_num1 == 0) {
            return;
        }
        forword = count_num1 / 2;
        back = count_num1 / 2;
        // imageProcess.noequl("sorta.length / 7= ", sorta.length / 7);
//        for (int loop = 3; loop >= 1;loop--) {
//            if (back - forword > 4 || loop == 0) {
//                // imageProcess.noequl("loop ", loop);
//                break;
//            }
//            forword = count_num1 / 2;
//            back = count_num1 / 2;
            // imageProcess.noequl("init forword ", forword);
            // imageProcess.noequl("init back ", back);
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
                        imageProcess.noequl("avrageHeight loop ", interval);
                        imageProcess.noequl("sorta[p * 7 + 1] - sorta[q * 7 + 2] ", sorta[p * 7 + 1] - sorta[q * 7 + 2]);
                        if (sorta[p * 7 + 1] - sorta[q * 7 + 2] > interval) {
                            forword = p;
                            break;
                        }
                    }else{
                        forword = p;
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
                        if (sorta[q * 7 + 1] - sorta[p * 7 + 2] > interval) {
                            back = p;
                            // imageProcess.noequl("break back ", p);
                            break;
                        }
                    } else {
                        // imageProcess.noequl("back mei you le ", p);
                        back = p;
                        break;
                    }
                }
            }
     //   }
        getAveragePercent(sorta);
    }

    public  void   removeBasedOnBouttomTop(int []sorta,int width,int height,int id){
        removeMaxMinNoise(sorta,width,avrageHeight,id);
        int count_num1 = 0;
        for (int i = 0; i < count; i++) {
            if (sorta[i * 7] == 0) {
                count_num1++;
            }
        }
        getAvrageHeight(sorta);

        for (int i = 0; i < count; i++) {
            if (sorta[i * 7] == 0) {
                //宽
                if (Math.abs(sorta[i * 7 + 2] - sorta[i * 7 + 1]) >= avrageHeight) {
                    sorta[i * 7] = 1;
                }
                if ((Math.abs(sorta[i * 7 + 2] - sorta[i * 7 + 1]) < 3)) {
                    sorta[i * 7] = 1;
                }
                //高
                if ((Math.abs(sorta[i * 7 + 4] - sorta[i * 7 + 3]) > avrageHeight*5/4)) {
                    sorta[i * 7] = 1;
                }
                if ((Math.abs(sorta[i * 7 + 4] - sorta[i * 7 + 3]) < avrageHeight*3/4)) {
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
        imageProcess.noequl("",1);
    }

    public void removeLine(int[] by, int[] pixels, int width, int height,int id) {
        //排除竖直长线
        int max=0;
        for (int i = width-1; i >0; i--) {
            int len = 0;
            for (int j = 0; j < height; j++) {
                if (by[i * height + j] != 255) {
                    len++;
                }
            }
            if(max<len){
                max=len;
            }
            if(len>=height-1){
                for (int j = 0; j < height; j++) {
                    by[i * height + j] = 255;
                    int gray = 255;
                    int newcolor = (gray << 16) | (gray << 8) | (gray);
                    pixels[i * height + j] = newcolor;
                }
            }
        }

        //排除水平长线
        int level = 3*height/4;// 55-id*10;
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

    public int  byAndPixelsOtsu(int[] by, int[] pixels, int width, int height, int startX, int startY, int localWidth, int localHeight,int id) {
        int thresholdValue = Binaryzation.otsu(by, width, height, 0, 0, localWidth, localHeight);
        // imageProcess.noequl("threshold==== ", thresholdValue);
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                int gray;
                if (by[j * width + i] > thresholdValue) {
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
        removeLine(by,pixels,width,height,id);
        return thresholdValue;
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
