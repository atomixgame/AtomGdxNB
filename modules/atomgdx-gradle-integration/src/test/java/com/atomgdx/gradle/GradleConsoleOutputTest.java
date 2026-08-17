package com.atomgdx.gradle;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GradleConsoleOutputTest {

    @Test
    void testOutputClassification() {
        GradleConsoleOutput output = new GradleConsoleOutput();

        output.appendLine("> Task :lwjgl3:compileJava UP-TO-DATE");
        output.appendLine("WARNING: An illegal reflective access operation has occurred");
        output.appendLine("FAILURE: Build failed with an exception.");
        output.appendLine("BUILD SUCCESSFUL in 2s");
        output.appendLine("Regular info line");

        assertThat(output.getLines()).hasSize(5);
        assertThat(output.getLines().get(0).getType()).isEqualTo(GradleConsoleOutput.LineType.SUCCESS);
        assertThat(output.getLines().get(1).getType()).isEqualTo(GradleConsoleOutput.LineType.WARNING);
        assertThat(output.getLines().get(2).getType()).isEqualTo(GradleConsoleOutput.LineType.ERROR);
        assertThat(output.getLines().get(3).getType()).isEqualTo(GradleConsoleOutput.LineType.SUCCESS);
        assertThat(output.getLines().get(4).getType()).isEqualTo(GradleConsoleOutput.LineType.STANDARD);
    }
}
