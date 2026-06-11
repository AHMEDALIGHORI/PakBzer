package com.pakbzer.config;

import com.pakbzer.model.Category;
import com.pakbzer.model.Product;
import com.pakbzer.model.Review;
import com.pakbzer.repository.ProductRepository;
import com.pakbzer.repository.ReviewRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Seeds the catalogue on first run with Pakistani-themed products, ~10 per
 * category section, plus a few sample reviews. Runs only when the DB is empty.
 *
 * Each product row is: { name, description, price, unsplashPhotoId }.
 * The photo id points at a curated Unsplash image that matches the product
 * (a kurta shows a kurta, shoes show shoes, etc.).
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;

    public DataSeeder(ProductRepository productRepository, ReviewRepository reviewRepository) {
        this.productRepository = productRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    public void run(String... args) {
        if (productRepository.count() > 0) {
            return;
        }

        // ---------------- MEN ---------------- (verified men's traditional-wear photos)
        seed(Category.MEN, "Junaid Jamshed", new String[][]{
                {"Classic White Shalwar Kameez", "Premium cotton wash-and-wear unstitched suit.", "4500", "photo-1701365676249-9d7ab5022dec"},
                {"Embroidered Kurta - Navy", "Festive embroidered kurta with fine threadwork.", "3800", "photo-1711044871601-301f9b0ecf91"},
                {"Waistcoat Formal Black", "Tailored waistcoat for weddings and events.", "5200", "photo-1594938298603-c8148c4dae35"},
                {"Casual Kurta Pajama Set", "Soft cotton everyday kurta pajama.", "2900", "photo-1734418038940-2e5ee6a1b478"},
                {"Sherwani Maroon Royal", "Hand-embellished groom sherwani.", "18500", "photo-1727835523545-70ee992b5763"},
                {"Cotton Polo Shirt", "Breathable polo for summers.", "1600", "photo-1576566588028-4147f3842f27"},
                {"Denim Casual Shirt", "Slim-fit denim shirt.", "2400", "photo-1602810318383-e386cc2a3ccf"},
                {"Peshawari Chappal", "Handmade traditional Peshawari chappal.", "3500", "photo-1595950653106-6c9ebd614d3a"},
                {"Wool Shawl - Grey", "Warm winter shawl, pure wool.", "4200", "photo-1520903920243-00d872a2d1c9"},
                {"Formal Dress Pant", "Wrinkle-free formal trouser.", "2700", "photo-1473966968600-fa801b869a1a"},
        });

        // ---------------- WOMEN ----------------
        seed(Category.WOMEN, "Khaadi", new String[][]{
                {"3-Piece Lawn Suit - Floral", "Unstitched printed lawn with chiffon dupatta.", "5600", "photo-1610030469983-98e550d6193c"},
                {"Embroidered Chiffon Saree", "Elegant saree with sequin work.", "9800", "photo-1583391733956-3750e0ff4e8b"},
                {"Silk Formal Maxi", "Flowing silk maxi for evenings.", "12500", "photo-1595777457583-95e059d581b8"},
                {"Khaddar Winter Suit", "Warm khaddar 2-piece suit.", "4900", "photo-1594633312681-425c7b97ccd1"},
                {"Bridal Lehenga - Red", "Heavy embroidered bridal lehenga.", "45000", "photo-1610189351675-3e0c7d2b4c84"},
                {"Printed Kurti - Casual", "Daily wear cotton kurti.", "1900", "photo-1623609163859-ca93c959b98a"},
                {"Banarsi Dupatta Gold", "Pure banarsi dupatta with zari border.", "3600", "photo-1612722432474-b971cdcea546"},
                {"Linen Office Suit", "Smart linen 2-piece for work.", "5200", "photo-1591047139829-d91aecb6caea"},
                {"Pashmina Shawl Maroon", "Authentic Kashmiri pashmina.", "8800", "photo-1520903920243-00d872a2d1c9"},
                {"Embroidered Khussa", "Hand-stitched traditional khussa.", "2800", "photo-1543163521-1bf539c55dd2"},
        });

        // ---------------- KIDS ----------------
        seed(Category.KIDS, "Minnie Minors", new String[][]{
                {"Kids Frock - Pink", "Frilly party frock for girls.", "2300", "photo-1518831959646-742c3a14ebf7"},
                {"Cotton T-shirt Pack (3)", "Soft cotton tees, pack of three.", "1800", "photo-1503944583220-79d8926ad5e2"},
                {"Kids Eid Kurta Set", "Festive kurta shalwar for kids.", "2600", "photo-1622290291468-a28f7a7dc6a8"},
                {"Denim Dungaree", "Cute adjustable dungaree.", "2100", "photo-1471286174890-9c112ffca5b4"},
                {"Cartoon Hoodie", "Warm fleece hoodie.", "1950", "photo-1620799140408-edc6dcb6d633"},
                {"Kids Sandals", "Comfortable summer sandals.", "1400", "photo-1525966222134-fcfa99b8ae77"},
                {"School Uniform Set", "Durable everyday uniform.", "2200", "photo-1503454537195-1dcabb73ffb9"},
                {"Winter Sweater - Red", "Knitted warm sweater.", "1700", "photo-1576871337622-98d48d1cf531"},
                {"Printed Pajama Set", "Soft sleepwear set.", "1300", "photo-1596870230751-ebdfce98ec42"},
                {"Baby Romper", "Cotton romper for infants.", "1100", "photo-1522771930-78848d9293e8"},
        });

        // ---------------- BOY ----------------
        seed(Category.BOY, "Ideas by GulAhmed", new String[][]{
                {"Boys Kurta Shalwar White", "Crisp white kurta for boys.", "2400", "photo-1622290291468-a28f7a7dc6a8"},
                {"Casual Cargo Pant", "Durable cargo trousers.", "1900", "photo-1517445312882-bc9910d016b7"},
                {"Graphic T-Shirt Blue", "Trendy printed tee.", "1200", "photo-1503944583220-79d8926ad5e2"},
                {"Boys Waistcoat Set", "Kurta with matching waistcoat.", "3200", "photo-1734418038940-2e5ee6a1b478"},
                {"Denim Jacket Junior", "Stylish denim jacket.", "2800", "photo-1551028719-00167b16eac5"},
                {"Sports Track Suit", "Lightweight track suit.", "2600", "photo-1556906781-9a412961c28c"},
                {"Formal Shirt - Sky", "Smart formal shirt.", "1600", "photo-1602810318383-e386cc2a3ccf"},
                {"Boys Joggers", "Comfy everyday joggers.", "1500", "photo-1552902865-b72c031ac5ea"},
                {"Sherwani Kids Cream", "Festive kids sherwani.", "5400", "photo-1727835523545-70ee992b5763"},
                {"Hooded Sweatshirt", "Warm cotton sweatshirt.", "1850", "photo-1620799140408-edc6dcb6d633"},
        });

        // ---------------- ADULT ----------------
        seed(Category.ADULT, "Bonanza Satrangi", new String[][]{
                {"Premium 3-Piece Suit", "Tailored formal suit for men.", "22000", "photo-1594938298603-c8148c4dae35"},
                {"Designer Evening Gown", "Statement evening gown.", "16500", "photo-1595777457583-95e059d581b8"},
                {"Velvet Blazer - Black", "Luxury velvet blazer.", "11900", "photo-1507003211169-0a1dd7228f2d"},
                {"Embellished Party Saree", "Glamorous party saree.", "14500", "photo-1583391733956-3750e0ff4e8b"},
                {"Leather Formal Shoes", "Genuine leather oxfords.", "7600", "photo-1549298916-b41d501d3772"},
                {"Classic Wrist Watch", "Premium analog wrist watch.", "8500", "photo-1524592094714-0f0654e20314"},
                {"Cashmere Overcoat", "Premium winter overcoat.", "19500", "photo-1539533018447-63fcce2678e3"},
                {"Pearl Jewellery Set", "Elegant pearl set.", "8900", "photo-1515562141207-7a88fb7ce338"},
                {"Designer Handbag", "Hand-finished leather handbag.", "5400", "photo-1584917865442-de89df76afd3"},
                {"Wedding Sherwani Gold", "Luxury groom sherwani.", "38000", "photo-1711044871601-301f9b0ecf91"},
        });

        seedSampleReviews();
    }

    private void seed(Category category, String brand, String[][] rows) {
        int seedIndex = category.ordinal() * 100;
        for (String[] row : rows) {
            seedIndex++;
            // row[3] is an Unsplash photo id; build a cropped product image URL.
            String image = "https://images.unsplash.com/" + row[3] + "?w=600&h=750&fit=crop&q=80";
            Product product = new Product(
                    row[0],
                    row[1],
                    new BigDecimal(row[2]),
                    category,
                    image,
                    brand,
                    25 + (seedIndex % 40));
            productRepository.save(product);
        }
    }

    private void seedSampleReviews() {
        List<Product> products = productRepository.findAll();
        if (products.isEmpty()) {
            return;
        }
        Product first = products.get(0);
        reviewRepository.save(new Review(first, "Ahmed Raza", 5,
                "Excellent quality and fast delivery across Pakistan. Highly recommended!"));
        reviewRepository.save(new Review(first, "Sana Tariq", 4,
                "Beautiful fabric, fits well. Will order again from PakBzer."));

        if (products.size() > 10) {
            Product women = products.get(10);
            reviewRepository.save(new Review(women, "Hira Khan", 5,
                    "Loved the lawn print, exactly as shown. Great Eid shopping!"));
        }
    }
}
