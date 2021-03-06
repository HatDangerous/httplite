package alexclin.httplite.okhttp.wrapper;

import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.io.InputStream;

import alexclin.httplite.MediaType;

/**
 * alexclin.httplite.okhttp.wrapper
 *
 * @author alexclin
 * @date 16/1/1 15:00
 */
public class ResponseBodyWrapper implements alexclin.httplite.ResponseBody {
    private ResponseBody realBody;
    private MediaType type;
    public ResponseBodyWrapper(ResponseBody realBody) {
        this.realBody = realBody;
        this.type = new MediaTypeWrapper(realBody.contentType());
    }

    @Override
    public MediaType contentType() {
        return type;
    }

    @Override
    public long contentLength() throws IOException{
        return realBody.contentLength();
    }

    @Override
    public InputStream stream() throws IOException{
        return realBody.byteStream();
    }

    @Override
    public void close() throws IOException {
        realBody.close();
    }
}
