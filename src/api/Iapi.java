package api;

import in.Request;

/**
 * Публичный контракт- наш сервер должен получать запрос (Request) и отправлять ответ (Response)
 * */
public interface Iapi {
    Response response(Request request);
}
