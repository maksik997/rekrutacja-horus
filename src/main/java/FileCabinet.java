import java.util.*;
import java.util.stream.Stream;

/**
 * Shouldn't it be "FolderCabinet"???
 * Otherwise, this class ought to be abstract if the task states:
 * <pre>{@code
 *      W odpowiedzi na zainteresowanie naszą ofertą pracy chcielibyśmy zaprosić Panią do pierwszego etapu rekrutacji na stanowisko Junior Java Developer w firmie Horus.
 *      Poniżej przekazujemy zadanie z prośbą o analizę poniższego kodu i samodzielne zaimplementowanie metod findFolderByName, findFolderBySize, count w klasie FolderCabinet
 *      - najchętniej unikając powielania kodu i umieszczając całą logikę w klasie FolderCabinet. Z uwzględnieniem w analizie i implementacji interfejsu MultiFolder!
 * }</pre>
 * or the program will fail.
 * <h1>Analysis:</h1>
 * Class representing a folder cabinet. Implements the {@link Cabinet} interface,
 * providing the following methods:
 * <ul>
 *     <li>{@link FileCabinet#findFolderByName(String)}</li>
 *     <li>{@link FileCabinet#findFoldersBySize(String)}</li>
 *     <li>{@link FileCabinet#count()}</li>
 * </ul>
 * This class maintains a list of {@link Folder}s and handles {@link MultiFolder}s.
 * It supports operations to find folders by name, filter folders by size, and count all folders
 * within the cabinet, including those contained within nested {@link MultiFolder}s.
 * <h2>Usage example:</h2>
 * <pre>{@code
 *      List<Folder> folders = List.of(...); // folders implementations, nested multi-folders, etc.
 *      Cabinet cabinet = new FileCabinet(folders);
 *      System.out.printf("Searched folder: %s%n", cabinet.findFolderByName("abc")); // Output: "Searched folder: abc" if cabinet contains this folder, or "Searched folder: Optional.empty" otherwise
 *      System.out.printf("Large folders: %s%n", cabinet.findFoldersBySize("LARGE")); // Output: "Large folders: [list-of-large-folders]"
 *      System.out.printf("Folders count: %d%n", cabinet.count()); // Output: Count of folders, including nested.
 * }</pre>
 * <h2>Important:</h2>
 * This class doesn't support circular dependencies.
 * <h3>For example:</h3>
 * <pre>{@code
 *      List<Folder> folders2 = new ArrayList<>();
 *      folders.add(new FolderSample("name1", "SMALL"));
 *      folders.add(new MultiFolderSample("name2", "LARGE", folders));
 *
 *      Cabinet c = new FileCabinet(folders);
 *      c.count();
 * }</pre>
 * Will end up throwing {@link StackOverflowError}.
 * The reason for avoiding circular dependencies is straightforward: they are inherently illogical.
 * For instance, consider a multi-folder that contains itself. If you were to search for a specific item within this multi-folder,
 * you would end up repeatedly navigating the same structure. This creates an infinite loop, rendering the search process both
 * ineffective and impractical. While such circular dependencies can be technically created, they lack logical sense and lead
 * to inefficiencies, at least in this specific context.
 **/
public class FileCabinet implements Cabinet {

    /**
     * The list of folders can include {@link MultiFolder} instances, which means that folders can be nested.
     **/
    private List<Folder> folders;

    /**
     * Constructs an instance of {@link FileCabinet} with the specified list of {@link Folder}s.
     *
     * @param folders the list of folders, which may include {@link MultiFolder} instances, allowing for nested folder structures.
     **/
    public FileCabinet(List<Folder> folders) {
        this.folders = folders;
    }

    /**
     * Finds a {@link Folder} with the specified name.
     *
     * @param name the name of the folder to find
     * @return an {@link Optional} containing the first found folder if it exists, or {@link Optional#empty()} if not found
     **/
    @Override
    public Optional<Folder> findFolderByName(String name) {
        return getAllFolders()
                .filter(f -> f.getName().equals(name))
                .findFirst();
    }

    /**
     * Finds all {@link Folder}s with the specified size.
     *
     * @param size the size of the folders to find (e.g., "SMALL", "MEDIUM", "LARGE")
     * @return a {@link List} of folders that match the specified size
     **/
    @Override
    public List<Folder> findFoldersBySize(String size) {
        return getAllFolders()
                .filter(f -> f.getSize().equals(size))
                .toList();
    }

