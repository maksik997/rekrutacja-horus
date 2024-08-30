import java.util.List;
import java.util.Optional;

/**
 * Interface representing a cabinet in which you can search by name, size.
 * Or count all the folders inside.
 * */
interface Cabinet {

    // zwraca dowolny element o podanej nazwie
    /**
     * Finds a {@link Folder} with specified name.
     * @param name the name of the folder to find
     * @return an {@link Optional} containing the found folder if it exists, or {@link Optional#empty()} if folder is not found.
     * */
    Optional<Folder> findFolderByName(String name);

    // zwraca wszystkie foldery podanego rozmiaru SMALL/MEDIUM/LARGE
    /**
     * Finds all {@link Folder}s with specified size.
     * @param size the size of the folder to find ("SMALL", "MEDIUM", "LARGE")
     * @return a {@link List} of folders that match specified size.
     * */
    List<Folder> findFoldersBySize(String size);

    //zwraca liczbę wszystkich obiektów tworzących strukturę
    /**
     * Counts all {@link Folder}s in the cabinet.
     * @return the total count of folders.
     * */
    int count();
}
