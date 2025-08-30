import java.util.Scanner;

/* ---------- Enum ---------- */
enum BookCategory {
    FICTION, SCIENCE, HISTORY, TECHNOLOGY, COMICS
}


class Book {
    private final int id;          // id should never change ⇒ final
    private String title;
    private String author;
    private BookCategory category;

    public Book(int id, String title, String author, BookCategory category) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.category = category;
    }

    /* Getters */
    public int getId()              { return id; }
    public String getTitle()        { return title; }
    public String getAuthor()       { return author; }
    public BookCategory getCategory(){ return category; }

    /* Setters */
    public void setTitle(String title)               { this.title = title; }
    public void setAuthor(String author)             { this.author = author; }
    public void setCategory(BookCategory category)   { this.category = category; }

    @Override
    public String toString() {
        return "[" + id + "] " + title + " by " + author + " (" + category + ")";
    }
}

/* ---------- Custom Exceptions ---------- */
class BookStorageFullException extends Exception {
    public BookStorageFullException(String msg) { super(msg); }
}

class BookNotFoundException extends Exception {
    public BookNotFoundException(String msg) { super(msg); }
}

/* ---------- Main  ---------- */
public class BookManagementSystem {

    private static final int MAX_BOOKS = 5;
    private static final Book[] books = new Book[MAX_BOOKS];
    private static int count   = 0;   // current # of books
    private static int nextId  = 1;   // auto-increment id

    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {      // auto-close Scanner
            System.out.println("=== Book Management System ===");
            boolean running = true;
            while (running) {
                printMenu();
                System.out.print("Enter choice: ");
                if (!sc.hasNextInt()) {               // guards against non-int input
                    System.out.println("Error: Enter a number!");
                    sc.nextLine();
                    continue;
                }
                int choice = sc.nextInt();
                sc.nextLine(); // clear newline
                switch (choice) {
                    case 1 -> addBook(sc);
                    case 2 -> viewBooks();
                    case 3 -> searchBooksByCategory(sc);
                    case 4 -> searchBookById(sc);
                    case 5 -> deleteBook(sc);
                    case 6 -> updateBook(sc);
                    case 7 -> {
                        running = false;
                        System.out.println("Exiting...");
                    }
                    default -> System.out.println("Invalid choice!");
                }
            }
        }
    }

    /* ---------- Menu ---------- */
    private static void printMenu() {
        System.out.println("""
                
                1. Add Book
                2. View All Books
                3. Search Books by Category
                4. Search Book by ID
                5. Delete Book by ID
                6. Update Book by ID
                7. Exit""");
    }

    

    private static void addBook(Scanner sc) {
        try {
            if (count >= MAX_BOOKS)
                throw new BookStorageFullException("Storage is full.");

            System.out.print("Enter Book Title : ");
            String title = sc.nextLine().trim();
            System.out.print("Enter Author Name: ");
            String author = sc.nextLine().trim();

            BookCategory category = readCategory(sc, "Enter Category: ");
            books[count++] = new Book(nextId++, title, author, category);
            System.out.println("Book added successfully!");

        } catch (BookStorageFullException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void viewBooks() {
        if (count == 0) {
            System.out.println("No books available.");
            return;
        }
        System.out.println("\n--- All Books ---");
        for (int i = 0; i < count; i++) System.out.println(books[i]);
    }

    private static void searchBooksByCategory(Scanner sc) {
        BookCategory category = readCategory(sc, "Enter Category to search: ");
        boolean found = false;
        for (int i = 0; i < count; i++)
            if (books[i].getCategory() == category) {
                if (!found) System.out.println("\nBooks in " + category + ":");
                System.out.println(books[i]);
                found = true;
            }
        if (!found) System.out.println("None found.");
    }

    private static void searchBookById(Scanner sc) {
        if (count == 0) { System.out.println("No books available."); return; }

        int id = readInt(sc, "Enter Book ID to search: ");
        try {
            Book book = findBookById(id);
            System.out.println("Found: " + book);
        } catch (BookNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void deleteBook(Scanner sc) {
        if (count == 0) { System.out.println("No books to delete."); return; }

        int id = readInt(sc, "Enter Book ID to delete: ");
        try {
            int idx = indexOf(id);
            // shift left
            for (int i = idx; i < count - 1; i++) books[i] = books[i + 1];
            books[--count] = null;
            System.out.println("Book deleted.");
        } catch (BookNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void updateBook(Scanner sc) {
        if (count == 0) { System.out.println("No books to update."); return; }

        int id = readInt(sc, "Enter Book ID to update: ");
        try {
            Book book = findBookById(id);

            System.out.print("New Title (blank = keep): ");
            String title = sc.nextLine();
            if (!title.isBlank()) book.setTitle(title.trim());

            System.out.print("New Author (blank = keep): ");
            String author = sc.nextLine();
            if (!author.isBlank()) book.setAuthor(author.trim());

            System.out.print("Change Category? (y/N): ");
            String ans = sc.nextLine().trim();
            if (ans.equalsIgnoreCase("y"))
                book.setCategory(readCategory(sc, "Enter new Category: "));

            System.out.println("Updated: " + book);

        } catch (BookNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    /* ---------- Helper Methods ---------- */

    private static Book findBookById(int id) throws BookNotFoundException {
        for (int i = 0; i < count; i++)
            if (books[i].getId() == id) return books[i];
        throw new BookNotFoundException("Book ID " + id + " not found.");
    }

    private static int indexOf(int id) throws BookNotFoundException {
        for (int i = 0; i < count; i++)
            if (books[i].getId() == id) return i;
        throw new BookNotFoundException("Book ID " + id + " not found.");
    }

    private static int readInt(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            if (sc.hasNextInt()) {
                int val = sc.nextInt();
                sc.nextLine();
                return val;
            }
            System.out.println("Invalid number!");
            sc.nextLine();
        }
    }

    private static BookCategory readCategory(Scanner sc, String prompt) {
        while (true) {
            System.out.println("Available: " + java.util.Arrays.toString(BookCategory.values()));
            System.out.print(prompt);
            String in = sc.nextLine().trim().toUpperCase();
            try {
                return BookCategory.valueOf(in);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid category, try again.");
            }
        }
    }
}
