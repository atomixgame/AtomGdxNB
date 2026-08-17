package com.atomgdx.viewer.media;

import java.io.File;
import java.io.Serializable;

/**
 * Metadata and playback settings for game audio files (.wav, .mp3, .ogg).
 */
public class AudioMetadata implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum AudioFormat {
        WAV,
        MP3,
        OGG,
        UNKNOWN
    }

    private final File audioFile;
    private final AudioFormat format;
    private float durationSeconds = 0.0f;
    private int sampleRate = 44100;
    private int channels = 2; // 1 = Mono, 2 = Stereo
    private int bitRate = 128; // kbps

    // Testing Controls
    private float volume = 1.0f;
    private float pitch = 1.0f;
    private float pan = 0.0f; // -1.0 left to +1.0 right
    private boolean looping = false;

    public AudioMetadata(File audioFile) {
        this.audioFile = audioFile;
        this.format = detectFormat(audioFile);
    }

    private AudioFormat detectFormat(File file) {
        if (file == null) return AudioFormat.UNKNOWN;
        String name = file.getName().toLowerCase();
        if (name.endsWith(".wav")) return AudioFormat.WAV;
        if (name.endsWith(".mp3")) return AudioFormat.MP3;
        if (name.endsWith(".ogg")) return AudioFormat.OGG;
        return AudioFormat.UNKNOWN;
    }

    public File getAudioFile() { return audioFile; }
    public AudioFormat getFormat() { return format; }

    public float getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(float durationSeconds) { this.durationSeconds = durationSeconds; }

    public int getSampleRate() { return sampleRate; }
    public void setSampleRate(int sampleRate) { this.sampleRate = sampleRate; }

    public int getChannels() { return channels; }
    public void setChannels(int channels) { this.channels = channels; }

    public int getBitRate() { return bitRate; }
    public void setBitRate(int bitRate) { this.bitRate = bitRate; }

    public float getVolume() { return volume; }
    public void setVolume(float volume) { this.volume = volume; }

    public float getPitch() { return pitch; }
    public void setPitch(float pitch) { this.pitch = pitch; }

    public float getPan() { return pan; }
    public void setPan(float pan) { this.pan = pan; }

    public boolean isLooping() { return looping; }
    public void setLooping(boolean looping) { this.looping = looping; }
}
