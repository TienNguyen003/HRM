package com.hrm.dto.request.user.employee;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeRequest {
    @NotBlank(message = "Tên không được để trống")
    @Pattern(regexp = "^[\\p{L}]+(\\s[\\p{L}]+)*$", message = "Tên chỉ được chứa chữ cái")
    String name;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    String email;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^[0-9]+$", message = "Số điện thoại chỉ được chứa số")
    String phone_number;

    @NotBlank(message = "Giới tính không được để trống")
    String gender;

    String image;

    @NotBlank(message = "Ngày sinh không được để trống")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Định dạng ngày phải là yyyy-MM-dd")
    String birth_date;

    @NotBlank(message = "Ngày bắt đầu không được để trống")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Định dạng ngày phải là yyyy-MM-dd")
    String hire_date;

    int shift_id;

    @NotNull(message = "Thời gian ần làm không được để trống")
    @Min(value = 0, message = "Thời gian ần làm phải lớn hơn 0")
    int vacationTime;

    @NotNull(message = "Thời gian không được để trống")
    @Min(value = 0, message = "Thời gian phải lớn hơn 0")
    int hourOff;

    @NotNull(message = "Số gi nghỉ phép không được để trống")
    @Min(value = 1, message = "Số gi nghỉ phép phải lớn hơn 0")
    int vacationHours;

    @NotNull(message = "Phòng không được để trống")
    @Min(value = 1, message = "Phòng phải lớn hơn 0")
    int departmentId;

    @NotNull(message = "Công thức lương không được để trống")
    @Min(value = 1, message = "Công thức lương phải lớn hơn 0")
    int formulaId;
}
