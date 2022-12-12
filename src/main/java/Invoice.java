import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.UUID;

public class Invoice {
    private long time;
    private String title;
    private String seller;
    private String buyer;
    private ArrayList<Car> list;
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

//            System.out.println(carInCars.getColor());
//            int color = 0;
//            String colorString = carInCars.getColor();
//            colorString = colorString.substring(1, colorString.length()-1);
//            for(int i = 0; i<colorString.length(); i++){
//                color = color + colorString.charAt(i); // do zrobienia kolor i obrazek wygenerowanych automatycznie
//            }
//
//            Font coloredFont = FontFactory.getFont(FontFactory.COURIER, 16, color);
            Paragraph chunk = new Paragraph("FAKTURA dla: " + carUUID, bigFont); // akapit
            document.add(chunk);
            chunk = new Paragraph("Model: " + carInCars.getModel(), bigFont); // akapit
            document.add(chunk);
            chunk = new Paragraph("Kolor: " + carInCars.getColor(), bigFont); // akapit
            document.add(chunk);
            chunk = new Paragraph("rok: " + carInCars.getYear(), bigFont); // akapit
            document.add(chunk);
            for (Airbag airbag :
                    carInCars.getAirbags()) {
                Paragraph paragraph = new Paragraph("poduszka: "+airbag.getDescription()+" - "+airbag.isValue());
                document.add(paragraph);
            }

        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }



        document.close();
    }
}
