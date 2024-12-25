package com.justin.clean.web.support;

import com.justin.clean.error.ErrorMessage;
import com.justin.clean.error.ErrorType;
import lombok.Getter;

public class ApiResponse<S> {

    @Getter
    private final ResultType result;

    private final S data;

    @Getter
    private final ErrorMessage error;

    private ApiResponse(ResultType result, S data, ErrorMessage error) {
        this.result = result;
        this.data = data;
        this.error = error;
    }

    public static ApiResponse<?> success() {
        return new ApiResponse<>(ResultType.SUCCESS, null, null);
    }

    public static <S> ApiResponse<S> success(S data) {
        return new ApiResponse<>(ResultType.SUCCESS, data, null);
    }

    public static ApiResponse<?> error(ErrorType error) {
        return new ApiResponse<>(ResultType.ERROR, null, new ErrorMessage(error));
    }

    public static ApiResponse<?> error(ErrorType error, Object errorData) {
        return new ApiResponse<>(ResultType.ERROR, null, new ErrorMessage(error, errorData));
    }

    public Object getData() {
        return data;
    }
}
