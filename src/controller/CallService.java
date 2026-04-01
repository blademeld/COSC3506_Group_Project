package controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.UUID;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

import model.Message;
import model.TranscriptStore;

public class CallService extends Thread {

    private final TranscriptStore store;
    private boolean isActive;
    private ConnectionHandler connection;
    private String localId;
    private String peerId;

    public CallService(){
        this.store = new TranscriptStore();
    }

    // Constructor that accepts TranscriptStore
    public CallService(TranscriptStore store) {
        this.store = store;
    }

    public void connect(String peerId) {
        this.peerId = peerId;
    }

    // Connects to the peer and initializes the connection
    public void connect(String localId, String peerId, ConnectionHandler connection) {
        this.localId = localId;
        this.peerId = peerId;
        this.connection = connection;
        this.isActive = true;
        System.out.println("[CallService] Connecting audio call with " + peerId);
    }

    public void disconnect(String peerId) {
        disconnect();
    }

    // Disconnects the call, logs the message, and clears state
    public void disconnect() {
        if (isActive && peerId != null && peerId.equals(peerId)) {
            isActive = false;

            String content = "Call ended with " + peerId;

            Message msg = new Message(
                    UUID.randomUUID().toString(),
                    localId,
                    peerId,
                    new Date(),
                    content);

            store.addMessage(msg);

            System.out.println("[CallService] Call ended with " + peerId);
        }
    }

    // Sends audio data from the microphone to the peer
    public void sendAudio() {
        if (!isActive) {
            System.out.println("[CallService] Cannot send audio, call is not active");
            return;
        }
        System.out.println("[CallService] Sending audio packet (mic -> peer)");
    }

    // Receives audio data from the peer and plays it on the speakers
    public void receiveAudio() {
        if (!isActive) {
            System.out.println("[CallService] Cannot receive audio, call is not active");
            return;
        }
        System.out.println("[CallService] Receiving audio packet (peer -> speaker)");
    }

    @Override
    public void run() {
        SourceDataLine speakers = null;
        TargetDataLine microphone = null;

        try {
            // Ensure that connection is initialized properly
            if (connection == null) {
                throw new IllegalStateException("Connection is not initialized");
            }

            // Get input and output streams from the connection
            InputStream in = connection.getInputStream();
            OutputStream out = connection.getOutputStream();
            // Socket socket = connection.getSocket();

            // Audio format configuration
            AudioFormat format = new AudioFormat(16000, 8, 2, true, true);

            // Setup speakers (output audio)
            DataLine.Info speakerInfo = new DataLine.Info(SourceDataLine.class, format);
            speakers = (SourceDataLine) AudioSystem.getLine(speakerInfo);
            speakers.open(format);
            speakers.start();

            // Setup microphone (input audio)
            DataLine.Info micInfo = new DataLine.Info(TargetDataLine.class, format);
            microphone = (TargetDataLine) AudioSystem.getLine(micInfo);
            microphone.open(format);
            microphone.start();

            // Buffer arrays
            byte[] bufferForOutput = new byte[1024];
            byte[] bufferForInput = new byte[1024];

            int bytesReadFromInput;
            int bytesReadFromMicrophone;

            // Continuous audio transmission while the call is active
            while (isActive) {
                // Read from the input stream (audio from the peer)
                bytesReadFromInput = in.read(bufferForInput);
                if (bytesReadFromInput > 0) {
                    speakers.write(bufferForInput, 0, bytesReadFromInput);
                }

                // Read from the microphone and send to peer
                bytesReadFromMicrophone = microphone.read(bufferForOutput, 0, bufferForOutput.length);
                if (bytesReadFromMicrophone > 0) {
                    out.write(bufferForOutput, 0, bytesReadFromMicrophone);
                }
            }
        } catch (IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}