package com.easyapp.ronnyy.demo;
 
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.easyapp.ronnyy.security.AppIntegrity;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends Activity { 

    TextView text;
     
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text = findViewById(R.id.activitymainTextView1);
        /*String path = getPackageCodePath();
        try{
           text.setText("md5 : " + SignatureUtils.fromApp(this, "md5").getDataInHex());
           text.append("\n256 : " + SignatureUtils.fromApp(this, "SHA-1").getDataInHex());
        }catch(Exception e){
            text.setText(e.toString());
        }

        try{
            List<File> list = AssetsUtils.listAllFiles(this, "");
            text.setText(list.size()+"\n");
            if(!list.isEmpty())
            for(File file : list){
                text.append(file.getAbsolutePath() + (AssetsUtils.isFile(this, file.getAbsolutePath()) ? " - FILE - " : " - DIR - ") + "\n");
            }
           
            
        }catch(Exception e){
            text.setText(e.toString());
        }*/
        
        AppIntegrity ai = AppIntegrity.newInstance(
        this,
        AppIntegrity.Assets.create("arquivo2.txt")
        //AppIntegrity.Signature.create("sihshshsh")
        );
        
        try{
         text.setText(ai.check().getType().name());
        }catch(Exception e){
            text.setText(e.toString());
        }
        
        //text.setText(MainActivity.class.getName());
        /*try{
            InputStream is = AssetsUtils.open(this, "arquivo1.txt");
            if(is!=null)
            text.setText(is.available() +"\n"+ StreamUtils.toString(is));
            else
            text.setText("nulo");
        }catch(Exception e){
            text.setText(e.toString());
        }*/
        // listAssetFiles("");
      
       // listAssets("");
       /* try{
            for(String pathAsset : getAssets().list("")){
                text.append(pathAsset + "\n");
            }
        }catch(IOException e){}*/
    }
    
    private boolean listAssetFiles(String path) {

        String [] list;
        try {
            path = path.startsWith("/") ? path.substring(1, path.length()) : path;
            list = getAssets().list(path);
            Log.d("Teste", path);
            if (list.length > 0) {
                // This is a folder
                for (String file : list) {
                    if (!listAssetFiles(path + "/" + file))
                        return false;
                    else {
                       // Log.d("Teste", file);
                        // This is a file
                        // TODO: add file name to an array list
                    }
                }
            } 
        } catch (IOException e) {
            return false;
        }

        return true; 
    }
    
    private ArrayList<String> listAssetFiless(String path) {
        ArrayList<String> fileList = new ArrayList<>();
        try {
            path = path.startsWith("/") ? path.substring(1, path.length()) : path;
            String[] list = getAssets().list(path);
            if (list.length > 0) {
                // This is a folder
                for (String file : list) {
                    fileList.addAll(listAssetFiless(path + "/" + file));
                }
            } else {
                // This is a file
                fileList.add(path);
            }
        } catch (IOException e) {
            Log.e("Error", "Failed to get asset file list.", e);
        }
        return fileList;
    }
    
    
	
} 
