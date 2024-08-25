package com.hrm.Exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
	UNCATEGORIZED_EXCEPTION(504, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
	INVALID(504, "Invalid message key", HttpStatus.BAD_REQUEST),
	UNAUTHENTICATED(504, "Unauthenticated", HttpStatus.UNAUTHORIZED),
	UNAUTHORIZED(505, "You don't have permission", HttpStatus.FORBIDDEN),

	// user name
	USERNAME_INVALID(500, "Tên đăng nhập phải có ít nhất 6 kí tự.", HttpStatus.BAD_REQUEST),
	USERNAME_NOT_EXISTED(504, "Tên đăng nhập không chính xác.", HttpStatus.NOT_FOUND),
	USER_NOT_EXISTED(504, "Tài khoản không chính xác.", HttpStatus.NOT_FOUND),
	USERNAME_EXISTED(502, "Tên đăng nhập đã tồn tại.", HttpStatus.BAD_REQUEST),

	// password
	PASSWORD_INVALID(500, "Mật khẩu phải có ít nhất 6 kí tự", HttpStatus.BAD_REQUEST),
	PASSWORD_INCORRECT(500, "Mật khẩu không chính xác. Vui lòng thử lại.", HttpStatus.NOT_FOUND),
	PASSWORD_NO_MATCH(500, "Mật khẩu mới không được trùng mật khẩu cũ.", HttpStatus.NOT_FOUND),
	OLD_PASS_INCORRECT(500, "Mật khẩu cũ không chính xác. Vui lòng thử lại.", HttpStatus.NOT_FOUND),

	// nhân viên
	EMPLOYEE_NOT_EXISTED(504, "Nhân viên không tồn tại.", HttpStatus.NOT_FOUND),

	// quyền
	ROLE_EXISTED(502, "Quyền đã tồn tại.", HttpStatus.BAD_REQUEST),
	ROLE_NOT_EXISTED(504, "Quyền không tồn tại.", HttpStatus.BAD_REQUEST),

	// phòng ban
	DEPARTMENT_EXISTED(502, "Phòng đã tồn tại.", HttpStatus.BAD_REQUEST),
	DEPARTMENT_NOT_EXISTED(504, "Phòng không tồn tại.", HttpStatus.BAD_REQUEST),

	// văn phòng
	OFFICE_EXISTED(502, "Văn phòng đã tồn tại.", HttpStatus.BAD_REQUEST),
	OFFICE_NOT_EXISTED(504, "Văn phòng không tồn tại.", HttpStatus.BAD_REQUEST),

	// ngân hàng
	BANK_EXISTED(502, "Ngân hàng đã tồn tại.", HttpStatus.NOT_FOUND),
	BANK_NOT_EXISTED(504, "Ngân hàng không tồn tại.", HttpStatus.NOT_FOUND),

	// hợp đồng
	CONTRACT_NOT_EXISTED(504, "Hợp đồng không tồn tại", HttpStatus.NOT_FOUND),

	//ngày nghỉ
	DAYOFF_EXISTED(502, "Ngày nghỉ đã tồn tại.", HttpStatus.BAD_REQUEST),
	DAYOFF_NOT_EXISTED(504, "Ngày nghỉ không tồn tại.", HttpStatus.BAD_REQUEST),
	DAYCATE_NOT_EXISTED(504, "Danh mục ngày nghỉ không tồn tại.", HttpStatus.BAD_REQUEST),

	//lương
	WAGECATE_EXISTED(502, "Danh mục lương đã tồn tại.", HttpStatus.BAD_REQUEST),
	WAGECATE_NOT_EXISTED(504, "Danh mục lương không tồn tại.", HttpStatus.BAD_REQUEST),
	WAGE_NOT_EXISTED(502, "Lương không tồn tại.", HttpStatus.BAD_REQUEST),

	// luong theo thang
	PAYROLL_EMPLOYEE_EXISTED(502, "Nhân viên đã có bảng lương tháng.", HttpStatus.BAD_REQUEST),

	// ứng lương
	ADVANCE_NOT_EXISTED(504, "Phiếu ứng lương không tồn tại.", HttpStatus.BAD_REQUEST),
	ADVANCE_NOT_EDIT(503, "Thông tin không thể chỉnh sửa.", HttpStatus.BAD_REQUEST),

	// công thức lương
	FORMULA_NOT_EXISTED(504, "Công thức không tồn tại.", HttpStatus.BAD_REQUEST),

	//chấm công
	TIME_NOT_EXISTED(504, "Không tồn tại.", HttpStatus.BAD_REQUEST),
	TIME_EXISTED(504, "Bạn đã điểm danh rồi.", HttpStatus.BAD_REQUEST);

	ErrorCode(int code, String message, HttpStatusCode statusCode) {
		this.code = code;
		this.message = message;
		this.statusCode = statusCode;
	}

	private int code;
	private String message;
	private HttpStatusCode statusCode;
}
