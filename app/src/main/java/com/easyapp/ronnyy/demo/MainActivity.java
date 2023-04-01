package com.easyapp.ronnyy.demo;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.easyapp.ronnyy.security.AppIntegrity;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import com.easyapp.ronnyy.rpeasyapp.RpService;
import com.http.ceas.callback.RestCallback;
import com.http.ceas.core.HttpHeaders;
import com.http.ceas.core.HttpStatus;
import com.easyapp.ronnyy.rpeasyapp.CipherApi;
import com.easyapp.ronnyy.rpeasyapp.UserModel;
import com.easyapp.ronnyy.rpeasyapp.RpCallback;
import com.easyapp.ronnyy.rpeasyapp.ErrorCode;

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

        
        UserModel model = UserModel.buider()
            .setName("carlosdemo")
            .setSha1("68ba07a72441703b")
            .setSha2("166082cfe709f30b")
            .setToken("-NR96y3H9yAsoczTOvzu")
            .create();

        RpService h = RpService.newRpService(model);
        
        h.login(new RpCallback(){

                @Override
                public void onResponse(String body, ErrorCode error) {
                    if (error == null) {
                        text.setText(body);
                    } else {
                        text.setText(error.message);
                    }
                }

                @Override
                public void onException(Exception e) {
                    text.setText(e.toString());
                }


            });

        /*try {
         text.setText(new String(
         new CipherApi(sha1, sha2).decrypt(txt)
         ));
         } catch (Exception e) {
         text.setText(e.toString());
         }*/


        //  text.setText(SignatureUtils.fromPath(this, getPackageCodePath(), "SHA-256").getDataInHex());
        try {
            // testeSegurança();
            // text.setText(ai.check().getType().name());
        } catch (Exception e) {
            //text.setText(e.toString());
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

    private void testeSegurança() throws Exception {
        String nameApp = "origin.apk";
        String sha256 = "83:88:BC:B7:87:F4:58:41:B6:9E:6A:F2:8C:EB:41:91:5F:D0:07:FB:73:E7:20:0E:FC:8E:CD:FB:E3:30:27:CB".replace(":", "");
        String apkPathSignature = getPackageCodePath();
        // msg(apkPathSignature);
        Class<?> classApp = Applications.class; //Nome da sua class que herda de Application

        AppIntegrity aI = AppIntegrity.newInstance(
            this,
            AppIntegrity.Assets.create(nameApp).setDefaultPath("/"),
            AppIntegrity.Signature.createFromPath(apkPathSignature, sha256),
            AppIntegrity.SuperClass.create(classApp, Application.class)
        );
        AppIntegrity.Status status = aI.check();
        text.setText(String.valueOf(status.isSafe()));
        if (!status.isSafe()) {

//            msg(status.getAbout());
//            finishAffinity();
        }
    }

} 
