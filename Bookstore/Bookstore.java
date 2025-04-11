import java.util.Scanner;

// --- Book Class ---
class Book {
    String name;
    String author;
    double price;
    int quantity;

    Book(String name, String author, double price, int quantity) {
        this.name = name;
        this.author = author;
        this.price = price;
        this.quantity = quantity;
    }

    public String toString() {
        return "Name: " + name + ", Author: " + author + ", Price: $" + price + ", Quantity: " + quantity;
    }
}

// --- Custom ArrayList Implementation ---
class ArrayList {
    Book[] data;
    int size;

    ArrayList() {
        data = new Book[10];
        size = 0;
    }

    void add(Book book) {
        if (size == data.length) resize();
        data[size++] = book;
    }

    void remove(int index) {
        if (index < 0 || index >= size) return;
        for (int i = index; i < size - 1; i++) {
            data[i] = data[i + 1];
        }
        size--;
    }

    Book get(int index) {
        return (index >= 0 && index < size) ? data[index] : null;
    }

    int getSize() {
        return size;
    }

    void resize() {
        Book[] newData = new Book[data.length * 2];
        for (int i = 0; i < data.length; i++) {
            newData[i] = data[i];
        }
        data = newData;
    }

    void quickSort(int low, int high) {
        if (low < high) {
            int pi = partition(low, high);
            quickSort(low, pi - 1);
            quickSort(pi + 1, high);
        }
    }

    int partition(int low, int high) {
        Book pivot = data[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (data[j].name.compareToIgnoreCase(pivot.name) < 0) {
                i++;
                Book temp = data[i];
                data[i] = data[j];
                data[j] = temp;
            }
        }
        Book temp = data[i + 1];
        data[i + 1] = data[high];
        data[high] = temp;
        return i + 1;
    }

    int searchByName(String name) {
        for (int i = 0; i < size; i++) {
            if (data[i].name.equalsIgnoreCase(name)) return i;
        }
        return -1;
    }

    int searchByAuthor(String author) {
        for (int i = 0; i < size; i++) {
            if (data[i].author.equalsIgnoreCase(author)) return i;
        }
        return -1;
    }

    void displayByFirstLetter(char ch) {
        boolean found = false;
        for (int i = 0; i < size; i++) {
            if (Character.toLowerCase(data[i].name.charAt(0)) == Character.toLowerCase(ch) ||
                    Character.toLowerCase(data[i].author.charAt(0)) == Character.toLowerCase(ch)) {
                System.out.println(data[i]);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No books found with the given starting letter.");
        }
    }
}

// --- Order Class ---
class Order {
    static int counter = 1000;
    Book[] books;
    int bookCount;
    int orderId;
    String buyerName;
    String address;

    Order(Book[] books, int count, String buyerName, String address) {
        this.books = new Book[count];
        for (int i = 0; i < count; i++) this.books[i] = books[i];
        this.bookCount = count;
        this.orderId = counter++;
        this.buyerName = buyerName;
        this.address = address;
    }

    double getTotalPrice() {
        double total = 0;
        for (int i = 0; i < bookCount; i++) {
            total += books[i].price;
        }
        return total;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("Order ID: " + orderId + "\nBuyer: " + buyerName + "\nAddress: " + address + "\n");
        for (int i = 0; i < bookCount; i++) {
            sb.append(books[i].toString()).append("\n");
        }
        return sb.toString();
    }
}

// --- Queue for Order Processing ---
class OrderQueue {
    Order[] queue;
    int front, rear, size;

    OrderQueue(int capacity) {
        queue = new Order[capacity];
        front = 0;
        rear = 0;
        size = 0;
    }

    void enqueue(Order order) {
        if (size == queue.length) return;
        queue[rear++] = order;
        size++;
    }

    Order dequeue() {
        if (size == 0) return null;
        Order order = queue[front++];
        size--;
        return order;
    }

    Order searchOrder(int orderId) {
        for (int i = front; i < rear; i++) {
            if (queue[i] != null && queue[i].orderId == orderId) return queue[i];
        }
        return null;
    }

