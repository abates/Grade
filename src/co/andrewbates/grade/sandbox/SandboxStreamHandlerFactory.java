package co.andrewbates.grade.sandbox;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class SandboxStreamHandlerFactory implements java.net.URLStreamHandlerFactory {
    URLStreamHandler streamHandler;

    public SandboxStreamHandlerFactory() {
        super();
        this.streamHandler = new URLStreamHandler() {
            @Override
            protected URLConnection openConnection(URL url) throws IOException {
                return new SandboxConnection(url);
            }
        };
    }

    @Override
    public URLStreamHandler createURLStreamHandler(String protocol) {
        if ("sandbox".equals(protocol)) {
            return streamHandler;
        }
        return null;
    }

}
