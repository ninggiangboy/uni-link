package dev.ngb.app;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ModularityTests {

    @Test
    void verifyModularStructure() {
        ApplicationModules.of(AppApplication.class).verify();
    }

    @Test
    void printAllModules() {
        ApplicationModules modules = ApplicationModules.of(AppApplication.class);
        for (var module : modules) {
            System.out.println(module);
        }
    }
}
