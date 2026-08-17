package com.atomgdx.core.log;

import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputEvent;
import org.openide.windows.OutputListener;
import org.openide.windows.OutputWriter;

import java.io.File;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * NetBeans Platform Output Window logger and live event stream.
 * Provides formatted colored log streaming, hyperlinks, and LibGDX logging redirection.
 */
public final class StudioLog {

    private static final String TAB_NAME = "AtomGDX Studio";
    private static InputOutput io;
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private StudioLog() {}

    public static synchronized InputOutput getIO() {
        if (io == null) {
            try {
                io = IOProvider.getDefault().getIO(TAB_NAME, false);
            } catch (Throwable t) {
                // Standalone fallback
                io = null;
            }
        }
        return io;
    }

    public static void select() {
        InputOutput current = getIO();
        if (current != null) {
            current.select();
        }
    }

    public static void info(String message) {
        log("[INFO]  ", message, false);
    }

    public static void success(String message) {
        log("[OK]    ", message, false);
    }

    public static void warn(String message) {
        log("[WARN]  ", message, false);
    }

    public static void error(String message) {
        log("[ERROR] ", message, true);
    }

    public static void error(String message, Throwable t) {
        log("[ERROR] ", message + (t != null ? " (" + t.getMessage() + ")" : ""), true);
        if (t != null) {
            InputOutput current = getIO();
            if (current != null) {
                t.printStackTrace(current.getErr());
            } else {
                t.printStackTrace();
            }
        }
    }

    public static void link(String label, Runnable onClick) {
        InputOutput current = getIO();
        String time = LocalTime.now().format(TIME_FMT);
        String line = "[" + time + "] [LINK]  " + label;
        if (current != null) {
            try {
                current.getOut().println(line, new OutputListener() {
                    @Override
                    public void outputLineSelected(OutputEvent ev) {}
                    @Override
                    public void outputLineAction(OutputEvent ev) {
                        if (onClick != null) onClick.run();
                    }
                    @Override
                    public void outputLineCleared(OutputEvent ev) {}
                });
                return;
            } catch (Throwable ignored) {}
        }
        System.out.println(line);
    }

    public static void linkFile(File file) {
        if (file == null) return;
        link("File: " + file.getName() + " (" + file.getAbsolutePath() + ")", () -> {
            try {
                java.awt.Desktop.getDesktop().open(file.getParentFile());
            } catch (Exception ignored) {}
        });
    }

    private static void log(String tag, String message, boolean isError) {
        String time = LocalTime.now().format(TIME_FMT);
        String formatted = "[" + time + "] " + tag + message;

        InputOutput current = getIO();
        if (current != null) {
            OutputWriter writer = isError ? current.getErr() : current.getOut();
            writer.println(formatted);
        } else {
            if (isError) {
                System.err.println(formatted);
            } else {
                System.out.println(formatted);
            }
        }
    }

    public static void clear() {
        InputOutput current = getIO();
        if (current != null) {
            try {
                current.getOut().reset();
            } catch (Throwable ignored) {}
        }
    }
}
