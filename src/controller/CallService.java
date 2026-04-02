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
        if (connection == null) {
            System.out.println("[CallService] Connection is not initialized");
            return;
        }

        AudioFormat format = new AudioFormat(44100, 16, 1, true, false);

        try {
            InputStream in = connection.getInputStream();
            OutputStream out = connection.getOutputStream();

            // Speaker: plays audio received from peer
            DataLine.Info speakerInfo = new DataLine.Info(SourceDataLine.class, format);
            SourceDataLine speakers = (SourceDataLine) AudioSystem.getLine(speakerInfo);
            speakers.open(format);
            speakers.start();

            // Microphone: captures local audio and sends to peer
            DataLine.Info micInfo = new DataLine.Info(TargetDataLine.class, format);
            TargetDataLine microphone = (TargetDataLine) AudioSystem.getLine(micInfo);
            microphone.open(format);
            microphone.start();

            // Thread 1: mic -> peer (send)
            Thread sendThread = new Thread(new Runnable() {
                public void run() {
                    byte[] buf = new byte[1024];
                    try {
                        while (isActive) {
                            int n = microphone.read(buf, 0, buf.length);
                            if (n > 0) {
                                out.write(buf, 0, n);
                                out.flush();
                            }
                        }
                    } catch (IOException e) {
                        // call ended or connection dropped
                    } finally {
                        microphone.stop();
                        microphone.close();
                    }
                }
            });
            sendThread.setDaemon(true);
            sendThread.start();

            // Thread 2: peer -> speakers (receive), runs on this thread
            byte[] buf = new byte[1024];
            try {
                while (isActive) {
                    int n = in.read(buf);
                    if (n > 0) {
                        speakers.write(buf, 0, n);
                    }
                }
            } catch (IOException e) {
                // call ended or connection dropped
            } finally {
                speakers.stop();
                speakers.close();
            }

        } catch (LineUnavailableException e) {
            System.out.println("[CallService] Audio device error: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("[CallService] Stream error: " + e.getMessage());
        }
    }
}