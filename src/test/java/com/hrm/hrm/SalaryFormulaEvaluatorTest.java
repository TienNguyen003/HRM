package com.hrm.hrm;

import com.hrm.Service.hrm.SalaryFormulaEvaluator;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Kiểm thử bộ tính biểu thức công thức lương. */
class SalaryFormulaEvaluatorTest {

    private final SalaryFormulaEvaluator evaluator = new SalaryFormulaEvaluator();

    private Map<String, Object> variables() {
        Map<String, Object> v = new LinkedHashMap<>();
        v.put("basic", 10_000_000d);
        v.put("allowances", 2_000_000d);
        v.put("overtime", 500_000d);
        v.put("advance", 1_000_000d);
        v.put("unpaidLeave", 250_000d);
        v.put("deductions", 1_250_000d);
        v.put("overtimeHours", 10d);
        v.put("leaveDays", 2d);
        return v;
    }

    @Test
    void calculatesPayrollFormulaWithVariables() {
        Double result = evaluator.evaluate(
                "basic + allowances + overtime - advance - unpaidLeave", variables());
        assertThat(result).isEqualTo(11_250_000d);
    }

    @Test
    void respectsPrecedenceParenthesesAndUnaryMinus() {
        assertThat(evaluator.evaluate("2 + 3 * 4", Map.of())).isEqualTo(14d);
        assertThat(evaluator.evaluate("(2 + 3) * 4", Map.of())).isEqualTo(20d);
        assertThat(evaluator.evaluate("-basic + 1000", Map.of("basic", 500d))).isEqualTo(500d);
        assertThat(evaluator.evaluate("basic * 1.5", Map.of("basic", 100d))).isEqualTo(150d);
    }

    @Test
    void supportsSafeFunctions() {
        assertThat(evaluator.evaluate("max(advance, 0)", Map.of("advance", 100d))).isEqualTo(100d);
        assertThat(evaluator.evaluate("max(advance, 0)", Map.of("advance", -5d))).isEqualTo(0d);
        assertThat(evaluator.evaluate("min(1, 2)", Map.of())).isEqualTo(1d);
        assertThat(evaluator.evaluate("abs(-7)", Map.of())).isEqualTo(7d);
        assertThat(evaluator.evaluate("round(1.2345)", Map.of())).isEqualTo(1.23d);
    }

    @Test
    void returnsNullForBlankFormula() {
        assertThat(evaluator.evaluate(null, variables())).isNull();
        assertThat(evaluator.evaluate("   ", variables())).isNull();
    }

    @Test
    void rejectsUnknownVariableBadSyntaxAndDivisionByZero() {
        assertThatThrownBy(() -> evaluator.evaluate("basic + unknown", variables()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("không tồn tại");
        assertThatThrownBy(() -> evaluator.evaluate("basic +", variables()))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> evaluator.evaluate("basic / 0", variables()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("chia cho 0");
        assertThatThrownBy(() -> evaluator.evaluate("sqrt(4)", variables()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("không được hỗ trợ");
    }

    @Test
    void rejectsAttemptsToExecuteCode() {
        assertThatThrownBy(() -> evaluator.evaluate("T(java.lang.Runtime).getRuntime()", variables()))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> evaluator.evaluate("basic; System.exit(0)", variables()))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
