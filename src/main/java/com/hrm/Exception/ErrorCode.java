package com.hrm.Exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
	UNCATEGORIZED_EXCEPTION(404, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
	INVALID(404, "Invalid message key", HttpStatus.BAD_REQUEST),
	USERNAME_INVALID(500, "Username must be at least 6 characters", HttpStatus.BAD_REQUEST),
	PASSWORD_INVALID(501, "Password must be at least 8 characters", HttpStatus.BAD_REQUEST),
	USER_EXISTED(502, "User existed", HttpStatus.BAD_REQUEST),
	ROLE_EXISTED(502, "Role existed", HttpStatus.BAD_REQUEST),
	DEPARTMENT_EXISTED(502, "Department existed", HttpStatus.BAD_REQUEST),
	OFFICE_EXISTED(502, "Office existed", HttpStatus.BAD_REQUEST),
	BANK_EXISTED(502, "Bank existed", HttpStatus.NOT_FOUND),
	UNAUTHENTICATED(503, "Unauthenticated", HttpStatus.UNAUTHORIZED),
	USER_NOT_EXISTED(504, "Tên đăng nhập không chính xác. Vui lòng thử lại.", HttpStatus.NOT_FOUND),
	PASSWORD_INCORRECT(504, "Mật khẩu không chính xác. Vui lòng thử lại.", HttpStatus.NOT_FOUND),
	UNAUTHORIZED(505, "You don't have permission", HttpStatus.FORBIDDEN),
	DayOff_EXISTED(502, "Day of existed", HttpStatus.BAD_REQUEST),
	WageCate_EXISTED(502, "This salary category of existed", HttpStatus.BAD_REQUEST),
	Advance_Not_Edit(502, "Information cannot be edited", HttpStatus.BAD_REQUEST);

	ErrorCode(int code, String message, HttpStatusCode statusCode) {
		this.code = code;
		this.message = message;
		this.statusCode = statusCode;
	}

	private int code;
	private String message;
	private HttpStatusCode statusCode;
}
