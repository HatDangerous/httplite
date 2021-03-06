package alexclin.httplite.sample.frag;

import android.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.example.FileInfo;
import com.example.Result;

import java.util.List;
import java.util.Map;

import alexclin.httplite.Request;
import alexclin.httplite.listener.Callback;
import alexclin.httplite.sample.App;
import alexclin.httplite.sample.R;
import alexclin.httplite.sample.model.ZhihuData;
import alexclin.httplite.util.LogUtil;

/**
 * CustomFrag
 *
 * @author alexclin
 * @date 16/1/10 11:37
 */
public class CustomFrag extends Fragment {

    private TextView mInfoTv;

    public void onTestBtn(View view){
        switch (view.getId()){
            case R.id.btn_test:
                App.httpLite(getActivity()).url("http://192.168.99.238:10080/").header("header","not chinese").header("test_header","2016-01-06")
                        .header("double_header","header1").addHeader("double_header","head2")
                        .param("param1","I'm god").param("param2","You dog").param("param3","中文").get().execute(new Callback<String>() {
                    @Override
                    public void onSuccess(String result,Map<String,List<String>> headers) {
                        mInfoTv.setText(result);
                    }

                    @Override
                    public void onFailed(Request req, Exception e) {
                        mInfoTv.setText("Error:"+e);
                    }
                });
                break;
            case R.id.btn_test2:
                App.httpLite(getActivity()).url("http://news-at.zhihu.com/api/4/news/latest").get().execute(new Callback<ZhihuData>() {
                    @Override
                    public void onSuccess(ZhihuData result,Map<String,List<String>> headers) {
                        LogUtil.e("Result:" + result);
                        mInfoTv.setText("Rsult:"+result);
                    }

                    @Override
                    public void onFailed(Request req, Exception e) {
                        e.printStackTrace();
                        mInfoTv.setText("Error:"+e);
                    }
                });
                break;
            case R.id.btn_test3:
                App.httpLite(getActivity()).url("http://192.168.99.238:10080/").header("header","not chinese").header("test_header","2016-01-06")
                        .header("double_header","header1").addHeader("double_header","head2")
                        .param("type","json").param("param2","You dog").param("param3", "中文").get().execute(new Callback<Result<List<FileInfo>>>() {
                    @Override
                    public void onSuccess(Result<List<FileInfo>> result,Map<String,List<String>> headers) {
                        mInfoTv.setText("Result:"+result);
                    }

                    @Override
                    public void onFailed(Request req, Exception e) {
                        mInfoTv.setText("Error:"+e);
                    }
                });
                break;
        }

    }
}
