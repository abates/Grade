package co.andrewbates.grade.sandbox;

import java.io.FilePermission;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.util.Enumeration;

public class SandboxPermissions extends PermissionCollection {
    private static final long serialVersionUID = 1L;
    private Permissions permissions = new Permissions();

    public SandboxPermissions(Sandbox sandbox) {
        permissions.add(new FilePermission(sandbox.getDir().toAbsolutePath().toString() + "/-", "read"));
    }

    @Override
    public void add(Permission permission) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean implies(Permission permission) {
        return permissions.implies(permission);
    }

    @Override
    public Enumeration<Permission> elements() {
        return permissions.elements();
    }

}
