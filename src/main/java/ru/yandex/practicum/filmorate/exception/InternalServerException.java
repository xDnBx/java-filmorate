package ru.yandex.practicum.filmorate.exception;

public class InternalServerException extends RuntimeException {
    public InternalServerException() {
        this("Произошла внутренняя ошибка сервера. Повторите запрос позднее или обратитесь к администратору");
    }

    public InternalServerException(String message) {
        super(message);
    }
}