    boolean cancelOrder(int orderId, ArrayList bookList) {
        for (int i = front; i < rear; i++) {
            if (queue[i] != null && queue[i].orderId == orderId) {
                for (int j = 0; j < queue[i].bookCount; j++) {
                    Book orderedBook = queue[i].books[j];
                    int index = bookList.searchByName(orderedBook.name);
                    if (index >= 0) {
                        bookList.get(index).quantity++;
                    }
                }
                queue[i] = null;
                return true;
            }
        }
        return false;
    }
}

// --- Main Bookstore Application ---
public class Bookstore {
    static ArrayList books = new ArrayList();
    static OrderQueue orders = new OrderQueue(100);
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        int choice;
        do {
            System.out.println("\n--- Bookstore Menu ---");
            System.out.println("Enter your option:");
            System.out.println("1. Display Books\n2. Create New Book\n3. Delete Book\n4. Order Book\n5. Process Order\n6. Search Order\n7. Cancel Order\n0. Exit");
            while (!scanner.hasNextInt()) {
                System.out.print("Please enter a number: ");
                scanner.next();
            }
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> displayBooks();
                case 2 -> createBook();
                case 3 -> deleteBook();
                case 4 -> orderBooks();
                case 5 -> processOrder();
                case 6 -> searchOrder();
                case 7 -> cancelOrder();
            }
        } while (choice != 0);
    }

    static void displayBooks() {
        books.quickSort(0, books.getSize() - 1);
        for (int i = 0; i < books.getSize(); i++) System.out.println(books.get(i));
    }

    static void createBook() {
        System.out.print("Enter book name: ");
        String name = scanner.nextLine();

        int existingIndex = books.searchByName(name);
        if (existingIndex >= 0) {
            System.out.println("A book with this name already exists. Please use a different name.");
            return;
        }

        System.out.print("Enter author name: ");
        String author = scanner.nextLine();

        double price;
        while (true) {
            System.out.print("Enter price: ");
            if (scanner.hasNextDouble()) {
                price = scanner.nextDouble();
                break;
            } else {
                System.out.println("Invalid input. Enter a number.");
                scanner.next();
            }
        }

        int quantity;
        while (true) {
            System.out.print("Enter quantity (> 0): ");
            if (scanner.hasNextInt()) {
                quantity = scanner.nextInt();
                if (quantity > 0) break;
            } else {
                scanner.next();
            }
            System.out.println("Invalid input. Quantity must be a number > 0.");
        }
        scanner.nextLine();

        books.add(new Book(name, author, price, quantity));
        System.out.println("Book added.");
    }

    static void deleteBook() {
        System.out.print("Enter book name to delete: ");
        String name = scanner.nextLine();
        int index = books.searchByName(name);
        if (index >= 0) {
            books.remove(index);
            System.out.println("Book deleted.");
        } else {
            System.out.println("Book not found.");
        }
    }

    static void orderBooks() {
        Book[] cart = new Book[10];
        int count = 0;

        System.out.print("Enter buyer name: ");
        String buyerName = scanner.nextLine();
        System.out.print("Enter address: ");
        String address = scanner.nextLine();

        while (true) {
            System.out.println("\nOrder Menu:\n1. Search by name or author\n0. Done ordering");
            System.out.print("Enter your option: ");
            while (!scanner.hasNextInt()) {
                System.out.print("Please enter a number: ");
                scanner.next();
            }
            int option = scanner.nextInt();
            scanner.nextLine();
            if (option == 0) break;

            System.out.print("Enter the first letter of book name or author: ");
            char ch = scanner.nextLine().charAt(0);
            books.displayByFirstLetter(ch);
            System.out.print("Enter the exact book name to order: ");
            String exactName = scanner.nextLine();

            int idx = books.searchByName(exactName);
            Book found = (idx >= 0) ? books.get(idx) : null;

            if (found != null && found.quantity > 0) {
                cart[count++] = new Book(found.name, found.author, found.price, 1);
                found.quantity--;
                System.out.println("Book added to cart.");
            } else {
                System.out.println("Book not available or out of stock.");
            }
        }
        if (count > 0) {
            Order order = new Order(cart, count, buyerName, address);
            orders.enqueue(order);
            System.out.println("Order placed with ID: " + order.orderId);
            System.out.printf("Total Price: $%.2f\n", order.getTotalPrice());
        }
    }

    static void processOrder() {
        Order order = orders.dequeue();
        if (order != null) {
            System.out.println("Processing order:");
            System.out.println(order);
            System.out.printf("Total Price: $%.2f\n", order.getTotalPrice());
        } else {
            System.out.println("No orders to process.");
        }
    }

    static void searchOrder() {
        System.out.print("Enter Order ID: ");
        while (!scanner.hasNextInt()) {
            System.out.print("Please enter a valid number: ");
            scanner.next();
        }
        int id = scanner.nextInt();
        scanner.nextLine();
        Order order = orders.searchOrder(id);
        if (order != null) System.out.println(order);
        else System.out.println("Order not found.");
    }

    static void cancelOrder() {
        System.out.print("Enter Order ID to cancel: ");
        while (!scanner.hasNextInt()) {
            System.out.print("Please enter a valid number: ");
            scanner.next();
        }
        int id = scanner.nextInt();
        scanner.nextLine();
        boolean success = orders.cancelOrder(id, books);
        if (success) System.out.println("Order cancelled and stock restored.");
        else System.out.println("Order not found or already processed.");
    }
}
