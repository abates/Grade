package co.andrewbates.grade.sandbox;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;

public class ClassLoader extends java.lang.ClassLoader {
    Sandbox sandbox;
    URLClassLoader parent;
    ProtectionDomain domain;
    CodeSource codeSource;

    public ClassLoader(Sandbox sandbox) {
        this.sandbox = sandbox;
        parent = new URLClassLoader(new URL[] { getURL() });
        codeSource = new CodeSource(getURL(), (Certificate[]) null);
        domain = new ProtectionDomain(codeSource, new SandboxPermissions(sandbox));
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return parent.loadClass(name);
    }

    private URL getURL() {
        URL url;
        try {
            url = new URL("sandbox://" + sandbox.getDir().getAbsolutePath());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return url;
    }

    public Class<?> loadClass(File classFile) throws IOException {
        byte[] bytes = Files.readAllBytes(classFile.toPath());
        return defineClass(null, bytes, 0, bytes.length, domain);
    }
}
