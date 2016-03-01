/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.dfa.unict;

/**
 *
 * @author mario
 */
public class AppInput {
    // Applicatoin inputs

    private String modelName;   // Filename for application input file
    private String jobIdentifier;   // User' given job identifier

    // Each inputSandobox file must be declared below
    // This variable contains the content of an uploaded file
    private String inputSandbox;

    // Some user level information
    // must be stored as well
    private String username;
    private String timestamp;

    /**
     * Standard constructor just initialize empty values
     */
    public AppInput() {
        this.modelName = 
        this.jobIdentifier = 
        this.inputSandbox = 
        this.username = 
        this.timestamp = "";
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getJobIdentifier() {
        return jobIdentifier;
    }

    public void setJobIdentifier(String jobIdentifier) {
        this.jobIdentifier = jobIdentifier;
    }

    public String getInputSandbox() {
        return inputSandbox;
    }

    public void setInputSandbox(String inputFile) {
//        this.inputSandbox += (this.modelName.equals("") ? "" : "," ) + inputFile;
    	this.inputSandbox += (this.inputSandbox.equals("") ? "" : "," ) + inputFile;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "AppInput{" + "modelName=" + modelName
                + ", jobIdentifier=" + jobIdentifier
                + ", inputSandbox=" + inputSandbox
                + ", username=" + username + ", timestamp=" + timestamp + '}';
    }

}
