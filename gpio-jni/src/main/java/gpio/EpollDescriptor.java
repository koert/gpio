package gpio;

/**
 * Descriptor for epoll.
 * @author Koert Zeilstra
 */
public class EpollDescriptor {

    static {
        System.loadLibrary("poll4j");
    }

    private int epFd;
    private int fd;

    public EpollDescriptor(final String fileName) {
        epFd = createEpFd();
        System.out.println("epFd: " + epFd);
        fd = addFile(epFd, fileName);
    }

    public void waitForEvent() {
        epollWait(epFd);
    }

    public void close() {
        removeFile(epFd, fd);
        closeEpFd(epFd);
    }

    private native int createEpFd();

    private native int addFile(final int epFd, final String fileName);

    private native void removeFile(final int epFd, final int fd);

    private native void epollWait(final int epFd);

    private native void closeEpFd(final int epFd);

}
