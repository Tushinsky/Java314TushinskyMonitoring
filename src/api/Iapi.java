package api;

import in.Request;

/**
 * Публичный контракт- наш сервер должен получать запрос (Request) и отправлять ответ (Response)
 * */
public interface Iapi {
    /**
     * Возвращает ответ базы данных на запрос пользователя
     * @param request запрос на получение данных
     * @return ответ базы данных
     */
    Response response(Request request);
}
