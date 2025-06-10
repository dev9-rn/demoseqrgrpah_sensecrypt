package com.sssl.seqrgraphdemo.networks.liveness;

import java.io.Serializable;

public class LivenessResponseModel implements Serializable {
    private double score;
    private double quality;
    private double probability;
    private String error;
    private String error_code;

    public LivenessResponseModel() {
    }

    public LivenessResponseModel(double score, double quality, double probability, String error, String error_code) {
        this.score = score;
        this.quality = quality;
        this.probability = probability;
        this.error = error;
        this.error_code = error_code;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public double getQuality() {
        return quality;
    }

    public void setQuality(double quality) {
        this.quality = quality;
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getError_code() {
        return error_code;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }
}

