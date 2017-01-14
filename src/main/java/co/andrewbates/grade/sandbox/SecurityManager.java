package co.andrewbates.grade.sandbox;

import java.io.FilePermission;
import java.security.AccessControlException;
import java.security.Permission;
import java.security.ProtectionDomain;
import java.security.SecurityPermission;

public class SecurityManager extends java.lang.SecurityManager {
    FilePermission sandboxRead;

    @Override
    public void checkPermission(Permission perm) {
        if (perm instanceof RuntimePermission) {
            if ("getProtectionDomain".equals(perm.getName())) {
                return;
            }
        } else if (perm instanceof SecurityPermission) {
            SecurityPermission sp = (SecurityPermission) perm;
            if ("getPolicy".equals(sp.getName())) {
                return;
            }
        }

        ProtectionDomain domain = getDomain();
        if (domain != null && !domain.implies(perm)) {
            throw new AccessControlException("Cannot access resources outside sandbox");
        }
    }

    @Override
    public void checkPermission(Permission perm, Object context) {
    }

    private ProtectionDomain getDomain() {
        Class<?>[] context = getClassContext();
        for (int i = 0; i < context.length; i++) {
            Class<?> c = context[i];
            if (c.getClassLoader() instanceof ClassLoader) {
                return c.getProtectionDomain();
            }
        }
        return null;
    }
}
