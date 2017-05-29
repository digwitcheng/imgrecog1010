package cn.hxc.imgrecognition;



/**
 * Created by hxc on 2017/5/8.
 */

public class Binaryzation {

    public static int maxValue;
    public  static int minValue;
    /*
    * 大津算法
    */
    public static int otsu(int[] by, int w, int h, int x0, int y0, int dx, int dy) {
        // unsigned char *np; // 图像指针
        int thresholdValue = 1; // 阈值
        int ihist[] = new int[256]; // 图像直方图，256个点
        int k; // various RES_COUNTers
        int n, n1, n2;
        double m1, m2, sum, csum, fmax, sb;
        int max=Integer.MIN_VALUE;
        int min=Integer.MAX_VALUE;
        // for (int i = 0; i < by.length; i++) {
        // int np = by[i];
        // ihist[np]++;
        // } //otsu(by, avga, width , height, width / 4, 0,width/2,0)
        for (int j = y0; j < y0 + dy; j++)
            for (int i = x0; i < x0 + dx; i++) {
                {
                    try {
                        int np = by[j * w + i];
                        ihist[np]++;
                    } catch (Exception e) {
                        System.out.println("yuejie .........." + i + "  " + j);
                    }
                }
            }

        sum = csum = 0.0;
        n = 0;
        for (k = 0; k <= 255; k++) {
            sum += (double) k * (double) ihist[k]; // x*f(x) 质量矩
            n += ihist[k]; // f(x) 质量
        }
        for (int i = 0; i < 256; i++) {
            if(ihist[i]>0){
                min=i;
                break;
            }
        }
        for (int i = 255; i >=0; i--) {
            if(ihist[i]>0){
                max=i;
                break;
            }
        }
        maxValue=max;
        minValue=min;
        if (n == 0) {
            return (160);
        }
        fmax = -1.0;
        n1 = 0;
        for (k = 0; k <= 255; k++) {
            n1 += ihist[k];
            if (n1 == 0) {
                continue;
            }
            n2 = n - n1;
            if (n2 == 0) {
                break;
            }
            csum += (double) k * ihist[k];
            m1 = csum / n1;
            m2 = (sum - csum) / n2;
            sb = (double) n1 * (double) n2 * (m1 - m2) * (m1 - m2);
			/**//* bbg: note: can be optimized. */
            if (sb > fmax) {
                fmax = sb;
                thresholdValue = k;
            }
        }
        return thresholdValue;// (thresholdValue);
    }
}
