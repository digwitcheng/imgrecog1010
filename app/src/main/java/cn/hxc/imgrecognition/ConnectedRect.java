package cn.hxc.imgrecognition;

/**
 * Created by hxc on 2017/5/23.
 */

public class ConnectedRect {
    public int minx;
    public int maxx;
    public int miny;
    public int maxy;
    public int pixelnum;//前景像素数
    public boolean isUsable;//true表示连通元可用，false表示不可用
    public int value;//component的标签值


    /*
    connectedRect=new ConnectedRect[count];
		int g=0;
		for (int j = 0; j < a.length/7; j++) {
			connectedRect[g]=new ConnectedRect();
			connectedRect[g].isUsable=(a[j*7]==0?true:false);
			connectedRect[g].minx=a[j*7+1];
			connectedRect[g].maxx=a[j*7+2];
			connectedRect[g].miny=a[j*7+3];
			connectedRect[g].maxy=a[j*7+4];
			connectedRect[g].pixelnum=a[j*7+5];
			connectedRect[g].value=a[j*7+6];
			g++;
		}
     */


    /*
     public  void CombainOne_jing(ConnectedRect []a,int width,int height){
        //TODO Combain

        int i,j;

        int count_new=0;
        ConnectedRect a1[]=new ConnectedRect[200];
        for(i=0;i<count;i++)
        {
            if(a[i].isUsable==false)
                continue;
            for(j=0;j<count;j++)
            {
                if(i!=j&&a[j].isUsable==true )
                {
                    if(a[i] .maxx-a[i] .minx+1+a[j] .maxx-a[j] .minx+1
                            >max(a[i] .maxx,a[j] .maxx)-min(a[i] .minx,a[j] .minx)+1
                            &&a[i] .maxy-a[i] .miny+1+a[j] .maxy-a[j] .miny+1
                            >max(a[i] .maxy,a[j] .maxy)-min(a[i] .miny,a[j] .miny)+1)
                    //	&&max(a[i] .maxx,a[j] .maxx)-min(a[i] .minx,a[j] .minx)+1<temp_wid+10
                    //	&&max(a[i] .maxy,a[j] .maxy)-min(a[i] .miny,a[j] .miny)+1<temp_hei+10)
                    {
                        if(i>j)//20090922做了修改
                        {
                            a[i].isUsable=false;
                            a[j] .minx=min(a[i] .minx,a[j] .minx);
                            a[j] .maxx=max(a[i] .maxx,a[j] .maxx);
                            a[j] .miny=min(a[i] .miny,a[j] .miny);
                            a[j] .maxy=max(a[i] .maxy,a[j] .maxy);
                            a[j].pixelnum=a[i].pixelnum+a[j].pixelnum;
                            i=j-1;
                            break;
                        }
                        else
                        {
                            a[j].isUsable=false;
                            a[i] .minx=min(a[i] .minx,a[j] .minx);
                            a[i] .maxx=max(a[i] .maxx,a[j] .maxx);
                            a[i] .miny=min(a[i] .miny,a[j] .miny);
                            a[i] .maxy=max(a[i] .maxy,a[j] .maxy);
                            a[i].pixelnum=a[i].pixelnum+a[j].pixelnum;
                            break;
                        }
                    }
                }
            }
        }

        for(i=0;i<count;i++)
        {
            if(a[i].isUsable ==true)
            {
                //	count_new++;
                a1[count_new].isUsable=true;
                a1[count_new] .minx=a[i] .minx;
                a1[count_new] .maxx=a[i] .maxx;
                a1[count_new] .miny=a[i] .miny;
                a1[count_new] .maxy=a[i] .maxy;
                a1[count_new].value=a[i].value;
                a1[count_new].pixelnum=a[i].pixelnum;
                count_new++;
            }
            if(count_new>199)
                break;
        }
   //     count=count_new;
//        //*****合并相交或包含关系的连通元******结束*******
    }
     */
}
