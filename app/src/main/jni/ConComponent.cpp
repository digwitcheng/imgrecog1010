#include <stdlib.h>
#include "ConComponent.h"
#include<malloc.h>
#include"android/log.h"
#include<string.h>
#define LOG_TAG "System.out"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)


//���ܣ��ͷ���ͬԪ�궨ʱ����������
void ReleaseList(runnode **HeadRun,int Length)
{
	runnode *current=NULL;
	runnode *precurrent=NULL;
	for(int i=0;i<Length;i++)//�ͷ��γ̱�
    {
		if(HeadRun[i]!=NULL)
		{
			current=HeadRun[i];
			while(current!=NULL)
			{
				precurrent=current;
				current=current->linenext;
				delete precurrent;
			}
		}
	}

}
//��ͨԪ�궨

int ConCompLabelling8(CodeComponent *rescomponent,int *lpDIB, int lWidth, int lHeight ,int max_num)
{
	// ָ��Դͼ���ָ��
	int *pdibData;
	//��ͼ���ָ��
	int *pdata;
	pdata=(int *)malloc((lWidth+2)*(lHeight+2)*sizeof(int));
	pdibData=(int *)malloc((lWidth)*(lHeight)*sizeof(int));
	if(pdata==NULL)
		return -1;
	// ͼ��ÿ�е��ֽ���
	int	lLineBytes;

//	pdibData = lpDIB;
	for (int i = 0; i < lWidth*lHeight; ++i) {
		pdibData[i]=lpDIB[i];
	}
    lLineBytes = lWidth;

	int count;
	int i,j;
	int temp,nexttemp;
	runnode *current=NULL;
	runnode *nextcurrent=NULL;
	runnode *precurrent=NULL;
	runnode *preceding=NULL;

	component stack[1600];//��ͨԪ��ǩ��ջ
    runnode **headrun;//ÿ�е��γ�ָ��
	headrun=(runnode**)malloc((lHeight+2)*sizeof(runnode*));
	if(headrun==NULL)
	{
		free(pdata);
		return -1;
	}
	for(i=0;i<lHeight+2;i++)
		headrun[i]=NULL;
	for(i=0;i<1600;i++)//��ʼ����ջ
	{
		stack[i].firstrunnode=NULL;
		stack[i].lastrunnode=NULL;
		stack[i].value=255;
		stack[i].pixelnum=0;
	}

	// ��ʼ������pdata��ʼ************
	for(i=0;i<lHeight+1;i++)
	{
		for(j=0;j<lWidth+2;j++)
		{
			if(i==0)
				pdata[j]=255;
			else
			{
				if(j!=0&&j!=(lWidth+1)) //////////////////////////////////////
				{
					temp=lLineBytes*(i-1)+j-1;
					pdata[i*(lWidth+2)+j]=(int)pdibData[temp];
				}
				else
				{
					pdata[i*(lWidth+2)+j]=255;
				}
			}
		}
	}
	for(i=lHeight+1,j=0;j<lWidth+2;j++)
		pdata[i*(lWidth+2)+j]=255;
	//��ʼ������pdata����********************

	//��ʼ����ͨԪ��ǩ***********************
	count=1;
	for(i=1;i<lHeight+1;i++)
	{
		for(j=1;j<lWidth+1;j++)
		{
			temp=(lWidth+2)*i+j;
			if(pdata[temp]!=255)//��ǰ����Ϊǰ������
			{
				if((pdata[temp-1]+pdata[temp-lWidth-1]+pdata[temp-lWidth-2]+pdata[temp-lWidth-3])!=255*4)
				{
					if(pdata[temp-1]!=255)//�������Ϊǰ������
					{
						current->endx++;
						pdata[temp]=stack[pdata[temp-1]].value;
					}
					else if(pdata[temp-lWidth-3]!=255)//����
					{
						preceding=current;
						current=new runnode;
						if(current==NULL)
						{
							ReleaseList(headrun,lHeight+2);///zhoulijun add
							free(pdata);
							free(headrun);
							return 0;
						}
						if(headrun[i]==NULL)
						{
							headrun[i]=current;
							preceding=current;
						}
						else
						preceding->linenext=current;
						current->y=i;
						current->beginx=j;
						current->endx=j;
						current->linenext=NULL;
						current->next=NULL;
						if(stack[pdata[temp-lWidth-3]].lastrunnode==NULL)
						{
							//AfxMessageBox("stack[pdata[temp-lWidth-3]].lastrunnode==NULL");
							ReleaseList(headrun,lHeight+2);///zhoulijun add
							free(pdata);
							free(headrun);
							return 0;

						}
						stack[pdata[temp-lWidth-3]].lastrunnode->next=current;
						stack[pdata[temp-lWidth-3]].lastrunnode=current;
						pdata[temp]=stack[pdata[temp-lWidth-3]].value;
					}//���Ͻ���

					else if(pdata[temp-lWidth-2]!=255)//��������
					{
						preceding=current;
						current=new runnode;
						if(current==NULL)
						{
							ReleaseList(headrun,lHeight+2);///zhoulijun add
							free(pdata);
							free(headrun);
							return 0;
						}
						if(headrun[i]==NULL)
						{
							headrun[i]=current;
							preceding=current;
						}
						else
							preceding->linenext=current;
						current->y=i;
						current->beginx=j;
						current->endx=j;
						current->linenext=NULL;
						current->next=NULL;
						if(stack[pdata[temp-lWidth-2]].lastrunnode==NULL)
						{
							//AfxMessageBox("stack[pdata[temp-lWidth-2]].lastrunnode==NULL");

							ReleaseList(headrun,lHeight+2);///zhoulijun add
							free(pdata);
							free(headrun);
							return 0;
						}
						if(stack[pdata[temp-lWidth-2]].lastrunnode->next==NULL)
							stack[pdata[temp-lWidth-2]].lastrunnode->next=current;
						stack[pdata[temp-lWidth-2]].lastrunnode=current;
						pdata[temp]=stack[pdata[temp-lWidth-2]].value;
					}//���Ͻ���

					else if(pdata[temp-lWidth-1]!=255)//����
					{
						preceding=current;
						current=new runnode;
						if(current==NULL)
						{
							ReleaseList(headrun,lHeight+2);///zhoulijun add
							free(pdata);
							free(headrun);
							return 0;
						}
						if(headrun[i]==NULL)
						{
							headrun[i]=current;
							preceding=current;
						}
						else
							preceding->linenext=current;
						current->y=i;
						current->beginx=j;
						current->endx=j;
						current->linenext=NULL;
						current->next=NULL;
						if(stack[pdata[temp-lWidth-1]].lastrunnode==NULL)
						{
							//AfxMessageBox("stack[pdata[temp-lWidth-1]].lastrunnode==NULL");
							ReleaseList(headrun,lHeight+2);///zhoulijun add
							free(pdata);
							free(headrun);
							return 0;
						}
						stack[pdata[temp-lWidth-1]].lastrunnode->next=current;
						stack[pdata[temp-lWidth-1]].lastrunnode=current;
						pdata[temp]=stack[pdata[temp-lWidth-1]].value;
					}//���Ͻ���
				}//end for (***if((pdata[temp-1]+pdata[temp-lWidth-1]+pdata[temp-lWidth-2]+pdata[temp-lWidth-3])!=255*4)***)
				else//��ʼһ���µ���ͨԪ
				{
					//LOGI("count else=%d",count);
					preceding=current;
					current=new runnode;
					if(current==NULL)
					{
						ReleaseList(headrun,lHeight+2);///zhoulijun add
						free(pdata);
						free(headrun);
						return 0;
					}
					if(headrun[i]==NULL)
					{
						headrun[i]=current;
						preceding=current;
					}
					else
						preceding->linenext=current;
					current->beginx=j;
					current->endx=j;
					current->y=i;
					current->linenext=NULL;
					current->next=NULL;
					pdata[temp]=count;
					count++;
					if(count>1599)
					{
						i=lHeight+2;
						j=lWidth+2;
					}
					if(count%255==0)
						count++;
					stack[pdata[temp]].compshape.minx=j;
					stack[pdata[temp]].firstrunnode=current;
					stack[pdata[temp]].value=pdata[temp];
					stack[pdata[temp]].lastrunnode=current;
				}
            }// end for (**if(pdata[temp]!=255)//��ǰ����Ϊǰ������**)
        }//end for (**for(j=1;j<lWidth+1;j++)**)
	}//end for (**for(i=1;i<lHeight+1;i++)**)
	//----------------------------------------------------
	int minx,maxx,miny,maxy;
	for(i=1;i<count;i++)
	{
		minx=lWidth;
		maxx=0;
		miny=lHeight;
		maxy=0;
		current=stack[i].firstrunnode;
		while(current!=NULL)
		{
			if(current->beginx<minx) minx=current->beginx;
			if(current->endx>maxx) maxx=current->endx;
			if(current->y<miny) miny=current->y;
			if(current->y>maxy) maxy=current->y;
			stack[i].pixelnum=stack[i].pixelnum+current->endx-current->beginx+1;
			current=current->next;
		}
		stack[i].compshape.minx=minx;
		stack[i].compshape.maxx=maxx;
		stack[i].compshape.miny=miny;
		stack[i].compshape.maxy=maxy;
		stack[i].sign=FALSE;
	}
	//---��ͨԪ��ǩ��ǽ���-----

	//---�ϲ���ǩΪ��ͨԪ------

	int k;
	int index,nextindex;
	for(i=lHeight;i>1;i--)
	{
		if(headrun[i]!=NULL&&headrun[i-1]!=NULL)
		{
			current=headrun[i];
			nextcurrent=headrun[i-1];
			while(current!=NULL&&nextcurrent!=NULL)
			{

				if((current->beginx)>=(nextcurrent->endx+2)||(current->endx)<=(nextcurrent->beginx-2))
				{
					if(current->beginx>=nextcurrent->endx+2)
						nextcurrent=nextcurrent->linenext;
					else current=current->linenext;
				}

				else if(current->beginx+1<=nextcurrent->beginx)
				{
					temp=current->y*(lWidth+2)+current->beginx;
					nexttemp=nextcurrent->y*(lWidth+2)+nextcurrent->beginx;
					if(stack[pdata[temp]].sign==FALSE&&stack[pdata[nexttemp]].sign==FALSE)
					{
						if(stack[pdata[temp]].value!=stack[pdata[nexttemp]].value)
						{
							index=pdata[temp];//index��ʾ��ǰ�γ��е�����������ͨԪ�ĸ�������
							nextindex=pdata[nexttemp];//nextindex��ʾ��һ�е�ǰ�γ��е�����������ͨԪ�ĸ�������
							stack[nextindex].value=index;//��ʼ������һ���е��γ����ڵ�component������ǰ�γ����ڵ�component
							stack[nextindex].sign=TRUE;
							stack[index].pixelnum=stack[index].pixelnum+stack[nextindex].pixelnum;
							if(stack[index].compshape.minx>stack[nextindex].compshape.minx)
								stack[index].compshape.minx=stack[nextindex].compshape.minx;
							if(stack[index].compshape.maxx<stack[nextindex].compshape.maxx)
								stack[index].compshape.maxx=stack[nextindex].compshape.maxx;
							if(stack[index].compshape.miny>stack[nextindex].compshape.miny)
								stack[index].compshape.miny=stack[nextindex].compshape.miny;
							if(stack[index].compshape.maxy<stack[nextindex].compshape.maxy)
								stack[index].compshape.maxy=stack[nextindex].compshape.maxy;//����
						}
					}
					else if(stack[pdata[temp]].sign==FALSE&&stack[pdata[nexttemp]].sign==TRUE)
					{
						index=pdata[temp];
						nextindex=pdata[nexttemp];
						while(stack[nextindex].sign==TRUE)
						{
							nextindex=stack[nextindex].value;
						}
						if(stack[nextindex].value!=index)
						{
							stack[nextindex].value=index;//��ʼ����һ���е��γ����ڵ�component������ǰ�γ����ڵ�component
							stack[nextindex].sign=TRUE;
							stack[index].pixelnum=stack[index].pixelnum+stack[nextindex].pixelnum;
							if(stack[index].compshape.minx>stack[nextindex].compshape.minx)
								stack[index].compshape.minx=stack[nextindex].compshape.minx;
							if(stack[index].compshape.maxx<stack[nextindex].compshape.maxx)
								stack[index].compshape.maxx=stack[nextindex].compshape.maxx;
							if(stack[index].compshape.miny>stack[nextindex].compshape.miny)
								stack[index].compshape.miny=stack[nextindex].compshape.miny;
							if(stack[index].compshape.maxy<stack[nextindex].compshape.maxy)
								stack[index].compshape.maxy=stack[nextindex].compshape.maxy;
						}//����
					}
					else if(stack[pdata[temp]].sign==TRUE&&stack[pdata[nexttemp]].sign==FALSE)
					{
						index=pdata[temp];
						while(stack[index].sign==TRUE)
						{
							index=stack[index].value;
						}
						nextindex=pdata[nexttemp];
						if(stack[nextindex].value!=index)
						{
							stack[nextindex].value=index;//��ʼ������һ���е��γ����ڵ�component������ǰ�γ����ڵ�component
							stack[nextindex].sign=TRUE;
							stack[index].pixelnum=stack[index].pixelnum+stack[nextindex].pixelnum;
							if(stack[index].compshape.minx>stack[nextindex].compshape.minx)
								stack[index].compshape.minx=stack[nextindex].compshape.minx;
							if(stack[index].compshape.maxx<stack[nextindex].compshape.maxx)
								stack[index].compshape.maxx=stack[nextindex].compshape.maxx;
							if(stack[index].compshape.miny>stack[nextindex].compshape.miny)
								stack[index].compshape.miny=stack[nextindex].compshape.miny;
							if(stack[index].compshape.maxy<stack[nextindex].compshape.maxy)
								stack[index].compshape.maxy=stack[nextindex].compshape.maxy;//����
						}
					}
					else
					{
						if(stack[pdata[temp]].value!=stack[pdata[nexttemp]].value)
						{
							index=pdata[temp];
							while(stack[index].sign==TRUE)
							{
								index=stack[index].value;
							}
							nextindex=pdata[nexttemp];
							while(stack[nextindex].sign==TRUE)
							{
								nextindex=stack[nextindex].value;
							}
							if(stack[nextindex].value!=index)
							{
								stack[nextindex].value=index;//��ʼ����һ���е��γ����ڵ�component������ǰ�γ����ڵ�component
								stack[nextindex].sign=TRUE;
								stack[index].pixelnum=stack[index].pixelnum+stack[nextindex].pixelnum;
								if(stack[index].compshape.minx>stack[nextindex].compshape.minx)
									stack[index].compshape.minx=stack[nextindex].compshape.minx;
								if(stack[index].compshape.maxx<stack[nextindex].compshape.maxx)
									stack[index].compshape.maxx=stack[nextindex].compshape.maxx;
								if(stack[index].compshape.miny>stack[nextindex].compshape.miny)
									stack[index].compshape.miny=stack[nextindex].compshape.miny;
								if(stack[index].compshape.maxy<stack[nextindex].compshape.maxy)
									stack[index].compshape.maxy=stack[nextindex].compshape.maxy;
							}
						}//����
					}


					if(current->endx<=nextcurrent->endx)
					{
						current=current->linenext;
					//	nextcurrent=nextcurrent->linenext;
					}
					else nextcurrent=nextcurrent->linenext;
				}//end for (**else if(current->beginx+1<=nextcurrent->beginx)**)

				else if(current->beginx>=nextcurrent->beginx-1)//��ǩ���ʱ�Ѿ����ǵ�
				{
					if(current->endx<=nextcurrent->endx)
					{
						current=current->linenext;
					//	nextcurrent=nextcurrent->linenext;
					}
					else
					{
						nextcurrent=nextcurrent->linenext;
					}

				}
			}//end for (**while(current!=NULL&&nextcurrent!=NULL)**)

		}//end for (**if(headrun[i]!=NULL&&headrun[i-1]!=NULL)**)
	}// ��ͨԪ�궨����



	//�˳������Լ�������̫�ٵ���ͨԪ------------------------------------------------------
	int SmallThresh;
	SmallThresh=5;

	int precdeletenum;//�˳�����ǰ����ͨԪ����
	for(i=1,k=0;i<count;i++)//�Ѹ���ͨԪ�ĸ���������ȡ���������pstack�����У��Լ��ٺ�����ʣ�������ʱ��
	{
		if(i%255==0)
			stack[i].sign=TRUE;
		if(stack[i].sign==FALSE)
		{
			if(stack[i].compshape.maxy-stack[i].compshape.miny<=1||stack[i].compshape.maxx-stack[i].compshape.minx<2)
			{
				stack[i].sign=TRUE;
				stack[i].value=255;
			}
			else
				k++;
		}
	}
	for(i=1,precdeletenum=0;i<count;i++)//�Ѹ���ͨԪ�ĸ���������ȡ���������pstack�����У��Լ��ٺ�����ʣ�������ʱ��
	{
		if(stack[i].sign==FALSE&&stack[i].pixelnum>SmallThresh)
		{
			rescomponent[precdeletenum].sign=FALSE;
			rescomponent[precdeletenum].pixelnum=stack[i].pixelnum;
			rescomponent[precdeletenum].compshape.maxx=stack[i].compshape.maxx-1;
			rescomponent[precdeletenum].compshape.minx=stack[i].compshape.minx-1;
			rescomponent[precdeletenum].compshape.maxy=stack[i].compshape.maxy-1;
			rescomponent[precdeletenum].compshape.miny=stack[i].compshape.miny-1;
			rescomponent[precdeletenum].value=stack[i].value;
//			    LOGI("precdeletenum=%d",precdeletenum);
//				LOGI("pixelnum=%d",rescomponent[precdeletenum].pixelnum);
//				LOGI("maxx=%d",rescomponent[precdeletenum].compshape.maxx);
//				LOGI("minx=%d",rescomponent[precdeletenum].compshape.minx);
//				LOGI("maxy=%d",rescomponent[precdeletenum].compshape.maxy);
//				LOGI("miny=%d",rescomponent[precdeletenum].compshape.miny);
//				LOGI("-------------------------------------------------");
			precdeletenum++;
		}
		if(precdeletenum>max_num-1)
			break;
	}


	//��ͼ���е�ǰ�����ظ�ֵΪ��������ͨԪ�ı�־ֵ������������Ӿ����е���������
	runnode *p;
	for(i=1;i<count;i++)
	{
		p=stack[i].firstrunnode;//
		while(p!=NULL)
		{
			for(j=p->beginx;j<=p->endx;j++)
			{
				temp=p->y*(lWidth+2)+j;
				index=pdata[temp];
				while(stack[index].sign==TRUE&&index!=255)
				{
					index=stack[index].value;
				}
				if(index==255) pdata[temp]=255;
				else
					pdata[temp]=index;


			}
			p=p->next;
		}
	}
	int anothertemp;
	for(i=1;i<lHeight+1;i++)
	{
		for(j=1;j<lWidth+1;j++)
		{
			temp=lLineBytes*(i-1)+j-1;
			anothertemp=i*(lWidth+2)+j;
			if(pdata[anothertemp]%255!=0&&pdata[anothertemp]!=0)
  	             pdibData[temp]=(int)pdata[anothertemp]%255;
			else
				pdibData[temp]=(int)pdata[anothertemp];
		}

	}
//	ReleaseList(headrun,lHeight+2);///�ͷ��γ̱�
	free(pdata);
	free(headrun);
	free(pdibData);
	return precdeletenum;
}
