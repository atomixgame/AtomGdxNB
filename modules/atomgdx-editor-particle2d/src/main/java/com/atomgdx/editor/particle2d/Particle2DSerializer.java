package com.atomgdx.editor.particle2d;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Robust serializer for LibGDX 2D Particle Effect files (.p / .particle).
 */
public class Particle2DSerializer {

    public static Particle2DEffectModel loadEffect(File file) throws IOException {
        String effectName = file.getName().replace(".particle", "").replace(".p", "");
        Particle2DEffectModel effect = new Particle2DEffectModel(effectName);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }

            int i = 0;
            while (i < lines.size()) {
                String current = lines.get(i).trim();
                if (current.isEmpty()) {
                    i++;
                    continue;
                }

                String emitterName;
                if (current.startsWith("-")) {
                    emitterName = "Emitter " + (effect.getEmitters().size() + 1);
                } else {
                    emitterName = current;
                    i++;
                }

                Particle2DEmitterModel emitter = new Particle2DEmitterModel(emitterName);

                while (i < lines.size()) {
                    String block = lines.get(i).trim();
                    if (block.isEmpty()) {
                        i++;
                        break;
                    }

                    if (block.startsWith("- Delay -")) {
                        i++;
                        while (i < lines.size() && !lines.get(i).trim().startsWith("-") && !lines.get(i).trim().isEmpty()) {
                            String l = lines.get(i).trim();
                            if (l.startsWith("lowMin:")) emitter.setDelay(parseFloat(l));
                            i++;
                        }
                    } else if (block.startsWith("- Duration -")) {
                        i++;
                        while (i < lines.size() && !lines.get(i).trim().startsWith("-") && !lines.get(i).trim().isEmpty()) {
                            String l = lines.get(i).trim();
                            if (l.startsWith("lowMin:")) emitter.setDuration(parseFloat(l));
                            i++;
                        }
                    } else if (block.startsWith("- Count -")) {
                        i++;
                        while (i < lines.size() && !lines.get(i).trim().startsWith("-") && !lines.get(i).trim().isEmpty()) {
                            String l = lines.get(i).trim();
                            if (l.startsWith("min:")) emitter.setMinParticleCount((int) parseFloat(l));
                            if (l.startsWith("max:")) emitter.setMaxParticleCount((int) parseFloat(l));
                            i++;
                        }
                    } else if (block.startsWith("- Emission -")) {
                        i++;
                        while (i < lines.size() && !lines.get(i).trim().startsWith("-") && !lines.get(i).trim().isEmpty()) {
                            String l = lines.get(i).trim();
                            if (l.startsWith("highMin:") || l.startsWith("lowMin:")) emitter.setEmissionRate(parseFloat(l));
                            i++;
                        }
                    } else if (block.startsWith("- Life -")) {
                        i++;
                        while (i < lines.size() && !lines.get(i).trim().startsWith("-") && !lines.get(i).trim().isEmpty()) {
                            String l = lines.get(i).trim();
                            if (l.startsWith("highMin:") || l.startsWith("lowMin:")) emitter.setLifeMin(parseFloat(l));
                            if (l.startsWith("highMax:") || l.startsWith("lowMax:")) emitter.setLifeMax(parseFloat(l));
                            i++;
                        }
                    } else if (block.startsWith("- Scale -")) {
                        i++;
                        while (i < lines.size() && !lines.get(i).trim().startsWith("-") && !lines.get(i).trim().isEmpty()) {
                            String l = lines.get(i).trim();
                            if (l.startsWith("highMin:") || l.startsWith("lowMin:")) emitter.setScaleMin(parseFloat(l));
                            if (l.startsWith("highMax:") || l.startsWith("lowMax:")) emitter.setScaleMax(parseFloat(l));
                            i++;
                        }
                    } else if (block.startsWith("- Velocity -")) {
                        i++;
                        while (i < lines.size() && !lines.get(i).trim().startsWith("-") && !lines.get(i).trim().isEmpty()) {
                            String l = lines.get(i).trim();
                            if (l.startsWith("highMin:") || l.startsWith("lowMin:")) emitter.setVelocityMin(parseFloat(l));
                            if (l.startsWith("highMax:") || l.startsWith("lowMax:")) emitter.setVelocityMax(parseFloat(l));
                            i++;
                        }
                    } else if (block.startsWith("- Angle -")) {
                        i++;
                        while (i < lines.size() && !lines.get(i).trim().startsWith("-") && !lines.get(i).trim().isEmpty()) {
                            String l = lines.get(i).trim();
                            if (l.startsWith("highMin:") || l.startsWith("lowMin:")) emitter.setAngleMin(parseFloat(l));
                            if (l.startsWith("highMax:") || l.startsWith("lowMax:")) emitter.setAngleMax(parseFloat(l));
                            i++;
                        }
                    } else if (block.startsWith("- Rotation -")) {
                        i++;
                        while (i < lines.size() && !lines.get(i).trim().startsWith("-") && !lines.get(i).trim().isEmpty()) {
                            String l = lines.get(i).trim();
                            if (l.startsWith("highMin:") || l.startsWith("lowMin:")) emitter.setRotationMin(parseFloat(l));
                            if (l.startsWith("highMax:") || l.startsWith("lowMax:")) emitter.setRotationMax(parseFloat(l));
                            i++;
                        }
                    } else if (block.startsWith("- Wind -")) {
                        i++;
                        while (i < lines.size() && !lines.get(i).trim().startsWith("-") && !lines.get(i).trim().isEmpty()) {
                            String l = lines.get(i).trim();
                            if (l.startsWith("highMin:") || l.startsWith("lowMin:")) emitter.setWind(parseFloat(l));
                            i++;
                        }
                    } else if (block.startsWith("- Gravity -")) {
                        i++;
                        while (i < lines.size() && !lines.get(i).trim().startsWith("-") && !lines.get(i).trim().isEmpty()) {
                            String l = lines.get(i).trim();
                            if (l.startsWith("highMin:") || l.startsWith("lowMin:")) emitter.setGravity(parseFloat(l));
                            i++;
                        }
                    } else if (block.startsWith("- Options -")) {
                        i++;
                        while (i < lines.size() && !lines.get(i).trim().startsWith("-") && !lines.get(i).trim().isEmpty()) {
                            String l = lines.get(i).trim();
                            if (l.startsWith("attached:")) emitter.setAttached(Boolean.parseBoolean(parseValue(l)));
                            if (l.startsWith("continuous:")) emitter.setContinuous(Boolean.parseBoolean(parseValue(l)));
                            if (l.startsWith("additive:")) emitter.setAdditive(Boolean.parseBoolean(parseValue(l)));
                            if (l.startsWith("behind:")) emitter.setBehind(Boolean.parseBoolean(parseValue(l)));
                            i++;
                        }
                    } else if (block.startsWith("- Image Paths -") || block.startsWith("- Image Path -")) {
                        i++;
                        if (i < lines.size() && !lines.get(i).trim().startsWith("-")) {
                            emitter.setImagePath(lines.get(i).trim());
                            i++;
                        }
                    } else {
                        i++;
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
                writer.write("active: " + (emitter.getDelay() > 0) + "\n");
                writer.write("lowMin: " + emitter.getDelay() + "\n");
                writer.write("lowMax: " + emitter.getDelay() + "\n");

                writer.write("- Duration -\n");
                writer.write("lowMin: " + emitter.getDuration() + "\n");
                writer.write("lowMax: " + emitter.getDuration() + "\n");

                writer.write("- Count -\n");
                writer.write("min: " + emitter.getMinParticleCount() + "\n");
                writer.write("max: " + emitter.getMaxParticleCount() + "\n");

                writer.write("- Emission -\n");
                writer.write("lowMin: 0.0\n");
                writer.write("lowMax: 0.0\n");
                writer.write("highMin: " + emitter.getEmissionRate() + "\n");
                writer.write("highMax: " + emitter.getEmissionRate() + "\n");

                writer.write("- Life -\n");
                writer.write("lowMin: 0.0\n");
                writer.write("lowMax: 0.0\n");
                writer.write("highMin: " + emitter.getLifeMin() + "\n");
                writer.write("highMax: " + emitter.getLifeMax() + "\n");

                writer.write("- Scale -\n");
                writer.write("lowMin: 0.0\n");
                writer.write("lowMax: 0.0\n");
                writer.write("highMin: " + emitter.getScaleMin() + "\n");
                writer.write("highMax: " + emitter.getScaleMax() + "\n");

                writer.write("- Velocity -\n");
                writer.write("lowMin: 0.0\n");
                writer.write("lowMax: 0.0\n");
                writer.write("highMin: " + emitter.getVelocityMin() + "\n");
                writer.write("highMax: " + emitter.getVelocityMax() + "\n");

                writer.write("- Angle -\n");
                writer.write("lowMin: 0.0\n");
                writer.write("lowMax: 0.0\n");
                writer.write("highMin: " + emitter.getAngleMin() + "\n");
                writer.write("highMax: " + emitter.getAngleMax() + "\n");

                writer.write("- Rotation -\n");
                writer.write("active: " + (emitter.getRotationMax() > 0) + "\n");
                writer.write("lowMin: 0.0\n");
                writer.write("lowMax: 0.0\n");
                writer.write("highMin: " + emitter.getRotationMin() + "\n");
                writer.write("highMax: " + emitter.getRotationMax() + "\n");

                writer.write("- Wind -\n");
                writer.write("active: " + (emitter.getWind() != 0) + "\n");
                writer.write("lowMin: " + emitter.getWind() + "\n");
                writer.write("lowMax: " + emitter.getWind() + "\n");

                writer.write("- Gravity -\n");
                writer.write("active: " + (emitter.getGravity() != 0) + "\n");
                writer.write("lowMin: " + emitter.getGravity() + "\n");
                writer.write("lowMax: " + emitter.getGravity() + "\n");

                writer.write("- Transparency -\n");
                writer.write("highMin: 1.0\n");
                writer.write("highMax: 1.0\n");

                writer.write("- Options -\n");
                writer.write("attached: " + emitter.isAttached() + "\n");
                writer.write("continuous: " + emitter.isContinuous() + "\n");
                writer.write("aligned: false\n");
                writer.write("additive: " + emitter.isAdditive() + "\n");
                writer.write("behind: " + emitter.isBehind() + "\n");
                writer.write("premultipliedAlpha: false\n");

                writer.write("- Image Paths -\n");
                String img = emitter.getImagePath();
                writer.write((img != null && !img.isEmpty() ? img : "particle.png") + "\n\n");
            }
        }
    }

    private static String parseValue(String line) {
        if (line.contains(":")) {
            return line.substring(line.indexOf(":") + 1).trim();
        }
        return line.trim();
    }

    private static float parseFloat(String line) {
        String val = parseValue(line);
        try {
            return Float.parseFloat(val);
        } catch (NumberFormatException e) {
            return 0f;
        }
    }
}
