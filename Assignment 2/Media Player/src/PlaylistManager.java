import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PlaylistManager {

    private final List<File> playlist;

    private int currentIndex;

    public PlaylistManager() {

        playlist = new ArrayList<>();

        currentIndex = -1;
    }

    public void addMedia(File file) {

        if (file == null) {
            return;
        }

        playlist.add(file);

        if (currentIndex == -1) {
            currentIndex = 0;
        }
    }

    public void removeMedia(int index) {

        if (index < 0 ||
                index >= playlist.size()) {

            return;
        }

        playlist.remove(index);

        if (playlist.isEmpty()) {

            currentIndex = -1;

        } else if (currentIndex >= playlist.size()) {

            currentIndex =
                    playlist.size() - 1;
        }
    }

    public File getCurrentMedia() {

        if (currentIndex < 0 ||
                currentIndex >= playlist.size()) {

            return null;
        }

        return playlist.get(currentIndex);
    }

    public File getMedia(int index) {

        if (index < 0 ||
                index >= playlist.size()) {

            return null;
        }

        return playlist.get(index);
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int index) {

        if (index >= 0 &&
                index < playlist.size()) {

            currentIndex = index;
        }
    }

    public int size() {
        return playlist.size();
    }

    public boolean isEmpty() {
        return playlist.isEmpty();
    }

    public List<File> getPlaylist() {
        return playlist;
    }

    public File getNext() {

        if (playlist.isEmpty()) {
            return null;
        }

        int nextIndex =
                currentIndex + 1;

        if (nextIndex >= playlist.size()) {
            nextIndex = 0;
        }

        currentIndex = nextIndex;

        return playlist.get(currentIndex);
    }

    public File getPrevious() {

        if (playlist.isEmpty()) {
            return null;
        }

        int previousIndex =
                currentIndex - 1;

        if (previousIndex < 0) {
            previousIndex =
                    playlist.size() - 1;
        }

        currentIndex = previousIndex;

        return playlist.get(currentIndex);
    }
}