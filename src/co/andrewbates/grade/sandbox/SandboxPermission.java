package co.andrewbates.grade.sandbox;

import java.security.Permission;

public class SandboxPermission extends Permission {
    private static final long serialVersionUID = 1L;
    public static final String NAME = "SandboxPermission";

    public SandboxPermission() {
        super(NAME);
    }

    @Override
    public boolean implies(Permission permission) {
        return this.equals(permission);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof SandboxPermission) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return NAME.hashCode();
    }

    @Override
    public String getActions() {
        return "";
    }

}
