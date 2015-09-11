package com.emptypointer.hellocdut.activity;

import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.utils.CommonUtils;
import com.joanzapata.pdfview.PDFView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BinaryHttpResponseHandler;

import org.apache.http.Header;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Sequarius on 2015/9/12.
 */
public class ReadTeachingPlanActivity extends BaseActivity {
    private static final String TAG ="ReadTeachingPlan" ;
    private PDFView pdfview;
    private android.widget.ProgressBar downloadbar;
    private android.widget.TextView resultView;
    private android.widget.LinearLayout layoutonload;
    private String pdfURL;
    private String fileName;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_read_teaching_plan);
        this.layoutonload = (LinearLayout) findViewById(R.id.layout_on_load);
        this.resultView = (TextView) findViewById(R.id.resultView);
        this.downloadbar = (ProgressBar) findViewById(R.id.downloadbar);
        this.pdfview = (PDFView) findViewById(R.id.pdfview);
        fileName = getIntent().getStringExtra(QueryTeachPlanActivity.INTENT_NAME);
        getActionBar().setTitle(fileName);

        pdfURL=getIntent().getStringExtra(QueryTeachPlanActivity.INTENT_PDF_URL);
        Log.i(TAG,"pdfurl="+pdfURL);
        if(!getLocalPDF(pdfURL).exists()) {
            loadDataFromServer();
        }else{
            loadPDF();
        }
    }

    private void loadPDF(){
        try {
            layoutonload.setVisibility(View.GONE);
            File file = getLocalPDF(fileName);
            Log.i("TAG", file.toString());
            pdfview.fromFile(file).load();
        } catch (RuntimeException e) {
            this.finish();
            e.printStackTrace();
        }
    }

    private File getLocalPDF(String name){
        File dir=new File("/data/data/com.emptypointer.hellocdut/files/");
        if(!dir.exists()){
            dir.mkdir();
        }
        return new File(dir,name);
    }

    private void loadDataFromServer() {
        AsyncHttpClient client = new AsyncHttpClient();
        String[] allowedContentTypes = new String[] { "application/pdf"};
        client.get(pdfURL, new BinaryHttpResponseHandler(allowedContentTypes) {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {

                File file = getLocalPDF(fileName);
                try {
                    FileOutputStream oStream = new FileOutputStream(file);
                    oStream.write(bytes);
                    oStream.flush();
                    oStream.close();
                    loadPDF();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                CommonUtils.customToast(R.string.message_weak_internet, ReadTeachingPlanActivity.this, true);
                Log.i(TAG,"code=="+i);
                for(Header header:headers) {
                    Log.i(TAG, header.getName()+"++" + header.getValue());
                }

//                ReadTeachingPlanActivity.this.finish();
            }

            @Override
            public void onProgress(int bytesWritten, int totalSize) {
                super.onProgress(bytesWritten, totalSize);
                float num = (float) bytesWritten / (float) totalSize;
                int result = (int) (num * 100);
                downloadbar.setProgress(result);
                resultView.setText("正在加载精彩内容" + bytesWritten/1000 + "kb/"+totalSize/1000+"kb");

                //显示下载成功信息
//                if (progressBar.getProgress() == progressBar.getMax()) {
////                        Toast.makeText(ReadActivity.this, "下载成功", Toast.LENGTH_SHORT).show();
//                    loadPDF();
//
//                }
            }
        });
    }
}
