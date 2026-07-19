import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class Test1 {
    public static void main(String[] args) {
        Invoice invoice = new Invoice("ACME Corp.", "123 Business Rd, Suite 100");
        invoice.addItem(new InvoiceItem("Widget", 4, 19.99));
        invoice.addItem(new InvoiceItem("Gadget", 2, 49.50));
        invoice.addItem(new InvoiceItem("Support (hour)", 3, 75.00));
        invoice.setTaxRate(0.07); // 7% sales tax

        System.out.println(invoice.formatInvoice());

        Scanner sc = new Scanner(System.in);
        System.out.print("Save invoice to file? (y/n): ");
        String ans = sc.nextLine().trim();
        if (ans.equalsIgnoreCase("y")) {
            System.out.print("Enter filename (invoice.txt): ");
            String fname = sc.nextLine().trim();
            if (fname.isEmpty()) fname = "invoice.txt";
            try {
                invoice.saveToFile(fname);
                System.out.println("Saved invoice to " + fname);
            } catch (IOException e) {
                System.err.println("Failed to save file: " + e.getMessage());
            }
        }
        sc.close();
    }
}

class InvoiceItem {
    private final String description;
    private final int quantity;
    private final double unitPrice;

    public InvoiceItem(String description, int quantity, double unitPrice) {
        this.description = description;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public String getDescription() { return description; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }
    public double getTotal() { return quantity * unitPrice; }
}

class Invoice {
    private final String customerName;
    private final String customerAddress;
    private final List<InvoiceItem> items = new ArrayList<>();
    private double taxRate = 0.0;

    public Invoice(String customerName, String customerAddress) {
        this.customerName = customerName;
        this.customerAddress = customerAddress;
    }

    public void addItem(InvoiceItem item) { items.add(item); }
    public void setTaxRate(double taxRate) { this.taxRate = taxRate; }

    public double getSubtotal() {
        return items.stream().mapToDouble(InvoiceItem::getTotal).sum();
    }

    public double getTax() { return getSubtotal() * taxRate; }
    public double getTotal() { return getSubtotal() + getTax(); }

    public String formatInvoice() {
        NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.getDefault());
        StringBuilder sb = new StringBuilder();
        sb.append("========== INVOICE ==========").append(System.lineSeparator());
        sb.append("Billed To: ").append(customerName).append(System.lineSeparator());
        sb.append(customerAddress).append(System.lineSeparator()).append(System.lineSeparator());

        sb.append(String.format("%-30s %6s %12s %12s", "Description", "Qty", "Unit", "Total")).append(System.lineSeparator());
        sb.append("---------------------------------------------------------------").append(System.lineSeparator());
        for (InvoiceItem it : items) {
            sb.append(String.format("%-30s %6d %12s %12s",
                    it.getDescription(),
                    it.getQuantity(),
                    nf.format(it.getUnitPrice()),
                    nf.format(it.getTotal())))
              .append(System.lineSeparator());
        }
        sb.append("\n");
        sb.append(String.format("%-50s %12s", "Subtotal:", nf.format(getSubtotal()))).append(System.lineSeparator());
        sb.append(String.format("%-50s %12s", String.format("Tax (%.2f%%):", taxRate * 100), nf.format(getTax()))).append(System.lineSeparator());
        sb.append(String.format("%-50s %12s", "Total:", nf.format(getTotal()))).append(System.lineSeparator());
        sb.append("===============================").append(System.lineSeparator());
        return sb.toString();
    }

    public void saveToFile(String filename) throws IOException {
        String content = formatInvoice();
        Path p = Path.of(filename);
        Files.writeString(p, content, StandardCharsets.UTF_8);
    }
}
