import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class Invoice {
    private long time;
    private String title;
    private String seller;
    private String buyer;
    private ArrayList<Car> list;

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    private int year;
    private int min;
    private int max;

    public Invoice(long time, String title, String seller, String buyer, ArrayList<Car> list, int min, int max) {
        this.time = time;
        this.title = title;
        this.seller = seller;
        this.buyer = buyer;
        this.list = list;
        this.min = min;
        this.max = max;
    }

    public Invoice(long time, String title, String seller, String buyer, ArrayList<Car> list) {
        this.time = time;
        this.title = title;
        this.seller = seller;
        this.buyer = buyer;
        this.list = list;
    }

    public Invoice(long time, String title, String seller, String buyer, ArrayList<Car> list, int year) {
        this.time = time;
        this.title = title;
        this.seller = seller;
        this.buyer = buyer;
        this.list = list;
        this.year = year;
    }

    public static void generateInvoice(Car carInCars){
        Document document = new Document(); // dokument pdf
        UUID carUUID = carInCars.getUuid();
        String path = "invoices/"+carUUID.toString()+".pdf"; // lokalizacja zapisu
        try {
            PdfWriter.getInstance(document, new FileOutputStream(path));
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        document.open();
        try {
            Font bigFont = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
            System.out.println(carInCars.getColor());
            Color color = Helpers.hexToColor(carInCars.getColor());
            System.out.println(color);
            BaseColor coloredFontColor = new BaseColor(color.getRed(), color.getGreen(), color.getBlue());
            Font coloredFont = FontFactory.getFont(FontFactory.COURIER, 16, coloredFontColor);
            Paragraph chunk = new Paragraph("FAKTURA dla: " + carUUID, bigFont); // akapit
            document.add(chunk);
            chunk = new Paragraph("Model: " + carInCars.getModel(), bigFont); // akapit
            document.add(chunk);
            chunk = new Paragraph("Kolor: " + carInCars.getColor(), coloredFont); // akapit
            document.add(chunk);
            chunk = new Paragraph("rok: " + carInCars.getYear(), bigFont); // akapit
            document.add(chunk);
            ArrayList<String> models = new ArrayList<>();
            models.add("Fiat");
            models.add("BMW");
            models.add("Audi");
            for (Airbag airbag :
                    carInCars.getAirbags()) {
                Paragraph paragraph = new Paragraph("poduszka: "+airbag.getDescription()+" - "+airbag.isValue());
                document.add(paragraph);
            }
            if(models.contains(carInCars.getModel())){
                Image img = Image.getInstance("presetcarphotos/"+carInCars.getModel()+".jpg");
                document.add(img);
            }

        } catch (DocumentException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        document.close();
    }
    public void generateAllCarsInvoice(ArrayList<Car> cars){
        Document document = new Document();
        Date current = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd/HH:mm:ss");
        String path = "invoices/invoice_all_cars_"+time+".pdf"; // lokalizacja zapisu
        System.out.println(path);
        try {
            PdfWriter.getInstance(document, new FileOutputStream(path));
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        document.open();
        Font bigFont = FontFactory.getFont(FontFactory.COURIER_BOLD, 16, BaseColor.BLACK);
        Font font = FontFactory.getFont(FontFactory.COURIER, 14, BaseColor.BLACK);
        Font redFont = FontFactory.getFont(FontFactory.COURIER_BOLD, 14, BaseColor.RED);
        try {
            Paragraph chunk = new Paragraph("FAKTURA: VAT/"+format.format(current), bigFont);
            document.add(chunk);
            chunk = new Paragraph("Nabywca: firma sprzedajaca auta", font);
            document.add(chunk);
            chunk = new Paragraph("Sprzedawca: firma sprzedajaca auta", font);
            document.add(chunk);
            chunk = new Paragraph("Faktura za wszystkie auta\n", redFont);
            document.add(chunk);
            chunk = new Paragraph("", redFont);
            document.add(chunk);
            PdfPTable table = new PdfPTable(4);
            int counter = 1;
            int sum = 0;
            PdfPCell counterHeaderCell = new PdfPCell(new Phrase("lp", font));
            table.addCell(counterHeaderCell);
            PdfPCell priceHeaderCell = new PdfPCell(new Phrase("cena", font));
            table.addCell(priceHeaderCell);
            PdfPCell vatHeaderCell = new PdfPCell(new Phrase("vat", font));
            table.addCell(vatHeaderCell);
            PdfPCell valueHeaderCell = new PdfPCell(new Phrase("wartosc", font));
            table.addCell(valueHeaderCell);
            for (Car car :
                    cars) {
                PdfPCell counterCell = new PdfPCell(new Phrase(String.valueOf(counter), font));
                table.addCell(counterCell);
                PdfPCell priceCell = new PdfPCell(new Phrase(String.valueOf(car.getPrice()), font));
                table.addCell(priceCell);
                PdfPCell vatCell = new PdfPCell(new Phrase(String.valueOf(car.getVat()), font));
                table.addCell(vatCell);
                PdfPCell valueCell = new PdfPCell(new Phrase(String.valueOf(car.getPrice()+car.getPrice()*car.getVat()/100), font));
                table.addCell(valueCell);
                sum += car.getPrice()+car.getPrice()*car.getVat()/100;
                counter++;
            }
            document.add(table);

            chunk = new Paragraph("DO ZAPŁATY "+sum+" PLN", bigFont);
            document.add(chunk);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }




        document.close();
    }
    public static void generateByPriceInvoice(ArrayList<Car> cars){

    }

    public void generateCarsByYearInvoice(ArrayList<Car> cars) {
        Document document = new Document();
        Date current = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd/HH:mm:ss");
        String path = "invoices/invoice_all_cars_by_year_"+time+".pdf"; // lokalizacja zapisu
        System.out.println(path);
        try {
            PdfWriter.getInstance(document, new FileOutputStream(path));
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        document.open();
        Font bigFont = FontFactory.getFont(FontFactory.COURIER_BOLD, 16, BaseColor.BLACK);
        Font font = FontFactory.getFont(FontFactory.COURIER, 14, BaseColor.BLACK);
        Font redFont = FontFactory.getFont(FontFactory.COURIER_BOLD, 14, BaseColor.RED);
        try {
            Paragraph chunk = new Paragraph("FAKTURA: VAT/"+format.format(current), bigFont);
            document.add(chunk);
            chunk = new Paragraph("Nabywca: firma sprzedajaca auta", font);
            document.add(chunk);
            chunk = new Paragraph("Sprzedawca: firma sprzedajaca auta", font);
            document.add(chunk);
            chunk = new Paragraph("Faktura za auta z roku: "+year+"\n", redFont);
            document.add(chunk);
            chunk = new Paragraph("", redFont);
            document.add(chunk);
            PdfPTable table = new PdfPTable(4);
            int counter = 1;
            int sum = 0;
            PdfPCell counterHeaderCell = new PdfPCell(new Phrase("lp", font));
            table.addCell(counterHeaderCell);
            PdfPCell priceHeaderCell = new PdfPCell(new Phrase("cena", font));
            table.addCell(priceHeaderCell);
            PdfPCell vatHeaderCell = new PdfPCell(new Phrase("vat", font));
            table.addCell(vatHeaderCell);
            PdfPCell valueHeaderCell = new PdfPCell(new Phrase("wartosc", font));
            table.addCell(valueHeaderCell);
            for (Car car :
                    cars) {
                PdfPCell counterCell = new PdfPCell(new Phrase(String.valueOf(counter), font));
                table.addCell(counterCell);
                PdfPCell priceCell = new PdfPCell(new Phrase(String.valueOf(car.getPrice()), font));
                table.addCell(priceCell);
                PdfPCell vatCell = new PdfPCell(new Phrase(String.valueOf(car.getVat()), font));
                table.addCell(vatCell);
                PdfPCell valueCell = new PdfPCell(new Phrase(String.valueOf(car.getPrice()+car.getPrice()*car.getVat()/100), font));
                table.addCell(valueCell);
                sum += car.getPrice()+car.getPrice()*car.getVat()/100;
                counter++;
            }
            document.add(table);

            chunk = new Paragraph("DO ZAPŁATY "+sum+" PLN", bigFont);
            document.add(chunk);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }




        document.close();
    }

    public void generateCarsByPriceInvoice(ArrayList<Car> carsByPrice) {
        Document document = new Document();
        Date current = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd/HH:mm:ss");
        String path = "invoices/invoice_all_cars_by_price_"+time+".pdf"; // lokalizacja zapisu
        System.out.println(path);
        try {
            PdfWriter.getInstance(document, new FileOutputStream(path));
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        document.open();
        Font bigFont = FontFactory.getFont(FontFactory.COURIER_BOLD, 16, BaseColor.BLACK);
        Font font = FontFactory.getFont(FontFactory.COURIER, 14, BaseColor.BLACK);
        Font redFont = FontFactory.getFont(FontFactory.COURIER_BOLD, 14, BaseColor.RED);
        try {
            Paragraph chunk = new Paragraph("FAKTURA: VAT/"+format.format(current), bigFont);
            document.add(chunk);
            chunk = new Paragraph("Nabywca: firma sprzedajaca auta", font);
            document.add(chunk);
            chunk = new Paragraph("Sprzedawca: firma sprzedajaca auta", font);
            document.add(chunk);
            chunk = new Paragraph("Faktura za auta w cenach: "+min+"-"+max+"\n", redFont);
            document.add(chunk);
            chunk = new Paragraph("", redFont);
            document.add(chunk);
            PdfPTable table = new PdfPTable(4);
            int counter = 1;
            int sum = 0;
            PdfPCell counterHeaderCell = new PdfPCell(new Phrase("lp", font));
            table.addCell(counterHeaderCell);
            PdfPCell priceHeaderCell = new PdfPCell(new Phrase("cena", font));
            table.addCell(priceHeaderCell);
            PdfPCell vatHeaderCell = new PdfPCell(new Phrase("vat", font));
            table.addCell(vatHeaderCell);
            PdfPCell valueHeaderCell = new PdfPCell(new Phrase("wartosc", font));
            table.addCell(valueHeaderCell);
            for (Car car :
                    carsByPrice) {
                PdfPCell counterCell = new PdfPCell(new Phrase(String.valueOf(counter), font));
                table.addCell(counterCell);
                PdfPCell priceCell = new PdfPCell(new Phrase(String.valueOf(car.getPrice()), font));
                table.addCell(priceCell);
                PdfPCell vatCell = new PdfPCell(new Phrase(String.valueOf(car.getVat()), font));
                table.addCell(vatCell);
                PdfPCell valueCell = new PdfPCell(new Phrase(String.valueOf(car.getPrice()+car.getPrice()*car.getVat()/100), font));
                table.addCell(valueCell);
                sum += car.getPrice()+car.getPrice()*car.getVat()/100;
                counter++;
            }
            document.add(table);

            chunk = new Paragraph("DO ZAPŁATY "+sum+" PLN", bigFont);
            document.add(chunk);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }




        document.close();
    }
}
