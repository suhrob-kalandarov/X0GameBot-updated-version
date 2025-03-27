package org.botcontrol.botservice.dbservice;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class ConfigManager {
    public static EntityManagerFactory EMF;

    public static void worker() {
        //EMF = Persistence.createEntityManagerFactory("myPU");
    }
}
