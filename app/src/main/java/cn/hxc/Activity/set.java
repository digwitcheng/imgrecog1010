package cn.hxc.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.Map;

import cn.hxc.ToolUtil.PreferencesService;
import cn.hxc.imgrecognition.R;
import cn.hxc.ImageUtil.imageProcess;

public class set extends Activity {
    static int marg=0;
    private PreferencesService service;
   @Override
protected void onCreate(Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    super.onCreate(savedInstanceState);
    setContentView(R.layout.settings);
    service=new PreferencesService(this);       
    Map<String, String> params= service.getPreferences("margain");// new Integer(params.get("margain")
   if(params.get("margain")==null){
	   service.save("0","margain");
   }
   
}
   public void  margain(View v){
	   //params.get("margain");
	    Map<String, String> params= service.getPreferences("margain");// new Integer(params.get("margain")
	   if(params.get("margain")==null){
		   service.save("90","margain");
	   }	   
	   
	   imageProcess.noequl( params.get("margain"),333333333);
	   int a=new Integer(params.get("margain"))+90;
	   a=a%360;
	   service.save(a+"","margain");
	   Toast.makeText(this, "成功旋转90°", 1).show();	    
	    Intent intent = new Intent(this,MainActivity.class);
	    startActivity(intent);
   }
   public void save(View v){
	   Intent intent = new Intent(this, saveSmg.class);
       startActivity(intent);
	   
//       String smsContent=content.getText().toString().trim();
//        service.save(smsContent);
//       Toast.makeText(this, "保存成功", 1).show();
//       Intent intent = new Intent(set.this,MainActivity.class);
//       startActivity(intent);
   }
}
