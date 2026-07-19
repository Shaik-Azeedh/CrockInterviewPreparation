import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class InvoiceGenerator {
    public static class InvoiceItem {
        private String description;
        private int quantity;
        private double unitPrice;

        public InvoiceItem(String description, int quantity, double unitPrice) {
            this.description = description;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }

        public double getTotal() {
            return quantity * unitPrice;
        }

        @Override
        public String toString() {
            return String.format("%-30s %5d %10.2f %12.2f",
                    description, quantity, unitPrice, getTotal());
        }
    }

    public static class Invoice {
        private String invoiceNumber;
        private String customerName;
        private LocalDate date;
        private List<InvoiceItem> items;

        public Invoice(String invoiceNumber, String customerName) {
            this.invoiceNumber = invoiceNumber;
            this.customerName = customerName;
            this.date = LocalDate.now();
            this.items = new ArrayList<>();
        }

        public void addItem(String description, int quantity, double unitPrice) {
            items.add(new InvoiceItem(description, quantity, unitPrice));
        }

        public double getSubtotal() {
            return items.stream().mapToDouble(InvoiceItem::getTotal).sum();
        }

        public double getTax(double taxRate) {
            return getSubtotal() * taxRate;
        }

        public double getTotal(double taxRate) {
            return getSubtotal() + getTax(taxRate);
        }

        public void printInvoice(double taxRate) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
            System.out.println("INVOICE");
            System.out.println("Invoice #: " + invoiceNumber);
            System.out.println("Date      : " + date.format(formatter));
            System.out.println("Customer  : " + customerName);
            System.out.println("------------------------------------------------------------");
            System.out.println(String.format("%-30s %5s %10s %12s", "Description", "Qty", "Unit", "Total"));
            System.out.println("------------------------------------------------------------");
            for (InvoiceItem item : items) {
                System.out.println(item);
            }
            System.out.println("------------------------------------------------------------");
            System.out.printf("%-48s %12.2f%n", "Subtotal:", getSubtotal());
            System.out.printf("%-48s %12.2f%n", String.format("Tax (%.0f%%):", taxRate * 100), getTax(taxRate));
            System.out.printf("%-48s %12.2f%n", "Total:", getTotal(taxRate));
        }
    }

    public static void main(String[] args) {
        Invoice invoice = new Invoice("INV-1001", "Acme Corporation");
        invoice.addItem("Web design services", 1, 1500.00);
        invoice.addItem("Hosting (12 months)", 1, 240.00);
        invoice.addItem("Maintenance", 6, 75.00);

        double taxRate = 0.08; // 8% tax
        invoice.printInvoice(taxRate);
    }
}

