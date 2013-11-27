package gpio.epoll;

import java.util.HashMap;
import java.util.Map;

/**
 * Descriptor for epoll.
 * @author Koert Zeilstra
 */
public class EpollDescriptor implements FileMonitor {

    static {
        System.loadLibrary("poll4j");
    }

    private int epFd;
    private Map<String, Integer> files = new HashMap<String, Integer>();

    /**
     * Constructor.
     */
    public EpollDescriptor() {
        epFd = createEpFd();
    }

    /**
     * Constructor.
     * @param fileName File name of file to monitor.
     */
    public EpollDescriptor(String fileName) {
        epFd = createEpFd();
        addFile(fileName);
    }

    /**
     * Add file to monitor.
     * @param fileName File name.
     */
    @Override
    public void addFile(String fileName) {
        int fd = addFile(epFd, fileName);
        files.put(fileName, fd);
    }

    /**
     * Wait for event on files.
     */
    @Override
    public void waitForEvent() {
        epollWait(epFd);
    }

    /**
     * Close descriptor.
     */
    @Override
    public void close() {
        for(Map.Entry<String, Integer> entry : files.entrySet()) {
            removeFile(epFd, entry.getValue());
        }
        closeEpFd(epFd);
    }

    private native int createEpFd();

    private native int addFile(final int epFd, final String fileName);

    private native void removeFile(final int epFd, final int fd);

    private native void epollWait(final int epFd);

    private native void closeEpFd(final int epFd);

}
