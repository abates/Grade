package co.andrewbates.grade.sandbox;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class SandboxConnection extends URLConnection {
    File file;
    FileInputStream inputStream;

    protected SandboxConnection(URL url) {
        super(url);
        file = new File(url.getFile());
    }

    @Override
    public void connect() throws IOException {
        inputStream = new FileInputStream(file);
        connected = true;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return inputStream;
    }
}
