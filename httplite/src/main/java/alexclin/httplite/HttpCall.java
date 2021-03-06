package alexclin.httplite;

import java.io.File;
import java.lang.reflect.Type;

import alexclin.httplite.listener.Callback;
import alexclin.httplite.util.Util;

/**
 * HttpCall
 *
 * @author alexclin
 * @date 16/1/26 22:12
 */
public class HttpCall implements Call{
    Request request;

    HttpCall(Request request) {
        this.request = request;
    }

    @SuppressWarnings("unchecked")
    public <T> void execute(Callback<T> callback){
        Type type = Util.type(Callback.class, callback);
        if(type==File.class){
            download((Callback<File>)callback);
        }else{
            ResultCallback rcb = createHttpCalback(callback,type,null);
            excuteSelf(rcb);
        }
    }

    public Response executeSync() throws Exception{
        return executeSyncInner(null);
    }

    @SuppressWarnings("unchecked")
    public <T> T executeSync(Clazz<T> clazz) throws Exception{
        Type type = clazz.type();
        ResultCallback<T> callback;
        if(type==File.class) {
            callback = (ResultCallback<T>)(createDownloadCallback(null,(Clazz<File>)clazz));
        }else{
            callback = createHttpCalback(null,type,clazz);
        }
        Response response = executeSyncInner(callback);
        return callback.praseResponse(response);
    }

    @Override
    public DownloadHandle download(Callback<File> callback) {
        final DownloadCallback rcb = createDownloadCallback(callback, null);
        excuteSelf(rcb);
        return rcb;
    }

    private <T> ResultCallback createHttpCalback(Callback<T> callback,Type type,Clazz<T> clazz) {
        HttpLite lite = request.lite;
        ResultCallback rcb = new HttpCallback<T>(callback,this,type);
        if(lite.getRequestFilter()!=null) lite.getRequestFilter().onRequest(request, rcb);
        lite.processCookie(request.url, request.headers);
        return rcb;
    }

    private DownloadCallback createDownloadCallback(Callback<File> callback,Clazz<File> clazz) {
        DownloadCallback.DownloadParams params = request.getDownloadParams();
        if(params==null){
            throw new IllegalArgumentException("to execute Callback<File>, you must call intoFile() on Request before execute");
        }
        final DownloadCallback rcb = new DownloadCallback(callback,this,params);
        request.lite.processCookie(request.url, request.headers);
        return rcb;
    }

    private Response executeSyncInner(ResultCallback callback) throws Exception{
        Runnable preWork = null;
        if(callback instanceof DownloadCallback){
            preWork = (DownloadCallback)callback;
        }
        HttpLite lite = request.lite;
        if(lite.getRequestFilter()!=null) lite.getRequestFilter().onRequest(request,callback);
        lite.processCookie(request.url,request.headers);
        if(preWork!=null) preWork.run();
        Response response = lite.getClient().executeSync(request, request.url, request.method, request.headers, request.body, request.tag);
        if(response!=null){
            lite.saveCookie(request.url,response.headers());
        }
        if(lite.getResponseFilter()!=null) lite.getResponseFilter().onResponse(request, response);
        return response;
    }

    <T> void excuteSelf(final ResultCallback<T> callback){
        HttpLite lite = request.lite;
        boolean isDownload = callback instanceof DownloadCallback;
        final Runnable preWork = isDownload?(DownloadCallback)callback:null;
        if(isDownload&&lite.getCustomDownloadExecutor()!=null){
            lite.getCustomDownloadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        callback.onResponse(executeSyncInner(callback));
                    } catch (Exception e) {
                        callback.onFailed(e);
                    }
                }
            });
        }else{
            lite.getClient().execute(request.url,request.method,request.headers,request.body,request.tag,callback,preWork);
        }
    }
}
