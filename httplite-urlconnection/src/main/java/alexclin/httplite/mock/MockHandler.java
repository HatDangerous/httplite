package alexclin.httplite.mock;

import alexclin.httplite.Request;

/**
 * MockHandler
 *
 * @author alexclin
 * @date 16/1/29 20:33
 */
public interface MockHandler {
    <T> void mock(Request request,MockResponse<T> response) throws Exception;
}
