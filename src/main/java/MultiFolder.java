import java.util.List;

/**
 * Interface representing a folder that can contain other {@link Folder}s, enabling nested folder structures.
 * Extends the {@link Folder} interface.
 */
public interface MultiFolder extends Folder {

    /**
     * Returns a list of folders contained within this multi-folder.
     *
     * @return a list of {@link Folder}s contained within this multi-folder
     */
    List<Folder> getFolders();
}