package com.hrm.Exception;

import com.hrm.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	/** Lỗi không lường trước: trả 500 và LOG stacktrace để không che nguyên nhân. */
	@ExceptionHandler(value = Exception.class)
	ResponseEntity<ApiResponse> handlingRuntimeException(Exception exception){
		log.error("Uncategorized error", exception);
		ApiResponse apiResponse = new ApiResponse<>();

		apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
		apiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());

		return ResponseEntity.status(ErrorCode.UNCATEGORIZED_EXCEPTION.getStatusCode()).body(apiResponse);
	}

	@ExceptionHandler(value = AppException.class)
	ResponseEntity<ApiResponse> handlingAppException(AppException exception){
		ErrorCode errorCode = exception.getErrorCode();
		String formattedMessage = exception.getMessage();

		ApiResponse apiResponse = new ApiResponse<>();
		apiResponse.setCode(errorCode.getCode());
		apiResponse.setMessage(formattedMessage);

		return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
	}

	/** 403 - đã đăng nhập nhưng thiếu quyền. */
	@ExceptionHandler(value = AccessDeniedException.class)
	ResponseEntity<ApiResponse> handlingAccessDExp(AccessDeniedException exception){
		ErrorCode errorCode = ErrorCode.UNAUTHORIZED;

		return ResponseEntity.status(errorCode.getStatusCode()).body(
				ApiResponse.builder()
						.code(errorCode.getCode())
						.message(errorCode.getMessage())
						.build());
	}

	@ExceptionHandler(value = MethodArgumentNotValidException.class)
	ResponseEntity<ApiResponse> handlingValidation(MethodArgumentNotValidException exception){
		String errorMessage = exception.getFieldError() == null
				? "Dữ liệu không hợp lệ" : exception.getFieldError().getDefaultMessage();

		ApiResponse apiResponse = new ApiResponse<>();
		apiResponse.setCode(ErrorCode.INVALID.getCode());
		apiResponse.setMessage(errorMessage);

		return ResponseEntity.badRequest().body(apiResponse);
	}

	/** Sai kiểu/thiếu query param: 400 kèm tên tham số, không còn là "Uncategorized error". */
	@ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
	ResponseEntity<ApiResponse> handlingTypeMismatch(MethodArgumentTypeMismatchException exception){
		return badRequest("Tham số '" + exception.getName() + "' không đúng kiểu.");
	}

	@ExceptionHandler(value = MissingServletRequestParameterException.class)
	ResponseEntity<ApiResponse> handlingMissingParam(MissingServletRequestParameterException exception){
		return badRequest("Thiếu tham số '" + exception.getParameterName() + "'.");
	}

	/** Đường dẫn không tồn tại: 404 thay vì 500/400. */
	@ExceptionHandler(value = NoResourceFoundException.class)
	ResponseEntity<ApiResponse> handlingNotFound(NoResourceFoundException exception){
		ApiResponse apiResponse = ApiResponse.builder()
				.code(ErrorCode.HRM_NOT_FOUND.getCode())
				.message("Không tìm thấy tài nguyên.")
				.build();
		return ResponseEntity.status(404).body(apiResponse);
	}

	private ResponseEntity<ApiResponse> badRequest(String message) {
		return ResponseEntity.badRequest().body(ApiResponse.builder()
				.code(ErrorCode.INVALID.getCode())
				.message(message)
				.build());
	}
}
