package com.ninja_squad.jb.codestory;

import com.google.common.io.Closeables;

import javax.annotation.Nonnull;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The main loop, which listens for new HTTP connections. Given the simplicity of what the server must do,
 * all the requests are served synchronously, by a single thread.
 * @author JB
 */
class ListenLoop implements Runnable {

    private final ServerSocket serverSocket;
    private final ActionFactory actionFactory;

    /**
     * Indicates that the thread should stop serving requests. We can't use standard thread interrupts because
     * the server socket doesn't react to them.
     */
    private final AtomicBoolean stopped = new AtomicBoolean(false);

    public ListenLoop(@Nonnull ServerSocket serverSocket,
                      @Nonnull ActionFactory actionFactory) {
        this.serverSocket = serverSocket;
        this.actionFactory = actionFactory;
    }

    /**
     * Stops the listen loop by closing the server socket.
     */
    public void stop() {
        stopped.set(true);
        try {
            serverSocket.close();
        }
        catch (IOException e) {
            // too bad, but we don't care.
        }
    }

    @Override
    public void run() {
        while (!stopped.get()) {
            try {
                Socket socket = serverSocket.accept();
                answerTo(socket);
            }
            catch (IOException e) {
                // too bad : either the request couldn't be served, and we don't care, or
                // the server socket can't accept a connection because it has been closed. If that's the
                // case, the stopped flag will be set to true and the loop will end.
            }
        }
    }

    /**
     * Answers to a request.
     */
    private void answerTo(Socket socket) throws IOException {
        try (InputStream in = new BufferedInputStream(socket.getInputStream());
             OutputStream out = new BufferedOutputStream(socket.getOutputStream())) {
            HttpRequest request = HttpRequest.parse(in);
            HttpResponse response = actionFactory.getAction(request).execute(request);
            response.send(out);
        }
        finally {
            Closeables.close(socket, true);
        }
    }
}
