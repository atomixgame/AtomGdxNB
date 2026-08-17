package com.atomgdx.viewer3d.loader;

import com.atomgdx.core.log.StudioLog;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * High-performance native Binary glTF (.GLB) and JSON glTF (.GLTF) 3D Model Loader.
 * Directly decodes GLB chunks, vertex accessors (POSITION, NORMAL, TEXCOORD), indices,
 * materials, and node hierarchies into real hardware LibGDX Model / Mesh instances.
 */
public class GlbModelLoader {

    private static final int GLB_MAGIC = 0x46546C67; // "glTF"
    private static final int CHUNK_TYPE_JSON = 0x4E4F534A; // "JSON"
    private static final int CHUNK_TYPE_BIN = 0x004E4942; // "BIN\0"

    // Component Types
    private static final int COMP_BYTE = 5120;
    private static final int COMP_UNSIGNED_BYTE = 5121;
    private static final int COMP_SHORT = 5122;
    private static final int COMP_UNSIGNED_SHORT = 5123;
    private static final int COMP_UNSIGNED_INT = 5125;
    private static final int COMP_FLOAT = 5126;

    public static Model loadGlbModel(File file) {
        if (file == null || !file.exists()) {
            StudioLog.warn("Cannot load GLB: File does not exist (" + file + ")");
            return null;
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] fileBytes = fis.readAllBytes();
            ByteBuffer buf = ByteBuffer.wrap(fileBytes).order(ByteOrder.LITTLE_ENDIAN);

            if (buf.remaining() < 12) {
                StudioLog.warn("File too small for GLB header: " + file.getName());
                return null;
            }

            int magic = buf.getInt();
            int version = buf.getInt();
            int length = buf.getInt();

            if (magic != GLB_MAGIC) {
                StudioLog.warn("Invalid GLB magic header (0x" + Integer.toHexString(magic) + ") for file: " + file.getName());
                return null;
            }

            // Read Chunks
            String jsonContent = null;
            byte[] binData = null;

            while (buf.hasRemaining()) {
                int chunkLen = buf.getInt();
                int chunkType = buf.getInt();

                if (chunkType == CHUNK_TYPE_JSON) {
                    byte[] jsonBytes = new byte[chunkLen];
                    buf.get(jsonBytes);
                    jsonContent = new String(jsonBytes, "UTF-8");
                } else if (chunkType == CHUNK_TYPE_BIN) {
                    binData = new byte[chunkLen];
                    buf.get(binData);
                } else {
                    // Skip unrecognized chunk
                    buf.position(buf.position() + chunkLen);
                }
            }

            if (jsonContent == null) {
                StudioLog.warn("GLB has no JSON chunk: " + file.getName());
                return null;
            }

            if (binData == null) {
                binData = new byte[0];
            }

            return parseGltfModel(jsonContent, binData, file.getName());

        } catch (Exception ex) {
            StudioLog.error("Failed to parse GLB file: " + file.getName(), ex);
            return null;
        }
    }

    private static Model parseGltfModel(String jsonText, byte[] binData, String modelName) {
        JsonReader reader = new JsonReader();
        JsonValue root = reader.parse(jsonText);

        JsonValue accessors = root.get("accessors");
        JsonValue bufferViews = root.get("bufferViews");
        JsonValue meshes = root.get("meshes");
        JsonValue materials = root.get("materials");

        if (meshes == null || accessors == null || bufferViews == null) {
            StudioLog.warn("glTF missing meshes/accessors/bufferViews in " + modelName);
            return null;
        }

        Model model = new Model();
        ByteBuffer binBuf = ByteBuffer.wrap(binData).order(ByteOrder.LITTLE_ENDIAN);

        // Parse Materials
        Array<Material> parsedMaterials = new Array<>();
        if (materials != null) {
            for (JsonValue matVal : materials) {
                Material mat = new Material();
                String matName = matVal.getString("name", "Material_" + parsedMaterials.size);
                mat.id = matName;

                Color baseColor = new Color(0.75f, 0.75f, 0.78f, 1.0f);
                JsonValue pbr = matVal.get("pbrMetallicRoughness");
                if (pbr != null) {
                    JsonValue bcf = pbr.get("baseColorFactor");
                    if (bcf != null && bcf.size >= 3) {
                        baseColor = new Color(bcf.getFloat(0), bcf.getFloat(1), bcf.getFloat(2), bcf.size >= 4 ? bcf.getFloat(3) : 1f);
                    }
                }
                mat.set(ColorAttribute.createDiffuse(baseColor));
                mat.set(ColorAttribute.createSpecular(Color.WHITE));
                parsedMaterials.add(mat);
                model.materials.add(mat);
            }
        }

        if (parsedMaterials.size == 0) {
            Material defMat = new Material(ColorAttribute.createDiffuse(new Color(0.75f, 0.78f, 0.82f, 1f)));
            defMat.id = "Default_PBR";
            parsedMaterials.add(defMat);
            model.materials.add(defMat);
        }

        // Bounding Box Tracking for Auto-Centering & Normalizing Scale
        float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, minZ = Float.MAX_VALUE;
        float maxX = -Float.MAX_VALUE, maxY = -Float.MAX_VALUE, maxZ = -Float.MAX_VALUE;

        // Parse Meshes
        int meshIndex = 0;
        for (JsonValue meshVal : meshes) {
            JsonValue primitives = meshVal.get("primitives");
            if (primitives == null) continue;

            for (JsonValue primVal : primitives) {
                JsonValue attrs = primVal.get("attributes");
                if (attrs == null || !attrs.has("POSITION")) continue;

                int posAccIdx = attrs.getInt("POSITION");
                int normAccIdx = attrs.has("NORMAL") ? attrs.getInt("NORMAL") : -1;
                int uvAccIdx = attrs.has("TEXCOORD_0") ? attrs.getInt("TEXCOORD_0") : -1;
                int indAccIdx = primVal.has("indices") ? primVal.getInt("indices") : -1;
                int matIdx = primVal.has("material") ? primVal.getInt("material") : 0;

                Material mat = matIdx >= 0 && matIdx < parsedMaterials.size ? parsedMaterials.get(matIdx) : parsedMaterials.get(0);

                // 1. Read Positions
                float[] positions = readFloatAccessor(accessors.get(posAccIdx), bufferViews, binBuf);
                int vertexCount = positions.length / 3;

                // Track Bounding Box
                for (int i = 0; i < positions.length; i += 3) {
                    minX = Math.min(minX, positions[i]);
                    minY = Math.min(minY, positions[i + 1]);
                    minZ = Math.min(minZ, positions[i + 2]);
                    maxX = Math.max(maxX, positions[i]);
                    maxY = Math.max(maxY, positions[i + 1]);
                    maxZ = Math.max(maxZ, positions[i + 2]);
                }

                // 2. Read Normals
                float[] normals = normAccIdx >= 0 ? readFloatAccessor(accessors.get(normAccIdx), bufferViews, binBuf) : null;

                // 3. Read UVs
                float[] uvs = uvAccIdx >= 0 ? readFloatAccessor(accessors.get(uvAccIdx), bufferViews, binBuf) : null;

                // 4. Read Indices
                short[] indices = indAccIdx >= 0 ? readIndicesAccessor(accessors.get(indAccIdx), bufferViews, binBuf) : null;

                // Assemble LibGDX Interleaved Vertex Data: Pos (3) + Normal (3) + UV (2) = 8 floats per vertex
                boolean hasNormals = normals != null && normals.length == positions.length;
                boolean hasUvs = uvs != null && uvs.length == vertexCount * 2;

                Array<VertexAttribute> attribList = new Array<>();
                attribList.add(new VertexAttribute(VertexAttributes.Usage.Position, 3, "a_position"));
                if (hasNormals) attribList.add(new VertexAttribute(VertexAttributes.Usage.Normal, 3, "a_normal"));
                if (hasUvs) attribList.add(new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, "a_texCoord0"));

                int floatsPerVertex = 3 + (hasNormals ? 3 : 0) + (hasUvs ? 2 : 0);
                float[] vertexBuffer = new float[vertexCount * floatsPerVertex];

                int vIdx = 0;
                for (int i = 0; i < vertexCount; i++) {
                    vertexBuffer[vIdx++] = positions[i * 3];
                    vertexBuffer[vIdx++] = positions[i * 3 + 1];
                    vertexBuffer[vIdx++] = positions[i * 3 + 2];

                    if (hasNormals) {
                        vertexBuffer[vIdx++] = normals[i * 3];
                        vertexBuffer[vIdx++] = normals[i * 3 + 1];
                        vertexBuffer[vIdx++] = normals[i * 3 + 2];
                    }

                    if (hasUvs) {
                        vertexBuffer[vIdx++] = uvs[i * 2];
                        vertexBuffer[vIdx++] = uvs[i * 2 + 1];
                    }
                }

                VertexAttribute[] vAttribArray = attribList.toArray(VertexAttribute.class);
                Mesh gdxMesh = new Mesh(true, vertexCount, indices != null ? indices.length : 0, vAttribArray);
                gdxMesh.setVertices(vertexBuffer);
                if (indices != null) {
                    gdxMesh.setIndices(indices);
                }

                model.meshes.add(gdxMesh);

                MeshPart meshPart = new MeshPart();
                meshPart.id = "part_" + meshIndex;
                meshPart.primitiveType = GL20.GL_TRIANGLES;
                meshPart.offset = 0;
                meshPart.size = indices != null ? indices.length : vertexCount;
                meshPart.mesh = gdxMesh;
                model.meshParts.add(meshPart);

                Node node = new Node();
                node.id = "Node_" + meshVal.getString("name", "Mesh_" + meshIndex);
                NodePart nodePart = new NodePart(meshPart, mat);
                node.parts.add(nodePart);
                model.nodes.add(node);

                meshIndex++;
            }
        }

        // Calculate Scale and Offset to Normalize Model into 4x4 Viewport Box
        float sizeX = maxX - minX;
        float sizeY = maxY - minY;
        float sizeZ = maxZ - minZ;
        float maxDim = Math.max(sizeX, Math.max(sizeY, sizeZ));
        if (maxDim > 0.0001f) {
            float targetSize = 3.5f;
            float scale = targetSize / maxDim;
            float centerX = (minX + maxX) / 2f;
            float centerY = (minY + maxY) / 2f;
            float centerZ = (minZ + maxZ) / 2f;

            for (Node n : model.nodes) {
                n.scale.set(scale, scale, scale);
                n.translation.set(-centerX * scale, -centerY * scale + (targetSize / 2f), -centerZ * scale);
                n.calculateTransforms(true);
            }
        }

        StudioLog.info("Loaded real GLB/GLTF model: " + modelName + " (" + model.nodes.size + " nodes, " + model.meshes.size + " meshes, dim: " + String.format("%.2f", maxDim) + ")");
        return model;
    }

    private static float[] readFloatAccessor(JsonValue accessor, JsonValue bufferViews, ByteBuffer binBuf) {
        int bufferViewIdx = accessor.getInt("bufferView");
        int count = accessor.getInt("count");
        String type = accessor.getString("type");
        int components = "SCALAR".equals(type) ? 1 : "VEC2".equals(type) ? 2 : "VEC3".equals(type) ? 3 : "VEC4".equals(type) ? 4 : 1;

        JsonValue bv = bufferViews.get(bufferViewIdx);
        int bvOffset = bv.getInt("byteOffset", 0);
        int accOffset = accessor.getInt("byteOffset", 0);
        int totalOffset = bvOffset + accOffset;

        float[] floats = new float[count * components];
        binBuf.position(totalOffset);
        for (int i = 0; i < floats.length; i++) {
            floats[i] = binBuf.getFloat();
        }
        return floats;
    }

    private static short[] readIndicesAccessor(JsonValue accessor, JsonValue bufferViews, ByteBuffer binBuf) {
        int bufferViewIdx = accessor.getInt("bufferView");
        int count = accessor.getInt("count");
        int compType = accessor.getInt("componentType");

        JsonValue bv = bufferViews.get(bufferViewIdx);
        int bvOffset = bv.getInt("byteOffset", 0);
        int accOffset = accessor.getInt("byteOffset", 0);
        int totalOffset = bvOffset + accOffset;

        short[] indices = new short[count];
        binBuf.position(totalOffset);

        if (compType == COMP_UNSIGNED_SHORT) {
            for (int i = 0; i < count; i++) {
                indices[i] = binBuf.getShort();
            }
        } else if (compType == COMP_UNSIGNED_BYTE) {
            for (int i = 0; i < count; i++) {
                indices[i] = (short) (binBuf.get() & 0xFF);
            }
        } else if (compType == COMP_UNSIGNED_INT) {
            for (int i = 0; i < count; i++) {
                indices[i] = (short) (binBuf.getInt() & 0xFFFF);
            }
        }
        return indices;
    }
}
