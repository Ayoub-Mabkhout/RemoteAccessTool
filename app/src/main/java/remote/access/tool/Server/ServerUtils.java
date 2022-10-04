package remote.access.tool.Server;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Robot;
import java.awt.image.BufferedImage;

import java.io.*;

public class ServerUtils {

    private static void commandExec(String command) {
        Runtime r = Runtime.getRuntime();
        try {
            r.exec(command);
        } catch (IOException e) {
            System.out.println("Exception: " + e);
        }
    }

    protected static void systemReboot() {
        commandExec("shutdown -r -t " + 5);
    }

    protected static void systemShutDown() {
        commandExec("shutdown -s -t " + 5);
    }

    protected static BufferedImage screenshot() throws AWTException, IOException {
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        BufferedImage capture = new Robot().createScreenCapture(screenRect);
        return capture;
    }

    protected static BufferedReader getProcessList() throws IOException {
        String command;
        String os = System.getProperty("os.name");
        if (os.contains("Windows"))
            command = System.getenv("windir") + "\\system32\\" + "tasklist.exe";
        else
            command = "ps -e";
        Process p = Runtime.getRuntime().exec(command);
        BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
        return input;
    }
}
