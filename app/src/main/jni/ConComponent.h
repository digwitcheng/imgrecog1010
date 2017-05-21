/*********************************/
/*Lv Shujing**********************/ 
/*���ܣ� ��ͨԪ���ٱ궨
/*********************************/
#ifndef _INC_ConComponentAPI
#define _INC_ConComponentAPI
#include<stdio.h>
//typedef unsigned long LONG;
//typedef int BOOL ;
//#define true 0
//#define false 1
typedef enum { FALSE, TRUE }BOOL;

//---begin---��ͨԪ�㷨�ṹ��---begin----//
typedef struct a{
	int y;//�γ̵�Y����ֵ
	int beginx;//�γ̿�ʼ����X����ֵ
	int endx;//�γ�ĩ�˵�X����ֵ
	struct a *next;//ָ����ͨԪ�е���һ���γ�
	struct a *linenext;//ָ������е���һ���γ�
}runnode;//�γ̽ڵ�
typedef struct b{
	int minx;//��ͨԪ����������ص�����
	int maxx;//��ͨԪ�����ұ����ص�����
	int miny;//��ͨԪ�����ϱ����ص�����
	int maxy;//��ͨԪ�����±����ص�����
}shape;
typedef struct d{
	runnode *firstrunnode;//��ͨԪ�еĵ�һ���γ�
	runnode *lastrunnode;//ָ����ͨԪ�е����һ���γ�
	int value;//��ͨԪ�ı�ǩֵ
	shape compshape;//��ͨԪ����״��Ϣ
	int pixelnum;//��ͨԪ�е�ǰ��������
	BOOL sign;//��signΪTRUE�����ʾvalueֵ��ʾ��component������ͨԪ�ĸ�������ֵ����signΪFALSE,���ʾ��component������ͨԪ�ĸ�����valueֵ��ʾ����ɫֵ
	//CPoint Barycenter;//��ͨԪ������
}component;

typedef struct e{
	int value;//component�ı�ǩֵ
	shape compshape;//component����������Ϣ
	int pixelnum;//component��ǰ��������
	BOOL sign;//��signΪTRUE�����ʾvalueֵ��ʾ��component������ͨԪ�ĸ�������ֵ����signΪFALSE,���ʾ��component������ͨԪ�ĸ�����valueֵ��ʾ����ɫֵ
	unsigned int distance;//��ˮ�صķ�����Ϊ1����ˮ�ط������ϣ���Ϊ-1����ˮ�ط������£���Ϊ0����һ���ն�����Ϊ����ֵ����˵������һ����ˮ��
	//CPoint Barycenter;//component������
}CodeComponent;
//---end---��ͨԪ�㷨�ṹ��----end------//

//�����ͨԪ
int ConCompLabelling8(CodeComponent *rescomponent,int *lpDIB, int lWidth, int lHeight ,int max_num);
void ReleaseList(runnode **HeadRun,int Length);//�ͷ���ͨԪ�б��ڴ�
#endif