    /**
     * Counts all {@link Folder}s in the cabinet, including those contained within nested {@link MultiFolder}s.
     *
     * @return the total count of folders
     **/
    @Override
    public int count() {
        return (int) getAllFolders().count();
    }

    /**
     * Recursively retrieves all {@link Folder}s from the cabinet, including those in nested {@link MultiFolder}s.
     *
     * @return a {@link Stream} of all folders
     **/
    private Stream<Folder> getAllFolders() {
        return folders.stream().flatMap(this::getFolders);
    }

    /**
     * Recursively retrieves all {@link Folder}s from the given folder, including those in nested {@link MultiFolder}s.
     * The retrieval process avoids circular references by ensuring that each folder is processed only once.
     *
     * @param folder the current folder to process
     * @return a {@link Stream} of all folders found within the given folder and its nested {@link MultiFolder}s
     **/
    private Stream<Folder> getFolders(Folder folder) {
        Stream<Folder> sf = Stream.of(folder);

        if (folder instanceof MultiFolder mf) {
            return Stream.concat(
                sf,
                mf.getFolders().stream()
                    .flatMap(this::getFolders)
            );
        }

        return sf;
    }

    /**
     * Test place for demonstrating the functionality of the {@link FileCabinet} class.
     * This method sets up a sample folder structure and performs various operations
     * using the methods provided by the {@link Cabinet} interface.
     * */
    public static void main(String[] args) {
        List<Folder> folders = List.of(
            new FolderSample("abc", "SMALL"), new FolderSample("bca", "MEDIUM"),
            new FolderSample("cab", "LARGE"), new MultiFolderSample("abc", "LARGE", List.of(new FolderSample("bca", "MEDIUM")))
        );

        Cabinet cabinet = new FileCabinet(folders);

        System.out.printf("Searched folder: %s%n", cabinet.findFolderByName("abc")); // Output: "Searched folder: Optional[Folder={name=abc; size=SMALL}]"
        System.out.printf("Large folders: %s%n", cabinet.findFoldersBySize("LARGE")); // Output: "Large folders: [Folder={name=cab; size=LARGE}, MultiFolder={name=abc; size=LARGE; folders=[Folder={name=bca; size=MEDIUM}]}]"
        System.out.printf("Folders count: %d%n", cabinet.count()); // Output: "Folders count: 5"
    }
}

/**
 * Sample {@link Folder} implementation.
 * Represents some folder with specified name and size.
 * Implements {@link Folder} interface.
 * */
class FolderSample implements Folder {

    /**
     * Just fields. Two final strings.
     * */
    protected final String name, size;

    /**
     * Plain constructor.
     * @param name Name of the folder.
     * @param size Size of the folder.
     * */
    public FolderSample(String name, String size) {
        this.name = name;
        this.size = size;
    }

    /**
     * Returns the name of this folder.
     * This method implements the {@link Folder#getName()} method from the {@link Folder} interface.
     *
     * @return the name of this folder
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Returns the size of this folder.
     * This method implements the {@link Folder#getSize()} method from the {@link Folder} interface.
     *
     * @return the size of this folder
     */
    @Override
    public String getSize() {
        return size;
    }

    /**
     * toString... Everyone knows what toString() does.
     * */
    @Override
    public String toString() {
        return String.format("Folder={name=%s; size=%s}", name, size);
    }
}

/**
 * Implementation of the {@link MultiFolder} interface, extending {@link FolderSample}.
 * Represents a folder that can contain a list of {@link Folder}s, allowing for nested folder structures.
 */
class MultiFolderSample extends FolderSample implements MultiFolder {

    /**
     * The list of folders contained within this multi-folder.
     */
    private final List<Folder> folders;

    /**
     * Constructs a {@link MultiFolderSample} with the specified name, size, and list of folders.
     *
     * @param name the name of the multi-folder
     * @param size the size of the multi-folder
     * @param folders the list of folders contained within this multi-folder
     */
    public MultiFolderSample(String name, String size, List<Folder> folders) {
        super(name, size);
        this.folders = folders;
    }

    /**
     * Returns the list of folders contained within this multi-folder.
     * This method implements the {@link MultiFolder#getFolders()} method from the {@link MultiFolder} interface.
     *
     * @return the list of folders contained within this multi-folder
     */
    @Override
    public List<Folder> getFolders() {
        return folders;
    }

    /**
     * toString... Everyone knows what toString() does.
     * */
    @Override
    public String toString() {
        return String.format("MultiFolder={name=%s; size=%s; folders=%s}", name, size, folders);
    }
}