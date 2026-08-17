package com.atomgdx.editor.particle2d;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Serializer for standard LibGDX 2D Particle Effect (.particle) files.
 */
public class Particle2DSerializer {

    public static Particle2DEffectModel loadEffect(File file) throws IOException {
        Particle2DEffectModel effect = new Particle2DEffectModel(file.getName().replace(".particle", ""));
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }

            // Parse emitters
            int index = 0;
            while (index < lines.size()) {
                String emitterName = lines.get(index++).trim();
                if (emitterName.isEmpty()) continue;

                Particle2DEmitterModel emitter = new Particle2DEmitterModel(emitterName);
                while (index < lines.size()) {
                    String propLine = lines.get(index++);
                    if (propLine.trim().isEmpty()) {
                        break; // End of this emitter block
                    }

                    if (propLine.startsWith("- Delay -")) {
                        emitter.setDelay(readNextFloat(lines, index++));
                    } else if (propLine.startsWith("- Duration -")) {
                        emitter.setDuration(readNextFloat(lines, index++));
                    } else if (propLine.startsWith("- Count -")) {
                        emitter.setMinParticleCount((int) readNextFloat(lines, index++));
                        emitter.setMaxParticleCount((int) readNextFloat(lines, index++));
                    } else if (propLine.startsWith("- Emission -")) {
                        emitter.setEmissionRate(readNextFloat(lines, index++));
                    } else if (propLine.startsWith("- Life -")) {
                        emitter.setLifeMin(readNextFloat(lines, index++));
                        emitter.setLifeMax(readNextFloat(lines, index++));
                    } else if (propLine.startsWith("- Scale -")) {
                        emitter.setScaleMin(readNextFloat(lines, index++));
                        emitter.setScaleMax(readNextFloat(lines, index++));
                    } else if (propLine.startsWith("- Velocity -")) {
                        emitter.setVelocityMin(readNextFloat(lines, index++));
                        emitter.setVelocityMax(readNextFloat(lines, index++));
                    } else if (propLine.startsWith("- Angle -")) {
                        emitter.setAngleMin(readNextFloat(lines, index++));
                        emitter.setAngleMax(readNextFloat(lines, index++));
                    } else if (propLine.startsWith("- Rotation -")) {
                        emitter.setRotationMin(readNextFloat(lines, index++));
                        emitter.setRotationMax(readNextFloat(lines, index++));
                    } else if (propLine.startsWith("- Wind -")) {
                        emitter.setWind(readNextFloat(lines, index++));
                    } else if (propLine.startsWith("- Gravity -")) {
                        emitter.setGravity(readNextFloat(lines, index++));
                    } else if (propLine.startsWith("- Options -")) {
                        if (index < lines.size()) emitter.setAttached(Boolean.parseBoolean(lines.get(index++).trim()));
                        if (index < lines.size()) emitter.setContinuous(Boolean.parseBoolean(lines.get(index++).trim()));
                        if (index < lines.size()) emitter.setAdditive(Boolean.parseBoolean(lines.get(index++).trim()));
                        if (index < lines.size()) emitter.setBehind(Boolean.parseBoolean(lines.get(index++).trim()));
                    } else if (propLine.startsWith("- Image Path -")) {
                        if (index < lines.size()) emitter.setImagePath(lines.get(index++).trim());
                    }
                }
                effect.addEmitter(emitter);
            }
        }
        return effect;
    }

    public static void saveEffect(Particle2DEffectModel effect, File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            for (Particle2DEmitterModel emitter : effect.getEmitters()) {
                writer.write(emitter.getName() + "\n");
                
                writer.write("- Delay -\n");
                writer.write("active: true\n");
                writer.write("lowMin: " + emitter.getDelay() + "\n");
                writer.write("lowMax: " + emitter.getDelay() + "\n");

                writer.write("- Duration -\n");
                writer.write("lowMin: " + emitter.getDuration() + "\n");
                writer.write("lowMax: " + emitter.getDuration() + "\n");

                writer.write("- Count -\n");
                writer.write("min: " + emitter.getMinParticleCount() + "\n");
                writer.write("max: " + emitter.getMaxParticleCount() + "\n");

                writer.write("- Emission -\n");
                writer.write("active: true\n");
                writer.write("lowMin: " + emitter.getEmissionRate() + "\n");
                writer.write("lowMax: " + emitter.getEmissionRate() + "\n");

                writer.write("- Life -\n");
                writer.write("active: true\n");
                writer.write("lowMin: " + emitter.getLifeMin() + "\n");
                writer.write("lowMax: " + emitter.getLifeMax() + "\n");

                writer.write("- Scale -\n");
                writer.write("active: true\n");
                writer.write("lowMin: " + emitter.getScaleMin() + "\n");
                writer.write("lowMax: " + emitter.getScaleMax() + "\n");

                writer.write("- Velocity -\n");
                writer.write("active: true\n");
                writer.write("lowMin: " + emitter.getVelocityMin() + "\n");
                writer.write("lowMax: " + emitter.getVelocityMax() + "\n");

                writer.write("- Angle -\n");
                writer.write("active: true\n");
                writer.write("lowMin: " + emitter.getAngleMin() + "\n");
                writer.write("lowMax: " + emitter.getAngleMax() + "\n");

                writer.write("- Rotation -\n");
                writer.write("active: true\n");
                writer.write("lowMin: " + emitter.getRotationMin() + "\n");
                writer.write("lowMax: " + emitter.getRotationMax() + "\n");

                writer.write("- Wind -\n");
                writer.write("active: true\n");
                writer.write("lowMin: " + emitter.getWind() + "\n");
                writer.write("lowMax: " + emitter.getWind() + "\n");

                writer.write("- Gravity -\n");
                writer.write("active: true\n");
                writer.write("lowMin: " + emitter.getGravity() + "\n");
                writer.write("lowMax: " + emitter.getGravity() + "\n");

                writer.write("- Options -\n");
                writer.write("attached: " + emitter.isAttached() + "\n");
                writer.write("continuous: " + emitter.isContinuous() + "\n");
                writer.write("additive: " + emitter.isAdditive() + "\n");
                writer.write("behind: " + emitter.isBehind() + "\n");

                writer.write("- Image Path -\n");
                writer.write(emitter.getImagePath() + "\n\n");
            }
        }
    }

    private static float readNextFloat(List<String> lines, int index) {
        if (index >= lines.size()) return 0f;
        String line = lines.get(index).trim();
        try {
            if (line.contains(":")) {
                line = line.substring(line.indexOf(":") + 1).trim();
            }
            return Float.parseFloat(line);
        } catch (NumberFormatException e) {
            return 0f;
        }
    }
}
