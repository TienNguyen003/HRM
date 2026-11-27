package com.hrm.Service.hrm;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Tính giá trị của công thức lương dạng biểu thức toán học với tên biến.
 *
 * <p>Ví dụ: {@code basic + allowances + overtime - advance - unpaidLeave}
 * hoặc {@code (basic + allowances) * 1.1 - max(advance, 0)}.</p>
 *
 * <p>Đây là bộ parse tự viết, chỉ hỗ trợ số, biến, {@code + - * / ()}, dấu trừ một ngôi
 * và một vài hàm toán học an toàn. Không dùng ScriptEngine/SpEL nên công thức lưu trong DB
 * không thể gọi method, truy cập class hay thực thi code tuỳ ý.</p>
 */
@Component
public class SalaryFormulaEvaluator {

    private static final int MAX_LENGTH = 500;
    private static final int MAX_DEPTH = 32;

    /**
     * @return giá trị biểu thức, hoặc {@code null} nếu công thức trống.
     * @throws IllegalArgumentException nếu biểu thức sai cú pháp, dùng biến/hàm không tồn tại hoặc chia cho 0.
     */
    public Double evaluate(String expression, Map<String, Object> variables) {
        if (expression == null || expression.isBlank()) return null;
        if (expression.length() > MAX_LENGTH)
            throw new IllegalArgumentException("Công thức quá dài (tối đa " + MAX_LENGTH + " ký tự)");

        Parser parser = new Parser(expression, variables == null ? Map.of() : variables);
        double value = parser.parseExpression();
        parser.skipWhitespace();
        if (!parser.atEnd()) throw parser.error("ký tự không hợp lệ '" + parser.peekChar() + "'");
        if (Double.isNaN(value) || Double.isInfinite(value))
            throw new IllegalArgumentException("Công thức lương trả về giá trị không hợp lệ");
        return value;
    }

    // ------------------------------------------------------------------ parser

    private static final class Parser {
        private final String input;
        private final Map<String, Object> variables;
        private int pos;
        private int depth;

        Parser(String input, Map<String, Object> variables) {
            this.input = input;
            this.variables = variables;
        }

        double parseExpression() {
            double value = parseTerm();
            while (true) {
                skipWhitespace();
                if (eat('+')) value += parseTerm();
                else if (eat('-')) value -= parseTerm();
                else return value;
            }
        }

        private double parseTerm() {
            double value = parseFactor();
            while (true) {
                skipWhitespace();
                if (eat('*')) {
                    value *= parseFactor();
                } else if (eat('/')) {
                    double divisor = parseFactor();
                    if (divisor == 0) throw error("chia cho 0");
                    value /= divisor;
                } else {
                    return value;
                }
            }
        }

        private double parseFactor() {
            if (++depth > MAX_DEPTH) throw error("biểu thức lồng nhau quá sâu");
            try {
                skipWhitespace();
                if (atEnd()) throw error("thiếu toán hạng");
                char c = input.charAt(pos);
                if (c == '+') { pos++; return parseFactor(); }
                if (c == '-') { pos++; return -parseFactor(); }
                if (c == '(') {
                    pos++;
                    double value = parseExpression();
                    skipWhitespace();
                    if (!eat(')')) throw error("thiếu ')'");
                    return value;
                }
                if (Character.isDigit(c) || c == '.') return parseNumber();
                if (Character.isLetter(c) || c == '_') return parseVariableOrFunction();
                throw error("ký tự không hợp lệ '" + c + "'");
            } finally {
                depth--;
            }
        }

        private double parseNumber() {
            int start = pos;
            boolean hasDot = false;
            while (!atEnd()) {
                char c = input.charAt(pos);
                if (Character.isDigit(c)) pos++;
                else if (c == '.' && !hasDot) { hasDot = true; pos++; }
                else break;
            }
            String text = input.substring(start, pos);
            if (text.isBlank() || text.equals(".")) throw error("số không hợp lệ '" + text + "'");
            try {
                return Double.parseDouble(text);
            } catch (NumberFormatException e) {
                throw error("số không hợp lệ '" + text + "'");
            }
        }

        private double parseVariableOrFunction() {
            int start = pos;
            while (!atEnd()) {
                char c = input.charAt(pos);
                if (Character.isLetterOrDigit(c) || c == '_') pos++;
                else break;
            }
            String name = input.substring(start, pos);

            skipWhitespace();
            if (eat('(')) {
                List<Double> args = new ArrayList<>();
                skipWhitespace();
                if (!eat(')')) {
                    do {
                        args.add(parseExpression());
                        skipWhitespace();
                    } while (eat(','));
                    if (!eat(')')) throw error("thiếu ')' cho hàm '" + name + "'");
                }
                return applyFunction(name, args);
            }

            Object value = variables.get(name);
            if (value == null) throw error("biến '" + name + "' không tồn tại");
            if (value instanceof Number n) return n.doubleValue();
            if (value instanceof String s) {
                try {
                    return Double.parseDouble(s.trim());
                } catch (NumberFormatException e) {
                    throw error("biến '" + name + "' không phải số");
                }
            }
            throw error("biến '" + name + "' không phải số");
        }

        private double applyFunction(String name, List<Double> args) {
            return switch (name) {
                case "min" -> {
                    requireArgs(name, args, 2);
                    yield Math.min(args.get(0), args.get(1));
                }
                case "max" -> {
                    requireArgs(name, args, 2);
                    yield Math.max(args.get(0), args.get(1));
                }
                case "abs" -> {
                    requireArgs(name, args, 1);
                    yield Math.abs(args.get(0));
                }
                case "round" -> {
                    requireArgs(name, args, 1);
                    yield Math.round(args.get(0) * 100.0) / 100.0;
                }
                default -> throw error("hàm '" + name + "' không được hỗ trợ");
            };
        }

        private void requireArgs(String name, List<Double> args, int expected) {
            if (args.size() != expected)
                throw error("hàm '" + name + "' cần " + expected + " đối số");
        }

        void skipWhitespace() {
            while (!atEnd() && Character.isWhitespace(input.charAt(pos))) pos++;
        }

        boolean atEnd() {
            return pos >= input.length();
        }

        char peekChar() {
            return atEnd() ? '\0' : input.charAt(pos);
        }

        private boolean eat(char expected) {
            if (atEnd() || input.charAt(pos) != expected) return false;
            pos++;
            return true;
        }

        IllegalArgumentException error(String message) {
            return new IllegalArgumentException(
                    "Công thức lương không hợp lệ tại vị trí " + pos + ": " + message);
        }
    }
}
