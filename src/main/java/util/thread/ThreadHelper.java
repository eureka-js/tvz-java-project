package util.thread;

import javafx.application.Platform;

import java.util.concurrent.Semaphore;

public interface ThreadHelper {
    static void platformRunLaterCatchUp() throws InterruptedException {
        Semaphore semaphore = new Semaphore(0);
        Platform.runLater(semaphore::release);
        semaphore.acquire();
    }
}
