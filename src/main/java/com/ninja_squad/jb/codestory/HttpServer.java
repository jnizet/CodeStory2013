package com.ninja_squad.jb.codestory;

import com.ninja_squad.jb.codestory.action.CodeStoryActionFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * HTTP Server for CodeStory.
 * @author JB
 */
public class HttpServer {

    public static final int DEFAULT_PORT = 80;
    private final ExecutorService mainExecutor;
    private ListenLoop listenLoop;
    private final int port;

    /**
     * Creates an HTTP Server that will listen on the given port
     * @param port the port to listen to
     */
    public HttpServer(int port) {
        this.port = port;
        mainExecutor = Executors.newSingleThreadExecutor();
    }

    /**
     * Starts the HTTP server. This method doesn't block. It returns as soon as the server socket has been opened
     * and the server starts listening
     * @param actionFactory the factory used to create actions and respond to requests
     * @throws IOException if the server socket couldn't be created
     */
    public void start(ActionFactory actionFactory) throws IOException {
        listenLoop = new ListenLoop(new ServerSocket(port), actionFactory);
        mainExecutor.execute(listenLoop);
    }

    /**
     * Stops the HTTP server. All the pending requests won't be served.
     */
    public void stop() {
        listenLoop.stop();
        mainExecutor.shutdownNow();
    }

    /**
     * Starts an HTTP server. By default, listens to port 80. The port can be passed as argument.
     * @throws IOException if the server can't be started
     */
    public static void main(String[] args) throws IOException {
        int port = DEFAULT_PORT;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        System.out.println("Starting HTTP Server on port " + port);
        new HttpServer(port).start(new CodeStoryActionFactory());
    }
}
