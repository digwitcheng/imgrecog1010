/*********************************/
/*Lv Shujing**********************/ 
/*功能： 连通元快速标定
/*********************************/
#ifndef _INC_ConComponentAPI
#define _INC_ConComponentAPI
#include<stdio.h>
//typedef unsigned long LONG;
//typedef int BOOL ;
//#define true 0
//#define false 1
typedef enum { FALSE, TRUE }BOOL;

//---begin---连通元算法结构体---begin----//
typedef struct a{
	int y;//游程的Y座标值
	int beginx;//游程开始处的X座标值
	int endx;//游程末端的X座标值
	struct a *next;//指向连通元中的下一个游程
	struct a *linenext;//指向该行中的下一个游程
}runnode;//游程节点
typedef struct b{
	int minx;//连通元的最左边像素的坐标
	int maxx;//连通元的最右边像素的坐标
	int miny;//连通元的最上边像素的坐标
	int maxy;//连通元的最下边像素的坐标
}shape;
typedef struct d{
	runnode *firstrunnode;//连通元中的第一个游程
	runnode *lastrunnode;//指向连通元中的最后一个游程
	int value;//连通元的标签值
	shape compshape;//连通元的形状信息
	int pixelnum;//连通元中的前景像素数
	BOOL sign;//若sign为TRUE，则表示value值表示该component所在连通元的根的索引值，若sign为FALSE,则表示该component所在连通元的根，其value值表示其颜色值
	//CPoint Barycenter;//连通元的重心
}component;

typedef struct e{
	int value;//component的标签值
	shape compshape;//component的外界矩阵信息
	int pixelnum;//component的前景像素数
	BOOL sign;//若sign为TRUE，则表示value值表示该component所在连通元的根的索引值，若sign为FALSE,则表示该component所在连通元的根，其value值表示其颜色值
	unsigned int distance;//蓄水池的方向，若为1则蓄水池方向向上，若为-1则蓄水池方向向下，若为0则是一个空洞，若为其他值，则说明不是一个蓄水池
	//CPoint Barycenter;//component的重心
}CodeComponent;
//---end---连通元算法结构体----end------//

//标记连通元
int ConCompLabelling8(CodeComponent *rescomponent,int *lpDIB, int lWidth, int lHeight ,int max_num);
void ReleaseList(runnode **HeadRun,int Length);//释放连通元列表内存
#endif

