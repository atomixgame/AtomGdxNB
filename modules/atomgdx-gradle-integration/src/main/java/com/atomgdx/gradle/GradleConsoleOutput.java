package com.atomgdx.gradle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Captures and parses Gradle process console output with colorization and error tagging.
 */
public class GradleConsoleOutput {
    public enum LineType {
        STANDARD,
        SUCCESS,
        WARNING,
        ERROR,
        HEADER
    }

    public static class OutputLine {
        private final String text;
        private final LineType type;

        public OutputLine(String text, LineType type) {
            this.text = text;
            this.type = type;
        }

        public String getText() {
            return text;
        }

        public LineType getType() {
            return type;
        }
    }

    private final List<OutputLine> lines = new CopyOnWriteArrayList<>();

    public void appendLine(String rawLine) {
        if (rawLine == null) return;

        LineType type = LineType.STANDARD;
        if (rawLine.startsWith("> Task :") || rawLine.startsWith("BUILD SUCCESSFUL")) {
            type = LineType.SUCCESS;
        } else if (rawLine.startsWith("FAILURE:") || rawLine.contains("FAILED") || rawLine.contains("Exception") || rawLine.contains("Error:")) {
            type = LineType.ERROR;
        } else if (rawLine.contains("WARNING:") || rawLine.contains("warning:")) {
            type = LineType.WARNING;
        } else if (rawLine.startsWith("===") || rawLine.startsWith("---")) {
            type = LineType.HEADER;
        }

        lines.add(new OutputLine(rawLine, type));
    }

    public List<OutputLine> getLines() {
        return new ArrayList<>(lines);
    }

    public void clear() {
        lines.clear();
    }
}
