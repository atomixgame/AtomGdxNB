package com.atomgdx.gradle;

import com.atomgdx.core.project.LibGdxProject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Executes Gradle tasks for a LibGDX project using gradle wrapper or system gradle.
 */
public class GradleRunner {

    public interface OutputListener {
        void onLineReceived(GradleConsoleOutput.OutputLine line);
    }

    public static CompletableFuture<Integer> executeTask(
            LibGdxProject project,
            GradleTask task,
            List<String> extraArgs,
            OutputListener listener
    ) {
        return CompletableFuture.supplyAsync(() -> {
            File rootDir = project.getRootDirectory();
            boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
            String gradleCmd = isWindows ? "gradlew.bat" : "./gradlew";

            File gradlew = new File(rootDir, gradleCmd);
            List<String> command = new ArrayList<>();

            if (gradlew.exists()) {
                command.add(gradlew.getAbsolutePath());
            } else {
                command.add(isWindows ? "gradle.bat" : "gradle");
            }

            command.add(task.getTaskName());
            if (extraArgs != null) {
                command.addAll(extraArgs);
            }

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(rootDir);
            pb.redirectErrorStream(true);

            GradleConsoleOutput consoleOutput = new GradleConsoleOutput();

            try {
                Process process = pb.start();
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        consoleOutput.appendLine(line);
                        if (listener != null) {
                            List<GradleConsoleOutput.OutputLine> lines = consoleOutput.getLines();
                            listener.onLineReceived(lines.get(lines.size() - 1));
                        }
                    }
                }
                return process.waitFor();
            } catch (Exception e) {
                String err = "Execution failed: " + e.getMessage();
                consoleOutput.appendLine(err);
                if (listener != null) {
                    listener.onLineReceived(new GradleConsoleOutput.OutputLine(err, GradleConsoleOutput.LineType.ERROR));
                }
                return -1;
            }
        });
    }
}
