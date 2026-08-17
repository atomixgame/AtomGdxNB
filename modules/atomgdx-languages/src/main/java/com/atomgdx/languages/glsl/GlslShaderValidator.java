package com.atomgdx.languages.glsl;

import java.util.ArrayList;
import java.util.List;

/**
 * Validates GLSL shader code for syntax errors and unbalanced constructs.
 */
public class GlslShaderValidator {

    public static class ShaderError {
        private final int lineNumber;
        private final String message;

        public ShaderError(int lineNumber, String message) {
            this.lineNumber = lineNumber;
            this.message = message;
        }

        public int getLineNumber() {
            return lineNumber;
        }

        public String getMessage() {
            return message;
        }

        @Override
        public String toString() {
            return "Line " + lineNumber + ": " + message;
        }
    }

    public static List<ShaderError> validateShader(String source) {
        List<ShaderError> errors = new ArrayList<>();
        if (source == null || source.isBlank()) {
            errors.add(new ShaderError(1, "Shader source is empty"));
            return errors;
        }

        String[] lines = source.split("\r?\n");
        int braceDepth = 0;
        int parenDepth = 0;
        boolean hasMain = false;

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.startsWith("//") || line.startsWith("#")) {
                continue;
            }

            if (line.contains("void main(")) {
                hasMain = true;
            }

            for (char c : line.toCharArray()) {
                if (c == '{') braceDepth++;
                if (c == '}') braceDepth--;
                if (c == '(') parenDepth++;
                if (c == ')') parenDepth--;
            }

            if (braceDepth < 0) {
                errors.add(new ShaderError(i + 1, "Unmatched closing brace '}'"));
                braceDepth = 0;
            }
            if (parenDepth < 0) {
                errors.add(new ShaderError(i + 1, "Unmatched closing parenthesis ')'"));
                parenDepth = 0;
            }
        }

        if (!hasMain) {
            errors.add(new ShaderError(1, "Missing entry point 'void main()'"));
        }
        if (braceDepth > 0) {
            errors.add(new ShaderError(lines.length, "Missing " + braceDepth + " closing brace(s) '}'"));
        }
        if (parenDepth > 0) {
            errors.add(new ShaderError(lines.length, "Missing " + parenDepth + " closing parenthesis ')'"));
        }

        return errors;
    }
}
